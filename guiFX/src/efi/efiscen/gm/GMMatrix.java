/* 
 * Copyright (C) 2016 European Forest Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package efi.efiscen.gm;

import efi.efiscen.com.ComFltPipeElement;
import efi.efiscen.com.ComFltPipe;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * The main unit of EFISCEN simulation. Keeps a collection of cells and a growth
 * function. Performs all main actions: growth, management and harvest.
 * 
 */
public class GMMatrix implements Serializable {

    // Not just an ID, but at the same time info storage:lowest, byte:spec,
    // highest:reg. Exactly - regionID::ownerID::siteID::speciesID.
    public Long m_wID;
    // Name
    public String m_sName;
    // Number of columns
    public int m_wXsize;
    // Number of rows
    public int m_wYsize;

    public ArrayList<Float> m_Xclasses;
    public ArrayList<Float> m_Yclasses;

    // Amout of deadwood keeping variable
    public float m_DeadWood;
    public float m_BareArea;
    public float m_FromBare;

    // Regorwing coeff, i.e. part of thinned area going to extra volume class
    public float m_RegrGamma;

    public ArrayList<GMCell> m_Cells;

    private float m_Xbottom;
    private float m_Xtop;
    private float m_Ybottom;
    private float m_Ytop;
    private float m_Xstep;
    private float m_Ystep;
    
    //low volume class variables
    private float m_LowVolArea;
    private float m_LowVolAge;
    //share of volume normally going to
    //from bare to first age/volume that goes low volume class
    private float m_lowVolShare = 0.1f;
    //volume
    private float m_LowVolYval;
    private float m_LowVolMoveXY;

    private GMGrFunction m_pFunction;

    // To keep deadwood monitoring
    private ComFltPipe m_fpDwPipe;

    /**
     * Default constructor.
     */
    public GMMatrix () {
        m_sName = "Undefined";
        m_Cells = null;
        m_Xclasses = null;
        m_Yclasses = null;
        m_pFunction = null;
        m_BareArea = 0.0f;
        m_FromBare = 0.0f;
        m_RegrGamma = 0.4f;
        m_DeadWood = 0.0f;
    }

    /**
     * Parametrized constuctor.
     * @param cols number of columns
     * @param rows number of rows
     */
    public GMMatrix (int cols, int rows) {
        m_sName = "Undefined";
        m_wXsize = cols;
        m_wYsize = rows;
        m_Cells = new ArrayList<>(cols*rows);
        //Remark 2012
        //We need to init arrays here
        //will reinit them later
        m_Xclasses = new ArrayList<>(cols);
        m_Yclasses = new ArrayList<>(rows);
        for (int i=0;i<cols;i++) {
            m_Xclasses.add(0.0f);
        }
        for (int j=0;j<rows;j++) {
            m_Yclasses.add(0.0f);
        }
        m_pFunction = null;
        m_fpDwPipe = new ComFltPipe(cols);
        m_BareArea = 0.0f;
        m_FromBare = 0.0f;
        m_RegrGamma = 0.4f;
        m_DeadWood = 0.0f;
    }

    /**
     * Get the potential increment per matrix.
     * @return the potential increment
     */
    public float getIncrement () {
        int nHowMany;
        double ret;
        double sumarea;
        ret = 0.0;
        sumarea = 0.0;
        GMCell pCell;
        ret+=m_FromBare*m_BareArea*m_Yclasses.get(0);
        sumarea+=m_BareArea;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            //factor = pFl.getF_ratio()*pCell.getM_FellingsShare();
            double ar,vol,incr;
            int cind = pCell.m_wY;
            // Normal growth
            ar = (pCell.getM_MoveByXY()+pCell.getM_MoveByY())*pCell.getArea();
            // Growth boost 1
            ar += (m_RegrGamma*pCell.getM_MoveByXY() + (1-m_RegrGamma)
                    *(pCell.getM_MoveByXY()+pCell.getM_MoveByY()))*pCell.getM_ThinArea();
            incr = 0.0;
            if (cind<m_wYsize)
                incr = m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
            vol = ar*incr;
            ret+=vol;
            if (cind<m_wYsize-1) {
                incr = m_Yclasses.get(cind+1) - m_Yclasses.get(cind-1);
                ar = m_RegrGamma*(pCell.getM_MoveByXY()+pCell.getM_MoveByY())*pCell.getM_ThinArea();
                vol = ar*incr;
                ret+=vol;
            }
            sumarea+=pCell.getArea() + pCell.getM_ThinArea();
        }
        //if (sumarea) ret = ret/sumarea;
        return (float)ret;
    }

    /**
     * Calculate areas that could be felled.
     * @param minage
     * @param maxage maximium age
     * @param mintr minimium trimmings
     * @param maxtr maximium trimmings
     * @param belowtr below trimmings
     * @param belowage below age
     * @return zero if succesful
     */
    public ArrayList setFellingsRegimes (float minage, float maxage, float mintr,
            float maxtr, float belowtr, float belowage) {
        int nHowMany;
        int ret;
        float xarg,yarg;
        float minageNetti, maxageNetti;
        GMCell pCell;

        ret = 0;
        minageNetti = minage;
        maxageNetti = maxage;
        //Temp solution
        //float shiftNetti = 0.15*minageNetti;
        //if (shiftNetti<5) shiftNetti = 5;
        //minageNetti -= shiftNetti;
        //maxageNetti -= shiftNetti;
        xarg = maxageNetti - minageNetti;
        yarg = maxtr - mintr;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if ((pCell.getM_Xmin() >= belowage) && (pCell.getM_Xmax() <= minageNetti))
                pCell.setM_FellingsShare(belowtr);
            if ((pCell.getM_Xmin() >= minageNetti) && (pCell.getM_Xmax() <= maxageNetti))
                pCell.setM_FellingsShare(mintr + (pCell.getM_Xval() - minageNetti)*yarg/xarg);
            if (pCell.getM_Xmin() >= maxageNetti)
                pCell.setM_FellingsShare(maxtr);
            m_Cells.set(i, pCell);
        }

        return m_Cells;
    }

    /**
     * Setting changes of growth by ratio.
     * @param ratio
     * @return list of cells
     */
    public ArrayList setClimGrow (float ratio) {
        int nHowMany;
        int ret;

        ret = 0;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            pCell.setM_MoveByXY(ratio*pCell.getM_MoveByXYOrg());
            if (pCell.getM_MoveByXY() > 1.0) {
                pCell.setM_MoveByXY(1.0f);
                ret+=1;
            }
            pCell.setM_MoveByX(1.0f - pCell.getM_MoveByXY());
            m_Cells.set(i, pCell);
        }
        return m_Cells;
    }

    /**
     * @deprecated 
     * To make thinnings by moving area one volume class down.
     * @param pFr fellings data
     * @param pCa carbon data
     * @return
     */
    public double doThinning (GMFellings pFr, GMCarbonAlloc pCa) {
        int nHowMany;
        double ret;
        double factor,cfactor,carbon;
        double dwoodharv,dwfell;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;

        ret = 0.0;
        cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;
        dwoodharv = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            pCell.setM_ThArea(0.0f);
            pCell.setM_ThRem(0.0f);
            pCell.setM_ThSlash(0.0f);
            if (pCell.isM_bThinned() && (pCell.getM_ThinShare() > 0)) {
                float ar,vol,incr;
                int cind = pCell.m_wY;

                factor = pFr.getF_ratio() * pCell.getM_ThinShare();
                ar = (float)factor*pCell.getArea();
                incr = 0.0f;
                if (cind<m_wYsize)
                    incr =  m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
                pFr.setF_area(pFr.getF_area()+ar);
                vol = ar*incr;
                ret+=vol*(pFr.getF_stem());

                // Mefique stuff!
                pCell.setM_ThArea(ar);
                pCell.setM_ThRem((float)(vol*pFr.getF_stem()));
                // End Mefique!

                //pFr.setF_volume(pFr.getF_volume()+vol);
                pCell.setM_MoveAsThin(ar);
                pCell.setM_Area((pCell.getArea()-ar));

                // Deadwood harvesting: will use "unused" pFr.getF_froots() as
                // a ratio of deadwood fellings.
                dwfell = pFr.getF_froots() * pCell.getM_DWood();
                if (dwfell > 0) {
                    pCell.setM_DWood((float)(pCell.getM_DWood()-dwfell));
                    dwoodharv+=dwfell;
                    pCell.setM_ThRem((float)(pCell.getM_ThRem()+dwfell));
                }

                // Now litter production
                carbon = vol*cfactor;
                int j=0;
                while (pCell.getM_Xval() > pCa.getCa_pxvals().get(j)
                        && j < pCa.getCa_nsize()-1)
                    j+=1;
                pFr.setF_volume(pFr.getF_volume()+vol*(pFr.getF_stem()));

                // Bioenergy stuff
                pCell.setM_ThSlash((float)(carbon*(pCa.getCa_pbranch().get(j)*pFr.getF_branch()
                        +pCa.getCa_pleaves().get(j)*pFr.getF_leaves())));
                sstem += carbon*(1-pFr.getF_stem());
                sbr   += pCa.getCa_pbranch().get(j)*carbon*(1-pFr.getF_branch());
                scr   += pCa.getCa_pcroots().get(j)*carbon*(1-pFr.getF_croots());
                sfr   += pCa.getCa_pfroots().get(j)*carbon;
                slv   += pCa.getCa_pleaves().get(j)*carbon*(1-pFr.getF_leaves());

            }
            /*
            pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
            pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
            pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
            pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
            pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);
            */
            m_Cells.set(i, pCell);
        }

        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);
        //Deadwood decr. because of thinnings
        m_DeadWood-=dwoodharv;
        
        return ret;
    }

    /**
     * The natural mortality implementation (and at same time deadwood decay).
     * Mortality ratios in pRat array and age dependence in pLims.
     * Approach of browsing - matrix as an array.
     * @param pFr fellings
     * @param pCa carbon
     * @param pLims dependence
     * @param pRat mortality ratios
     * @param nsize array size
     * @return the volume
     */
    public double doNaturalMortality (GMFellings pFr, GMCarbonAlloc pCa,
            ArrayList<Float> pLims, ArrayList<Float> pRat, int nsize) {
        int nHowMany;
        //int nret;
        double ret;
        double factor,cfactor,carbon;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;
        GMCell pCellTarget;

        ret = 0.0;
        cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;

        // Dead wood decay with litter collecting
        float decay = (float)(m_DeadWood*pFr.getF_ratio());
        m_DeadWood-=decay;
        sstem+=decay*cfactor;

        // Browsing now
        pCellTarget = null;
        nHowMany = m_wXsize*m_wYsize - 1;
        float[] vols = new float[m_wXsize];

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            // Try to find cell one age class older and one volume class lower.
            // May be better one volume class down and same age?
            pCellTarget = getAt(pCell.m_wX+1,pCell.m_wY-1);
            // If no success - second try (oldest ages will go down)
            if (pCellTarget == null)
                pCellTarget = getAt(pCell.m_wX,pCell.m_wY-1);
            // If we find target
            if (pCellTarget != null) {
                double ar,vol,incr;
                // Finding ratio
                int j=0;
                while (j < nsize-1 && (pCell.getM_Xmax() > pLims.get(j)))
                    j+=1;
                factor = pRat.get(j);
                // Area calculation
                incr = 0.0;
                incr = pCell.getM_Yval() - pCellTarget.getM_Yval();
                factor*=pCell.getM_Yval()/incr;
                if (factor>=1.0) {
                    //nret+=1; 
                    //System.out.println("Mortality: too big rate!");
                    factor = 1.0;
                }
                ar = factor*pCell.getArea();
                pFr.setF_area(pFr.getF_area()+ar);
                pCell.setM_Area((float)(pCell.getArea()-ar));

                // Getting area away from grow
                //! Should we do like this: pCell.setM_MoveAway(ar)? !
                //! May be not! !
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+ar));

                // Losses calculation and adding to DeadWood
                //incr = 0.0;
                //incr = pCell.getM_Yval() - pCellTarget.getM_Yval();
                vol = ar*incr;
                
                decay = (float)(pCell.getM_DWood() * pFr.getF_ratio());
                pCell.setM_DWood(pCell.getM_DWood()-decay);
                pCell.setM_DWood((float)(pCell.getM_DWood()+vol));
                m_DeadWood+=vol;
                ret+=vol;
                pFr.setF_volume(pFr.getF_volume()+vol);
                Float ageClassVol = vols[pCell.m_wX-1];
                vols[pCell.m_wX-1] = (float)(vol + ageClassVol);

                // Deadwood pipe filling March 2008
               /* ComFltPipeElement pcfp = m_fpDwPipe.getElement(pCell.m_wX-1);
                decay = (float)(pcfp.getCfp_value() * pFr.getF_ratio());
                pcfp.setCfp_value(pcfp.getCfp_value()-decay);
                pcfp.setCfp_value((float)(pcfp.getCfp_value()+vol));*/

                // Natural mortality keeping
                pCell.setM_NatMrt((float)vol);
                // Now litter production
                carbon = vol*cfactor;
                j=0;
                while ((pCell.getM_Xval() > pCa.getCa_pxvals().get(j))
                        && j < pCa.getCa_nsize()-1)
                    j+=1;

                // With assumtion at now that all stem goes to deadwood and all
                // other compartments to the litter but leave commented useful
                // shares for future modifications.
                //sstem += carbon*(1-pFr.getF_stem());
                sbr   += pCa.getCa_pbranch().get(j)*carbon;//*(1-pFr.getF_branch());
                scr   += pCa.getCa_pcroots().get(j)*carbon;//*(1-pFr.getF_croots());
                sfr   += pCa.getCa_pfroots().get(j)*carbon;//*(1-pFr.getF_froots());
                slv   += pCa.getCa_pleaves().get(j)*carbon;//*(1-pFr.getF_leaves());
            }
            /*
            pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
            pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
            pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
            pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
            pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);
            */
            m_Cells.set(i,pCell);
        }
        ComFltPipeElement pPipe;
        nHowMany = m_fpDwPipe.m_nSize;
        for (int i=0;i<nHowMany;i++) {
            float vol = vols[i];
            pPipe = m_fpDwPipe.getElement(i);
            decay = (float) (pPipe.getCfp_value() * pFr.getF_ratio());
            pPipe.setCfp_value((float)(pPipe.getCfp_value()-decay+vol));
        }
        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);
        
        return ret;
    }

    /**
     * Initialisation of deadwood amount by using current state of matrix as
     * spinup.
     * @param pLims age limits
     * @param pRat rations
     * @param nsize array size
     * @param dec amount of decay
     * @return the amount of deadwood
     */
    public double initDeadWood (ArrayList<Float> pLims, ArrayList<Float> pRat,
            int nsize, float dec) {
        int nHowMany;
        double ret;
        double factor;
        double sstem;
        GMCell pCell;
        GMCell pCellTarget;

        ret = 0.0;
        //cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;
        // Checking decay rate
        if (dec<0.001) dec = 0.001f;

        // Browsing now
        pCellTarget = null;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            // Try to find cell one age class older and one volume class lower
            pCellTarget = getAt(pCell.m_wX+1,pCell.m_wY-1);
            // If no success - second try (oldest ages will go down)
            if (pCellTarget == null) 
                pCellTarget = getAt(pCell.m_wX,pCell.m_wY-1);
            // If we find target
            if (pCellTarget != null) {
                double ar,vol,incr;

                // Finding ratio
                int j=0;
                while (j < nsize-1 && (pCell.getM_Xmax() > pLims.get(j)))
                    j+=1;
                factor = pRat.get(j);

                // Income to deadwood spwinup
                incr = 0.0;
                incr = pCell.getM_Yval() - pCellTarget.getM_Yval();
                factor*=pCell.getM_Yval()/incr;
                if (factor>=1.0) {
                    //nret+=1;
                    //System.out.println("Mortality: to big rate!");
                    factor = 1.0;
                }

                // Area calculation
                ar = factor*pCell.getArea();
                vol = ar*incr;
                pCell.setM_DWood((float)vol/dec);

                // Init deadwood pipe values
                ComFltPipeElement pcfp = m_fpDwPipe.getElement(pCell.m_wX-1);
                pcfp.setCfp_value(pcfp.getCfp_value()+pCell.getM_DWood());
                sstem+=vol;
                ret+=vol;
            }
            m_Cells.set(i, pCell);
        }
        m_DeadWood = (float)sstem/dec;

        return m_DeadWood;
    }

    /**
     * Initialisation of deadwood amount by using current state of matrix as
     * spinup.
     * @param pLims
     * @param pRat rations
     * @param nsize array size
     * @param decay amount of decay
     * @param threm thinnings removals
     * @param felrem fellings removals
     * @return the amount of deadwood
     */
    public double initDeadWoodEx (ArrayList<Float> pLims, ArrayList<Float> pRat, int nsize,
            float decay, float threm, float felrem) {
        int nHowMany;
        double ret;
        double factor;
        double sstem;
        float dec_int;
        GMCell pCell;
        GMCell pCellTarget;

        ret = 0.0;
        //cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;

        // Browsing now
        pCellTarget = null;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            // Try to find cell one age class older and one volume class lower
            pCellTarget = getAt(pCell.m_wX+1,pCell.m_wY-1);
            // If no success - second try (oldest ages will go down)
            if (pCellTarget == null) pCellTarget = getAt(pCell.m_wX,pCell.m_wY-1);
            // If we find target
            if (pCellTarget != null) {
                double ar,vol,incr;
                dec_int = decay;

                // Finding ratio
                int j=0;
                while (j < nsize-1 && (pCell.getM_Xmax() > pLims.get(j)))
                    j+=1;
                factor = pRat.get(j);

                // Income to deadwood spwinup
                incr = 0.0;
                incr = pCell.getM_Yval() - pCellTarget.getM_Yval();
                factor*=pCell.getM_Yval()/incr;
                if (factor>=1.0) {
                    //nret+=1;
                    //System.out.println("Mortality: to big rate!");
                    factor = 1.0;
                }

                // Area calculation
                ar = factor*pCell.getArea();
                vol = ar*incr;
                // Bug fixing
                if (pCell.isM_bThinned())
                    dec_int = threm + dec_int - dec_int*threm;
                else
                    if (pCell.getM_FellingsShare() > 0)
                        dec_int = felrem + dec_int - dec_int*felrem;
                if (dec_int>1.0f)
                    dec_int = 1.0f;
                if (dec_int<0.001f) 
                    dec_int = 0.001f;
                pCell.setM_DWood((float)vol/dec_int);

                // Init deadwood pipe values
                ComFltPipeElement pcfp = m_fpDwPipe.getElement(pCell.m_wX-1);
                pcfp.setCfp_value((float) (pcfp.getCfp_value()+(vol/dec_int)));
                sstem+=vol;
                ret+=vol/dec_int;

            }
            m_Cells.set(i, pCell);
        }
        m_DeadWood = (float)ret;
        
        return m_DeadWood;
    }

    /**
     * Reporting harverst.
     * @param pFl fellings
     * @return fellings object with harvest data
     */
    public GMFellings reportHarvest (GMFellings pFl) {
        int nHowMany;
        //double ret = 0.0;
        double factor;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            factor = pFl.getF_ratio() * pCell.getM_FellingsShare();
            if (factor>0.0) {
                double ar;
                ar = factor*pCell.getArea();
                pFl.setF_area(pFl.getF_area()+ar);
                /** TODO Check! Previous code line:
                *
                pFl.setF_volume((pFl.getF_volume()+ar)*pCell.getM_Yval()
                        *pFl.getF_stem());
                *
                *  Now:
                * @see #makeFellings()
                */
                pFl.setF_volume(pFl.getF_volume()+ar*pCell.getM_Yval()
                        *pFl.getF_stem());
            }
            //ret+=(pCell.getArea())*(pCell.getM_Xval());
        }
        return pFl;
    }

    /**
     * Reporting thinnigs and growth.
     * Idea: to make thinnings by moving area one volume class down.
     * @param pFl fellings
     * @return fellings object with thinnings data
     */
    public GMFellings reportThinnings (GMFellings pFl) {
        int nHowMany;
        //double ret = 0.0;
        double factor;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            //factor = pFl.getF_ratio()*pCell.getM_FellingsShare();
            if (pCell.isM_bThinned() && pCell.getM_ThinShare() > 0) {
                double ar,vol,incr;
                int cind = pCell.m_wY;
                factor = pFl.getF_ratio()*pCell.getM_ThinShare();
                ar = factor*pCell.getArea();
                incr = 0.0;
                if (cind<m_wYsize)
                    incr = m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
                vol = ar*incr;
                pFl.setF_area(pFl.getF_area()+ar);
                // Very temporary!
                pFl.setF_volume(pFl.getF_volume() + vol*(pFl.getF_stem()));
            }
            //ret+=(pCell.getArea())*(pCell.getM_Xval());
        }

        return pFl;
    }

    /**
     * Version from "old" EFISCEN thanks to ALTERRA (MJ and Ari)
     * Reporting thinnigs and growth.
     * Idea: to make thinnings by moving area one volume class down.
     * @param pFl fellings
     * @return fellings object with thinnings data
     */
    public GMFellings reportThinningsV4 (GMFellings pFl) {
        int nHowMany;
        //double ret = 0.0;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            //factor = pFl.getF_ratio()*pCell.getM_FellingsShare();
            if (pCell.isM_bThinned()) {
                double ar,vol,incr;
                int cind = pCell.m_wY - 1; //one volume class down!;
                ar = pFl.getF_ratio()*pCell.getArea();
                incr = 0.0;
                // Lowest volume class could not be thinned (at the moment at least!)
                if (cind > 0)
                    incr = m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
                vol = ar*incr;
                if (incr > 0)
                    pFl.setF_area(pFl.getF_area()+ar);
                // Very temporary!
                pFl.setF_volume(pFl.getF_volume() + vol*(pFl.getF_stem()));
            }
            //ret+=(pCell.getArea())*(pCell.getM_Xval());
        }
        return pFl;
    }

    /**
     * New version of thinnings - from "old" EFISCEN
     * Thanks to MJ and Ari
     * Idea: to make thinnings by moving area one volume class down
     * Difference in couple of lines only!
     * One of set of V4 functions.
     * @param pFr fellings removals
     * @param pCa carbon
     * @return
     */
    public double doThinningV4 (GMFellings pFr, GMCarbonAlloc pCa) {
        int nHowMany;
        double ret;
        double factor,cfactor,carbon;
        double dwoodharv,dwoodharv1,dwfell;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;
        GMCell pCellTarget;

        ret = 0.0;
        cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;
        dwoodharv = 0.0;
        dwoodharv1 = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            pCell.setM_ThArea(0.0f);
            pCell.setM_ThRem(0.0f);
            pCell.setM_ThSlash(0.0f);
            pCellTarget = getAt(pCell.m_wX,pCell.m_wY-1);
            if (pCell.isM_bThinned() && pCellTarget != null) {
                float ar,vol,incr;
                ar = (float)(pFr.getF_ratio()*pCell.getArea());
                incr = pCell.getM_Yval() - pCellTarget.getM_Yval();
                pFr.setF_area(pFr.getF_area()+ar);
                vol = ar*incr;
                ret+=vol*(pFr.getF_stem());

                // Mefique stuff!
                pCell.setM_ThArea(ar);
                pCell.setM_ThRem((float)(vol*pFr.getF_stem()));
                // End Mefique!

                //pFr.setF_volume(pFr.getF_volume()+vol);
                pCellTarget.setM_MoveAsThin(pCellTarget.getM_MoveAsThin()+ar);
                pCell.setM_Area((pCell.getArea()-ar));

                // Deadwood harvesting: will use "unused" pFr.getF_froots() as
                // a ratio of deadwood fellings.
                // Commented out due to removals dwood from pipe! March 2008
                
                dwfell = pFr.getF_froots() * pCell.getM_DWood();
                if (dwfell > 0) {
                    pCell.setM_DWood((float)(pCell.getM_DWood()-dwfell));
                    //BUG fixing: double accounting of DW harvest. Now accumulate
                    //here in separate variable dwoodharv1 instead dwoodharv as
                    //it was before
                    //TODO: debugging shows huge difference between two approaches
                    //dwoodharv seems to be correct! February 2023
                    dwoodharv1+=dwfell;
                    //pCell.setM_ThRem((float)(pCell.getM_ThRem()+dwfell));
                }
                
                // Now litter production
                carbon = vol*cfactor;
                int j=0;
                while (pCell.getM_Xval() > pCa.getCa_pxvals().get(j)
                        && j < pCa.getCa_nsize()-1)
                    j+=1;
                pFr.setF_volume(pFr.getF_volume()+vol*(pFr.getF_stem()));

                // Bioenergy stuff. Updated September 2009 (Uppsala) : coarse roots added
                pCell.setM_ThSlash((float)(carbon*(pCa.getCa_pbranch().get(j)
                        *pFr.getF_branch()+pCa.getCa_pcroots().get(j)
                        *pFr.getF_croots()+pCa.getCa_pleaves().get(j)*pFr.getF_leaves())));
                sstem += carbon*(1-pFr.getF_stem());
                sbr   += pCa.getCa_pbranch().get(j)*carbon*(1-pFr.getF_branch());
                scr   += pCa.getCa_pcroots().get(j)*carbon*(1-pFr.getF_croots());
                sfr   += pCa.getCa_pfroots().get(j)*carbon;
                slv   += pCa.getCa_pleaves().get(j)*carbon*(1-pFr.getF_leaves());

            }
            /*
            pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
            pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
            pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
            pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
            pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);
            */
        }

        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);

        // Implementig deadwood harvesting by using dwpipe! March 2008
        ComFltPipeElement pPipe;
        nHowMany = m_fpDwPipe.m_nSize;
        for (int i=0;i<nHowMany;i++) {
            pPipe = m_fpDwPipe.getElement(i);
            dwfell = pFr.getF_froots()*pPipe.getCfp_value();
            pPipe.setCfp_value((float)(pPipe.getCfp_value()-dwfell));
            pPipe.setCfp_threm((float)dwfell);
            dwoodharv+=dwfell;
        }

        //Deadwood decr. because of thinnings
        m_DeadWood-=dwoodharv;

        return ret;
    }

    /**
     * Calculate the growth by cells.
     * New version of the grow - from "old" EFISCEN
     * Thanks to MJ and Ari
     * Idea: to make thinnings by moving area one volume class down.
     * One of set of V4 functions.
     * @return cells
     */
    public ArrayList growV4 () {
        int i,j;
        GMCell pCell = m_Cells.get(0);
        GMCell pCellTarget;
        double partMove,rgrArea;
        double partMoveByX,partMoveByXY;
        partMove = m_FromBare*m_BareArea;
        if (m_BareArea-partMove<0.00000001)
            partMove = m_BareArea;
        if (pCell != null) {
            pCell.setM_Move(0.0f);
            pCell.setM_Income((float)partMove);
            m_Cells.set(0, pCell);
        }
        m_BareArea-=partMove;

        if (partMove < 0) {
            System.out.println("Debug:Area "+partMove+"\nDebug:");
            System.out.println("Debug:less than zero");
            System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                    +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
            System.out.println("Debug:less than zero");
        }
        for (i=1;i<m_wXsize;i++) {
            for (j=1;j<m_wYsize;j++) {
                //pCell = m_Cells.get(j-1);
                pCell = getAt(i,j);
                rgrArea = 0.0;
                pCell.setM_Move(0f);

                // We have three "funds" of area:
                // Normal, Just thinned, With thinned status
                // First normal area
                pCellTarget = getAt(i+1,j+1);
                partMove = pCell.getM_MoveByXY()*pCell.getArea();
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));

                setAt(i+1,j+1,pCellTarget);
                pCellTarget = getAt(i+1,j);
                partMove = pCell.getArea() - pCell.getM_Move();

                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i+1,j,pCellTarget);
                // Just this step thinned area - normal grow, but source is m_ThinArea basket in target
                if (pCell.getM_MoveAsThin() > 0) {
                    partMove = pCell.getM_MoveByX()*pCell.getM_MoveAsThin();
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMove));
                    pCell.setM_MoveAsThin((float)(pCell.getM_MoveAsThin()-partMove));
                    setAt(i+1,j,pCellTarget);
                    pCellTarget = getAt(i+1,j+1);
                    pCellTarget.setM_MoveAway(pCellTarget.getM_MoveAway()+pCell.getM_MoveAsThin());
                    pCell.setM_MoveAsThin(0.0f);
                    setAt(i+1,j+1,pCellTarget);
                }

                // Thinned area from previous steps development
                if (pCell.getM_ThinArea() > 0) {
                    // Distributing thin area by destinations
                    partMoveByX = pCell.getM_MoveByX()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //no grow - thin status j
                    partMoveByXY = pCell.getM_MoveByXY()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //grow - thin status j+1
                    partMove = pCell.getM_MoveByX()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status j+1
                    rgrArea = pCell.getM_MoveByXY()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status
                    //i+1,j+1
                    pCellTarget = getAt(i+1,j+1);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByXY));
                    pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                    //pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                    //i+1,j
                    setAt(i+1,j+1,pCellTarget);
                    pCellTarget = getAt(i+1,j);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByX));
                    //i+1,j+2
                    setAt(i+1,j,pCellTarget);
                    pCellTarget = getAt(i+1,j+2);
                    if (pCellTarget != null) {
                        pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+rgrArea));
                        setAt(i+1,j+2,pCellTarget);
                    }
                    else {
                        pCellTarget = getAt(i+1,j+1);
                        pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+rgrArea));
                        setAt(i+1,j+1,pCellTarget);
                    }
                    pCell.setM_ThinArea(0.0f);
                }
                //m_Cells.set(j-1, pCell);
                setAt(i,j,pCell);
            } // for
            //pCell = m_Cells.get(i-1);
            pCell = getAt(i,j);
            // Top row - highest volume class
            pCellTarget = getAt(i+1,j);
            partMove = pCell.getM_MoveByX()*pCell.getArea();
            if (partMove < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            if (pCell.getM_ThinArea() < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            if (pCell.getM_ThinArea() > 0) {
                pCellTarget.setM_Income(pCellTarget.getM_Income()+pCell.getM_ThinArea());
                pCell.setM_ThinArea(0.0f);
            }
            setAt(i+1,j,pCellTarget);
            //m_Cells.set(i-1,pCell);
            setAt(i,j,pCell);
        } // for
        // Last column - highest age
        for (j=1;j<m_wYsize;j++) {
            pCell = getAt(i,j);
            pCell.setM_Move(0.0f);
            pCellTarget = getAt(i,j+1);
            partMove = pCell.getM_MoveByXY()*pCell.getArea();
            if (partMove < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }

            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            //pCell.setM_Income(pCell.getM_Income()+pCell.getM_Area()-partMove);
            setAt(i,j+1,pCellTarget);
            // Just this step thinned area - normal grow, but source is m_ThinArea basket in target
            if (pCell.getM_MoveAsThin() > 0) {
                partMove = pCell.getM_MoveByX()*pCell.getM_MoveAsThin();
                pCell.setM_MoveAway((float)(pCell.getM_MoveAway()+partMove));
                pCell.setM_MoveAsThin((float)(pCell.getM_MoveAsThin()-partMove));
                pCellTarget.setM_MoveAway(pCellTarget.getM_MoveAway()+pCell.getM_MoveAsThin());
                pCell.setM_MoveAsThin(0.0f);
                setAt(i,j+1,pCellTarget);
            }
            if (pCell.getM_ThinArea() > 0) {
                // Distributing thin area by destinations
                partMoveByX = pCell.getM_MoveByX()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //no grow - thin status j
                partMoveByXY = pCell.getM_MoveByXY()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //grow - thin status j+1
                partMove = pCell.getM_MoveByX()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status j+1
                rgrArea = pCell.getM_MoveByXY()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status
                //i+1,j+1
                pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByXY));
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                //pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i,j+1,pCellTarget);
                pCell.setM_MoveAway((float)(pCell.getM_MoveAway()+partMoveByX));
                //i+1,j+2
                pCellTarget = getAt(i,j+2);
                if (pCellTarget != null) {
                    pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+rgrArea));
                    setAt(i,j+2,pCellTarget);
                }
                else {
                    pCellTarget = getAt(i,j+1);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+rgrArea));
                    setAt(i,j+1,pCellTarget);
                }
                pCell.setM_ThinArea(0.0f);
            }
            //m_Cells.set(i-1, pCell);
            setAt(i,j,pCell);
        } // for
        return m_Cells;
    }
    
    /**
     * version of growV4 method with low volume
     * class.
     * @return 
     */
    public ArrayList growV4LV () {
        int i,j;
        GMCell pCell = m_Cells.get(0);
        GMCell pCellTarget;
        double partMove,rgrArea;
        double partMoveByX,partMoveByXY;
        partMove = m_FromBare*m_BareArea;
        if (m_BareArea-partMove<0.00000001)
            partMove = m_BareArea;
        if (pCell != null) {
            pCell.setM_Move(0.0f);
            pCell.setM_Income((float)partMove*(1f-m_lowVolShare));
            m_Cells.set(0, pCell);
        }
        m_LowVolArea += m_lowVolShare*partMove;
        m_BareArea-=partMove;

        if (partMove < 0) {
            System.out.println("Debug:Area "+partMove+"\nDebug:");
            System.out.println("Debug:less than zero");
            System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                    +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
            System.out.println("Debug:less than zero");
        }
        pCell = getAt(1,1);
        pCellTarget = getAt(1,2);
        partMove = pCell.getM_MoveByXY()*m_LowVolArea;
        pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
        m_LowVolArea-=partMove;
        
        for (i=1;i<m_wXsize;i++) {
            for (j=1;j<m_wYsize;j++) {
                //pCell = m_Cells.get(j-1);
                pCell = getAt(i,j);
                rgrArea = 0f;
                pCell.setM_Move(0f);

                // We have three "funds" of area:
                // Normal, Just thinned, With thinned status
                // First normal area
                pCellTarget = getAt(i+1,j+1);
                partMove = pCell.getM_MoveByXY()*pCell.getArea();
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));

                setAt(i+1,j+1,pCellTarget);
                pCellTarget = getAt(i+1,j);
                partMove = pCell.getArea() - pCell.getM_Move();

                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i+1,j,pCellTarget);
                // Just this step thinned area - normal grow, but source is m_ThinArea basket in target
                if (pCell.getM_MoveAsThin() > 0) {
                    partMove = pCell.getM_MoveByX()*pCell.getM_MoveAsThin();
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMove));
                    pCell.setM_MoveAsThin((float)(pCell.getM_MoveAsThin()-partMove));
                    setAt(i+1,j,pCellTarget);
                    pCellTarget = getAt(i+1,j+1);
                    pCellTarget.setM_MoveAway(pCellTarget.getM_MoveAway()+pCell.getM_MoveAsThin());
                    pCell.setM_MoveAsThin(0.0f);
                    setAt(i+1,j+1,pCellTarget);
                }

                // Thinned area from previous steps development
                if (pCell.getM_ThinArea() > 0) {
                    // Distributing thin area by destinations
                    partMoveByX = pCell.getM_MoveByX()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //no grow - thin status j
                    partMoveByXY = pCell.getM_MoveByXY()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //grow - thin status j+1
                    partMove = pCell.getM_MoveByX()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status j+1
                    rgrArea = pCell.getM_MoveByXY()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status
                    //i+1,j+1
                    pCellTarget = getAt(i+1,j+1);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByXY));
                    pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                    //pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                    //i+1,j
                    setAt(i+1,j+1,pCellTarget);
                    pCellTarget = getAt(i+1,j);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByX));
                    //i+1,j+2
                    setAt(i+1,j,pCellTarget);
                    pCellTarget = getAt(i+1,j+2);
                    if (pCellTarget != null) {
                        pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+rgrArea));
                        setAt(i+1,j+2,pCellTarget);
                    }
                    else {
                        pCellTarget = getAt(i+1,j+1);
                        pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+rgrArea));
                        setAt(i+1,j+1,pCellTarget);
                    }
                    pCell.setM_ThinArea(0.0f);
                }
                //m_Cells.set(j-1, pCell);
                setAt(i,j,pCell);
            } // for
            //pCell = m_Cells.get(i-1);
            pCell = getAt(i,j);
            // Top row - highest volume class
            pCellTarget = getAt(i+1,j);
            partMove = pCell.getM_MoveByX()*pCell.getArea();
            if (partMove < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            if (pCell.getM_ThinArea() < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            if (pCell.getM_ThinArea() > 0) {
                pCellTarget.setM_Income(pCellTarget.getM_Income()+pCell.getM_ThinArea());
                pCell.setM_ThinArea(0.0f);
            }
            setAt(i+1,j,pCellTarget);
            //m_Cells.set(i-1,pCell);
            setAt(i,j,pCell);
        } // for
        // Last column - highest age
        for (j=1;j<m_wYsize;j++) {
            pCell = getAt(i,j);
            pCell.setM_Move(0.0f);
            pCellTarget = getAt(i,j+1);
            partMove = pCell.getM_MoveByXY()*pCell.getArea();
            if (partMove < 0) {
                System.out.println("Debug:Oooops111\nDebug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }

            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            //pCell.setM_Income(pCell.getM_Income()+pCell.getM_Area()-partMove);
            setAt(i,j+1,pCellTarget);
            // Just this step thinned area - normal grow, but source is m_ThinArea basket in target
            if (pCell.getM_MoveAsThin() > 0) {
                partMove = pCell.getM_MoveByX()*pCell.getM_MoveAsThin();
                pCell.setM_MoveAway((float)(pCell.getM_MoveAway()+partMove));
                pCell.setM_MoveAsThin((float)(pCell.getM_MoveAsThin()-partMove));
                pCellTarget.setM_MoveAway(pCellTarget.getM_MoveAway()+pCell.getM_MoveAsThin());
                pCell.setM_MoveAsThin(0.0f);
                setAt(i,j+1,pCellTarget);
            }
            if (pCell.getM_ThinArea() > 0) {
                // Distributing thin area by destinations
                partMoveByX = pCell.getM_MoveByX()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //no grow - thin status j
                partMoveByXY = pCell.getM_MoveByXY()*(1-m_RegrGamma)*pCell.getM_ThinArea(); //grow - thin status j+1
                partMove = pCell.getM_MoveByX()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status j+1
                rgrArea = pCell.getM_MoveByXY()*m_RegrGamma*pCell.getM_ThinArea(); //regrow - no thin status
                //i+1,j+1
                pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+partMoveByXY));
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                //pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i,j+1,pCellTarget);
                pCell.setM_MoveAway((float)(pCell.getM_MoveAway()+partMoveByX));
                //i+1,j+2
                pCellTarget = getAt(i,j+2);
                if (pCellTarget != null) {
                    pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+rgrArea));
                    setAt(i,j+2,pCellTarget);
                }
                else {
                    pCellTarget = getAt(i,j+1);
                    pCellTarget.setM_MoveAway((float)(pCellTarget.getM_MoveAway()+rgrArea));
                    setAt(i,j+1,pCellTarget);
                }
                pCell.setM_ThinArea(0.0f);
            }
            //m_Cells.set(i-1, pCell);
            setAt(i,j,pCell);
        } // for
        return m_Cells;
    }

    /**
     * New version of the update method - from "old" EFISCEN
     * Thanks to MJ and Ari
     * Idea: to make thinnings by moving area one volume class down.
     * One of set of V4 functions.
     * @return
     */
    public float updateV4 () {
        int nHowMany;
        double diff,check;
        float ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            diff = pCell.getM_Income() - pCell.getM_Move();
            check = pCell.getArea() - pCell.getM_Move();

            if (diff < 0 && Math.abs(diff) > pCell.getArea()) {
                System.out.println("Debug:Oooops1\nDebug:Area "+pCell.getArea()+"\nDebug:plus "
                        +pCell.getM_Income()+"\nDebug:minus "+diff+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "
                        +getOwnerID()+"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            check = pCell.getArea() + diff;
            if (check < 0) {
                System.out.println("Debug:Oooops2\nDebug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            pCell.setM_Area((float)(pCell.getArea() + diff)); //pCell.getM_Income() - pCell.getM_Move() - pCell.getM_MoveAway();
            //pCell.setM_Area(pCell.getM_Income());
            //if (pCell.getArea() < 0.000001) pCell.setM_Area(0.0f);
            if (pCell.getArea() < 0) {
                System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:negative area");
            }
            // Thinnined area update
            if (pCell.isM_bThinned())
                pCell.setM_ThinArea(pCell.getM_ThinArea()+pCell.getM_MoveAway());
            else
                pCell.setM_Area(pCell.getArea()+pCell.getM_MoveAway());
            // End thinnings update
            ret+=(pCell.getArea() + pCell.getM_ThinArea()) * pCell.getM_Yval();
            pCell.setM_Move(0.0f);
            pCell.setM_MoveAway(0.0f);
            pCell.setM_Income(0.0f);
            m_Cells.set(i, pCell);
        }
        //no shifting
        //m_fpDwPipe.shift();
        return ret;
    }
    
    /**
     * @deprecated
     * version of updateV4 method with low volume class
     */
    public float updateV4LV() {
        int nHowMany;
        double diff,check;
        float ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;

        pCell = m_Cells.get(0);
        ret+=m_LowVolArea*m_LowVolYval;
        
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            diff = pCell.getM_Income() - pCell.getM_Move();
            check = pCell.getArea() - pCell.getM_Move();

            if (diff < 0 && Math.abs(diff) > pCell.getArea()) {
                System.out.println("Debug:Oooops1\nDebug:Area "+pCell.getArea()+"\nDebug:plus "
                        +pCell.getM_Income()+"\nDebug:minus "+diff+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "
                        +getOwnerID()+"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            check = pCell.getArea() + diff;
            if (check < 0) {
                System.out.println("Debug:Oooops2\nDebug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            pCell.setM_Area((float)(pCell.getArea() + diff)); //pCell.getM_Income() - pCell.getM_Move() - pCell.getM_MoveAway();
            //pCell.setM_Area(pCell.getM_Income());
            //if (pCell.getArea() < 0.000001) pCell.setM_Area(0.0f);
            if (pCell.getArea() < 0) {
                System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:negative area");
            }
            // Thinnined area update
            if (pCell.isM_bThinned())
                pCell.setM_ThinArea(pCell.getM_ThinArea()+pCell.getM_MoveAway());
            else
                pCell.setM_Area(pCell.getArea()+pCell.getM_MoveAway());
            // End thinnings update
            ret+=(pCell.getArea() + pCell.getM_ThinArea()) * pCell.getM_Yval();
            pCell.setM_Move(0.0f);
            pCell.setM_MoveAway(0.0f);
            pCell.setM_Income(0.0f);
            m_Cells.set(i, pCell);
        }
        m_fpDwPipe.shift();
        return ret;
    }

    /**
     * Setting changes of growth by ratio where ratios depends on age!
     * Age limits are in the pLims and ratios are in the pRat.
     * @param pLims age limits
     * @param pRat ratios
     * @param nsize array size
     * @return amount of moves
     */
    public int setClimGrowV4 (ArrayList<Float> pLims, ArrayList<Float> pRat, int nsize) {
        int nHowMany;
        int ret;
        float ratio;

        ret = 0;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            // Finding ratio
            int j=0;
            while (j<nsize-1 && (pCell.getM_Xmax() > pLims.get(j)))
                j+=1;
            ratio = pRat.get(j);
            pCell.setM_MoveByXY(ratio*pCell.getM_MoveByXYOrg());
            if (pCell.getM_MoveByXY() > 1.0) {
                pCell.setM_MoveByXY(1.0f);
                ret+=1;
            }
            pCell.setM_MoveByX(1.0f - pCell.getM_MoveByXY());
            m_Cells.set(i, pCell);
        }

        return ret;
    }
    
    /**
     * Fills specials fields in the pLc by calculating litters
     * (really Carbon, but can be easily modified).
     * @param pLc litter to be collected
     * @return GMLitterCollect object containing the litter for one year
     */
    public GMLitterCollect getLitter (GMLitterCollect pLc) {
        int nHowMany;
        //float ret;
        float carbon;
        double sstem,sbr,scr,sfr,slv;
        float factor;
        GMCell pCell;

        if (pLc.getLc_nsize()<=0)
            return null;
        if (pLc.getLc_pCalloc().getCa_nsize()<=0)
            return null;
        factor = (pLc.getLc_pCalloc().getCa_dns())*(pLc.getLc_pCalloc().getCa_ccont());

        sstem = 0.0;
        sbr   = 0.0;
        scr   = 0.0;
        sfr   = 0.0;
        slv   = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if ((pCell.getArea() + pCell.getM_ThinArea()) > 0.00000001) {
                carbon = (pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Yval())*factor;
                int j=0;
                int jj=0;
                while ((pCell.getM_Xval() > pLc.getLc_pCalloc().getCa_pxvals().get(j))
                        && (j < pLc.getLc_pCalloc().getCa_nsize()-1))
                    j+=1;
                while ((pCell.getM_Xval() > pLc.getLc_pxvals().get(jj))
                        && (jj < pLc.getLc_nsize()-1))
                    jj+=1;
                sstem += carbon*pLc.getLc_pstem().get(jj);
                sbr   += pLc.getLc_pCalloc().getCa_pbranch().get(j)*carbon
                        *pLc.getLc_pbranch().get(jj);
                scr   += pLc.getLc_pCalloc().getCa_pcroots().get(j)*carbon
                        *pLc.getLc_pcroots().get(jj);
                sfr   += pLc.getLc_pCalloc().getCa_pfroots().get(j)*carbon
                        *pLc.getLc_pfroots().get(jj);
                slv   += pLc.getLc_pCalloc().getCa_pleaves().get(j)*carbon
                        *pLc.getLc_pleaves().get(jj);
            }
        }
        pLc.setLc_cstem(pLc.getLc_cstem()+sstem);
        pLc.setLc_cbranch(pLc.getLc_cbranch()+sbr);
        pLc.setLc_ccroots(pLc.getLc_ccroots()+scr);
        pLc.setLc_cfroots(pLc.getLc_cfroots()+sfr);
        pLc.setLc_cleaves(pLc.getLc_cleaves()+slv);
        //ret = (float)(sstem + sbr + scr + sfr + slv);

        return pLc;
    }

    /**
     * Set simple values for areas that could be felled.
     * @param age age limit
     * @return list of cells
     */
    public ArrayList setFellingsSimple (float age) {
        int nHowMany;
        //int ret = 0;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.getM_Xmin() >= age) {
                pCell.setM_FellingsShare(1.0f);
                //m_Cells.set(i, pCell);
            }
            else {
                pCell.setM_FellingsShare(0.0f);
            }
            m_Cells.set(i, pCell);
        }
        return m_Cells;
    }

    /**
     * Set simple values for areas that could be thinned by taking account
     * age.
     * @param agel minimium age
     * @param ageh maximium age
     * @return list of cells
     */
    public ArrayList setThinningsSimple (float agel, float ageh) {
        int nHowMany;
        //int ret = 0;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.getM_Xmin() >= agel && pCell.getM_Xmax() <= ageh) {
                pCell.setM_ThinShare(pCell.getM_MoveByXY() + pCell.getM_MoveByY());
                pCell.setM_bThinned(true);
                //m_Cells.set(i, pCell);
            }
            else {
                pCell.setM_ThinShare(0.f);
                pCell.setM_bThinned(false);
            }
            m_Cells.set(i, pCell);
        }
        return m_Cells;
    }

    /**
     * Fills specials fields in the pCa by calculating biomasses
     * (really Carbon, but can be easy modified).
     * @param pCa
     * @return total biomass (carbon)
     */
    public float getBiomass (GMCarbonAlloc pCa) {
        int nHowMany;
        float ret,carbon,factor;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;

        if (pCa.getCa_nsize() <= 0)
            return 0.0f;

        factor = pCa.getCa_dns()*pCa.getCa_ccont();
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.getArea()+pCell.getM_ThinArea() > 0.00000001) {
                carbon = (pCell.getArea() + pCell.getM_ThinArea())
                        *(pCell.getM_Yval())*factor;
                int j=0;
                while ((pCell.getM_Xval() > pCa.getCa_pxvals().get(j))
                        && (j < pCa.getCa_nsize()-1))
                    j+=1;

                sstem += carbon;
                sbr   += pCa.getCa_pbranch().get(j)*carbon;
                scr   += pCa.getCa_pcroots().get(j)*carbon;
                sfr   += pCa.getCa_pfroots().get(j)*carbon;
                slv   += pCa.getCa_pleaves().get(j)*carbon;

                //ret+=(pCell.getArea())*(pCell.getM_Yval());
            }
        }
        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);

        ret = (float)(sstem + sbr + scr + sfr + slv);
        return ret;
    }

    /**
     * Report biomass (carbon) of diff compartments distributed by age classes
     * (defined in pLims).
     * @param pCa carbon
     * @param pLims age limits
     * @param pSt
     * @param pBr
     * @param pLv
     * @param pCr
     * @param pFr
     * @param nsize array size
     * @return total biomass (carbon)
     */
    public float getBiomassDistr (GMCarbonAlloc pCa, ArrayList<Float> pLims, 
            ArrayList<Float> pSt, ArrayList<Float> pBr, ArrayList<Float> pLv,
            ArrayList<Float> pCr, ArrayList<Float> pFr, int nsize) {
        int nHowMany;
        float ret,carbon,factor;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;

        if (pCa.getCa_nsize()<=0)
            return 0.0f;

        factor = pCa.getCa_dns()*pCa.getCa_ccont();
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.getArea()+pCell.getM_ThinArea() > 0.00000001) {
                carbon = (pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Yval())
                        *factor;

                int j=0;
                double fsbr,fscr,fsfr,fslv;

                while (pCell.getM_Xval() > pCa.getCa_pxvals().get(j)
                        && j < pCa.getCa_nsize()-1)
                    j+=1;

                fsbr   = pCa.getCa_pbranch().get(j)*carbon;
                fscr   = pCa.getCa_pcroots().get(j)*carbon;
                fsfr   = pCa.getCa_pfroots().get(j)*carbon;
                fslv   = pCa.getCa_pleaves().get(j)*carbon;
                sstem += carbon;
                sbr   += fsbr; //pCa.getCa_pbranch().get(j)*carbon;
                scr   += fscr; //pCa.getCa_pcroots().get(j)*carbon;
                sfr   += fsfr; //pCa.getCa_pfroots().get(j)*carbon;
                slv   += fslv; //pCa.getCa_pleaves().get(j)*carbon;

                int jj=0;
                while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                    jj+=1;

                pSt.set(jj, pSt.get(jj)+(float)carbon);
                pBr.set(jj, pBr.get(jj)+(float)fsbr);
                pLv.set(jj, pLv.get(jj)+(float)fslv);
                pCr.set(jj, pCr.get(jj)+(float)fscr);
                pFr.set(jj, pFr.get(jj)+(float)fsfr);
            }
        }
        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);

        ret = (float)(sstem + sbr + scr + sfr + slv);
        return ret;
    }

    /**
     * Fellings; we will use GMCarbonAlloc object to collect litter.
     * @param pFr fellings
     * @param pCa carbon
     * @return
     */
    public double makeFellings (GMFellings pFr, GMCarbonAlloc pCa) {
        int nHowMany;
        double ret;
        double factor,cfactor,carbon;
        double dwoodharv,dwoodharv1,dwfell;
        double sstem,sbr,scr,sfr,slv;
        GMCell pCell;

        ret = 0.0;
        cfactor = (pCa.getCa_dns())*(pCa.getCa_ccont());
        sstem = 0.0;
        sbr = 0.0;
        scr = 0.0;
        sfr = 0.0;
        slv = 0.0;
        dwoodharv = 0.0;
        dwoodharv1 = 0.0;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            pCell.setM_FelArea(0.0f);
            pCell.setM_FelRem(0.0f);
            pCell.setM_FelSlash(0.0f);

            factor = pFr.getF_ratio()*pCell.getM_FellingsShare();
            if (factor>0.0) {
                double ar,vol;
                ar = factor*pCell.getArea();
                pFr.setF_area(pFr.getF_area()+ar);
                vol = ar*pCell.getM_Yval();
                // Mefique stuff!
                pCell.setM_FelArea((float)ar);
                pCell.setM_FelRem((float)(vol*pFr.getF_stem()));
                // End Mefique!

                //pFr.setF_volume(pFr.getF_volume()+vol);
                pCell.setM_Area((float)(pCell.getArea()-ar));
                if (pCell.getArea() < 0) {
                    System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                    System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "
                            +getOwnerID()+"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSiteID()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                }

                // Deadwood harvesting: will use "unused" pFr.getF_froots() as
                // a ratio of deadwood fellings moved harvesting from the pipe!
                // March 2008
                
                dwfell = pFr.getF_froots()*pCell.getM_DWood();
                if (dwfell > 0) {
                    pCell.setM_DWood((float) (pCell.getM_DWood()-dwfell));
                    //BUG fixing: double accounting of DW harvest. Now accumulate
                    //here in separate variable dwoodharv1 instead dwoodharv as
                    //it was before
                    //TODO: debugging shows huge difference between two approaches
                    //dwoodharv seems to be correct! February 2023
                    dwoodharv1+=dwfell;
                    pCell.setM_FelRem((float) (pCell.getM_FelRem()+dwfell));
                }
                
                // Now litter production
                carbon = vol*cfactor;
                int j=0;
                while (pCell.getM_Xval() > pCa.getCa_pxvals().get(j)
                        && j < pCa.getCa_nsize()-1)
                    j+=1;
                pFr.setF_volume(pFr.getF_volume()+vol*(pFr.getF_stem()));
                ret+=vol*(pFr.getF_stem());
                // Bioenergy stuff. Updated September 2009 (Uppsala) : coarse roots added
                pCell.setM_FelSlash((float)(carbon*(pCa.getCa_pbranch().get(j)
                        *pFr.getF_branch()+pCa.getCa_pcroots().get(j)
                        *pFr.getF_croots()+pCa.getCa_pleaves().get(j)*pFr.getF_leaves())));
                sstem += carbon*(1-pFr.getF_stem());
                sbr   += pCa.getCa_pbranch().get(j)*carbon*(1-pFr.getF_branch());
                scr   += pCa.getCa_pcroots().get(j)*carbon*(1-pFr.getF_croots());
                sfr   += pCa.getCa_pfroots().get(j)*carbon;
                slv   += pCa.getCa_pleaves().get(j)*carbon*(1-pFr.getF_leaves());
            }
            //ret+=(pCell.getArea())*(pCell.getM_Xval());
            m_Cells.set(i, pCell);
        }
        pCa.setCa_cstem(pCa.getCa_cstem()+sstem);
        pCa.setCa_cbranch(pCa.getCa_cbranch()+sbr);
        pCa.setCa_ccroots(pCa.getCa_ccroots()+scr);
        pCa.setCa_cfroots(pCa.getCa_cfroots()+sfr);
        pCa.setCa_cleaves(pCa.getCa_cleaves()+slv);

        // Implementig deadwood harvesting by using dwpipe! March 2008
        ComFltPipeElement pPipe;
        nHowMany = m_fpDwPipe.m_nSize;
        for (int i=0;i<nHowMany;i++) {
            pPipe = m_fpDwPipe.getElement(i);
            dwfell = pFr.getF_froots()*pPipe.getCfp_value();
            pPipe.setCfp_value((float)(pPipe.getCfp_value()-dwfell));
            pPipe.setCfp_felrem((float)dwfell);
            dwoodharv+=dwfell;
        }
        // Deadwood decr. because of thinnings
        m_DeadWood-=dwoodharv;
        return ret;
    }

    /**
     * Get species id
     * @return char representation of species id
     */
    public int getSpeciesID () {
        byte sp = (byte)m_wID.intValue();
        return (int)sp;
    }

    /**
     * Get site id
     * @return char representation of site id
     */
    public int getSiteID () {
        Long copy = m_wID >> 8;
        byte si = (byte)copy.intValue();
        return (int)si;
    }

    /**
     * Get owner id
     * @return char representation of owner id
     */
    public int getOwnerID () {
        Long copy = m_wID >> 16;
        byte ow = (byte)copy.intValue();
        return (int)ow;
    }

    /**
     * Get region id
     * @return char representation of region id
     */
    public int getRegionID () {
        Long copy = m_wID >> 24;
        byte re = (byte)copy.intValue();
        return (int)re;
    }

    /**
     * Scaling area - GertJan. Scale only area, not thinned area.
     * Use before SetThinHistory()!
     * @param scf scaling factor
     * @return scaling area
     */
    public float scaleArea (float scf) {
        int nHowMany;
        float ret;
        GMCell pCell;

        if (scf<0)
            return -13.0f;

        m_BareArea*=scf;
        ret = m_BareArea;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            pCell.setM_Area(pCell.getArea()*scf);
            ret+=pCell.getArea();
            m_Cells.set(i,pCell);
        }
        return ret;
    }

    /**
     * Return total area (bare land included).
     * @return total area
     */
    public float getArea () {
        int nHowMany;
        float ret;

        ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            ret+=pCell.getArea() + pCell.getM_ThinArea();
        }
        return ret+m_BareArea;
    }
    
    /**
     * returns total area with low volume class.
     * @return 
     */
    public float getAreaLV() {
        int nHowMany;
        float ret;

        ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            ret+=pCell.getArea() + pCell.getM_ThinArea();
        }
        return ret+m_BareArea+m_LowVolArea;
    }

    /**
     * Return total volume (bare land not included).
     * @return total volume
     */
    public float getValue () {
        int nHowMany;
        float ret;

        ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            ret+=(pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Yval());
        }
        return ret;
    }
    
    /**
     * get total volume with low volume class.
     * @return 
     */
    public float getValueLV() {
        int nHowMany;
        float ret;

        ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            ret+=(pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Yval());
        }
        ret+=m_LowVolArea*m_LowVolYval;
        return ret;
    }

    /**
     * Return total X value; useful to calculate Mean X (age) for group of
     * matrixes.
     * @return x value
     */
    public float getValueByX () {
        int nHowMany;
        float ret;

        ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            ret+=(pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Xval());
        }
        return ret;
    }

    /**
     * Return mean value by X (usually mean age).
     * @return mean x value
     */
    public float getMeanX () {
        int nHowMany;
        float ret;
        double sumx,suma;
        GMCell pCell;

        ret = 0.0f;
        sumx = 0.0;
        suma = 0.0;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            sumx+=(pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Xval());
            suma+=pCell.getArea() + pCell.getM_ThinArea();
        }
        if (suma > 0.0)
            ret = (float)(sumx/suma);
        return ret;
    }

    /**
     * Fills pDest by pLims, i.e build distribution of areas by given Xaxis
     * limits.
     * @param pLims age limits
     * @param pDest destination
     * @param nsize array size
     * @return
     */
    public ArrayList getAreaDistr (ArrayList<Float> pLims, ArrayList<Float> pDest,
            int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj)) 
                jj+=1;
            pDest.set(jj, pDest.get(jj) + pCell.getArea() + pCell.getM_ThinArea());
            //ret+=(pCell.getArea() + pCell.getM_ThinArea());
        }
        return pDest;
    }

    /**
     * Fills pDest by pLims, i.e build distribution of Growing stock by given
     * Xaxis limits.
     * @param pLims age limits
     * @param pDest destination
     * @param nsize array size
     * @return list of distribution
     */
    public ArrayList getStockDistr (ArrayList<Float> pLims, ArrayList<Float> pDest,
            int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                jj+=1;
            pDest.set(jj, pDest.get(jj) + (pCell.getArea()
                + pCell.getM_ThinArea())*(pCell.getM_Yval()));
            //ret+=(pCell.getArea() + pCell.getM_ThinArea())*(pCell.getM_Yval());
        }
        return pDest;
    }

    /**
     * Fills pDest by pLims, i.e build distribution of Natural mortality by
     * given Xaxis limits.
     * @param pLims age limits
     * @param pDest destination array
     * @param nsize array size
     * @return list of mortality
     */
    public ArrayList getMortDistr (ArrayList<Float> pLims, ArrayList<Float> pDest,
            int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                jj+=1;
            pDest.set(jj, pDest.get(jj) + pCell.getM_NatMrt());
            //ret+=pCell.getM_NatMrt();
        }
        return pDest;
    }

    /**
     * Fills pDest by pLims, i.e build distribution of Deadwood by given Xaxis
     * limits.
     * @param pLims age limits
     * @param pDest destination array
     * @param nsize array size
     * @return list of dwood
     */
    public ArrayList getDwoodDistr (ArrayList<Float> pLims, ArrayList<Float> pDest,
            int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                jj+=1;
            pDest.set(jj, pDest.get(jj) + pCell.getM_DWood());
            //ret+=pCell.getM_DWood();
        }
        return pDest;
    }

    /**
     * Fills pDest by pLims, i.e build distribution of Deadwood by given Xaxis
     * limits using deadwood pipe.
     * @param pLims age limits
     * @param pDest destination array
     * @param nsize array size
     * 
     */
    public void getDwoodDistrFromPipe (ArrayList<Float> pLims, ArrayList<Float> pDest,
            int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        ComFltPipeElement pPipe;

        nHowMany = m_fpDwPipe.m_nSize;
        for (int i=0;i<nHowMany;i++) {
            int jj = 0;
            pPipe = m_fpDwPipe.getElement(i);
            while (jj < nsize-1 && pPipe.getCfp_uplim() > pLims.get(jj))
                jj+=1;
            pDest.set(jj, pDest.get(jj) + pPipe.getCfp_value());
            //ret+=pPipe.getCfp_value();
        }
    }

    /**
     * Fills pDests by pLims, i.e build distribution of areas by given Xaxis
     * limits, mefique version.
     * @param pLims age limits
     * @param pDestTha list of thinnings area in/out
     * @param pDestThr list of thinnings removals in/out
     * @param pDestFla list of fellings area in/out
     * @param pDestFlr list of fellings removals in/out
     * @param nsize array size
     * 
     */
    public void getMefiqueDistr (ArrayList<Float> pLims, ArrayList<Float> pDestTha,
            ArrayList<Float> pDestThr, ArrayList<Float> pDestFla,
            ArrayList<Float> pDestFlr, int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;
        
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                jj+=1;
            pDestTha.set(jj, pDestTha.get(jj) + pCell.getM_ThArea());
            pDestThr.set(jj, pDestThr.get(jj) + pCell.getM_ThRem());
            pDestFla.set(jj, pDestFla.get(jj) + pCell.getM_FelArea());
            pDestFlr.set(jj, pDestFlr.get(jj) + pCell.getM_FelRem());

            //ret+=(pCell.getArea() + pCell.getM_ThinArea());
        }
        ComFltPipeElement pPipe;

        nHowMany = m_fpDwPipe.m_nSize;
        for (int i=0;i<nHowMany;i++) {
            int jj = 0;
            pPipe = m_fpDwPipe.getElement(i);
            while (jj < nsize-1 && pPipe.getCfp_uplim() > pLims.get(jj)) 
                jj+=1;
            pDestThr.set(jj, pDestThr.get(jj) + pPipe.getCfp_threm());
            pDestFlr.set(jj, pDestFlr.get(jj) + pPipe.getCfp_felrem());
        }
    }

    /**
     * Fills pDests by pLims, i.e build distribution of areas by given Xaxis
     * limits, bioenergy stuff.
     * @param pLims age limits
     * @param pDestThs list of thinnigs slash distribution
     * @param pDestFls list of fellings slash distribution
     * @param nsize array size
     * 
     */
    public void getSlashDistr (ArrayList<Float> pLims, ArrayList<Float> pDestThs,
            ArrayList<Float> pDestFls, int nsize) {
        int nHowMany;
        //float ret = 0.0f;
        GMCell pCell;

        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            int jj = 0;
            while (jj < nsize-1 && pCell.getM_Xmax() > pLims.get(jj))
                jj+=1;
            pDestThs.set(jj, pDestThs.get(jj) + pCell.getM_ThSlash());
            pDestFls.set(jj, pDestFls.get(jj) + pCell.getM_FelSlash());
        }
    }

    /**
     * Transitions calculation.
     * @param pvols
     * @param pinvols
     * @param ntop
     * @param beta
     * @return list of cells
     */
    public ArrayList calcTransitions (ArrayList<Float> pvols, ArrayList<Float> pinvols,
            int ntop, float beta) {
        float volser,incr;
        float rate,ysize,fraction;
        //float ret = 0.0f;
        GMCell pCell;
        for (int i=1;i<=m_wXsize;i++) {
            pCell = getAt(i,1); //m_Cells.get(i);
            // Important!
            int jj=0;
            while (jj < ntop && pCell.getM_Xval() > pinvols.get(jj))
                jj+=1;

            //0if (jj != 0)
            //    volser = pvols.get(jj-1);
            //else
                volser = pvols.get(jj);
            incr = (float)(0.01*m_pFunction.calculate(m_Xclasses.get(i-1)));
            if (incr < 0.)
                incr = 0.f;
            int j = 0;
            for (j=1;j<m_wYsize;j++) {
                int cind = pCell.m_wY;
                pCell.setM_MoveByY(0.0f);
                rate = volser/pCell.getM_Yval();
                ysize = pCell.getM_Ymax() - pCell.getM_Ymin();
                if (cind < m_wYsize)
                    ysize = m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
                //ysize = pCell.getM_Ymax() - pCell.getM_Ymin();
                if (rate>1.0)
                    fraction = ((float)(pCell.getM_Yval()*incr
                            *Math.exp((double)(beta*Math.log((double)rate)))/ysize));
                else
                    fraction = incr*volser/ysize;
                if (fraction>1.) 
                    fraction = 1.f;
                pCell.setM_MoveByXY(fraction);
                pCell.setM_MoveByXYOrg(fraction);
                pCell.setM_MoveByX(1.f - pCell.getM_MoveByXY());
                pCell.setM_MoveByXOrg(1.f - pCell.getM_MoveByXY());
                setAt(i,j,pCell);
                pCell = getAt(i,j+1);
            }
            pCell.setM_MoveByY(0.0f);
            pCell.setM_MoveByXY(0.0f);
            pCell.setM_MoveByX(1.0f);
            setAt(i,j+1,pCell);//m_Cells.set(i, pCell);
        }
        /*for (int j=0;j<m_wYsize;j++) {
            pCell = m_Cells.get(j);
            pCell.setM_MoveByY(1.0);
            pCell.setM_MoveByXY(0.0);
            pCell.setM_MoveByX(0.0);
            m_Cells.set(j, pCell);
        }*/
        return m_Cells;
    }
    
    /**
     * transition calculations low volume version.
     * @param pvols
     * @param pinvols
     * @param ntop
     * @param beta
     * @return 
     */
    public ArrayList calcTransitionsLV(ArrayList<Float> pvols, ArrayList<Float> pinvols,
            int ntop, float beta) {
        float volser,incr;
        float rate,ysize,fraction;
        //float ret = 0.0f;
        //low volume class calculations
        incr = (float)(0.01*m_pFunction.calculate(m_LowVolYval));
        if (incr < 0f)
            incr = 0f;
        ysize = m_LowVolYval;
        volser = pvols.get(0)*m_lowVolShare;
        rate = volser/m_LowVolYval;
        if(rate>1.0)
            fraction = ((float)(m_LowVolYval*incr
                *Math.exp((double)(beta*Math.log((double)rate)))/ysize));
        //m_LowVolMoveXY
        //normal calculations
        GMCell pCell;
        for (int i=1;i<=m_wXsize;i++) {
            pCell = getAt(i,1); //m_Cells.get(i);
            // Important!
            int jj=0;
            while (jj < ntop && pCell.getM_Xval() > pinvols.get(jj))
                jj+=1;

            //0if (jj != 0)
            //    volser = pvols.get(jj-1);
            //else
            volser = pvols.get(jj);
            incr = (float)(0.01*m_pFunction.calculate(m_Xclasses.get(i-1)));
            if (incr < 0.)
                incr = 0.f;
            int j = 0;
            for (j=1;j<m_wYsize;j++) {
                int cind = pCell.m_wY;
                pCell.setM_MoveByY(0.0f);
                rate = volser/pCell.getM_Yval();
                ysize = pCell.getM_Ymax() - pCell.getM_Ymin();
                if (cind < m_wYsize)
                    ysize = m_Yclasses.get(cind) - m_Yclasses.get(cind-1);
                //ysize = pCell.getM_Ymax() - pCell.getM_Ymin();
                if (rate>1.0)
                    fraction = ((float)(pCell.getM_Yval()*incr
                            *Math.exp((double)(beta*Math.log((double)rate)))/ysize));
                else
                    fraction = incr*volser/ysize;
                if (fraction>1.) 
                    fraction = 1.f;
                pCell.setM_MoveByXY(fraction);
                pCell.setM_MoveByXYOrg(fraction);
                pCell.setM_MoveByX(1.f - pCell.getM_MoveByXY());
                pCell.setM_MoveByXOrg(1.f - pCell.getM_MoveByXY());
                setAt(i,j,pCell);
                pCell = getAt(i,j+1);
            }
            pCell.setM_MoveByY(0.0f);
            pCell.setM_MoveByXY(0.0f);
            pCell.setM_MoveByX(1.0f);
            setAt(i,j+1,pCell);//m_Cells.set(i, pCell);
        }
        /*for (int j=0;j<m_wYsize;j++) {
            pCell = m_Cells.get(j);
            pCell.setM_MoveByY(1.0);
            pCell.setM_MoveByXY(0.0);
            pCell.setM_MoveByX(0.0);
            m_Cells.set(j, pCell);
        }*/
        return m_Cells;
    }

    /**
     * @deprecated
     * Update the cells according to the differences (income, move) made.
     * @return
     */
    public float update () {
        int nHowMany;
        double diff,check;
        float ret = 0.0f;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;

        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            diff = pCell.getM_Income() - pCell.getM_Move() - pCell.getM_MoveAway();

            if (diff < 0 && Math.abs(diff) > pCell.getArea()) {
                System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:plus "
                        +pCell.getM_Income()+"\nDebug:minus "+diff+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "
                        +getOwnerID()+"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }
            check = pCell.getArea() + diff;
            if (check < 0) {
                System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:less than zero");
            }

            pCell.setM_Area((float)(pCell.getArea() + diff));
            //pCell.getM_Income() - pCell.getM_Move() - pCell.getM_MoveAway();
            if (pCell.getArea() < 0) {
                System.out.println("Debug:Area "+pCell.getArea()+"\nDebug:plus "
                        +diff+"\nDebug:minus "+check+"\nDebug:");
                System.out.println("Debug:negative area");
            }
            // Thinnings update
            if (pCell.getM_MoveAsThin() > 0) {
                GMCell pCellTarget = getAt(pCell.m_wX+1,pCell.m_wY);
                if (pCellTarget != null) {
                    pCellTarget.setM_ThinArea(pCellTarget.getM_ThinArea()
                            + pCell.getM_MoveAsThin());
                    pCell.setM_MoveAsThin(0.0f);
                    setAt(pCell.m_wX+1,pCell.m_wY,pCellTarget);
                }
            }
            // End thinnings update
            ret+=pCell.getArea() * pCell.getM_Yval();
            pCell.setM_Move(0.0f);
            pCell.setM_MoveAway(0.0f);
            pCell.setM_Income(0.0f);
            m_Cells.set(i, pCell);
        }
        return ret;
    }

    /**
     * Calculate the growth by cells.
     * @return
     */
    public float grow () {
        int i,j;
        GMCell pCell = m_Cells.get(0);
        GMCell pCellTarget;
        double partMove,rgrArea;
        partMove = m_FromBare*m_BareArea;
        if (m_BareArea-partMove<0.00000001)
            partMove = m_BareArea;
        if (pCell != null) {
            pCell.setM_Move(0.0f);
            pCell.setM_Income((float)partMove);
            m_Cells.set(0, pCell);
        }
        m_BareArea-=partMove;

        if (partMove < 0) {
            System.out.println("Debug:Area "+partMove+"\nDebug:");
            System.out.println("Debug:less than zero");
            System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                    +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
            System.out.println("Debug:less than zero");
        }
        for (i=1;i<m_wXsize;i++) {
            for (j=1;j<m_wYsize;j++) {
                //pCell = m_Cells.get(j-1);
                pCell = getAt(i,j);
                rgrArea = 0.0;
                pCell.setM_Move(0f);

                double x_gamma = pCell.getM_MoveByXY(); // Real increment because of thinnings
                if (pCell.getM_MoveAsThin() > 0)
                    x_gamma = (pCell.getArea() > 0) ? (pCell.getM_MoveByXY()
                            -pCell.getM_MoveAsThin()/pCell.getArea()
                            *(1-pCell.getM_MoveByXY())) : pCell.getM_MoveByXY();
                if (x_gamma < 0.00001)
                    x_gamma = 0.0;

                pCellTarget = getAt(i+1,j);
                partMove = (1-x_gamma)*pCell.getArea();
                //partMove = pCell.getM_MoveByX()*pCell.getArea();
                if (partMove < 0) {
                    System.out.println("Debug:Area "+partMove+"\nDebug:");
                    System.out.println("Debug:less than zero");
                    System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                }


                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                // Thinning realisation first part
                if (pCell.getM_ThinArea() > 0) {
                    rgrArea = m_RegrGamma*pCell.getM_ThinArea();
                    pCell.setM_ThinArea((float)(pCell.getM_ThinArea()-rgrArea));
                    pCellTarget.setM_Income(+pCell.getM_MoveByX()*pCell.getM_ThinArea());
                    //pCell.setM_MoveAsThin(0.0f);
                }
                setAt(i+1,j,pCellTarget);
                // End thinning part 1
                pCellTarget = getAt(i,j+1);
                partMove = pCell.getM_MoveByY()*pCell.getArea();
                if (partMove < 0) {
                    System.out.println("Debug:Area "+partMove+"\nDebug:");
                    System.out.println("Debug:less than zero");
                    System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                }

                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i,j+1,pCellTarget);
                pCellTarget = getAt(i+1,j+1);
                // improvement of thinned
                //double x_gamma = pCell.getM_MoveByXY(); // Real increment because of thinnings
                //if (pCell.getM_MoveAsThin() > 0)
                //	x_gamma = (pCell.getArea() > 0) ? (pCell.getM_MoveByXY()-pCell.getM_MoveAsThin()/pCell.getArea()*(1-pCell.getM_MoveByXY())) : pCell.getM_MoveByXY();
                //if (x_gamma < 0.00001)
                //  x_gamma = 0.0;
                partMove = x_gamma*pCell.getArea();

                if (partMove < 0) {
                    System.out.println("Debug:Area "+partMove+"\nDebug:");
                    System.out.println("Debug:less than zero");
                    System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                }

                if (partMove+pCell.getM_MoveAway()+pCell.getM_Move() > pCell.getArea())
                    partMove = pCell.getArea() - pCell.getM_MoveAway() - pCell.getM_Move();

                if (partMove < 0) {
                    System.out.println("Debug:Area "+partMove+"\nDebug:");
                    System.out.println("Debug:less than zero");
                    System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                        +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                    System.out.println("Debug:less than zero");
                }

                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
                // Thinned area
                pCellTarget.setM_Income(pCellTarget.getM_Income()+pCell.getM_MoveByXY()
                        *pCell.getM_ThinArea());
                pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+rgrArea));
                pCell.setM_ThinArea(0.0f);
                // End thinned
                pCell.setM_Move((float)(pCell.getM_Move()+partMove));
                setAt(i+1,j+1,pCellTarget);
                setAt(i,j,pCell);
                //m_Cells.set(j-1, pCell);
            }
            //pCell = m_Cells.get(i-1);
            pCell = getAt(i,j);
            pCellTarget = getAt(i+1,j);
            partMove = pCell.getM_MoveByX()*pCell.getArea();
            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            setAt(i+1,j,pCellTarget);
            //m_Cells.set(i-1,pCell);
            setAt(i,j,pCell);
        }
        for (j=1;j<m_wYsize;j++) {
            //pCell = m_Cells.get(j-1);
            pCell = getAt(i,j);
            pCell.setM_Move(0f);
            pCellTarget = getAt(i,j+1);
            partMove = pCell.getM_MoveByXY()*pCell.getArea();

            if (partMove < 0) {
                System.out.println("Debug:Area "+partMove+"\nDebug:");
                System.out.println("Debug:less than zero");
                System.out.println("Debug:Matrix\nDebug:Reg "+getRegionID()+"\nDebug:Owner "+getOwnerID()
                    +"\nDebug:Site "+getSiteID()+"\nDebug:Spec "+getSpeciesID()+"\nDebug:");
                System.out.println("Debug:less than zero");
            }

            pCellTarget.setM_Income((float)(pCellTarget.getM_Income()+partMove));
            pCell.setM_Move((float)(pCell.getM_Move()+partMove));
            setAt(i,j+1,pCellTarget);
            //m_Cells.set(j-1,pCell);
            setAt(i,j,pCell);
        }
        return 0.0f;
    }

    /**
     * Adds given value to the "barearea".
     * @param ar value to be added
     * @return the new barearea
     */
    public float addToBare (float ar) {
        m_BareArea+=ar;
        return m_BareArea;
    }

    /**
     * Sets the given GMGrFunction to the current.
     * @param pFun GMGrFunction
     */
    public void setGrFunction (GMGrFunction pFun) {
        m_pFunction = pFun;
    }

    /**
     * Init regular values.
     * @param pMi regular values
     */
    public void initRegular (GMMatrixInit pMi) {
        m_wID	  = pMi.getMi_id();
        m_Xbottom = pMi.getMi_xb();
        m_Xtop	  = pMi.getMi_xt();
        m_Ybottom = pMi.getMi_yb();
        m_Ytop    = pMi.getMi_yt();
        m_Xstep   = pMi.getMi_xs();
        m_Ystep   = pMi.getMi_ys();
        int i;
        for (i=1;i<=m_wXsize;i++) {
            m_Xclasses.set(i-1,new Float(m_Xbottom + m_Xstep/2.0 + (i-1)*m_Xstep));
            ComFltPipeElement pcfp = m_fpDwPipe.getElement(i-1);
            pcfp.setCfp_uplim(m_Xbottom + i*m_Xstep);
            m_fpDwPipe.setElement(i-1, pcfp);
        }

        for (i=1;i<=m_wYsize;i++)
            m_Yclasses.set(i-1,new Float(m_Ybottom + m_Ystep/2.0 + (i-1)*m_Ystep));
    }

    /**
     * Fill regular matrix i.e. with constant size by X and by Y.
     * @param carea area
     * @return list of cells
     */
    public ArrayList fillRegular (float carea) {
        if (m_Cells == null)
            return null;
        GMCellInit pCi = new GMCellInit();
        GMCell pCell;
        float x_min = m_Xbottom;
        float y_min;
        float x_step = (float)(m_Xstep/2.0);
        float y_step = (float)(m_Ystep/2.0);
        pCi.setCi_powner(this);
        m_BareArea = 0;
        m_FromBare = 0.75f;
        for (int i=0;i<m_wXsize;i++) {
            y_min = m_Ybottom;
            pCi.setCi_wx(i + 1);
            pCi.setCi_xmin(x_min);
            pCi.setCi_x(x_min + x_step);
            pCi.setCi_xmax(x_min + m_Xstep);

            for (int j=0;j<m_wYsize;j++) {
                //pCell = m_Cells.get(j);
                pCi.setCi_wy(j + 1);
                pCi.setCi_ymin(y_min);
                pCi.setCi_y(y_min + y_step);
                pCi.setCi_ymax(y_min + m_Ystep);
                pCi.setCi_area(carea); //(i+1)*100.0 + (j+1)*1.0;
                pCell = new GMCell(pCi);
                pCell.setM_MoveByX(0.25f);
                pCell.setM_MoveByY(0.25f);
                pCell.setM_MoveByXY(0.25f);
                y_min += m_Ystep;
                m_Cells.add(pCell);
            }
            x_min += m_Xstep;
        }
        return m_Cells;
    }

    /**
     * Fill matrix irregular by Y, i.e. sizes by Y are given in volims array.
     * @param carea area
     * @param volims sizes by y.
     * @return list of cells
     */
    public ArrayList fillRegularByX (float carea, ArrayList<Float> volims) {
        if (m_Cells == null)
            return null;

        GMCellInit pCi = new GMCellInit();
        GMCell pCell;
        // Y_classes recalculation first
        m_Yclasses.set(0,(volims.get(0) - m_Ybottom)/2);
        for (int iv=1;iv<m_wYsize;iv++)
            m_Yclasses.set(iv,(volims.get(iv) + volims.get(iv-1))/2);
        m_Ytop = volims.get(m_wYsize-1);
        float x_min = m_Xbottom;
        float y_min;
        float x_step = (float)(m_Xstep/2.0);
        //float y_step = (float)(m_Ystep/2.0);
        pCi.setCi_powner(this);
        m_BareArea = 0;
        m_FromBare = 0.75f;
        for (int i=0;i<m_wXsize;i++) {
            y_min = m_Ybottom;
            pCi.setCi_wx(i + 1);
            pCi.setCi_xmin(x_min);
            pCi.setCi_x(x_min + x_step);
            pCi.setCi_xmax(x_min + m_Xstep);

            for (int j=0;j<m_wYsize;j++) {
                //pCell = m_Cells.get(j);
                //y_step = (volims.get(j) - y_min)/2;
                pCi.setCi_wy(j + 1);
                pCi.setCi_ymin(y_min);
                pCi.setCi_y(m_Yclasses.get(j));
                pCi.setCi_ymax(volims.get(j));
                pCi.setCi_area(carea); //(i+1)*100.0 + (j+1)*1.0;
                pCell = new GMCell(pCi);
                pCell.setM_MoveByX(0.25f);
                pCell.setM_MoveByY(0.25f);
                pCell.setM_MoveByXY(0.25f);
                y_min = volims.get(j);
                m_Cells.add(pCell);
            }
            x_min += m_Xstep;
        }
        return m_Cells;
    }

    /**
     * Get Cell by index.
     * @param nCol the number of column
     * @param nRow the number of row
     * @return GMCell object if successful else null
     */
    public GMCell getAt (int nCol, int nRow) {
        if (m_Cells == null)
            return null;
        if (nCol <= 0 || nCol > m_wXsize)
            return null;
        if (nRow <= 0 || nRow > m_wYsize)
            return null;
        return m_Cells.get((nCol-1)*m_wYsize+(nRow-1));
    }

    /**
     * Set Cell by index.
     * @param nCol the number of column
     * @param nRow the number of row
     * @param cell cell to be set
     */
    public void setAt (int nCol, int nRow, GMCell cell) {
        if (m_Cells == null)
            return;
        if (nCol <= 0 || nCol > m_wXsize)
            return;
        if (nRow <= 0 || nRow > m_wYsize)
            return;
        m_Cells.set((nCol-1)*m_wYsize+(nRow-1), cell);
        cell.m_wX = nCol;
        cell.m_wY = nRow;
    }

    /**
     * Find Cell by index.
     * @param nCol the number of column
     * @param nRow the number of row
     * @return GMCell object if succesful else null
     */
    public GMCell findCell (int nCol, int nRow) {
        GMCell pCell;
        if (m_Cells == null)
            return null;
        if (nCol <= 0 || nCol > m_wXsize)
            return null;
        if (nRow <= 0 || nRow > m_wYsize)
            return null;
        pCell = m_Cells.get((nCol-1)*m_wYsize+(nRow-1));
        return pCell;
    }

    /**
     * Setting Thinnings "history" by assigning the ratio*Area value
     * to the m_ThinArea.
     * @param ratio given ratio
     * @return list of cells
     */
    public ArrayList setThinHistory (float ratio) {
        int nHowMany;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.isM_bThinned()) {
                pCell.setM_ThinArea(ratio*pCell.getArea());
                pCell.setM_Area(pCell.getArea()-pCell.getM_ThinArea());
                m_Cells.set(i, pCell);
            }
        }
        return m_Cells;
    }
    
    /**
     * resets the thinning history.
     * @param ratio
     * @param scaling
     * @return 
     */
    public ArrayList resetThinHistory (float ratio,float scaling) {
        int nHowMany;
        GMCell pCell;
        nHowMany = m_wXsize*m_wYsize - 1;
        for (int i=0;i<=nHowMany;i++) {
            pCell = m_Cells.get(i);
            if (pCell.isM_bThinned()) {
                //restore values to pre thinhistory
                float thinArea = pCell.getM_ThinArea()*scaling;
                pCell.setM_Area(pCell.getArea()+thinArea);
                
                pCell.setM_ThinArea(ratio*pCell.getArea());
                pCell.setM_Area(pCell.getArea()-pCell.getM_ThinArea());
                m_Cells.set(i, pCell);
            }
        }
        return m_Cells;
    }
    
    public boolean equals(Object obj) {
        if(!(obj instanceof GMMatrix))
            return false;
        else {
            GMMatrix temp = (GMMatrix)obj;
            if(this.m_BareArea != temp.m_BareArea) return false;
            if(this.m_DeadWood != temp.m_DeadWood) return false;
            if(this.m_FromBare != temp.m_FromBare) return false;
            if(this.m_RegrGamma != temp.m_RegrGamma) return false;
            for(int i = 0; i < m_Xclasses.size(); i++) {
                if(!this.m_Xclasses.get(i).equals(temp.m_Xclasses.get(i))) 
                    return false;
            }
            for(int i = 0; i < m_Yclasses.size(); i++) {
                if(!this.m_Yclasses.get(i).equals(temp.m_Yclasses.get(i))) 
                    return false;
            }
            if(this.m_sName.equals(temp.m_sName)) return false;
            if(this.m_wID != temp.m_wID) return false;
            if(this.m_wXsize != temp.m_wXsize) return false;
            if(this.m_wYsize != temp.m_wYsize) return false;
            if(this.m_Xbottom != temp.m_Xbottom) return false;
            if(this.m_Ybottom != temp.m_Ybottom) return false;
            if(this.m_Ystep != temp.m_Ystep) return false;
            if(this.m_Xstep != temp.m_Xstep) return false;
            if(this.getArea() != temp.getArea()) return false;
            if(this.getIncrement() != temp.getIncrement()) return false;
            if(this.getMeanX() != temp.getMeanX()) return false;
            for(int i = 0; i < m_Cells.size(); i++) {
                if(!m_Cells.get(i).equals(temp.m_Cells.get(i))) return false;
            }
        }
        return true;
    }

    /**
     * @return the m_LowVolVolume
     */
    public float getM_LowVolVolume() {
        return m_LowVolArea*m_LowVolYval;
    }

    /**
     * @return the m_LowVolAge
     */
    public float getM_LowVolAge() {
        return m_LowVolAge;
    }

    /**
     * @param m_LowVolAge the m_LowVolAge to set
     */
    public void setM_LowVolAge(float m_LowVolAge) {
        if(m_LowVolAge<0f) m_LowVolAge=0f;
        if(m_LowVolAge>1f) m_LowVolAge=1f;
        this.m_LowVolAge = m_LowVolAge;
    }

    /**
     * @return the m_lowVolShare
     */
    public float getM_lowVolShare() {
        return m_lowVolShare;
    }

    /**
     * @param m_lowVolShare the m_lowVolShare to set
     */
    public void setM_lowVolShare(float m_lowVolShare) {
        this.m_lowVolShare = m_lowVolShare;
    }
    
    public float getM_Xbottom() {
        return m_Xbottom;
    }
    
    public float getM_Xtop() {
        return m_Xtop;
    }
    
    public float getM_Ybottom() {
        return m_Ybottom;
    }
    
    public float getM_Ytop() {
        return m_Ytop;
    }
    
    public float getM_Xstep() {
        return m_Xstep;
    }
    
    public float getM_Ystep() {
        return m_Ystep;
    }
    
    public ComFltPipe getM_fpDwPipe() {
        return m_fpDwPipe;
    }
}

