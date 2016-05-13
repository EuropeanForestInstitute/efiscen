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
package efi.efiscen.io;

import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMSimulation;
import efi.efiscen.gm.GMMatrix;
import efi.efiscen.gm.GMCell;
import efi.efiscen.gm.GMSoil;
import efi.efiscen.gm.GMParArray;
import efi.efiscen.gm.GMParLocator;
import efi.efiscen.gm.GMCollection;
import efi.efiscen.com.ComArFlt;
import efi.efiscen.com.ComFltPipe;
import efi.efiscen.com.ComFltPipeElement;
import java.io.*;
import java.util.ArrayList;

/**
 * Saves data to files.btw 
 * 
 */
public class FileSaver {

    /**
     * Line separator symbol. Read from Java.Lang.System.
     */
    public static final String newLine = System.getProperty("line.separator");

    private GMEfiscen m_pExperiment;
    private PrintWriter out;

    /**
     * Default constructor.
     */
    public FileSaver () {
    }

    /**
     * Parameterized constructor.
     * @param m_pExperiment current GMEfiscen experiment collection which the data is read from
     */
    public FileSaver (GMEfiscen m_pExperiment) {
        this.m_pExperiment = m_pExperiment;
    }

    /**
     * Exports "history" data to given file (data of growing) by matrixes.
     * @param pfm filename for writing
     * @return true if successful else false, false if not
     * @throws java.io.IOException
     */
    public boolean exportMain (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float grs,garea,hrvthin,hrvfel,grincr,grsav,felav,dwood,nmort;
            float grsprev;
            // To get the increment
            float hrvthinres,hrvfelres,ccont,dens,cfactor;
            ArrayList<Float> pdist;
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,GrStock,Area,DeadWood,NatMort,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv");
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",A_%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",A_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",A_%s",sLims);
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",V_%s",sLims);
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",V_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",V_%s",sLims);
            out.print(newLine);
            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                try {
                    pTable = m_pExperiment.m_mTables.get(uKey);
                    ur = pTable.getRegionID();
                    uo = pTable.getOwnerID();
                    ust = pTable.getSiteID();
                    usp = pTable.getSpeciesID();
                    //float val;
                    ComArFlt<Float> pComar,pComarDW,pComarNM;
                    ComArFlt<ArrayList<Float>> pPComar,pPStComar; // ptr
                    ComArFlt<ArrayList<Float>> pPComarFelRes,pPComarThRes,pPComarFelResRem,pPComarThResRem; // ptr
                    pComar = m_pExperiment.m_mafGrStock.get(uKey);
                    pComarDW = m_pExperiment.m_mafDeadWood.get(uKey);
                    pComarNM = m_pExperiment.m_mafNatMort.get(uKey);
                    pPComar = m_pExperiment.m_mafAreas.get(uKey);
                    pPStComar = m_pExperiment.m_mafStocks.get(uKey);
                    pPComarFelRes = m_pExperiment.m_mafFelRsd.get(uKey);
                    pPComarThRes = m_pExperiment.m_mafThRsd.get(uKey);
                    pPComarFelResRem = m_pExperiment.m_mafFelRsdRem.get(uKey);
                    pPComarThResRem = m_pExperiment.m_mafThRsdRem.get(uKey);

                    ccont = 0;
                    dens = 0;
                    ccont = m_pExperiment.m_plCcont.getParameterValue(uKey,0);
                    dens  = m_pExperiment.m_plWoodDens.getParameterValue(uKey,0);
                    if (ccont==0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont = 0.5f;

                    }
                    if (dens==0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        dens = 0.45f;
                    }
                    cfactor = ccont*dens;
                    int nHowMany = pComar.getSize();
                    grsprev = 0;
                    Object ptemp;
                    for (int i=0;i<nHowMany;i++) {
                        //grs = pComar.getData(i);
                        hrvthin = 0;
                        hrvfel = 0;
                        hrvthinres = 0;
                        hrvfelres = 0;
                        grincr = 0;
                        grsav = 0;
                        felav = 0;
                        dwood = 0;
                        nmort = 0;
                        //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                        grs = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafGrStock,i);
                        garea = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafArea,i);
                        dwood = (Float)pComarDW.getData(i);
                        if (i>0) {
                            nmort = (Float)pComarNM.getData(i-1);
                            hrvthin = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafThinnings,i-1);
                            hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);
                            // useless output ;) debugging leftovers
                            //for (int ii = 0; ii < m_pExperiment.m_mafFellings.size(); ii++)
                            //    System.out.println(m_pExperiment.m_mafFellings.get(ii));
                            ArrayList<Float> arrayTemp = pPComarThRes.getData(i-1);
                            hrvthinres = arrayTemp.get(0);
                            arrayTemp = pPComarFelRes.getData(i-1);
                            hrvfelres = arrayTemp.get(0);
                            // Adding topwood removals ...
                            arrayTemp = pPComarThResRem.getData(i-1);
                            hrvthinres += arrayTemp.get(0);
                            arrayTemp = pPComarFelResRem.getData(i-1);
                            hrvfelres += arrayTemp.get(0);

                            grincr = (grs - grsprev + hrvthin + hrvfel + nmort
                                    + hrvthinres/cfactor + hrvfelres/cfactor);

                        }
                        if (garea > 0) {
                            grsav = grs/garea;
                            felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                            grincr/=garea*m_pExperiment.m_nStep;
                        }
                        grsprev = grs;
                        out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs);

                        out.printf(",%f,%f,%f,%f,%f,%f,%f,%f",garea,dwood,nmort,hrvthin,hrvfel,felav,grsav,grincr);

                        //pdist = (ArrayList) pPComar.getData(i);
                        pdist = pPComar.getData(i);
                        for (int id=0; id < pdist.size(); id++) {
                            out.printf(",%f",pdist.get(id));
                        }
                        //pdist = (ArrayList) pPStComar.getData(i);
                        pdist = pPStComar.getData(i);
                        for (int id=0; id < pdist.size(); id++) {
                            out.printf(",%f",pdist.get(id));
                        }
                        out.print(newLine);
                        //out.println(pfm);
                    }
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports "history" data to given file (data of growing) by matrixes.
     * Biomass distribution.
     * @param pfm filename for writing
     * @return true if successful
     * @throws java.io.IOException
     */
    public boolean exportMainCarbon (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float grs; //,garea,hrvthin,hrvfel,grincr,grsav,felav;
            //float grsprev;
            ArrayList<Float> pdist;
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,C_Trees");
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",C_St_%s",sLims);
            int ih = 1;
            for(ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",C_St_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",C_St_%s",sLims);

            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",C_Br_%s",sLims);
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",C_Br_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",C_Br_%s",sLims);

            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",C_Lv_%s",sLims);
            for(ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",C_Lv_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",C_Lv_%s",sLims);

            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",C_Cr_%s",sLims);
            for(ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",C_Cr_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",C_Cr_%s",sLims);

            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",C_Fr_%s",sLims);
            for(ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",C_Fr_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",C_Fr_%s",sLims);
            out.print(newLine);

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPStComar,pPLvComar,pPCrComar,pPFrComar; // ptr
                pComar = m_pExperiment.m_mafBiomass.get(uKey);
                pPComar = m_pExperiment.m_mafCStem.get(uKey);
                pPStComar = m_pExperiment.m_mafCBranches.get(uKey);
                pPLvComar = m_pExperiment.m_mafCLeaves.get(uKey);
                pPCrComar = m_pExperiment.m_mafCCRoots.get(uKey);
                pPFrComar = m_pExperiment.m_mafCFRoots.get(uKey);
                int nHowMany = pComar.getSize();
                //grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    grs = pComar.getData(i);

                    out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs);

                    //out.printf(",%f,%f,%f,%f,%f,%f",garea,hrvthin,hrvfel,felav,grsav,grincr);
                    pdist = pPComar.getData(i); //stem
                    for (int id=0;id<pdist.size();id++)
                        out.printf(",%f",pdist.get(id));
                    pdist = pPStComar.getData(i); //branches
                    for (int id=0;id<pdist.size();id++)
                        out.printf(",%f",pdist.get(id));
                    pdist = pPLvComar.getData(i); //leaves
                    for (int id=0;id<pdist.size();id++)
                        out.printf(",%f",pdist.get(id));
                    pdist = pPCrComar.getData(i); //Coarse roots
                    for (int id=0;id<pdist.size();id++)
                        out.printf(",%f",pdist.get(id));
                    pdist = pPFrComar.getData(i); //fine roots
                    for (int id=0;id<pdist.size();id++)
                        out.printf(",%f",pdist.get(id));

                    out.print(newLine);
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports "history" data to given file (data of growing).
     * @param pfm filename for writing
     * @return true if successful, false if not
     */
    public boolean exportGeneral (String pfm) {
        synchronized(m_pExperiment) {
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
            // Header first
            float grs,hrvthin,hrvfel;

            out.println("EFISCEN3 results (Generalized by Regions)");
            out.printf("Experiment - %s",m_pExperiment.m_sName);
            out.print(newLine);
            int nRegs = m_pExperiment.m_mRegions.size();
            ArrayList<Long> pulRegs = new ArrayList<>(nRegs);
            ArrayList<Long> pulRid = new ArrayList<>(nRegs);
            int ncount = nRegs-1;
            GMCollection pRgn; // region
            for (Long uKey : m_pExperiment.m_mRegions.keySet())
            {
                pRgn = m_pExperiment.m_mRegions.get(uKey);
                //char uKey;
                pulRid.add(ncount, (long) pRgn.m_ucID);
                pulRegs.add(ncount--, (long) pRgn.m_lISOID);
            }
            out.printf("Region,%d",pulRegs.get(0));
            int ip = 1;
            for (ip=1;ip<nRegs;ip++)
                out.printf(pfm,",,,%d",pulRegs.get(ip));
            out.print(",,,Total");
            out.print(newLine);
            out.printf("Step,GrStock,ThinHarvest,FelHarvest");
            for (ip=1;ip<nRegs;ip++)
                out.printf(",GrStock,ThinHarvest,FelHarvest");
            out.print(",GrStock,ThinHarvest,FelHarvest");
            out.print(newLine);
            int nHowMany = m_pExperiment.m_afStock.getSize();
            for (int i=0;i<nHowMany;i++) {
                hrvthin = 0;
                hrvfel = 0;
                out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                for (int jj=0;jj<nRegs;jj++) {
                    grs = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafGrStock,i);
                    if (i>0) {
                        hrvthin = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafThinnings,i-1);
                        hrvfel = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafFellings,i-1);
                    }
                    out.printf(",%f,%f,%f",grs,hrvthin,hrvfel);
                }
                grs = (Float)m_pExperiment.m_afStock.getData(i);
                hrvthin = 0;
                hrvfel = 0;
                if (i>0) {
                    hrvthin = (Float) m_pExperiment.m_afThinVolume.getData(i-1);
                    hrvfel = (Float) m_pExperiment.m_afFellVolume.getData(i-1);
                }
                out.printf(",%f,%f,%f",grs,hrvthin,hrvfel);
                out.print(newLine);
            }
            out.close();
            return true;
        }
    }

    /**
     * exports "history" data to given file (data of growing) GENERALIZED by Species
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportGeneralBySpec (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float garea,grs,hrvthin,hrvfel;
            ArrayList<Float> gar = new ArrayList<>(4); //(Uppsala)
            float thinres,felres,thinressum,felressum;
            float grsc,dwoodc,felremc,thinremc,grscsum,dwoodcsum,felremcsum,thinremcsum;
            float grsav,felav,grincr,gengrsprev = 0,nmort;
            float cfactor;
            ArrayList<Float> grsprev,ccont,dens;
            out.println("EFISCEN3 results (Generalized by Species)");
            out.println("C_STOCK carbon in gr.stock C_DWOOD -in standing deadwood C_THREM - carbon removals incl. leaves and branches C_FELREM - same for fellings");
            out.printf("Experiment - %s",m_pExperiment.m_sName);
            out.print(newLine);
            ArrayList<Long> pulRegs,pulRid;
            int nRegs = m_pExperiment.m_mSpecies.size();
            pulRegs = new ArrayList<>(nRegs);
            pulRid = new ArrayList<>(nRegs);
            ccont = new ArrayList<>(nRegs);
            dens = new ArrayList<>(nRegs);
            grsprev = new ArrayList<>(nRegs);
            for(int q = 0; q < nRegs; q++) {
                grsprev.add(0f);
            }
            int ncount = 0;//nRegs-1;
            GMCollection pRgn; // species
            for (Long uKey : m_pExperiment.m_mSpecies.keySet())
            {
                try {
                    pRgn = m_pExperiment.m_mSpecies.get(uKey);
                    ccont.add(ncount, m_pExperiment.m_plCcont.getParameterValue(uKey,0));
                    dens.add(ncount, m_pExperiment.m_plWoodDens.getParameterValue(uKey,0));
                    if (ccont.get(ncount) == 0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont.add(ncount, 0.5f);
                    }
                    if (dens.get(ncount) == 0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        dens.add(ncount, 0.45f);
                    }
                    pulRid.add(ncount, (long) pRgn.m_ucID);
                    //pulRegs.add(ncount--, (long) pRgn.m_ucID);
                    pulRegs.add(ncount++, (long) pRgn.m_ucID);
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }
            out.printf("Species,%d",pulRegs.get(0));
            for (int ip=1;ip<nRegs;ip++)
                out.printf(",,,,,,,,,,,%d",pulRegs.get(ip));
            out.println(",,,,,,,,,,,Total");
            out.printf("Step,Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            for (int ip=1;ip<nRegs;ip++)
                out.printf(",Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            out.println(",Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            int nHowMany = m_pExperiment.m_afStock.getSize();
            for (int i=0;i<nHowMany;i++) {
                hrvthin = 0;
                hrvfel = 0;
                grincr = 0;
                nmort = 0;
                felres = 0;
                thinres = 0;
                felressum = 0;
                thinressum = 0;
                grscsum = 0;
                dwoodcsum = 0;
                felremcsum = 0;
                thinremcsum = 0;
                grsc = 0;
                dwoodc = 0;
                felremc = 0;
                thinremc = 0;
                out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                for (int jj=0;jj<nRegs;jj++) {
                    cfactor = ccont.get(jj)*dens.get(jj);
                    grs = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafGrStock,i);
                    garea = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafArea,i);
                    dwoodc = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafDeadWood,i)*cfactor;
                    dwoodcsum+=dwoodc;
                    grsc = grs*cfactor;
                    grscsum+=grsc;
                    if (i>0) {
                        hrvthin = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafThinnings,i-1);
                        hrvfel = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafFellings,i-1);
                        nmort = m_pExperiment.summarize(0,0,0,pulRid.get(jj),m_pExperiment.m_mafNatMort,i-1);
                        gar.clear();
                        for (int ij = 0; ij < 4; ij++)
                            gar.add(ij,0f);
                        felres = m_pExperiment.summarizeArrays(0,0,0,pulRid.get(jj),m_pExperiment.m_mafFelRsd,i-1)/cfactor;
                        gar.clear();
                        for (int ij = 0; ij < 4; ij++)
                            gar.add(ij,0f);
                        thinres = m_pExperiment.summarizeArrays(0,0,0,pulRid.get(jj),m_pExperiment.m_mafThRsd,i-1)/cfactor;

                        // Then carbon and taking into account toopwood removals
                        gar.clear();
                        for (int ij = 0; ij < 4; ij++)
                            gar.add(ij,0f);
                        felres+=m_pExperiment.summarizeArrays(0,0,0,pulRid.get(jj),m_pExperiment.m_mafFelRsdRem,i-1)/cfactor;
                        felremc = gar.get(0) + gar.get(1) + gar.get(2) + gar.get(3) + hrvfel*cfactor;
                        gar.clear();
                        for (int ij = 0; ij < 4; ij++)
                            gar.add(ij,0f);
                        thinres+=m_pExperiment.summarizeArrays(0,0,0,pulRid.get(jj),m_pExperiment.m_mafThRsdRem,i-1)/cfactor;
                        thinremc = gar.get(0) + gar.get(1) + gar.get(2) + gar.get(3) + hrvthin*cfactor;

                        felressum+=felres;
                        thinressum+=thinres;
                        felremcsum+=felremc;
                        thinremcsum+=thinremc;
                        grincr = (grs - grsprev.get(jj) + hrvthin + hrvfel + nmort + felres + thinres);
                    }
                    grsav = 0.0f;
                    felav = 0.0f;
                    if (garea > 0) {
                        grsav = grs/garea;
                        felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                        grincr/=garea*m_pExperiment.m_nStep;
                    }
                    grsprev.set(jj, grs);
                    out.printf(",%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",garea,grs,hrvthin,hrvfel,felav,grsav,grincr,grsc,dwoodc,thinremc,felremc);
                }
                grs = (Float)m_pExperiment.m_afStock.getData(i);
                garea = m_pExperiment.summarize(0,0,0,0,m_pExperiment.m_mafArea,i);
                hrvthin = 0;
                hrvfel = 0;
                nmort = 0;
                if (i>0) {
                    hrvthin = (Float)m_pExperiment.m_afThinVolume.getData(i-1);
                    hrvfel = (Float)m_pExperiment.m_afFellVolume.getData(i-1);
                    nmort = m_pExperiment.summarize(0,0,0,0,m_pExperiment.m_mafNatMort,i-1);
                    grincr = (grs - gengrsprev + hrvthin + hrvfel + nmort + felressum + thinressum);
                }
                grsav = 0.0f;
                felav = 0.0f;
                if (garea > 0) {
                    grsav = grs/garea;
                    felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                    grincr/=garea*m_pExperiment.m_nStep;
                }
                gengrsprev = grs;
                out.printf(",%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",garea,grs,hrvthin,hrvfel,felav,grsav,grincr,grscsum,dwoodcsum,thinremcsum,felremcsum);
                out.print(newLine);
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports "history" data to pfm (data of growing) GENERALIZED by Regions.
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportGeneralByRegs (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float garea,grs,hrvthin,hrvfel;
            float thinres,felres,thinressum,felressum;
            float grsc,dwoodc,felremc,thinremc,grscsum,dwoodcsum,felremcsum,thinremcsum;
            float grsav,felav,grincr,gengrsprev = 0,nmort;
            ArrayList<Float> grsprev,ccont,dens;
            float cfactor;
            out.println("EFISCEN3 results (Generalized by Regions)");
            out.println("C_STOCK carbon in gr.stock C_DWOOD -in standing deadwood C_THREM - carbon removals incl. leaves and branches C_FELREM - same for fellings");
            out.printf("Experiment - %s",m_pExperiment.m_sName);
            out.print(newLine);
            ArrayList<Long> pulRegs,pulRid,pulSid;
            int nSpec = m_pExperiment.m_mSpecies.size();
            pulSid = new ArrayList<>(nSpec);
            ccont = new ArrayList<>(nSpec);
            dens = new ArrayList<>(nSpec);
            int nspcount = 0;
            GMCollection pSp; // species
            for (Long uKey : m_pExperiment.m_mSpecies.keySet())
            {
                try {
                    pSp = m_pExperiment.m_mSpecies.get(uKey);
                    ccont.add(nspcount, m_pExperiment.m_plCcont.getParameterValue(uKey,0));
                    dens.add(nspcount, m_pExperiment.m_plWoodDens.getParameterValue(uKey,0));
                    if (ccont.get(nspcount) == 0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont.add(nspcount, 0.5f);
                    }
                    if (dens.get(nspcount) == 0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        dens.add(nspcount, 0.45f);
                    }
                    pulSid.add(nspcount, (long) pSp.m_ucID);
                    nspcount++;
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }

            int nRegs = m_pExperiment.m_mRegions.size();
            pulRegs = new ArrayList<>(nRegs);
            pulRid = new ArrayList<>(nRegs);
            grsprev = new ArrayList<>(nRegs);
            for(int q = 0; q < nRegs; q++) {
                grsprev.add(0f);
            }
            int ncount = 0;//nRegs-1;
            GMCollection pRgn; // region
            for (Long uKey : m_pExperiment.m_mRegions.keySet())
            {
                pRgn = m_pExperiment.m_mRegions.get(uKey);
                pulRid.add(ncount, (long) pRgn.m_ucID);
                //pulRegs.add(ncount--, (long) pRgn.m_ucID);
                pulRegs.add(ncount++, (long) pRgn.m_ucID);
            }

            out.printf("Region,%d",pulRegs.get(0));
            for (int ip=1;ip<nRegs;ip++)
                out.printf(",,,,,,,,,,,%d",pulRegs.get(ip));
            out.println(",,,,,,,,,,,Total");
            out.print("Step,Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            for (int ip=1;ip<nRegs;ip++)
                out.printf(",Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            out.println(",Area,GrStock,ThinRems,FelRems,RemsAv,GrStockAv,IncrAv,C_GrStock,C_DWood,C_ThRem,C_FelRem");
            int nHowMany = m_pExperiment.m_afStock.getSize();
            for (int i=0;i<nHowMany;i++) {
                hrvthin = 0;
                hrvfel = 0;
                grincr = 0;
                nmort = 0;
                felres = 0;
                thinres = 0;
                felressum = 0;
                thinressum = 0;
                grscsum = 0;
                dwoodcsum = 0;
                felremcsum = 0;
                thinremcsum = 0;
                grsc = 0;
                dwoodc = 0;
                felremc = 0;
                thinremc = 0;
                out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                for (int jj=0;jj<nRegs;jj++) {
                    grs = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafGrStock,i);
                    garea = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafArea,i);
                    dwoodc = 0.0f;
                    grsc = 0.0f;
                    for (int js=0;js<nSpec;js++) {
                        cfactor = ccont.get(js)*dens.get(js);
                        dwoodc += m_pExperiment.summarize(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafDeadWood,i)*cfactor;
                        grsc += m_pExperiment.summarize(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafGrStock,i)*cfactor;
                    }
                    dwoodcsum+=dwoodc;
                    grscsum+=grsc;
                    if (i>0) {
                        hrvthin = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafThinnings,i-1);
                        hrvfel = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafFellings,i-1);
                        nmort = m_pExperiment.summarize(pulRid.get(jj),0,0,0,m_pExperiment.m_mafNatMort,i-1);
                        felres = 0;
                        thinres = 0;
                        felremc = 0;
                        thinremc = 0;
                        for (int js=0;js<nSpec;js++) {
                            cfactor = ccont.get(js)*dens.get(js);
                            felres += m_pExperiment.summarizeArrays(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafFelRsd,i-1)/cfactor;
                            thinres += m_pExperiment.summarizeArrays(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafThRsd,i-1)/cfactor;
                            // Then carbon and topwwod removals for inicrement output
                            felres+=m_pExperiment.summarizeArrays(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafFelRsdRem,i-1)/cfactor;
                            felremc += m_pExperiment.summarize(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafFellings,i-1)*cfactor;
                            thinres+=m_pExperiment.summarizeArrays(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafThRsdRem,i-1)/cfactor;
                            thinremc += m_pExperiment.summarize(pulRid.get(jj),0,0,pulSid.get(js),m_pExperiment.m_mafThinnings,i-1)*cfactor;
                        }
                        felressum+=felres;
                        thinressum+=thinres;
                        felremcsum+=felremc;
                        thinremcsum+=thinremc;
                        grincr = (grs - grsprev.get(jj) + hrvthin + hrvfel + nmort + felres + thinres);
                    }
                    grsav = 0.0f;
                    felav = 0.0f;
                    if (garea > 0) {
                        grsav = grs/garea;
                        felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                        grincr/=garea*m_pExperiment.m_nStep;
                    }
                    grsprev.set(jj, grs);
                    out.printf(",%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",garea,grs,hrvthin,hrvfel,felav,grsav,grincr,grsc,dwoodc,thinremc,felremc);
                }
                grs = (Float)m_pExperiment.m_afStock.getData(i);
                garea = m_pExperiment.summarize(0,0,0,0,m_pExperiment.m_mafArea,i);
                hrvthin = 0;
                hrvfel = 0;
                nmort = 0;
                if (i>0) {
                    hrvthin = (Float)m_pExperiment.m_afThinVolume.getData(i-1);
                    hrvfel = (Float)m_pExperiment.m_afFellVolume.getData(i-1);
                    nmort = m_pExperiment.summarize(0,0,0,0,m_pExperiment.m_mafNatMort,i-1);
                    grincr = (grs - gengrsprev + hrvthin + hrvfel + nmort + felressum + thinressum);
                }
                grsav = 0.0f;
                felav = 0.0f;
                if (garea > 0) {
                    grsav = grs/garea;
                    felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                    grincr/=garea*m_pExperiment.m_nStep;
                }
                gengrsprev = grs;
                out.printf(",%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",garea,grs,hrvthin,hrvfel,felav,grsav,grincr,grscsum,dwoodcsum,thinremcsum,felremcsum);
                out.print(newLine);
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports "history" data to pfm (data of Soil) GENERAL
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportGenSoil (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost,csoil;
            float ctot,cstem,cbr,clvs,ccroots,cfroots;
            //out.println("EFISCEN3 results");
            //out.printf("Experiment - %s - General Carbon Data",m_pExperiment.m_sName);
            //out.print(newLine);
            //out.printf("Step,C_TOT,C_STEM,C_LEAVES,C_BRANCHES,C_CROOTS,C_FROOTS,CWL,FWL,NWL,SOL,CEL,LIG,HUM1,HUM2,COUT");
            //out.print(newLine);
            out.println("Step,C_Trees,C_Stem,C_Leaves,C_Branches,C_CRoots,C_FRoots,C_Soil,CWL,FWL,NWL,SOL,CEL,LIG,HUM1,HUM2,COUT");
            int nHowMany = m_pExperiment.m_afSoilCwl.getSize();
            for (int i=0;i<nHowMany;i++) {
                cwl = (Float)m_pExperiment.m_afSoilCwl.getData(i);
                fwl = (Float)m_pExperiment.m_afSoilFwl.getData(i);
                nwl = (Float)m_pExperiment.m_afSoilNwl.getData(i);
                sol = (Float)m_pExperiment.m_afSoilSol.getData(i);
                cel = (Float)m_pExperiment.m_afSoilCel.getData(i);
                lig = (Float)m_pExperiment.m_afSoilLig.getData(i);
                hum1 = (Float)m_pExperiment.m_afSoilHm1.getData(i);
                hum2 = (Float)m_pExperiment.m_afSoilHm2.getData(i);
                csoil = cwl + fwl + nwl + sol + cel + lig + hum1 + hum2;
                clost = (Float)m_pExperiment.m_afSoilClost.getData(i);
                clost = clost/m_pExperiment.m_nStep;
                ctot	= (Float)m_pExperiment.m_afCarbon.getData(i);
                cstem	= ((Double)m_pExperiment.m_afStem.getData(i)).floatValue();
                clvs	= ((Double)m_pExperiment.m_afLeaves.getData(i)).floatValue();
                cbr		= ((Double)m_pExperiment.m_afBranches.getData(i)).floatValue();
                ccroots = ((Double)m_pExperiment.m_afCroots.getData(i)).floatValue();
                cfroots = ((Double)m_pExperiment.m_afFroots.getData(i)).floatValue();
                out.printf("%d,%f,%f,%f,%f,%f,%f,%f,",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ctot,cstem,clvs,cbr,ccroots,cfroots,csoil);
                out.printf("%f,%f,%f,%f,%f,%f,%f,%f,%f",cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost);
                out.print(newLine);
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports "history" data to given file (data of Soil) MAIN
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportMainSoil (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            Float cwl = 0f,fwl = 0f,nwl = 0f,sol = 0f,cel = 0f,lig = 0f,hum1 = 0f,hum2 = 0f,
                    clost = 0f,litin = 0f,ctot = 0f,ctrees = 0f;
            Float stprev = 0f,cwlin = 0f,fwlin = 0f,nwlin = 0f,cinout = 0f;
            //int ur,uo,ust,usp;
            long lr,lo,lst,lsp;
            out.println("S_ID,REG,OWN,ST,SP,Step,C_Trees,CWL,FWL,NWL,SOL,CEL,LIG,HUM1,HUM2,C_Soil,COUT,LITIN,CWL_IN,FWL_IN,NWL_IN,C_BAL");

            GMSoil pSoil;
            for (Long uKey : m_pExperiment.m_mSoils.keySet())
            {
                pSoil = m_pExperiment.m_mSoils.get(uKey);
                lr = lo = lst = lsp = uKey;
                lr = lr>>24;
                lo = lo&0xFF0000;
                lo = lo>>16;
                lst = lst&0xFF00;
                lst = lst>>8;
                lsp = uKey&0xFF;
                //ur = lr>>24;
                //uo = lo>>16;
                //ust = lst>>8;
                //usp = lsp;
                //float val;

                ComArFlt pcomCwl,pcomFwl,pcomNwl,pcomCel,pcomLig,pcomSol;
                ComArFlt pcomHm1,pcomHm2,pcomClost,pcomCwlIn,pcomFwlIn,pcomNwlIn,pcomInOut;

                pcomCwl = m_pExperiment.m_mafSoilCwl.get(uKey);
                pcomFwl = m_pExperiment.m_mafSoilFwl.get(uKey);
                pcomNwl = m_pExperiment.m_mafSoilNwl.get(uKey);
                pcomCel = m_pExperiment.m_mafSoilCel.get(uKey);
                pcomLig = m_pExperiment.m_mafSoilLig.get(uKey);
                pcomSol = m_pExperiment.m_mafSoilSol.get(uKey);
                pcomHm1 = m_pExperiment.m_mafSoilHm1.get(uKey);
                pcomHm2 = m_pExperiment.m_mafSoilHm2.get(uKey);
                pcomClost = m_pExperiment.m_mafSoilClost.get(uKey);
                pcomCwlIn = m_pExperiment.m_mafSoilCwlIn.get(uKey);
                pcomFwlIn = m_pExperiment.m_mafSoilFwlIn.get(uKey);
                pcomNwlIn = m_pExperiment.m_mafSoilNwlIn.get(uKey);
                pcomInOut = m_pExperiment.m_mafSoilInOut.get(uKey);
                stprev = 0f;
                if (pcomCwl == null)
                    continue;
                int nHowMany = pcomCwl.getSize();
                Object tmp;
                for (int i=0;i<nHowMany;i++) {
                    tmp = pcomCwl.getData(i);
                    if (tmp instanceof Float)
                        cwl = (Float)tmp;
                    else if (tmp instanceof Double)
                        cwl = ((Double)tmp).floatValue();
                    tmp = pcomFwl.getData(i);
                    if (tmp instanceof Float)
                        fwl = (Float)tmp;
                    else if (tmp instanceof Double)
                        fwl = ((Double)tmp).floatValue();
                    tmp = pcomNwl.getData(i);
                    if (tmp instanceof Float)
                        nwl = (Float)tmp;
                    else if (tmp instanceof Double)
                        nwl = ((Double)tmp).floatValue();
                    tmp = pcomCel.getData(i);
                    if (tmp instanceof Float)
                        cel = (Float)tmp;
                    else if (tmp instanceof Double)
                        cel = ((Double)tmp).floatValue();
                    tmp = pcomLig.getData(i);
                    if (tmp instanceof Float)
                        lig = (Float)tmp;
                    else if (tmp instanceof Double)
                        lig = ((Double)tmp).floatValue();
                    tmp = pcomSol.getData(i);
                    if (tmp instanceof Float)
                        sol = (Float)tmp;
                    else if (tmp instanceof Double)
                        sol = ((Double)tmp).floatValue();
                    tmp = pcomHm1.getData(i);
                    if (tmp instanceof Float)
                        hum1 = (Float)tmp;
                    else if (tmp instanceof Double)
                        hum1 = ((Double)tmp).floatValue();
                    tmp = pcomHm2.getData(i);
                    if (tmp instanceof Float)
                        hum2 = (Float)tmp;
                    else if (tmp instanceof Double)
                        hum2 = ((Double)tmp).floatValue();
                    tmp = pcomClost.getData(i);
                    if (tmp instanceof Float)
                        clost = (Float)tmp;
                    else if (tmp instanceof Double)
                        clost = ((Double)tmp).floatValue();
                    tmp = pcomCwlIn.getData(i);
                    if (tmp instanceof Float)
                        cwlin = (Float)tmp;
                    else if (tmp instanceof Double)
                        cwlin = ((Double)tmp).floatValue();
                    tmp = pcomFwlIn.getData(i);
                    if (tmp instanceof Float)
                        fwlin = (Float)tmp;
                    else if (tmp instanceof Double)
                        fwlin = ((Double)tmp).floatValue();
                    tmp = pcomNwlIn.getData(i);
                    if (tmp instanceof Float)
                        nwlin = (Float)tmp;
                    else if (tmp instanceof Double)
                        nwlin = ((Double)tmp).floatValue();
                    tmp = pcomInOut.getData(i);
                    if (tmp instanceof Float)
                        cinout = (Float)tmp;
                    else if (tmp instanceof Double)
                        cinout = ((Double)tmp).floatValue();
                    clost = clost/m_pExperiment.m_nStep;

                    ctot = cwl + fwl + nwl + cel + lig + sol + hum1 + hum2;
                    ctrees = m_pExperiment.summarize(lr,lo,lst,lsp,m_pExperiment.m_mafBiomass,i);
                    litin = 0f;
                    if (i > 0) 
                        litin = ctot - stprev + m_pExperiment.m_nStep*clost;
                    // Rownding errors
                    if ((Float)litin < 0.001)
                        litin = 0.0f;
                    stprev = ctot;
                    out.printf("%d,%d,%d,%d,%d,%d,%f,%f,%f,%f,",uKey,lr,lo,lst,lsp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ctrees,cwl,fwl,nwl);
                    //out.printf("%f,%f,%f,%f,%f,%f,",ctot,cstem,clvs,cbr,ccroots,cfroots);
                    out.printf("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",sol,cel,lig,hum1,hum2,ctot,clost,litin,cwlin,fwlin,nwlin,cinout);
                    out.print(newLine);
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports Natural mortality
     * 
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportNatMort (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float grs,grsdw;
            ArrayList<Float> pdist = new ArrayList<>();
            float ccont,dens,cfactor;
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,Nmort,DWood,C_DWood");
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",DW_%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",DW_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",DW_%s",sLims);

            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",NM_%s",sLims);
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",NM_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",NM_%s",sLims);
            out.print(newLine);

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                try {
                    pTable = m_pExperiment.m_mTables.get(uKey);
                    ur = pTable.getRegionID();
                    uo = pTable.getOwnerID();
                    ust = pTable.getSiteID();
                    usp = pTable.getSpeciesID();
                    //float val;
                    ComArFlt<Float> pComar,pComarDW;
                    ComArFlt<ArrayList<Float>> pPComar,pPComarDW; // ptr
                    pComarDW = m_pExperiment.m_mafDeadWood.get(uKey);
                    pComar = m_pExperiment.m_mafNatMort.get(uKey);
                    pPComar = m_pExperiment.m_mafNatMortDistr.get(uKey);
                    pPComarDW = m_pExperiment.m_mafDeadWoodDistr.get(uKey);
                    // To have carbon in deadwood
                    ccont = 0;
                    dens = 0;
                    ccont = m_pExperiment.m_plCcont.getParameterValue(uKey,0);
                    dens  = m_pExperiment.m_plWoodDens.getParameterValue(uKey,0);
                    if (ccont==0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont = 0.5f;
                    }
                    if (dens==0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        ccont = 0.45f;
                    }
                    cfactor = ccont*dens;
                    int nHowMany = pComar.getSize() + 1; // Because we have one element more in distribution!
                    //grsprev = 0;
                    for (int i=0;i<nHowMany;i++) {
                        grs = 0;
                        grsdw = 0;
                        if (i < pComar.getSize()+1)
                            if (i>0)
                                grs = (Float)pComar.getData(i-1);
                        if (i < pComarDW.getSize()+1)
                            grsdw = (Float)pComarDW.getData(i);
                        out.printf("%d,%d,%d,%d,%d,%d,%f,%f,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs,grsdw,grsdw*cfactor);

                        //out.printf(",%f,%f,%f,%f,%f,%f",garea,hrvthin,hrvfel,felav,grsav,grincr);
                        pdist = pPComarDW.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));

                        //out.print(newLine);
                        pdist = pPComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        out.print(newLine);
                    }
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports Mefique data to given (Fellings removals)
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportMefiqFelRems (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            float hrvfel;
            float grsprev;
            ArrayList<Float> pdist;
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,FelRem");
            /*
            String sLims;
            sLims  = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0]);
            out.printf(",%s",sLims);
            for (int ih=1;ih<15;ih++) {
                sLims  = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1],m_pExperiment.m_pDistrLims.get(ih]);
                out.printf(",%s",sLims);
            }
            sLims  = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1]);
            out.printf(",%s",sLims);
            out.print(newLine);
            */
            String sLims;
            sLims  = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",A_%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims  = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",A_%s",sLims);
            }
            sLims  = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",A_%s",sLims);
            sLims  = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",V_%s",sLims);
            for (ih=1;ih<15;ih++) {
                sLims  = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",V_%s",sLims);
            }
            sLims  = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",V_%s",sLims);
            out.print(newLine);

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPArComar; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafMfqFelRems.get(uKey);
                pPArComar = m_pExperiment.m_mafMfqFelAreas.get(uKey);
                int nHowMany = pComar.getSize();
                grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    //grs = pComar.getData(i);
                    hrvfel = 0;
                    /*
                    hrvthin = 0;
                    hrvfel = 0;
                    grincr = 0;
                    //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                    grs = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafGrStock,i);
                    garea = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafArea,i);
                    */
                    if (i>0) {
                        hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);

                        out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel);
                        pdist = pPArComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        pdist = pPComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        out.print(newLine);
                    }
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports Mefique data to given file (Thinnings removals)
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportMefiqThinRems (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            //float grs,garea,hrvthin,hrvfel,grincr,grsav,felav;
            float hrvfel;
            float grsprev;
            ArrayList<Float> pdist;
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,ThinRem");

            /*
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0]);
            out.printf(",%s",sLims);
            for(int ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1],m_pExperiment.m_pDistrLims.get(ih]);
                out.printf(",%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1]);
            out.printf(",%s",sLims);
            out.print(newLine);
            */

            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",A_%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",A_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",A_%s",sLims);
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",V_%s",sLims);
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",V_%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",V_%s",sLims);
            out.print(newLine);

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPArComar; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafMfqThRems.get(uKey);
                pPArComar = m_pExperiment.m_mafMfqThAreas.get(uKey);

                int nHowMany = pComar.getSize();
                grsprev = 0;
                Object ptemp;
                for (int i=0;i<nHowMany;i++) {
                    //grs = pComar.getData(i);
                    hrvfel = 0;
                    /*
                    hrvthin = 0;
                    hrvfel = 0;
                    grincr = 0;
                    //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                    grs = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafGrStock,i);
                    garea = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafArea,i);
                    */
                    if (i>0) {
                        hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafThinnings,i-1);

                        out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel);
                        pdist = pPArComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        pdist = pPComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        out.print(newLine);
                    }
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports Bioenergy data to given file (Fellings slash)
     * @param pfm filename for writing
     * @return true if successful, false if not
     * @throws java.io.IOException
     */
    public boolean exportBeFelSlash (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            //float hrvfel;
            //float grsprev;
            ArrayList<Float> pdist = new ArrayList<>();
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            // September 2009 Uppsala - Coarse roots res/rems to output
            out.printf("M_ID,REG,OWN,ST,SP,Step,C_TopsRes,C_BrRes,C_LvRes,C_CrRes,C_TopsRem,C_BrRem,C_LvRem,C_CrRem");
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",%s",sLims);
            out.print(newLine);
            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPComRes,pPComResRem; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafBeFelSlash.get(uKey);
                pPComRes = m_pExperiment.m_mafFelRsd.get(uKey);
                pPComResRem = m_pExperiment.m_mafFelRsdRem.get(uKey);
                int nHowMany = pComar.getSize();
                //grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    if (i>0) {
                        //hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);

                        out.printf("%d,%d,%d,%d,%d,%d",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                        /*for (int j = 0; j < 4; j++) {
                            out.printf(",%f", pPComRes.getData(4*i-4+j));
                        }*/
                        pdist = pPComRes.getData(i-1);
                        for (int j = 0; j < pdist.size(); j++)
                            out.printf(",%f",pdist.get(j));
                        //out.printf(",%f,%f,%f,%f",pdist.get(0),pdist.get(1),pdist.get(2),pdist.get(3));


                        /*for (int j = 0; j < 4; j++) {
                            out.printf(",%f", pPComResRem.getData(4*i-4+j));
                        }*/
                        pdist = pPComResRem.getData(i-1);
                        for (int k = 0; k < pdist.size(); k++)
                            out.printf(",%f",pdist.get(k));
                        //out.printf(",%f,%f,%f,%f",pdist.get(0),pdist.get(1),pdist.get(2),pdist.get(3));
                        pdist = pPComar.getData(i);
                        for (int id=0;id<pdist.size();id++)
                            out.printf(",%f",pdist.get(id));
                        out.print(newLine);
                    }
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Exports Bioenergy data to given file(Thinning slash)
     * @param pfm filename for writing
     * @return true successful
     * @throws java.io.IOException
     */
    public boolean exportBeThinSlash (String pfm) throws IOException {
        synchronized(m_pExperiment) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(pfm)));
            // Header first
            //float hrvfel;
            //float grsprev;
            ArrayList<Float> pdist = new ArrayList<>();
            int ur,uo,ust,usp;
            //out.println("EFISCEN3 results");
            out.printf("M_ID,REG,OWN,ST,SP,Step,C_TopsRes,C_BrRes,C_LvRes,C_CrRes,C_TopsRem,C_BrRem,C_LvRem,C_CrRem");
            String sLims;
            sLims = String.format("0 - %3.0f",m_pExperiment.m_pDistrLims.get(0));
            out.printf(",%s",sLims);
            int ih = 1;
            for (ih=1;ih<15;ih++) {
                sLims = String.format("%3.0f - %3.0f",m_pExperiment.m_pDistrLims.get(ih-1),m_pExperiment.m_pDistrLims.get(ih));
                out.printf(",%s",sLims);
            }
            sLims = String.format("> %3.0f",m_pExperiment.m_pDistrLims.get(ih-1));
            out.printf(",%s",sLims);
            out.print(newLine);
            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPComRes,pPComResRem; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafBeThSlash.get(uKey);
                pPComRes = m_pExperiment.m_mafThRsd.get(uKey);
                pPComResRem = m_pExperiment.m_mafThRsdRem.get(uKey);

                int nHowMany = pComar.getSize();
                //grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    if (i>0) {
                        //hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);
                        out.printf("%d,%d,%d,%d,%d,%d",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);

                        /*for (int j = 0; j < 4; j++) {
                            out.printf(",%f", pPComRes.getData(4*i-4+j));
                        }*/
                        pdist = pPComRes.getData(i-1);
                        for (int j = 0; j < pdist.size(); j++) {
                            out.printf(",%f", pdist.get(j));

                        }
                        //out.printf(",%f,%f,%f,%f",pdist.get(0),pdist.get(1),pdist.get(2),pdist.get(3)); //(Uppsala)
                        /*for (int j = 0; j < 4; j++) {
                            out.printf(",%f", pPComResRem.getData(4*i-4+j));
                        }*/
                        pdist = pPComResRem.getData(i-1);
                        for (int k = 0; k < pdist.size(); k++)
                            out.printf(",%f", pdist.get(k));
                            //System.out.println(ptemp);
                        //out.printf(",%f,%f,%f,%f",pdist.get(0),pdist.get(1),pdist.get(2),pdist.get(3)); //(Uppsala)
                        pdist = pPComar.getData(i);
                        for (int id=0;id<pdist.size();id++) {
                            out.printf(",%f",pdist.get(id));
                            //System.out.println(pdist.get(id));
                        }

                        out.print(newLine);
                    }
                }
            }
            out.close();
            return true;
        }
    }

    /**
     * Saves all data from GMEfiscen object to a text file.
     * @param sPname file name
     * @param sExt file extension
     * @return true if saving was successful else false
     */
    public boolean saveAll (String sPname, String sExt) {
        String sFname,sFMain,sFMainC,sFGen,sFGenSpec,sFslGen,sFslMain;
        String sFext;
        String sFileName;
        sFext = sExt;
        sFileName = sPname;
        sFname = sFileName;
        //sFname.format("%s",m_pExperiment.m_pExperiment.m_pExperiment.m_sName);
        if (!sFileName.contains(".csv"))
            sFMain = sFileName + ".csv";
        else sFMain = sFileName;
        sFMainC = sFname + "_treeC_matr.csv";
        sFGen = sFname + "_gdat.csv";
        sFGenSpec = sFname + "_gspec.csv";
        sFslGen = sFname + "_carbon_country.csv";
        sFslMain = sFname + "_carbon_soil.csv";
        final String errorMsg = "ERROR saving to file ";
        try {
            exportMain(sFMain);
            exportMainCarbon(sFMainC);
            exportGeneralByRegs(sFGen);
            exportGeneralBySpec(sFGenSpec);
            exportGenSoil(sFslGen);
            exportMainSoil(sFslMain);
        } catch (IOException ex) {
            System.err.println(errorMsg + sFMain);
            return false;
        }
        // Mefique stuff and Bioenergy!
        String sFmef;
        sFmef = sFname + "_fell_matr.csv";
        try {
            exportMefiqFelRems(sFmef);
            sFmef = sFname + "_thin_matr.csv";
            exportMefiqThinRems(sFmef);
            sFmef = sFname + "_fell_residues.csv";
            exportBeFelSlash(sFmef);
            sFmef = sFname + "_thin_residues.csv";
            exportBeThinSlash(sFmef);
            // End Mefique!
            // Natural mortality Femke and MJ :-)
            sFmef = sFname + "_natmort.csv";
            exportNatMort(sFmef);
        } catch (IOException ex) {
            System.err.println(errorMsg + sFmef);
            return false;
        }
        return true;
    }
  /*  
   // /**
     * Saves the selected files.
     * @param filename
     * @param selections
     * @return 
  
    public boolean saveSelectedFile(String filename,String selection) {
        if(filename.contains("."))
            filename = filename.substring(0,filename.length() 
                    - filename.indexOf("."));
        String sFmef;
        boolean rValue = true;
        switch (selection) {
            case "detV":
                sFmef = filename + ".csv";
                if (!exportMain(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "gdat":
                sFmef = filename + "_gdat.csv";
                if (!exportGeneralByRegs(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "gspec":
                sFmef = filename + "_gspec.csv";
                if (!exportGeneralBySpec(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "fell_matr":
                sFmef = filename + "_fell_matr.csv";
                if (!exportMefiqFelRems(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "thin_matr":
                sFmef = filename + "_thin_matr.csv";
                if (!exportMefiqThinRems(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "natmort":
                sFmef = filename + "_natmort.csv";
                if (!exportNatMort(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "carbon_soil":
                sFmef = filename + "_carbon_soil.csv";
                if (!exportMainSoil(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "treeC_matr":
                sFmef = filename + "_treeC_matr.csv";
                if (!exportMainCarbon(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "carbon_country":
                sFmef = filename + "_carbon_country.csv";
                if (!exportGenSoil(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "fell_res":
                sFmef = filename + "_fell_residues.csv";
                if (!exportBeFelSlash(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
            case "thin_res":
                sFmef = filename + "_thin_residues.csv";
                if (!exportBeThinSlash(sFmef)) {
                    System.out.println("ERROR saving file " + sFmef);
                    rValue = false;
                }
                break;
        }
            
        return rValue;
    }*/
    
    /**
     * Saves selected outputs to the file given in parameter outputName.
     *
     * @param outputName File path to where the output files will be saved. Path
     * must include a file name ( for example C:\Folder\FileName would save the
     * outputs in C:\Folder\ and the files would have names starting with
     * FileName ).
     * @param selectedOutputs File path to a text file containing definitions
     * about which outputs to save. The Path must include the file name and the
     * name must end with “.txt”. The same selections are used for saving
     * outputs to files and to a database. If this file is not used then all
     * outputs are saved.
     */
    public void saveSelected(String outputName, String selectedOutputs) {
        BufferedReader reader = null;
        //FileReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(selectedOutputs));
        } catch (FileNotFoundException ex) {
            System.err.println("Error reading output selection file");
            return;
        }
        String line;
        try {
            line = reader.readLine();
        } catch (IOException ex) {
            System.err.println("Error reading output selection file");
            return;
        }
        while (line != null) {
            String[] temp = line.split(" ");
            String id = temp[0];
            int value = Integer.parseInt(temp[1]);
            try {
                switch (id) {
                    case "base":
                        if (value == 1) {
                            exportMain(outputName + ".csv");
                            break;
                        }
                    case "gdat":
                        if (value == 1) {
                            exportGeneralByRegs(outputName + "_gdat.csv");
                            break;
                        }
                    case "gspec":
                        if (value == 1) {
                            exportGeneralBySpec(outputName + "_gspec.csv");
                            break;
                        }
                    case "felling_matrix":
                        if (value == 1) {
                            exportMefiqFelRems(outputName + "_fell_matr.csv");
                            break;
                        }
                    case "thinning_matrix":
                        if (value == 1) {
                            exportMefiqThinRems(outputName + "_thin_matr.csv");
                            break;
                        }
                    case "natmort":
                        if (value == 1) {
                            exportNatMort(outputName + "_natmort.csv");
                            break;
                        }
                    case "carbon_soil":
                        if (value == 1) {
                            exportMainSoil(outputName + "_carbon_soil.csv");
                            break;
                        }
                    case "treec_matrix":
                        if (value == 1) {
                            exportMainCarbon(outputName + "_treeC_matr.csv");
                            break;
                        }
                    case "carbon_country":
                        if (value == 1) {
                            exportGenSoil(outputName + "_carbon_country.csv");
                            break;
                        }
                    case "felling_residues":
                        if (value == 1) {
                            exportBeFelSlash(outputName + "_fell_res.csv");
                            break;
                        }
                    case "thinning_residues":
                        if (value == 1) {
                            exportBeThinSlash(outputName + "_thin_res.csv");
                            break;
                        }
                }
            } catch (IOException ex) {
                System.err.println("Error saving to files");
                System.err.println(ex);
            }
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                System.err.println("Error reading output selection file");
                return;
            }
        }
    }
    
    /**
     * Extracts matrices to a file
     * @param fileName name of the file
     * @param path path of the file
     * @param volClassFile volClassfile
     * @return false if there occurred an error, true if successful
     */
    public boolean extractMatrices(String fileName, String path, String volClassFile) {
        if (!fileName.endsWith(".aer")) fileName = fileName.concat(".aer");
        if (volClassFile != null)
            if (volClassFile.contains("\\")) volClassFile = volClassFile.substring(volClassFile.lastIndexOf("\\"));
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.concat(fileName))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }

        out.printf("#EFISCEN3 input file");
        out.print(newLine);
        /*for (Long r : m_pExperiment.m_mRegions.keySet() ) {
            GMCollection region = (GMCollection) m_pExperiment.getRegions().get(r);
            out.printf(region.m_sName.concat(", "));
        }
        for (Long o : m_pExperiment.m_mOwners.keySet() ) {
            GMCollection owner = (GMCollection) m_pExperiment.getRegions().get(o);
            out.printf(owner.m_sName.concat(", "));
        }
        for (Long st : m_pExperiment.m_mSites.keySet() ) {
            GMCollection site = (GMCollection) m_pExperiment.getRegions().get(st);
            out.printf(site.m_sName.concat(", "));
        }
        for (Long sp : m_pExperiment.m_mSpecies.keySet() ) {
            GMCollection spec = (GMCollection) m_pExperiment.getRegions().get(sp);
            out.printf(spec.m_sName.concat(", "));
        }*/

        out.printf("#volume classes are in the file:");
        out.print(newLine);
        out.printf(volClassFile);
        out.print(newLine);
        out.printf("#First how many");
        out.print(newLine);

        // Total number of matrices
        int matrix = 0;
        for (Long r : m_pExperiment.m_mRegions.keySet()) {
            for (Long o : m_pExperiment.m_mOwners.keySet()) {
                for (Long st : m_pExperiment.m_mSites.keySet()) {
                    for (Long sp : m_pExperiment.m_mSpecies.keySet()) {
                        matrix += m_pExperiment.getNumMatr(r, o, st, sp);
                    }
                }
            }
        }
        out.printf(Integer.toString(matrix));
        out.print(newLine);

        out.printf("# REG OWNER SITE SPECIES");
        out.print(newLine);

        // Printing matrices
        for (Long r : m_pExperiment.m_mRegions.keySet()) {
            for (Long o : m_pExperiment.m_mOwners.keySet()) {
                for (Long st : m_pExperiment.m_mSites.keySet()) {
                    for (Long sp : m_pExperiment.m_mSpecies.keySet()) {
                        try {
                            out.printf("\t"+Long.toString(r)+"\t"+Long.toString(o)+
                                    "\t"+Long.toString(st)+"\t"+Long.toString(sp));
                            out.print(newLine);

                            long uKey = (r<<24) + (o<<16) + (st<<8) + sp;
                            int na = (int) m_pExperiment.m_plAgeNum.getParameterValue(uKey, 0);
                            int nv = (int) m_pExperiment.m_plVolNum.getParameterValue(uKey, 0);

                            // Bare land class
                            float value = m_pExperiment.getZeroClass(r, o, st, sp);

                            out.printf("\t\t%1$#.5f",value);
                            for (int j = 0; j < na-1; j++) {
                            out.printf("\t\t%1$#.5f",0f);
                            }
                            out.print(newLine);

                            GMMatrix pTable = m_pExperiment.m_mTables.get(uKey);

                            GMParArray parAgew = m_pExperiment.m_plAgeLims.getParameter(uKey);
                            ArrayList<Float> pagew = parAgew.m_Vals;
                            int[] pnumc = new int[na];
                            pnumc[0] = (int) (pagew.get(0) / m_pExperiment.m_nStep);
                            for (int ja = 1; ja < na; ja++) {
                                pnumc[ja] = (int) ((pagew.get(ja) - pagew.get(ja - 1)) / m_pExperiment.m_nStep);
                            }

                            // Printing matrix
                            for (int ii = 0; ii < nv; ii++) {
                                int ncurind = 0;
                                for (int j = 0; j < na; j++) {
                                    int npart = pnumc[j];
                                    GMCell pCell = pTable.getAt(ncurind + npart, ii + 1);
                                    float ar = pCell.getArea()+pCell.getM_ThinArea();
                                    float part = 0;
                                    for (int jj = 1; jj < npart; jj++) {
                                        pCell = pTable.getAt(ncurind + jj, ii + 1);
                                        part = pCell.getArea()+pCell.getM_ThinArea();
                                            ar += part;
                                    }

                                    ncurind += npart;
                                    if(ar>0f)
                                        value = ar;
                                    else 
                                        value = 0f;

                                    out.printf("\t\t%1$#.5f",value);
                                }
                                out.print(newLine);
                            }
                        } catch (GMParLocator.GMParLocatorException ex) {
                            System.err.println(ex);
                        }
                    }
                }
            }
        }
        out.printf("#");
        out.print(newLine);
        out.printf("#THE END");
        out.close();
        return true;
    }
    
    /**
     * Extracts parameters to a file
     * @param filename the name of the file
     * @param path the path of the file
     * @return false if there occurred an error, true if successfull
     */
    public boolean extractParameters(String filename,String path) {
        try {
            if(!filename.endsWith(".prs")) filename = filename.concat(".prs");
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.concat(filename))));
        } catch (IOException ioe) {
            return false;
        }

        out.println("#experiment's parameters file");
        out.println("#"+m_pExperiment.m_sName);
        out.println("#Step of simulation (how many years are in one tick)");
        out.println(m_pExperiment.m_nStep);
        out.println("#Number of age classes (X axis)");
        out.println("AgeClassNum " + m_pExperiment.m_plAgeNum.getNumElements());
        printParams(m_pExperiment.m_plAgeNum);
        
        out.println("#size of age class (X axis)");
        out.println("X1 " + m_pExperiment.m_plAgeClasses.getNumElements());
        printParams(m_pExperiment.m_plAgeClasses);
        
        out.println("#Number of volume classes");
        out.println("VolClassNum " + m_pExperiment.m_plVolNum.getNumElements());
        printParams(m_pExperiment.m_plVolNum);
        
        out.println("#size of volume class (Y axis)");
        out.println("Y1 " + m_pExperiment.m_plVolClasses.getNumElements());
        printParams(m_pExperiment.m_plVolClasses);
        
        out.println("#Growing function's coeff.");
        out.println("GrFunction " + m_pExperiment.m_plGrCoeff.getNumElements());
        printParams(m_pExperiment.m_plGrCoeff);
        
        out.println("#Young forest coeff");
        out.println("YForest " + m_pExperiment.m_plYoungCoeff.getNumElements());
        printParams(m_pExperiment.m_plYoungCoeff);
        
        
        out.println("#Regrow after thinnings");
        out.println("Gamma " + m_pExperiment.m_plRegrowCoeff.getNumElements());
        printParams(m_pExperiment.m_plRegrowCoeff);
        
        out.println("#Age of Harvest");
        out.println("Harvest " + m_pExperiment.m_plHarvestAge.getNumElements());
        printParams(m_pExperiment.m_plHarvestAge);
        
        out.println("#Thinnings range");
        out.println("Thinrange " + m_pExperiment.m_plThinRange.getNumElements());
        printParams(m_pExperiment.m_plThinRange);
        
        out.println("#Beta coeff");
        out.println("Beta " + m_pExperiment.m_plBeta.getNumElements());
        printParams(m_pExperiment.m_plBeta);
        
        out.println("#Volume series: pair - first age classes limits;second volumes");
        long key;
        key = (m_pExperiment.m_plAgeLims.getM_bRegion()<<24) +
                (m_pExperiment.m_plAgeLims.getM_bOwner()<<16) +
                (m_pExperiment.m_plAgeLims.getM_bSite()<<8) +
                m_pExperiment.m_plAgeLims.getM_bSpecies();
        out.println("AgeLims " + m_pExperiment.m_plAgeLims.getNumElements());
        for(GMParArray pAr : m_pExperiment.m_plAgeLims.getElements().values()) {
            out.println(pAr.m_uRegion +" "+ pAr.m_uOwner +" "+ pAr.m_uSite + " "
                    + pAr.m_uSpecies);
            try {
                out.print(m_pExperiment.m_plAgeNum.getParameter(key).
                        m_Vals.get(0).intValue() + " ");
            } catch (GMParLocator.GMParLocatorException ex) {
                System.err.println(ex);
            }
            for(Number i : pAr.m_Vals) {
                out.print(i + " ");
            }
            out.print(newLine);
        }
        
        out.println("Volsers " + m_pExperiment.m_plVolSers.getNumElements());
        printParams(m_pExperiment.m_plVolSers);
        
        out.println("#Natural mortality stuff");
        out.println("MortAgeLims " + m_pExperiment.m_plMortRateXvals.getNumElements());
        printParams(m_pExperiment.m_plMortRateXvals);
        
        out.println("MortRates " + m_pExperiment.m_plMortRate.getNumElements());
        printParams(m_pExperiment.m_plMortRate);
        
        out.println("#Deadwood decay rate");
        out.println("Decay " + m_pExperiment.m_plDeadWoodDrate.getNumElements());
        printParams(m_pExperiment.m_plDeadWoodDrate);
        
        out.println("#thinning history");
        out.println("Thhistory " + m_pExperiment.m_mTables.size());
        //out.println("Thhistory " + m_pExperiment.m_plThHistory.getNumElements());
        for(GMMatrix pTable : m_pExperiment.m_mTables.values()) {
            out.println(pTable.getRegionID() + " "
                    + pTable.getOwnerID() + " "
                    + pTable.getSiteID() + " "
                    + pTable.getSpeciesID());
            float thinArea = 0f, area = 0f;
            for(GMCell pCell : pTable.m_Cells) {
                if(pCell.isM_bThinned()) {
                    thinArea+=pCell.getM_ThinArea();
                    area+=pCell.getArea()+pCell.getM_ThinArea();
                }
            }
            float val = 0f;
            if(area>0f && thinArea>0f) val = thinArea/area;
            //if(thinArea>0f && area>0f) val = (thinArea+pTable.m_BareArea)/pTable.getArea();
            out.printf("1 %1$#.5f\n", val);
        }
        //printParams(m_pExperiment.m_plThHistory);
        out.printf("#");
        out.print(newLine);
        out.printf("#THE END");
        out.close();
        return true;
    }
    
    /**
     * Prints parameters to a file given in exportMain()
     * @param locator Parameter locator
     */
    private void printParams(GMParLocator locator) {
        for(GMParArray pAr : locator.getElements().values()) {
            out.println(pAr.m_uRegion +" "+ pAr.m_uOwner +" "+ pAr.m_uSite + " "
                    + pAr.m_uSpecies);
            out.print(pAr.m_Vals.size() + " ");
            for(Number i : pAr.m_Vals) {
                out.print(i + " ");
            }
            out.print(newLine);
        }
    }
    
    /**
     * Extracts soil information to given file
     * @param fileName name of the file
     * @param path path of the file
     * @return false if there occurred an error, true if successful
     */
    public boolean extractSoils(String fileName, String path) {
        if (!fileName.endsWith("_soils.par")) fileName = fileName.concat("_soils.par");
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.concat(fileName))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        out.printf("#Parameter file for EFISCEN3 (soil), country "+m_pExperiment.m_sName+":");
        out.print(newLine);
        out.printf("#Number of soil types");
        out.print(newLine);
        out.printf("soils "+m_pExperiment.m_mSoils.size());
        out.print(newLine);
        
        // Printing soils
        for (Long r : m_pExperiment.m_mRegions.keySet()) {
            for (Long o : m_pExperiment.m_mOwners.keySet()) {
                for (Long st : m_pExperiment.m_mSites.keySet()) {
                    for (Long sp : m_pExperiment.m_mSpecies.keySet()) {
                        if (sp != 0 || st != 0 || o != 0 || r != 0) {
                            out.printf("#");
                            long reg = 0;
                            long own = 0;
                            long sit = 0;
                            long spe = 0;
                            
                            if (!m_pExperiment.m_mRegions.get(r).m_sName.equals("All") &&
                                    !m_pExperiment.m_mRegions.get(r).m_sName.equals("ALL")) {
                                out.printf(" "+m_pExperiment.m_mRegions.get(r).m_sName);
                            }
                            reg = r;
                            if (!m_pExperiment.m_mOwners.get(o).m_sName.equals("All") &&
                                    !m_pExperiment.m_mOwners.get(o).m_sName.equals("ALL")) {
                                out.printf(" "+m_pExperiment.m_mOwners.get(o).m_sName);
                                System.out.println(m_pExperiment.m_mOwners.get(o).m_sName);
                            }
                            own = o ;
                            if (!m_pExperiment.m_mSites.get(st).m_sName.equals("All") &&
                                    !m_pExperiment.m_mSites.get(st).m_sName.equals("ALL")) {
                                out.printf(" "+m_pExperiment.m_mSites.get(st).m_sName);
                            }
                            sit = st;
                            if (!m_pExperiment.m_mSpecies.get(sp).m_sName.equals("All") &&
                                    !m_pExperiment.m_mSpecies.get(sp).m_sName.equals("ALL")) {
                                out.printf(" "+m_pExperiment.m_mSpecies.get(sp).m_sName);
                            }
                            spe = sp;
                            out.print(newLine);
                            
                            out.printf(Long.toString(reg)+"\t"+Long.toString(own)+"\t"+
                                    Long.toString(sit)+"\t"+Long.toString(spe));
                            out.print(newLine);
                            
                            long ulKey = (r<<24) + (o<<16) + (st<<8) + sp;
                            GMSoil pSol = m_pExperiment.findSoil(ulKey);
                            
                            out.print("#initial storages, just in case");
                            out.print(newLine);
                            out.print("#coarse wl, fine wl, non wl, soluble, cellulose, lignin, humus1, humus2");
                            out.print(newLine);
                            ArrayList<Double> list = pSol.getCompartments();
                            for (int i = 0; i < list.size(); i ++)
                                out.print(list.get(i)+" ");
                            out.print(newLine);
                            
                            out.print("#decomposition rates");
                            out.print(newLine);
                            out.print("#acwl afwl anwl ksol kcel klig khum1 khum2");
                            out.print(newLine);
                            list = pSol.getDecompositionRates();
                            for (int i = 0; i < list.size(); i ++)
                                out.print(list.get(i)+" ");
                            out.print(newLine);
                            
                            out.print("#transfer proportions");
                            out.print(newLine);
                            out.print("#psol pcel plig phum");
                            out.print(newLine);
                            list = pSol.getTransferredParts();
                            for (int i = 0; i < list.size(); i ++)
                                out.print(list.get(i)+" ");
                            out.print(newLine);
                            
                            out.print("#Litter composition (NOTE we'll not use toLignin rate)");
                            out.print(newLine);
                            out.print("#cw2cel cw2sol fw2cel fw2sol nw2cel nw2sol");
                            out.print(newLine);
                            list = pSol.getFractioningRates();
                            for (int i = 0; i < list.size(); i ++)
                                out.print(list.get(i)+" ");
                            out.print(newLine);
                            
                            out.print("#Climate dependence parameters");
                            out.print(newLine);
                            out.print("#chum1 chum2 (really in efiscen chum1=0.6 and chum2=0.36, i.e chum1**2)");
                            out.print(newLine);
                            list = pSol.getClimPar();
                            for (int i = 0; i < list.size(); i ++)
                                out.print(list.get(i)+" ");
                            out.print(newLine);
                        }
                    }
                }
            }
        }
        out.printf("#");
        out.print(newLine);
        out.printf("#THE END");
        out.close();
        
        return true;
    }
    
    /**
     * Main extract method to extract data to given file
     * @param fileName name of the file
     * @param path path of the file
     * @param bioparametersFile name of the bio parameters file
     * @param step current step
     * @return false if there occurred an error, true if successful
     */
    public boolean extractMain(String fileName, String path, String bioparametersFile, int step) {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path.concat(fileName).concat(".efs"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        out.printf("EFISCEN experiment file");
        out.print(newLine);
        out.printf("#Experiment's initialisation file");
        out.print(newLine);
        out.printf("#EFISCEN 3 - "+m_pExperiment.m_sName);
        out.print(newLine);
        out.printf(m_pExperiment.m_sName);
        out.print(newLine);
        out.printf("#Base year (starting simulation)");
        out.print(newLine);
        out.printf(Integer.toString(m_pExperiment.m_nBaseYear+step));
        out.print(newLine);
        
        // Printing regions
        out.printf("#Regions should be listed first, started from how many");
        out.print(newLine);
        out.printf(Integer.toString(m_pExperiment.m_mRegions.size()));
        out.print(newLine);
        
        int i = 0;
        for (Long r : m_pExperiment.m_mRegions.keySet()) {
            i++;
            out.printf(r+" "+m_pExperiment.m_mRegions.get(r).m_lISOID+" "+m_pExperiment.m_mRegions.get(r).m_sName);
            out.print(newLine);
        }
        
        // Printing owners
        out.printf("#Owners");
        out.print(newLine);
        
        out.printf(Integer.toString(m_pExperiment.m_mOwners.size()));
        out.print(newLine);
        
        i = 0;
        for (Long r : m_pExperiment.m_mOwners.keySet()) {
            i++;
            out.printf(r+" "+m_pExperiment.m_mOwners.get(r).m_sName);
            out.print(newLine);
        }
        
        // Printing sites
        out.printf("#Sites");
        out.print(newLine);
        
        out.printf(Integer.toString(m_pExperiment.m_mSites.size()));
        out.print(newLine);
        
        i = 0;
        for (Long r : m_pExperiment.m_mSites.keySet()) {
            i++;
            out.printf(r+" "+m_pExperiment.m_mSites.get(r).m_sName);
            out.print(newLine);
        }
        
        // Printing species
        out.printf("#Species");
        out.print(newLine);
        
        out.printf(Integer.toString(m_pExperiment.m_mSpecies.size()));
        out.print(newLine);
        
        i = 0;
        for (Long r : m_pExperiment.m_mSpecies.keySet()) {
            i++;
            out.printf(r+" "+m_pExperiment.m_mSpecies.get(r).m_sName);
            out.print(newLine);
        }
        
        // File names
        out.printf("#File name for parameters");
        out.print(newLine);
        out.printf(fileName.concat(".prs"));
        out.print(newLine);
        out.printf("#");
        out.print(newLine);
        out.printf("#File name for bioparameters");
        out.print(newLine);
        out.printf(bioparametersFile);
        out.print(newLine);
        out.printf("#File name for matrixes");
        out.print(newLine);
        out.printf(fileName.concat(".aer"));
        out.print(newLine);
        out.printf("#");
        out.print(newLine);
        out.printf("#File name for soils");
        out.print(newLine);
        out.printf(fileName.concat("_soils.par"));
        out.print(newLine);
        out.printf("#File name for extra information");
        out.print(newLine);
        out.printf(fileName.concat("_extra.eif"));
        out.print(newLine);
        out.printf("#THE END");
        
        out.close();
        
        return true;
    }

    /**
     * Writes the given GMSimulation object to the given file
     * @param fileName path of the file
     * @param sim GMSimulation object 
     * @throws IOException when there was an error with IO
     */
    public void serializeSession(String fileName,GMSimulation sim) throws IOException {
        ObjectOutputStream out = null;
        FileOutputStream str = null;
        try {
            str = new FileOutputStream(fileName);
            out = new ObjectOutputStream(str);
        } catch (FileNotFoundException ex) {}
        out.writeObject(sim);
        out.close();
    }
    
    /**
     * Extracts information about GMEfiscen object's length of the name, ID, base year,
     * step's size in years, 
     * GMEfiscen object to a file.
     * @param fileName name of the file
     * @param path path of the file
     * @param step current step
     * @param m_scaleAreas scaling factor that is applied globally to areas of all the matrices
     * @return false if there occurred an exception writing the file, true if successful
     */
    public boolean extractExtraInformation(String fileName, String path, int step,
            float m_scaleAreas) {
        try {
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream
                         (path.concat(fileName)))) {
                String str = m_pExperiment.m_sName;
                byte[] data = str.getBytes("UTF-8");
                os.writeInt(m_pExperiment.m_sName.length());
                os.write(data);
                os.writeInt(m_pExperiment.m_ID);
                os.writeInt(m_pExperiment.m_nBaseYear);
                os.writeInt(step);
                os.writeInt(m_pExperiment.m_nStep);
                os.writeBoolean(m_pExperiment.m_bIsStart);
                os.writeFloat(m_pExperiment.m_FelInt);
                os.writeFloat(m_pExperiment.m_ThinInt);
                
                os.writeFloat(m_scaleAreas);
                
                os.writeInt(m_pExperiment.historyUpdateCounter);
                
                // Regions
                int size = m_pExperiment.m_mRegions.size();
                os.writeInt(size);
                for (long key : m_pExperiment.m_mRegions.keySet()) {
                    os.writeLong(m_pExperiment.m_mRegions.get(key).m_lISOID);
                    os.writeInt(m_pExperiment.m_mRegions.get(key).m_ucID);
                    
                    str = m_pExperiment.m_mRegions.get(key).m_sName;
                    data = str.getBytes("UTF-8");
                    os.writeInt(data.length);
                    os.write(data);
                }
                
                // Owners
                size = m_pExperiment.m_mOwners.size();
                os.writeInt(size);
                for (long key : m_pExperiment.m_mOwners.keySet()) {
                    os.writeLong(m_pExperiment.m_mOwners.get(key).m_lISOID);
                    os.writeInt(m_pExperiment.m_mOwners.get(key).m_ucID);
                    
                    str = m_pExperiment.m_mOwners.get(key).m_sName;
                    data = str.getBytes("UTF-8");
                    os.writeInt(data.length);
                    os.write(data);
                }
                
                // Sites
                size = m_pExperiment.m_mSites.size();
                os.writeInt(size);
                for (long key : m_pExperiment.m_mSites.keySet()) {
                    os.writeLong(m_pExperiment.m_mSites.get(key).m_lISOID);
                    os.writeInt(m_pExperiment.m_mSites.get(key).m_ucID);
                    
                    str = m_pExperiment.m_mSites.get(key).m_sName;
                    data = str.getBytes("UTF-8");
                    os.writeInt(data.length);
                    os.write(data);
                }
                
                // Species
                size = m_pExperiment.m_mSpecies.size();
                os.writeInt(size);
                for (long key : m_pExperiment.m_mSpecies.keySet()) {
                    os.writeLong(m_pExperiment.m_mSpecies.get(key).m_lISOID);
                    os.writeInt(m_pExperiment.m_mSpecies.get(key).m_ucID);
                   
                    str = m_pExperiment.m_mSpecies.get(key).m_sName;
                    data = str.getBytes("UTF-8");
                    os.writeInt(data.length);
                    os.write(data);
                }
                
                ComArFlt comAr;
                os.writeInt(m_pExperiment.m_mTables.size());
                
                for(GMMatrix pTable : m_pExperiment.m_mTables.values()) {
                    int r = pTable.getRegionID();
                    int o = pTable.getOwnerID();
                    int st = pTable.getSiteID();
                    int sp = pTable.getSpeciesID();
                    long uKey = (r<<24) + (o<<16) + (st<<8) + sp;
                    os.writeInt(r);
                    os.writeInt(o);
                    os.writeInt(st);
                    os.writeInt(sp);
                    
                    os.writeFloat(pTable.getM_Xbottom());
                    os.writeFloat(pTable.getM_Xtop());
                    os.writeFloat(pTable.getM_Ybottom());
                    os.writeFloat(pTable.getM_Ytop());
                    os.writeFloat(pTable.getM_Xstep());
                    os.writeFloat(pTable.getM_Ystep());
                    
                    os.writeInt(pTable.m_wXsize);
                    os.writeInt(pTable.m_wYsize);
                    
                    os.writeInt(pTable.m_Cells.size());
                    for(int i=0;i<pTable.m_Cells.size();i++) {
                        GMCell pCell = pTable.m_Cells.get(i);
                        os.writeInt(pCell.m_wX);
                        os.writeInt(pCell.m_wY);
                        os.writeLong(pCell.m_wID);
                        os.writeFloat(pCell.getM_Xmin());
                        os.writeFloat(pCell.getM_Xmax());
                        os.writeFloat(pCell.getM_Xval());
                        os.writeFloat(pCell.getM_Ymin());
                        os.writeFloat(pCell.getM_Ymax());
                        os.writeFloat(pCell.getM_Yval());
                        
                        os.writeFloat(pCell.getM_ThArea());
                        os.writeFloat(pCell.getM_ThRem());
                        os.writeFloat(pCell.getM_FelArea());
                        os.writeFloat(pCell.getM_FelRem());
                        os.writeFloat(pCell.getM_ThSlash());
                        os.writeFloat(pCell.getM_FelSlash());
                        os.writeFloat(pCell.getM_NatMrt());
                        os.writeFloat(pCell.getM_DWood());
                        os.writeFloat(pCell.getM_FireReplSus());
                        os.writeFloat(pCell.getM_FireNonReplSus());
                        os.writeFloat(pCell.getM_WindReplSus());
                        os.writeFloat(pCell.getM_WindNonReplSus());
                        os.writeFloat(pCell.getM_InsReplSus());
                        os.writeFloat(pCell.getM_InsNonReplSus());
                        os.writeFloat(pCell.getArea());
                        os.writeFloat(pCell.getM_ThinArea());
                        os.writeFloat(pCell.getM_MoveAsThin());
                        os.writeFloat(pCell.getM_MoveByX());
                        os.writeFloat(pCell.getM_MoveByY());
                        os.writeFloat(pCell.getM_MoveByXY());
                        os.writeFloat(pCell.getM_MoveByXOrg());
                        os.writeFloat(pCell.getM_MoveByYOrg());
                        os.writeFloat(pCell.getM_MoveByXYOrg());
                        os.writeFloat(pCell.getM_Move());
                        os.writeFloat(pCell.getM_MoveAway());
                        os.writeFloat(pCell.getM_FellingsShare());
                        os.writeFloat(pCell.getM_ThinShare());
                        os.writeFloat(pCell.getM_Income());
                        os.writeBoolean(pCell.isM_bThinned());
                    }
                    
                    os.writeFloat(pTable.m_DeadWood);
                    os.writeFloat(pTable.m_BareArea);
                    os.writeFloat(pTable.m_FromBare);
                    os.writeFloat(pTable.m_RegrGamma);
                    os.writeDouble(Double.parseDouble(Float.toString(m_pExperiment.m_BareFund.getFund(uKey))));
                    os.writeFloat(m_pExperiment.grsprev.get(uKey));
                    
                    ComFltPipe m_fpDwPipe = pTable.getM_fpDwPipe();
                    for (int i = 0; i < m_fpDwPipe.m_nSize; i++) {
                        ComFltPipeElement m_pData = m_fpDwPipe.getElement(i);
                        os.writeInt(m_pData.getCfp_nind());
                        os.writeFloat(m_pData.getCfp_value());
                        os.writeFloat(m_pData.getCfp_threm());
                        os.writeFloat(m_pData.getCfp_felrem());
                        os.writeFloat(m_pData.getCfp_uplim());
                    }
                    
                    comAr = m_pExperiment.m_mafGrStock.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafArea.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafAfforFund.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafBareArea.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafBiomass.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafCStem.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafCLeaves.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafCBranches.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafCCRoots.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafCFRoots.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    // Areas and stocks distribution
                    comAr = m_pExperiment.m_mafAreas.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafStocks.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    // Mefique stuff!
                    comAr = m_pExperiment.m_mafMfqThAreas.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafMfqThRems.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafMfqFelAreas.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafMfqFelRems.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    // End Mefique!
                    // Bioenergy stuff
                     comAr = m_pExperiment.m_mafBeThSlash.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                   
                    comAr = m_pExperiment.m_mafBeFelSlash.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafNatMortDistr.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafDeadWoodDistr.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                     
                    comAr = m_pExperiment.m_mafIncrement.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                     
                    comAr = m_pExperiment.m_mafAvrIncrement.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                   
                    comAr = m_pExperiment.m_mafDeadWood.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafNatMort.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafThRsd.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafFelRsd.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafThRsdRem.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafFelRsdRem.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                   
                    comAr = m_pExperiment.m_mafThinnings.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafFellings.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeFlValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafPotentialFellingsArea.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeDblValue(comAr.getData(j), os);
                    
                    comAr = m_pExperiment.m_mafPotentialFellingsVolume.get(uKey);
                    os.writeInt(comAr.getSize());
                    for (int j = 0; j < comAr.getSize(); j++)
                        writeDblValue(comAr.getData(j), os);
                    
                } 
                
                writeSessionSoils(os);
                
                writeSessionBioParameters(os);
                
                writeSessionParameters(os);
                
                comAr = m_pExperiment.m_afStock;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afCarbon;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afStem;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afBranches;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afLeaves;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afCroots;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afFroots;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afThinVolume;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_afFellVolume;
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Writes session parameters. Writes error message in case of an error.
     * @param os used DataOutputStream for the method
     * @return false if there occurred an error writing parameters, true if successful
     */
    private boolean writeSessionParameters(DataOutputStream os) {
        try {
            writeLocatorParams(os, m_pExperiment.m_plAgeNum);
            writeLocatorParams(os, m_pExperiment.m_plAgeClasses);
            writeLocatorParams(os, m_pExperiment.m_plVolNum);
            writeLocatorParams(os, m_pExperiment.m_plVolClasses);
            writeLocatorParams(os, m_pExperiment.m_plGrCoeff);
            writeLocatorParams(os, m_pExperiment.m_plYoungCoeff);
            writeLocatorParams(os, m_pExperiment.m_plRegrowCoeff);
            writeLocatorParams(os, m_pExperiment.m_plHarvestAge);
            writeLocatorParams(os, m_pExperiment.m_plThinRange);
            writeLocatorParams(os, m_pExperiment.m_plBeta);
            writeLocatorParams(os, m_pExperiment.m_plVolSers);
            writeLocatorParams(os, m_pExperiment.m_plMortRateXvals);
            writeLocatorParams(os, m_pExperiment.m_plMortRate);
            writeLocatorParams(os, m_pExperiment.m_plDeadWoodDrate);
            
            os.writeInt(m_pExperiment.m_mTables.size());
            for(GMMatrix pTable : m_pExperiment.m_mTables.values()) {
                os.writeInt(pTable.getRegionID());
                os.writeInt(pTable.getOwnerID());
                os.writeInt(pTable.getSiteID());
                os.writeInt(pTable.getSpeciesID());
                float thinArea = 0f, area = 0f;
                for(GMCell pCell : pTable.m_Cells) {
                    if(pCell.isM_bThinned()) {
                        thinArea+=pCell.getM_ThinArea();
                        area+=pCell.getArea()+pCell.getM_ThinArea();
                    }
                }
                float val = 0f;
                if(area>0f && thinArea>0f) val = thinArea/area;
                os.writeInt(1);
                os.writeFloat(val);
            }

            return true;
        } catch (IOException ex) {
            System.err.println("Error in writing parameters");
            return false;
        }
    }
    
    /**
     * Writes locator parameters from GMParLocator object.  
     * @param os used DataOutputStream for the method
     * @param locator parameter locator
     * @return false if there occurred an error , true if successful
     */
    private boolean writeLocatorParams(DataOutputStream os,GMParLocator locator) {
        try {
            os.writeInt(locator.getNumElements());
            for(GMParArray pAr : locator.getElements().values()) {
                os.writeInt(pAr.m_uRegion);
                os.writeInt(pAr.m_uOwner);
                os.writeInt(pAr.m_uSite);
                os.writeInt(pAr.m_uSpecies);
                os.writeInt(pAr.m_nSize);
                for(Float i : pAr.m_Vals) {
                    os.writeFloat(i);
                }
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Writes session's bio parameters
     * @param os used DataOutputStream for the method
     * @return true
     */
    private boolean writeSessionBioParameters(DataOutputStream os) {
            writeLocatorParams(os, m_pExperiment.m_plCcont);
            writeLocatorParams(os, m_pExperiment.m_plWoodDens);
            writeLocatorParams(os, m_pExperiment.m_plCompXvals);
            writeLocatorParams(os, m_pExperiment.m_plStemShare);
            writeLocatorParams(os, m_pExperiment.m_plBranchShare);
            writeLocatorParams(os, m_pExperiment.m_plCrootsShare);
            writeLocatorParams(os, m_pExperiment.m_plFrootsShare);
            writeLocatorParams(os, m_pExperiment.m_plLeavesShare);
            writeLocatorParams(os, m_pExperiment.m_plLtrCompXvals);
            writeLocatorParams(os, m_pExperiment.m_plCroots2CWL);
            
            //write shares
            writeLocatorParams(os, m_pExperiment.m_plLtrStemShare);
            writeLocatorParams(os, m_pExperiment.m_plLtrBranchShare);
            writeLocatorParams(os, m_pExperiment.m_plLtrCrootsShare);
            writeLocatorParams(os, m_pExperiment.m_plLtrFrootsShare);
            writeLocatorParams(os, m_pExperiment.m_plLtrLeavesShare);
            return true;
    }
    
    /**
     * Writes data of the session's soils 
     * @param os used DataOutputStream
     * @return false if there occurred an error writing data , true if successful
     */
    private boolean writeSessionSoils(DataOutputStream os) {
        try {
            os.writeInt(m_pExperiment.m_mSoils.size());
            
            // Soils update - main
            for (Long uKey : m_pExperiment.m_mSoils.keySet()) {
                GMSoil pSl = m_pExperiment.m_mSoils.get(uKey);
                os.writeLong(uKey);
                
                ArrayList<Double> array = pSl.getCompartments();
                for (int i = 0; i < array.size(); i++) {
                    os.writeDouble(array.get(i));
                }

                array = pSl.getDecompositionRates();
                for (int i = 0; i < array.size(); i++) {
                    os.writeDouble(array.get(i));
                }

                array = pSl.getTransferredParts();
                for (int i = 0; i < array.size(); i++) {
                    os.writeDouble(array.get(i));
                }

                array = pSl.getFractioningRates();
                for (int i = 0; i < array.size(); i++) {
                    os.writeDouble(array.get(i));
                }

                array = pSl.getClimPar();
                for (int i = 0; i < array.size(); i++) {
                    os.writeDouble(array.get(i));
                }

                    os.writeDouble(pSl.m_CwBasket);
                    os.writeDouble(pSl.m_FwBasket);
                    os.writeDouble(pSl.m_NwBasket);
                    os.writeDouble(pSl.getInOut());
            }
            
            ComArFlt comAr;
            
            for (Long uKey : m_pExperiment.m_mSoils.keySet()) {
                comAr = m_pExperiment.m_mafSoilCwl.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilFwl.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilNwl.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilCel.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilSol.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilLig.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilHm1.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilHm2.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilClost.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafCSoil.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeFlValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilCwlIn.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilFwlIn.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilNwlIn.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
                comAr = m_pExperiment.m_mafSoilInOut.get(uKey);
                os.writeInt(comAr.getSize());
                for (int j = 0; j < comAr.getSize(); j++)
                    writeDblValue(comAr.getData(j), os);
                
            }
            
            
            comAr = m_pExperiment.m_afSoilCwl;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilFwl;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilNwl;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilCel;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilSol;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilLig;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilHm1;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilHm2;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            
            comAr = m_pExperiment.m_afSoilClost;
            os.writeInt(comAr.getSize());
            for (int j = 0; j < comAr.getSize(); j++)
                writeFlValue(comAr.getData(j), os);
            return true;
        } catch (IOException ex) {
            System.err.println("Error in writing soils");
            return false;
        }
    }
  
  /**
   * Writes float values
   * @param ptemp ArrayList of floats or just one float value
   * @param os used DataOutputStream
   * @return false if there occurred an error, true if successful
   */
  private boolean writeFlValue(Object ptemp, DataOutputStream os) {
        try {
            if (ptemp instanceof ArrayList) {
                ArrayList<Float> list = (ArrayList<Float>) ptemp;
                os.writeInt(list.size());
                for (int p = 0; p < list.size(); p++)
                    os.writeFloat(list.get(p));
            } else if (ptemp instanceof Float) {
                os.writeInt(-1);
                os.writeFloat((Float)ptemp);
            }
            
            return true;
        } catch (IOException ex) {
                return false;
        }
    }
  
  /**
   * Writes double values
   * @param ptemp ArrayList of doubles or just one double value
   * @param os used DataOutputStream
   * @return false if there occurred an error, true if successful
   */
  private boolean writeDblValue(Object ptemp, DataOutputStream os) {
        try {
            if (ptemp instanceof ArrayList) {
                ArrayList<Double> list = (ArrayList<Double>) ptemp;
                os.writeInt(list.size());
                for (int p = 0; p < list.size(); p++)
                    os.writeDouble(list.get(p));
            } else if (ptemp instanceof Double) {
                os.writeInt(-1);
                os.writeDouble((Double)ptemp);
            }
            
            return true;
        } catch (IOException ex) {
                return false;
        }
    }
}
