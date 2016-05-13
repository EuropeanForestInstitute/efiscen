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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * YASSO soil model realization; takes care of soils simulation.
 * 
 */
public class GMSoil implements Serializable {

    // Name
    /**
     * Name
     */
    public String m_sName;

    // "Boxes" to collect income litter
    /**
     *  income of coarse woody litter
     */
    public double m_CwBasket; // Coarse woody litter
    /**
     * income of fine woody litter
     */
    public double m_FwBasket; // Fine woody litter
    /**
     * income of non woody litter
     */
    public double m_NwBasket; // Non woody litter

    // "Box" to collect carbon out
    /**
     * "Box" to collect carbon out
     */
    public double m_CoutBasket;

    /**
     * Identifier
     */
    public long m_wID;

    // Driving variables
    private double m_NwLitter; // Non woody litter
    private double m_FwLitter; // Fine woody litter
    private double m_CwLitter; // Coarse woody litter
    private double m_SolComp; // Soluble compounds
    private double m_CelComp; // Holocellulose
    private double m_LigComp; // Ligning like compounds
    private double m_HumusOne; // Humus1 in Yasso model
    private double m_HumusTwo; // Humus2 in Yasso model
    private double m_CarbonLost; // Special one to keep loosed carbon

    // Main decomposition rates (original values)
    private double m_aNwL; // For corresponding litter's cohorts
    private double m_aFwL;
    private double m_aCwL;
    private double m_kSol; // Soluble
    private double m_kCel; // Holocellulose
    private double m_kLig; // Lignin
    private double m_kHumOne; // Corresponding humus
    private double m_kHumTwo; // Corresponding humus

    // Main decomposition rates (current, corrected by climat conditions)
    private double m_caNwL; // For corresponding litter's cohorts
    private double m_caFwL;
    private double m_caCwL;
    private double m_ckSol; // Soluble
    private double m_ckCel; // Holocellulose
    private double m_ckLig; // Lignin
    private double m_ckHumOne; // Corresponding humus
    private double m_ckHumTwo; // Corresponding humus

    // Fractions of decomposed, moving to the next "stage"
    private double m_cpSol; // From solluble to lignin
    private double m_cpCel; // From holocellulose to lignin
    private double m_cpLig; // From lignin to humus1
    private double m_cpHum; // From humus1 to humus2
    private double m_Nw2Sol; // From NonWoody to Soluble and Homocellulose
                             // respectively, the rest -> Lignin
    private double m_Nw2Cel; 
    private double m_Fw2Sol; // from FineWoody to Soluble and Homocellulose
                             // respectively, the rest -> Lignin
    private double m_Fw2Cel;
    private double m_Cw2Sol; // From CoarseWoody to Soluble and Homocellulose
                             // respectively, the rest -> Lignin

    private double m_Cw2Cel;

    // Coefficients and parameters for climate dependence function
    private double m_clBeta;
    private double m_clGamma;
    private double m_cpTav;
    private double m_cpDi;

    // Temperature-moisture dependence parameters for Humus stocks decomposition
    private double m_clHumOne;
    private double m_clHumTwo;
    
    private double m_InOut; // "Box" to collect forest areas changes due to
                            // afforestation/deforestation (April 2010)

    /**
     * Default constructor. Sets all compartments to zero 
     */
    public GMSoil () {
        m_NwLitter = 0.0;
        m_FwLitter = 0.0;
        m_CwLitter = 0.0;
        m_SolComp = 0.0;
        m_CelComp = 0.0;
        m_LigComp = 0.0;
        m_HumusOne = 0.0;
        m_HumusTwo = 0.0;
        m_CwBasket = 0.0;
        m_FwBasket = 0.0;
        m_NwBasket = 0.0;
        m_CoutBasket = 0.0;
        m_CarbonLost = 0.0;
        m_InOut = 0.0;
    }

    /**
     * Parameterized constructor
     * @param si_nwl non woody litter
     * @param si_fwl fine woody litter
     * @param si_cwl coarse woody litter
     * @param si_sol soluble compartment
     * @param si_cel cellulose compartment
     * @param si_lig lignin compartment
     * @param si_hm1 humus1 compartment
     * @param si_hm2 humus2 compartment
     * @param si_acwl decomposition rate for coarse woody litter
     * @param si_afwl decomposition rate for fine woody litter
     * @param si_anwl decomposition rate for non woody litter
     * @param si_ksol decomposition rate for soluble
     * @param si_kcel decomposition rate for cellulose
     * @param si_klig decomposition rate for lignin
     * @param si_khm1 decomposition rate for humus1
     * @param si_khm2 decomposition rate for humus2
     * @param si_pcel transferred part from cellulose
     * @param si_psol transferred part from soluble
     * @param si_plig transferred part from lignin
     * @param si_phum  transferred part from humus1
     * @param si_cw2cel fractioning from coarse woody litter to cellulose
     * @param si_cw2sol fractioning from coarse woody litter to soluble
     * @param si_fw2cel fractioning from fine woody litter to cellulose
     * @param si_fw2sol fractioning from fine woody litter to soluble
     * @param si_nw2cel fractioning from non woody litter to cellulose
     * @param si_nw2sol fractioning from non woody litter to soluble
     * @param si_clhum1 climate dependence parameter for humus1
     * @param si_clhum2 climate dependence parameter for humus2
     */
    public GMSoil (double si_nwl, double si_fwl, double si_cwl, double si_sol,
            double si_cel, double si_lig, double si_hm1, double si_hm2,
            double si_acwl, double si_afwl, double si_anwl, double si_ksol,
            double si_kcel, double si_klig, double si_khm1, double si_khm2,
            double si_pcel, double si_psol, double si_plig, double si_phum,
            double si_cw2cel, double si_cw2sol, double si_fw2cel, double si_fw2sol,
            double si_nw2cel, double si_nw2sol, double si_clhum1, double si_clhum2) {
        // Compartments
        m_NwLitter = si_nwl;
        m_FwLitter = si_fwl;
        m_CwLitter = si_cwl;
        m_SolComp  = si_sol;
        m_CelComp  = si_cel;
        m_LigComp  = si_lig;
        m_HumusOne = si_hm1;
        m_HumusTwo = si_hm2;

        // Decomposition rates
        m_aCwL = m_caCwL = si_acwl;
        m_aFwL = m_caFwL = si_afwl;
        m_aNwL = m_caNwL = si_anwl;
        m_kCel = m_ckCel = si_kcel;
        m_kSol = m_ckSol = si_ksol;
        m_kLig = m_ckLig = si_klig;
        m_kHumOne = m_ckHumOne = si_khm1;
        m_kHumTwo = m_ckHumTwo = si_khm2;

        // Transferred parts
        m_cpCel = si_pcel;
        m_cpSol = si_psol;
        m_cpLig = si_plig;
        m_cpHum = si_phum;

        // Fractioning rates for litter cohorts
        m_Cw2Cel = si_cw2cel;
        m_Cw2Sol = si_cw2sol;
        m_Fw2Cel = si_fw2cel;
        m_Fw2Sol = si_fw2sol;
        m_Nw2Cel = si_nw2cel;
        m_Nw2Sol = si_nw2sol;
        m_clHumOne = si_clhum1;
        m_clHumTwo = si_clhum2;

        // Climate functions variables and parameters
        m_clBeta = 0.0937;
        m_clGamma = 0.00229;
        m_cpTav = 4;
        m_cpDi = -50;
    }

    /**
     * Gets the current in/out pool.
     * @return the current in/out pool
     */
    public double getInOut () {
        return m_InOut;
    }

    /**
     * Sets the given value to be the in/out pool.
     * @param val value to be assigned
     */
    public void setInOut (double val) {
        m_InOut = val;
    }

    /**
     * Adds the given value to the in/out pool.
     * @param pool value to be added
     */
    public void addToPool (double pool) {
        m_InOut+=pool;
    }

    /**
     * Calculates ratio of changing carbon by given factors. 
     * (not in use in current version!)
     * @param armat area of matrix
     * @param artot total area
     * @return the reduced amount of carbon
     *  
     */
    public double reduceCarbon (float armat, float artot) {
        double ctot, dc;
        ctot = reportTotalCarbon();
        if (ctot==0.0) return -1.0;
        if (artot==0) return -1.0;
        dc = armat/artot;
        return dc;
    }

    /**
     * To increase/decrease carbon because afforestations/deforestation (2010)
     * @param tarea changes in forest area related to the soil
     * @return total change in soil Carbon
     */
    public double changeCarbon (double tarea) {
        if (m_InOut == 0.0)// || tarea == 0.0)
            return 0.0;
        // Is it possible that due to deforestation we remove more Carbon? Probably not ;-)
        // Probably yes, as GetAreaBySoil does not take into account bare land...???? July 2015
        double factor = m_InOut/(Math.abs(m_InOut)+tarea);
        double retval;
        retval = factor*(m_CwLitter + m_FwLitter + m_NwLitter + m_CelComp 
                + m_SolComp + m_LigComp + m_HumusOne + m_HumusTwo + m_CwBasket + m_FwBasket + m_NwBasket);

        // Apply changes
        m_NwLitter+=factor*m_NwLitter;			// Non woody litter
        m_FwLitter+=factor*m_FwLitter;			// Fine woody litter
        m_CwLitter+=factor*m_CwLitter;			// Coarse woody litter
        m_SolComp+=factor*m_SolComp;			// Soluble compounds
        m_CelComp+=factor*m_CelComp;			// Holocellulose
        m_LigComp+=factor*m_LigComp;			// Lignin-like compounds
        m_HumusOne+=factor*m_HumusOne;			// Humus1 in Jari's model
        m_HumusTwo+=factor*m_HumusTwo;
        m_CwBasket+=factor*m_CwBasket;                  //change the litter "containers" 
        m_FwBasket+=factor*m_FwBasket;
        m_NwBasket+=factor*m_NwBasket;
        m_InOut = retval; //0.0; // Here better to set m_InOut to retval and reset to zero after keeping change in history: August 2010
        return retval;
    }

    /**
     * Report the values of Climate function to the given GMCLFUNCTION object.
     * @param pCf container for the values
     * @return GMClFunction containing the values
     */
    public GMClFunction reportClFunction (GMClFunction pCf) {
        pCf.setCf_beta(m_clBeta);
        pCf.setCf_gamma(m_clGamma);
        pCf.setCf_tav(m_cpTav);
        pCf.setCf_di(m_cpDi);
        return pCf;
    }

    /**
     * Sets the values for Climate function from given GMCLFUNCTION object
     * @param pCf GMClFunction with values to be set
     */
    public void setClFunction (GMClFunction pCf) {
        m_clBeta  = pCf.getCf_beta();
        m_clGamma = pCf.getCf_gamma();
        m_cpTav   = pCf.getCf_tav();
        m_cpDi    = pCf.getCf_di();
    }

    /**
     * Set the climate dependent decomposition rates and fraction
     * @param t temperature
     * @param pe (precipitation) drought index
     * @return 0
     */
    public int setClimate (float t, float pe) {
        double cfa,cfk,cfkh;

        // I do not like this at the moment!
        if (pe>0)
            pe = 0;

        // Temperature moisture dependence
        cfa = m_clBeta*(t - m_cpTav);
        cfk = m_clHumOne*cfa;
        cfkh = m_clHumTwo*cfa;
        cfa += 1.0 + m_clGamma*(pe - m_cpDi);
        cfk += 1.0 + m_clGamma*(pe - m_cpDi);
        cfkh += 1.0 + m_clGamma*(pe - m_cpDi);

        // Litter cohorts
        m_caCwL = cfa*m_aCwL;
        if (m_caCwL>1.0) m_caCwL = 1.0;
        if (m_caCwL<0.0) m_caCwL = 0.0;
        m_caFwL = cfa*m_aFwL;
        if (m_caFwL>1.0) m_caFwL = 1.0;
        if (m_caFwL<0.0) m_caFwL = 0.0;
        m_caNwL = cfa*m_aNwL;
        if (m_caNwL>1.0) m_caNwL = 1.0;
        if (m_caNwL<0.0) m_caNwL = 0.0;

        // Compounds
        m_ckSol = cfa*m_kSol;
        if (m_ckSol>1.0) m_ckSol = 1.0;
        if (m_ckSol<0.0) m_ckSol = 0.0;
        m_ckCel = cfa*m_kCel;
        if (m_ckCel>1.0) m_ckCel = 1.0;
        if (m_ckCel<0.0) m_ckCel = 0.0;
        m_ckLig = cfa*m_kLig;
        if (m_ckLig>1.0) m_ckLig = 1.0;
        if (m_ckLig<0.0) m_ckLig = 0.0;

        // Humus stocks
        m_ckHumOne = cfk*m_kHumOne;
        if (m_ckHumOne>1.0) m_ckHumOne = 1.0;
        if (m_ckHumOne<0.0) m_ckHumOne = 0.0;
        m_ckHumTwo = cfkh*m_kHumTwo;
        if (m_ckHumTwo>1.0) m_ckHumTwo = 1.0;
        if (m_ckHumTwo<0.0) m_ckHumTwo = 0.0;

        return 0;
    }

    /**
     * Report stockings (Carbon in different soil compartments).
     * @param pSComp stockings container
     * @return GMSoilComp object containing the stockings
     */
    public GMSoilComp reportStocks (GMSoilComp pSComp) {
        pSComp.setSc_cwl(pSComp.getSc_cwl()+(float)m_CwLitter);
        pSComp.setSc_fwl(pSComp.getSc_fwl()+(float)m_FwLitter);
        pSComp.setSc_nwl(pSComp.getSc_nwl()+(float)m_NwLitter);
        pSComp.setSc_sol(pSComp.getSc_sol()+(float)m_SolComp);
        pSComp.setSc_cel(pSComp.getSc_cel()+(float)m_CelComp);
        pSComp.setSc_lig(pSComp.getSc_lig()+(float)m_LigComp);
        pSComp.setSc_hm1(pSComp.getSc_hm1()+(float)m_HumusOne);
        pSComp.setSc_hm2(pSComp.getSc_hm2()+(float)m_HumusTwo);
        pSComp.setSc_clost(pSComp.getSc_clost()+(float)m_CoutBasket);
        return pSComp;
    }

    /**
     * Reports the total amount of carbon.
     * @return all carbon in soil
     */
    public float reportTotalCarbon () {
        return (float) (m_CwLitter + m_FwLitter + m_NwLitter +
            m_CelComp + m_SolComp + m_LigComp +
            m_HumusOne + m_HumusTwo);
    }

    /**
     * Adds litter to the litter income.
     * @param nw non woody litter
     * @param fw fine woody litter
     * @param cw coarse wood litter
     * @return the total of added litter
     */
    public float addLitter (double nw, double fw, double cw) {
        m_CwBasket+=cw;
        m_FwBasket+=fw;
        m_NwBasket+=nw;
        return (float) (nw + fw + cw);
    }

    /**
     * Initialization soil stocks by using average litter input 
     * by assuming "default" climate
     * @param nw non woody litter
     * @param fw fine woody litter
     * @param cw coarse woody litter
     * @return zero if already initialized else the amount of stocks
     */
    public double reInitStocks (double nw, double fw, double cw) {
        double dec,ret;
        double cw2lig,fw2lig,nw2lig;
        ret = 0.0;

        // Already initialized?
        if (reportTotalCarbon() > 0)
            return ret;

        // Litters
        dec = m_caCwL;
        if (dec<0.0001) dec = 0.0001;
        m_CwLitter = cw/dec;
        ret+=m_CwLitter;
        dec = m_caFwL;
        if (dec<0.0001) dec = 0.0001;
        m_FwLitter = fw/dec;
        ret+=m_FwLitter;
        dec = m_caNwL;
        if (dec<0.0001) dec = 0.0001;
        m_NwLitter = nw/dec;
        ret+=m_NwLitter;

        // Compounds
        dec = m_ckCel;
        if (dec<0.0001) dec = 0.0001;
        //m_CelComp = (cw*m_Cw2Cel + fw*m_Fw2Cel + nw*m_Nw2Cel)/dec;
        m_CelComp = (m_caCwL*m_Cw2Cel*m_CwLitter + m_caFwL*m_Fw2Cel*m_FwLitter
                + m_caNwL*m_Nw2Cel*m_NwLitter)/dec;
        ret+=m_CelComp;
        dec = m_ckSol;
        if (dec<0.0001) dec = 0.0001;
        //m_SolComp = (cw*m_Cw2Sol + fw*m_Fw2Sol + nw*m_Nw2Sol)/dec;
        m_SolComp = (m_caCwL*m_Cw2Sol*m_CwLitter + m_caFwL*m_Fw2Sol*m_FwLitter
                + m_caNwL*m_Nw2Sol*m_NwLitter)/dec;
        ret+=m_SolComp;
        dec = m_ckLig;
        if (dec<0.0001) dec = 0.0001;
        cw2lig = 1.0 - m_Cw2Cel - m_Cw2Sol;
        if (cw2lig<0.0) cw2lig = 0.0;
        fw2lig = 1.0 - m_Fw2Cel - m_Fw2Sol;
        if (fw2lig<0.0) fw2lig = 0.0;
        nw2lig = 1.0 - m_Nw2Cel - m_Nw2Sol;
        if (nw2lig<0.0) nw2lig = 0.0;

        //m_LigComp = (cw*cw2lig + fw*fw2lig + nw*nw2lig)/dec;
        m_LigComp = (m_caCwL*cw2lig*m_CwLitter + m_caFwL*fw2lig*m_FwLitter
                + m_caNwL*nw2lig*m_NwLitter)/dec;
        m_LigComp += (m_CelComp*m_cpCel*m_ckCel + m_SolComp*m_cpSol*m_ckSol)/dec;
        ret+=m_LigComp;

        // Humus stocks
        dec = m_ckHumOne;
        if (dec<0.0001) dec = 0.0001;
        m_HumusOne = (m_LigComp*m_cpLig*m_ckLig)/dec;
        ret+=m_HumusOne;
        dec = m_ckHumTwo;
        if (dec<0.0001) dec = 0.0001;
        m_HumusTwo = (m_HumusOne*m_cpHum*m_ckHumOne)/dec;
        ret+=m_HumusTwo;

        return ret;
    }

    /**
     * Initialization soil stocks by using average litter input 
     * by assuming "current" climate
     * @param nw non woody litter
     * @param fw fine woody litter
     * @param cw coarse woody litter
     * @return zero if already initialized else the total amount of Carbon stock
     */
    public double initStocks (double nw, double fw, double cw) {
        double dec,ret;
        double cw2lig,fw2lig,nw2lig;
        ret = 0.0;

        // Already initialized?
        if (reportTotalCarbon() > 0)
            return ret;

        // Litters
        dec = m_aCwL;
        if (dec<0.0001) dec = 0.0001;
        m_CwLitter = cw/dec;
        ret+=m_CwLitter;
        dec = m_aFwL;
        if (dec<0.0001) dec = 0.0001;
        m_FwLitter = fw/dec;
        ret+=m_FwLitter;
        dec = m_aNwL;
        if (dec<0.0001) dec = 0.0001;
        m_NwLitter = nw/dec;
        ret+=m_NwLitter;

        // Compounds
        dec = m_kCel;
        if (dec<0.0001) dec = 0.0001;
        //m_CelComp = (cw*m_Cw2Cel + fw*m_Fw2Cel + nw*m_Nw2Cel)/dec;
        m_CelComp = (m_aCwL*m_Cw2Cel*m_CwLitter + m_aFwL*m_Fw2Cel*m_FwLitter
                + m_aNwL*m_Nw2Cel*m_NwLitter) / dec;
        ret+=m_CelComp;
        dec = m_kSol;
        if (dec<0.0001) dec = 0.0001;
        //m_SolComp = (cw*m_Cw2Sol + fw*m_Fw2Sol + nw*m_Nw2Sol)/dec;
        m_SolComp = (m_aCwL*m_Cw2Sol*m_CwLitter + m_aFwL*m_Fw2Sol*m_FwLitter
                + m_aNwL*m_Nw2Sol*m_NwLitter) / dec;
        ret+=m_SolComp;
        dec = m_kLig;
        if (dec<0.0001) dec = 0.0001;
        cw2lig = 1.0 - m_Cw2Cel - m_Cw2Sol;
        if (cw2lig<0.0) cw2lig = 0.0;
        fw2lig = 1.0 - m_Fw2Cel - m_Fw2Sol;
        if (fw2lig<0.0) fw2lig = 0.0;
        nw2lig = 1.0 - m_Nw2Cel - m_Nw2Sol;
        if (nw2lig<0.0) nw2lig = 0.0;

        //m_LigComp = (cw*cw2lig + fw*fw2lig + nw*nw2lig)/dec;
        m_LigComp = (m_aCwL*cw2lig*m_CwLitter + m_aFwL*fw2lig*m_FwLitter
                + m_aNwL*nw2lig*m_NwLitter) / dec;
        m_LigComp += (m_CelComp*m_cpCel*m_kCel + m_SolComp*m_cpSol*m_kSol) /dec;
        ret+=m_LigComp;

        // Humus stocks
        dec = m_kHumOne;
        if (dec<0.0001) dec = 0.0001;
        m_HumusOne = (m_LigComp*m_cpLig*m_kLig)/dec;
        ret+=m_HumusOne;
        dec = m_kHumTwo;
        if (dec<0.0001) dec = 0.0001;
        m_HumusTwo = (m_HumusOne*m_cpHum*m_kHumOne)/dec;
        ret+=m_HumusTwo;

        return ret;
    }

    /**
     * Initialization soil stocks by using average litter input 
     * by assuming "given" climate
     * @param nw non woody litter
     * @param fw fine woody litter 
     * @param cw coarse woody litter
     * @param t temperature
     * @param pe precipitation (drought index)
     * @return zero if already initialized else the amount of Carbon stock
     */
    public double initStocksEx (double nw, double fw, double cw, double t,
            double pe) {
        double dec,ret;
        double cw2lig,fw2lig,nw2lig;
        ret = 0.0;

        // Already initialized?
        if (reportTotalCarbon() > 0)
            return ret;

        // Climate dependence
        setClimateSimple(t,pe);

        // Litters
        dec = m_caCwL;
        if (dec<0.0001)
            dec = 0.0001;
        m_CwLitter = cw/dec;
        ret+=m_CwLitter;
        dec = m_caFwL;
        if (dec<0.0001)
            dec = 0.0001;
        m_FwLitter = fw/dec;
        ret+=m_FwLitter;
        dec = m_caNwL;
        if (dec<0.0001)
            dec = 0.0001;
        m_NwLitter = nw/dec;
        ret+=m_NwLitter;

        // Compounds
        dec = m_ckCel;
        if (dec<0.0001)
            dec = 0.0001;
        //m_CelComp = (cw*m_Cw2Cel + fw*m_Fw2Cel + nw*m_Nw2Cel)/dec;
        m_CelComp = (m_caCwL*m_Cw2Cel*m_CwLitter + m_caFwL*m_Fw2Cel*m_FwLitter
                + m_caNwL*m_Nw2Cel*m_NwLitter)/dec;
        ret+=m_CelComp;
        dec = m_ckSol;
        if (dec<0.0001)
            dec = 0.0001;
        //m_SolComp = (cw*m_Cw2Sol + fw*m_Fw2Sol + nw*m_Nw2Sol)/dec;
        m_SolComp = (m_caCwL*m_Cw2Sol*m_CwLitter + m_caFwL*m_Fw2Sol*m_FwLitter
                + m_caNwL*m_Nw2Sol*m_NwLitter)/dec;
        ret+=m_SolComp;
        dec = m_ckLig;
        if (dec<0.0001)
            dec = 0.0001;
        cw2lig = 1.0 - m_Cw2Cel - m_Cw2Sol;
        if (cw2lig<0.0)
            cw2lig = 0.0;
        fw2lig = 1.0 - m_Fw2Cel - m_Fw2Sol;
        if (fw2lig<0.0)
            fw2lig = 0.0;
        nw2lig = 1.0 - m_Nw2Cel - m_Nw2Sol;
        if (nw2lig<0.0)
            nw2lig = 0.0;

        //m_LigComp = (cw*cw2lig + fw*fw2lig + nw*nw2lig)/dec;
        m_LigComp = (m_caCwL*cw2lig*m_CwLitter + m_caFwL*fw2lig*m_FwLitter
                + m_caNwL*nw2lig*m_NwLitter)/dec;
        m_LigComp += (m_CelComp*m_cpCel*m_ckCel + m_SolComp*m_cpSol*m_ckSol)/dec;
        ret+=m_LigComp;

        // Humus stocks
        dec = m_ckHumOne;
        if (dec<0.0001)
            dec = 0.0001;
        m_HumusOne = (m_LigComp*m_cpLig*m_ckLig)/dec;
        ret+=m_HumusOne;
        dec = m_ckHumTwo;
        if (dec<0.0001)
            dec = 0.0001;
        m_HumusTwo = (m_HumusOne*m_cpHum*m_ckHumOne)/dec;
        ret+=m_HumusTwo;

        return ret;
    }

    /**
     * Set the climate dependent decomposition rates and fraction
     * by using default parameters.
     * @param t temperature
     * @param pe precipitation (drought index)
     * @return 0
     */
    public int setClimateSimple (double t, double pe) {
        double cfa,cfk,cfkh;

        // I do not like this at the moment!
        if (pe>0)
            pe = 0;

        // Temperature moisture dependence
        cfa = 0.09368*(t - 4);
        cfk = m_clHumOne*cfa;
        cfkh = m_clHumTwo*cfa;
        cfa += 1.0 + 0.00229*(pe + 50);
        cfk += 1.0 + 0.00229*(pe + 50);
        cfkh += 1.0 + 0.00229*(pe + 50);

        // Litter cohorts
        m_caCwL = cfa*m_aCwL;
        if (m_caCwL>1.0) m_caCwL = 1.0;
        if (m_caCwL<0.0) m_caCwL = 0.0;
        m_caFwL = cfa*m_aFwL;
        if (m_caFwL>1.0) m_caFwL = 1.0;
        if (m_caFwL<0.0) m_caFwL = 0.0;
        m_caNwL = cfa*m_aNwL;
        if (m_caNwL>1.0) m_caNwL = 1.0;
        if (m_caNwL<0.0) m_caNwL = 0.0;

        // Compounds
        m_ckSol = cfa*m_kSol;
        if (m_ckSol>1.0) m_ckSol = 1.0;
        if (m_ckSol<0.0) m_ckSol = 0.0;
        m_ckCel = cfa*m_kCel;
        if (m_ckCel>1.0) m_ckCel = 1.0;
        if (m_ckCel<0.0) m_ckCel = 0.0;
        m_ckLig = cfa*m_kLig;
        if (m_ckLig>1.0) m_ckLig = 1.0;
        if (m_ckLig<0.0) m_ckLig = 0.0;

        // Humus stocks
        m_ckHumOne = cfk*m_kHumOne;
        if (m_ckHumOne>1.0) m_ckHumOne = 1.0;
        if (m_ckHumOne<0.0) m_ckHumOne = 0.0;
        m_ckHumTwo = cfkh*m_kHumTwo;
        if (m_ckHumTwo>1.0) m_ckHumTwo = 1.0;
        if (m_ckHumTwo<0.0) m_ckHumTwo = 0.0;

        return 0;
    }

    /**
     * Decomposition step.
     * @return Carbon loosed by the soil
     */
    public double yearStep () {
        double ret;
        double NwLost,FwLost,CwLost,SolLost,CelLost,
               LigLost,HumOneLost,HumTwoLost;
        double portionSol,portionCel;

        ret = 0.0;
        NwLost = m_caNwL*m_NwLitter;
        FwLost = m_caFwL*m_FwLitter;
        CwLost = m_caCwL*m_CwLitter;
        SolLost = m_ckSol*m_SolComp;
        CelLost = m_ckCel*m_CelComp;
        LigLost = m_ckLig*m_LigComp;
        HumOneLost = m_ckHumOne*m_HumusOne;
        HumTwoLost = m_ckHumTwo*m_HumusTwo;

        // Lost masses first and add "fresh" litter
        m_NwLitter += m_NwBasket - NwLost;
        m_FwLitter += m_FwBasket - FwLost;
        m_CwLitter += m_CwBasket - CwLost;

        m_SolComp -= SolLost;
        m_CelComp -= CelLost;
        m_LigComp -= LigLost;
        m_HumusOne -= HumOneLost;
        m_HumusTwo -= HumTwoLost;

        // Now distribute decomposition
        // Litter first
        portionSol = m_Nw2Sol*NwLost;
        portionCel = m_Nw2Cel*NwLost;
        m_SolComp += portionSol;
        m_CelComp += portionCel;
        m_LigComp += NwLost - portionSol - portionCel;
        portionSol = m_Fw2Sol*FwLost;
        portionCel = m_Fw2Cel*FwLost;
        m_SolComp += portionSol;
        m_CelComp += portionCel;
        m_LigComp += FwLost - portionSol - portionCel;
        portionSol = m_Cw2Sol*CwLost;
        portionCel = m_Cw2Cel*CwLost;
        m_SolComp += portionSol;
        m_CelComp += portionCel;
        m_LigComp += CwLost - portionSol - portionCel;

        // Then cohorts
        double portion;

        portion = SolLost*m_cpSol;
        m_LigComp += portion;
        ret += SolLost - portion;

        portion = CelLost*m_cpCel;
        m_LigComp += portion;
        ret += CelLost - portion;

        portion = LigLost*m_cpLig;
        m_HumusOne += portion;
        ret += LigLost - portion;

        portion = HumOneLost*m_cpHum;
        m_HumusTwo += portion;
        ret += HumOneLost - portion;

        m_CarbonLost = ret + HumTwoLost;
        m_CoutBasket+=m_CarbonLost;
        
        return m_CarbonLost;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GMSoil))
            return false;
        else {
            GMSoil temp = (GMSoil)obj;
            if(this.m_CoutBasket != temp.m_CoutBasket) return false;
            if(this.m_CwBasket != temp.m_CwBasket) return false;
            if(this.m_FwBasket != temp.m_FwBasket) return false;
            if(this.m_NwBasket != temp.m_NwBasket) return false;
            if(this.m_sName != null && temp.m_sName != null) {
                if(!this.m_sName.equals(temp.m_sName)) 
                    return false;
            }
            if(this.m_CarbonLost != temp.m_CarbonLost) return false;
            if(this.m_CelComp != temp.m_CelComp) return false;
            if(this.m_Cw2Cel != temp.m_Cw2Cel) return false;
            if(this.m_Cw2Sol != temp.m_Cw2Sol) return false;
            if(this.m_CwLitter != temp.m_CwLitter) return false;
            if(this.m_Fw2Cel != temp.m_Fw2Cel) return false;
            if(this.m_Fw2Sol != temp.m_Fw2Sol) return false;
            if(this.m_FwLitter != temp.m_FwLitter) return false;
            if(this.m_HumusOne != temp.m_HumusOne) return false;
            if(this.m_HumusTwo != temp.m_HumusTwo) return false;
            if(this.m_InOut != temp.m_InOut) return false;
            if(this.m_LigComp != temp.m_LigComp) return false;
            if(this.m_Nw2Cel != temp.m_Nw2Cel) return false;
            if(this.m_Nw2Sol != temp.m_Nw2Sol) return false;
            if(this.m_NwLitter != temp.m_NwLitter) return false;
            if(this.m_SolComp != temp.m_SolComp) return false;
            if(this.m_aCwL != temp.m_aCwL) return false;
            if(this.m_aNwL != temp.m_aNwL) return false;
            
            if(this.m_caCwL != temp.m_caCwL) return false;
            if(this.m_caFwL != temp.m_caFwL) return false;
            if(this.m_ckCel != temp.m_ckCel) return false;
            if(this.m_ckHumOne != temp.m_ckHumOne) return false;
            if(this.m_ckHumTwo != temp.m_ckHumTwo) return false;
            if(this.m_ckLig != temp.m_ckLig) return false;
            if(this.m_ckSol != temp.m_ckSol) return false;
            if(this.m_clBeta != temp.m_clBeta) return false;
            if(this.m_clGamma != temp.m_clGamma) return false;
            if(this.m_clHumOne != temp.m_clHumOne) return false;
            if(this.m_clHumTwo != temp.m_clHumTwo) return false;
            if(this.m_cpCel != temp.m_cpCel) return false;
            if(this.m_cpDi != temp.m_cpDi) return false;
            if(this.m_cpHum != temp.m_cpHum) return false;
            if(this.m_cpLig != temp.m_cpLig) return false;
            if(this.m_cpSol != temp.m_cpSol) return false;
            if(this.m_cpTav != temp.m_cpTav) return false;
            if(this.m_kCel != temp.m_kCel) return false;
            if(this.m_kHumOne != temp.m_kHumOne) return false;
            if(this.m_kHumTwo != temp.m_kHumTwo) return false;
            if(this.m_kLig != temp.m_kLig) return false;
            if(this.m_kSol != temp.m_kSol) return false;
            
            if(this.m_wID != temp.m_wID) return false;
        }
        return true;
    }
    
    // Compartments
    /**
     * Getter for compartments
     * @return list of values for soil compartments
     */
    public ArrayList<Double> getCompartments() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(m_NwLitter);
        list.add(m_FwLitter);
        list.add(m_CwLitter);
        list.add(m_SolComp);
        list.add(m_CelComp);
        list.add(m_LigComp);
        list.add(m_HumusOne);
        list.add(m_HumusTwo);
        
        return list;
    }
    
    // Decomposition rates
    /**
     * Getter for decomposition rates
     * @return list of decomposition rates
     */
    public ArrayList<Double> getDecompositionRates() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(m_caCwL);
        list.add(m_caFwL);
        list.add(m_caNwL);
        list.add(m_ckSol);
        list.add(m_ckCel);
        list.add(m_ckLig);
        list.add(m_ckHumOne);
        list.add(m_ckHumTwo);
        
        return list;
    }
    
    // Transferred parts
    /**
     * Getter for transferring coefficients
     * @return list of transferring coefficients
     */
    public ArrayList<Double> getTransferredParts() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(m_cpSol);
        list.add(m_cpCel);
        list.add(m_cpLig);
        list.add(m_cpHum);
        
        return list;
    }
    
    // Fractioning rates for litter cohorts
    /**
     * Getter for fractioning rates for litter cohorts
     * @return list of fractioning rates
     */
    public ArrayList<Double> getFractioningRates() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(m_Cw2Cel);
        list.add(m_Cw2Sol);
        list.add(m_Fw2Cel);
        list.add(m_Fw2Sol);
        list.add(m_Nw2Cel);
        list.add(m_Nw2Sol);
        
        return list;
    }
    
    // Climate dependence parameters
    /**
     * Getter for climate dependent parameters
     * @return list of parameters
     */
    public ArrayList<Double> getClimPar() {
        ArrayList<Double> list = new ArrayList<>();
        list.add(m_clHumOne);
        list.add(m_clHumTwo);
        
        return list;
    }
}