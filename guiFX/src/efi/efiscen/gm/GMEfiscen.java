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

import efi.efiscen.com.ComArFlt;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
//import int_.efi.efiscen.io.*;

/**
 * Realization of the experiment; keeps a collection of matrixes, soils and
 * space of parameters. Takes care of the output.
 * 
 */
public class GMEfiscen implements Serializable {

    public int historyUpdateCounter = 0;//Added by Janne for GUI purposes
    
    public int m_ID; // Just id
    public String m_sName; // Name
    public int m_nBaseYear; // Base year
    public int m_nStep; // Size of step in years

    public GMBare m_BareFund;

    // Parameters locators
    public GMParLocator m_plAgeNum; // Number of age classes
    public GMParLocator m_plAgeClasses; // Age classes (X axis)
    public GMParLocator m_plVolNum; // Number of volume classes
    public GMParLocator m_plVolClasses; // Volume classes (Y axis)
    public GMParLocator m_plGrCoeff; // Coefficients for growing function
    public GMParLocator m_plAgeLims; // Age limits for volume series
    public GMParLocator m_plVolSers; // Volume series.
    public GMParLocator m_plYoungCoeff; // Afforestation paremeters.
    public GMParLocator m_plBeta; // Beta coefficient.
    public GMParLocator m_plRegrowCoeff; // Regrow after thinnings coeff.
    public GMParLocator m_plThHistory; // Beta coefficient
    public GMParLocator m_plCcont; // Carbon content in wood
    public GMParLocator m_plWoodDens; // Wood density
    public GMParLocator m_plCompXvals; // Arguments for compartments
    public GMParLocator m_plStemShare; // Stem share
    public GMParLocator m_plBranchShare; // Branches share
    public GMParLocator m_plCrootsShare; // Coarse roots share
    public GMParLocator m_plFrootsShare; // Fine roots share
    public GMParLocator m_plLeavesShare; // Leaves share
    // Added  January 2010
    public GMParLocator m_plCroots2CWL;  // Share of coarse roots going to
                                         // coarse woody litter.

    // Litter production
    public GMParLocator m_plLtrCompXvals; // Arguments for compartments
    public GMParLocator m_plLtrStemShare; // Stem share
    public GMParLocator m_plLtrBranchShare; // Branches share
    public GMParLocator m_plLtrCrootsShare; // Coarse roots share
    public GMParLocator m_plLtrFrootsShare; // Fine roots share
    public GMParLocator m_plLtrLeavesShare; // Leaves share

    public GMParLocator m_plHarvestAge; // Age of harvest
    public GMParLocator m_plThinRange; // Limits for ages of thinnings

    // Natural mortality stuff
    public GMParLocator m_plDeadWoodDrate; // Decay rate of deadwood
                                           // (exponential decay).
    public GMParLocator m_plMortRateXvals; // Assuming age dependence of natural
                                           // mortality rates.
    public GMParLocator m_plMortRate; // Mortality rates as a share of area
                                      // (may be not thinned?)

    // Main matrixes data
    public HashMap<Long,ComArFlt<Float>> m_mafArea;
    public HashMap<Long,ComArFlt<Float>> m_mafGrStock;
    public HashMap<Long,ComArFlt<Float>> m_mafIncrement;
    public HashMap<Long,ComArFlt<Float>> m_mafAvrIncrement;
    public HashMap<Long,ComArFlt<Float>> m_mafThinnings;
    public HashMap<Long,ComArFlt<Float>> m_mafFellings;
    public HashMap<Long,ComArFlt<Float>> m_mafBiomass;
    public HashMap<Long,ComArFlt<Float>> m_mafDeadWood;
    public HashMap<Long,ComArFlt<Float>> m_mafNatMort;
    
    // Affor fund added by Janne 2012 for storing afforfund historydata
    public HashMap<Long,ComArFlt<Float>> m_mafAfforFund;
    public HashMap<Long,ComArFlt<Float>> m_mafBareArea;
    public HashMap<Long,ComArFlt<Float>> m_mafPotentialFellingsArea;
    public HashMap<Long,ComArFlt<Float>> m_mafPotentialFellingsVolume;
    // Soils data
    public HashMap<Long,ComArFlt<Float>> m_mafSoilCwl;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilFwl;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilNwl;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilCel;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilSol;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilLig;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilHm1;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilHm2;
    public HashMap<Long,ComArFlt<Float>> m_mafSoilClost;
    public HashMap<Long,ComArFlt<Float>> m_mafCSoil;   // Total carbon stock in soil

    // Soil data - litter input. Added 12.07.2007 
    // 
    public HashMap<Long,ComArFlt<Double>> m_mafSoilCwlIn;
    public HashMap<Long,ComArFlt<Double>> m_mafSoilFwlIn;
    public HashMap<Long,ComArFlt<Double>> m_mafSoilNwlIn;
    // To keep stock changes due to affor/defor - August 2010
    public HashMap<Long,ComArFlt<Double>> m_mafSoilInOut; 

    public float m_FelInt;
    public float m_ThinInt;
    public boolean m_bIsStart;

    public HashMap<Long,GMMatrix> m_mTables;
    public HashMap<Long,GMCollection> m_mRegions;
    public HashMap<Long,GMCollection> m_mOwners;
    public HashMap<Long,GMCollection> m_mSites;
    public HashMap<Long,GMCollection> m_mSpecies;
    public HashMap<Long,GMSoil> m_mSoils;

    public ArrayList<Float> m_pDistrLims;

    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafAreas;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafStocks;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafCStem;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafCBranches;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafCLeaves;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafCCRoots;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafCFRoots;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafMfqThAreas;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafMfqThRems;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafMfqFelAreas;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafMfqFelRems;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafBeThSlash;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafBeFelSlash;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafThRsd;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafFelRsd;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafThRsdRem;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafFelRsdRem;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafNatMortDistr;
    public HashMap<Long,ComArFlt<ArrayList<Float>>> m_mafDeadWoodDistr;
    public HashMap<Long,Float> grsprev;

    // Growing stock and biomass data
    public ComArFlt<Float> m_afStock;
    public ComArFlt<Float> m_afCarbon;
    public ComArFlt<Double> m_afStem;
    public ComArFlt<Double> m_afBranches;
    public ComArFlt<Double> m_afLeaves;
    public ComArFlt<Double> m_afCroots;
    public ComArFlt<Double> m_afFroots;

    // Harvest data
    public ComArFlt<Float> m_afThinVolume;
    public ComArFlt<Float> m_afFellVolume;

    // Soil data
    public ComArFlt<Float> m_afSoilCwl;
    public ComArFlt<Float> m_afSoilFwl;
    public ComArFlt<Float> m_afSoilNwl;
    public ComArFlt<Float> m_afSoilCel;
    public ComArFlt<Float> m_afSoilSol;
    public ComArFlt<Float> m_afSoilLig;
    public ComArFlt<Float> m_afSoilHm1;
    public ComArFlt<Float> m_afSoilHm2;
    public ComArFlt<Float> m_afSoilClost;
    
    public boolean deferr = false;   //true if not enough area to perform deforestation
    
    /**
     * Default constructor.
     */
    public GMEfiscen () {
        m_BareFund = new GMBare();

        m_bIsStart = true;
        m_sName = "undefined";
        m_ID = 0;
        m_nStep = 5;
        m_FelInt = 1.0f;
        m_ThinInt = 1.0f;
        m_mTables = new HashMap<>();
        m_mRegions = new HashMap<>();
        m_mOwners = new HashMap<>();
        m_mSites = new HashMap<>();
        m_mSpecies = new HashMap<>();
        m_mSoils = new HashMap<>();
        grsprev = new HashMap<>();

        m_plAgeNum = new GMParLocator("Age class count");
        m_plAgeClasses  = new GMParLocator("Age classes");
        m_plVolNum = new GMParLocator("Volume class count");
        m_plVolClasses = new GMParLocator("Volume classes");
        m_plGrCoeff = new GMParLocator("Growth coefficients");
        m_plAgeLims = new GMParLocator("Age limits");
        m_plVolSers = new GMParLocator("Volume series");
        m_plYoungCoeff = new GMParLocator("Young coefficients");
        m_plBeta = new GMParLocator("Beta");
        m_plRegrowCoeff = new GMParLocator("Regrow coefficients");
        m_plThHistory = new GMParLocator("Thinning history");
        m_plCcont = new GMParLocator("m_plCcont");
        m_plWoodDens = new GMParLocator("Wood density");
        m_plCompXvals = new GMParLocator("CompXVals");
        m_plStemShare = new GMParLocator("Stem share");
        m_plBranchShare = new GMParLocator("Branch share");
        m_plCrootsShare = new GMParLocator("Coarse roots share");
        m_plFrootsShare = new GMParLocator("Fine roots share");
        m_plLeavesShare = new GMParLocator("Leaves share");
        m_plCroots2CWL = new GMParLocator("Coarse roots 2CWL");

        m_plLtrCompXvals = new GMParLocator("Litter CompXVals");
        m_plLtrStemShare = new GMParLocator("Litter stem share");
        m_plLtrBranchShare = new GMParLocator("Litter branch share");
        m_plLtrCrootsShare = new GMParLocator("Litter coarse roots share");
        m_plLtrFrootsShare = new GMParLocator("Litter fine roots share");
        m_plLtrLeavesShare = new GMParLocator("Litter leaves share");

        m_plHarvestAge = new GMParLocator("Harvest age");
        m_plThinRange = new GMParLocator("Thinning range");

        m_plDeadWoodDrate = new GMParLocator("Deadwood rate");
        m_plMortRateXvals = new GMParLocator("Mortality rate Xvals");
        m_plMortRate = new GMParLocator("Mortality rate");

        m_mafArea = new HashMap<>();
        m_mafGrStock = new HashMap<>();
        m_mafIncrement = new HashMap<>();
        m_mafAvrIncrement = new HashMap<>();
        m_mafThinnings = new HashMap<>();
        m_mafFellings = new HashMap<>();
        m_mafBiomass = new HashMap<>();
        m_mafDeadWood = new HashMap<>();
        m_mafNatMort = new HashMap<>();
        m_mafAfforFund = new HashMap<>();
        m_mafBareArea = new HashMap<>();
        m_mafPotentialFellingsArea = new HashMap<>();
        m_mafPotentialFellingsVolume = new HashMap<>();

        m_mafSoilCwl = new HashMap<>();
        m_mafSoilFwl = new HashMap<>();
        m_mafSoilNwl = new HashMap<>();
        m_mafSoilCel = new HashMap<>();
        m_mafSoilSol = new HashMap<>();
        m_mafSoilLig = new HashMap<>();
        m_mafSoilHm1 = new HashMap<>();
        m_mafSoilHm2 = new HashMap<>();
        m_mafSoilClost = new HashMap<>();
        m_mafCSoil = new HashMap<>();

        m_mafSoilCwlIn = new HashMap<>();
        m_mafSoilFwlIn = new HashMap<>();
        m_mafSoilNwlIn = new HashMap<>();
        m_mafSoilInOut = new HashMap<>();

        m_mafAreas = new HashMap<>();
        m_mafStocks = new HashMap<>();
        m_mafCStem = new HashMap<>();
        m_mafCBranches = new HashMap<>();
        m_mafCLeaves = new HashMap<>();
        m_mafCCRoots = new HashMap<>();
        m_mafCFRoots = new HashMap<>();
        m_mafMfqThAreas = new HashMap<>();
        m_mafMfqThRems = new HashMap<>();
        m_mafMfqFelAreas = new HashMap<>();
        m_mafMfqFelRems = new HashMap<>();
        m_mafBeThSlash = new HashMap<>();
        m_mafBeFelSlash = new HashMap<>();
        m_mafThRsd = new HashMap<>();
        m_mafFelRsd = new HashMap<>();
        m_mafThRsdRem = new HashMap<>();
        m_mafFelRsdRem = new HashMap<>();
        m_mafNatMortDistr = new HashMap<>();
        m_mafDeadWoodDistr = new HashMap<>();

        m_pDistrLims = new ArrayList<>(16);

        for (int i=0;i<16;i++)
            m_pDistrLims.add((float)((i+1)*10.0));

        m_afStock = new ComArFlt();
        m_afCarbon = new ComArFlt();
        m_afStem = new ComArFlt();
        m_afBranches = new ComArFlt();
        m_afLeaves = new ComArFlt();
        m_afCroots = new ComArFlt();
        m_afFroots = new ComArFlt();

        m_afThinVolume = new ComArFlt();
        m_afFellVolume = new ComArFlt();

        m_afSoilCwl = new ComArFlt();
        m_afSoilFwl = new ComArFlt();
        m_afSoilNwl = new ComArFlt();
        m_afSoilCel = new ComArFlt();
        m_afSoilSol = new ComArFlt();
        m_afSoilLig = new ComArFlt();
        m_afSoilHm1 = new ComArFlt();
        m_afSoilHm2 = new ComArFlt();
        m_afSoilClost = new ComArFlt();
    }

    /**
     * Parametrised constructor.
     * @param sname name
     * @param id id
     */
    public GMEfiscen (String sname, int id) {
        m_BareFund = new GMBare();

        m_bIsStart = true;
        m_sName = sname;
        m_ID = id;
        m_nStep = 5;
        m_FelInt = 1.0f;
        m_ThinInt = 1.0f;
        m_mTables = new HashMap<>();
        m_mRegions = new HashMap<>();
        m_mOwners = new HashMap<>();
        m_mSites = new HashMap<>();
        m_mSpecies = new HashMap<>();
        m_mSoils = new HashMap<>();

        m_plAgeNum = new GMParLocator("Age class count");
        m_plAgeClasses  = new GMParLocator("Age classes");
        m_plVolNum = new GMParLocator("Volume class count");
        m_plVolClasses = new GMParLocator("Volume classes");
        m_plGrCoeff = new GMParLocator("Growth coefficients");
        m_plAgeLims = new GMParLocator("Age limits");
        m_plVolSers = new GMParLocator("Volume series");
        m_plYoungCoeff = new GMParLocator("Young coefficients");
        m_plBeta = new GMParLocator("Beta");
        m_plRegrowCoeff = new GMParLocator("Regrow coefficients");
        m_plThHistory = new GMParLocator("Thinning history");
        m_plCcont = new GMParLocator("m_plCcont");
        m_plWoodDens = new GMParLocator("Wood density");
        m_plCompXvals = new GMParLocator("CompXVals");
        m_plStemShare = new GMParLocator("Stem share");
        m_plBranchShare = new GMParLocator("Branch share");
        m_plCrootsShare = new GMParLocator("Coarse roots share");
        m_plFrootsShare = new GMParLocator("Fine roots share");
        m_plLeavesShare = new GMParLocator("Leaves share");
        m_plCroots2CWL = new GMParLocator("Coarse roots 2CWL");

        m_plLtrCompXvals = new GMParLocator("Litter CompXVals");
        m_plLtrStemShare = new GMParLocator("Litter stem share");
        m_plLtrBranchShare = new GMParLocator("Litter branch share");
        m_plLtrCrootsShare = new GMParLocator("Litter coarse roots share");
        m_plLtrFrootsShare = new GMParLocator("Litter fine roots share");
        m_plLtrLeavesShare = new GMParLocator("Litter leaves share");

        m_plHarvestAge = new GMParLocator("Harvest age");
        m_plThinRange = new GMParLocator("Thinning range");

        m_plDeadWoodDrate = new GMParLocator("Deadwood rate");
        m_plMortRateXvals = new GMParLocator("Mortality rate Xvals");
        m_plMortRate = new GMParLocator("Mortality rate");
        grsprev = new HashMap<>();

        m_mafArea = new HashMap<>();
        m_mafGrStock = new HashMap<>();
        m_mafIncrement = new HashMap<>();
        m_mafAvrIncrement = new HashMap<>();
        m_mafThinnings = new HashMap<>();
        m_mafFellings = new HashMap<>();
        m_mafBiomass = new HashMap<>();
        m_mafDeadWood = new HashMap<>();
        m_mafNatMort = new HashMap<>();
        m_mafAfforFund = new HashMap<>();
        m_mafBareArea = new HashMap<>();
        m_mafPotentialFellingsArea = new HashMap<>();
        m_mafPotentialFellingsVolume = new HashMap<>();

        m_mafSoilCwl = new HashMap<>();
        m_mafSoilFwl = new HashMap<>();
        m_mafSoilNwl = new HashMap<>();
        m_mafSoilCel = new HashMap<>();
        m_mafSoilSol = new HashMap<>();
        m_mafSoilLig = new HashMap<>();
        m_mafSoilHm1 = new HashMap<>();
        m_mafSoilHm2 = new HashMap<>();
        m_mafSoilClost = new HashMap<>();
        m_mafCSoil = new HashMap<>();

        m_mafSoilCwlIn = new HashMap<>();
        m_mafSoilFwlIn = new HashMap<>();
        m_mafSoilNwlIn = new HashMap<>();
        m_mafSoilInOut = new HashMap<>();

        m_mafAreas = new HashMap<>();
        m_mafStocks = new HashMap<>();
        m_mafCStem = new HashMap<>();
        m_mafCBranches = new HashMap<>();
        m_mafCLeaves = new HashMap<>();
        m_mafCCRoots = new HashMap<>();
        m_mafCFRoots = new HashMap<>();
        m_mafMfqThAreas = new HashMap<>();
        m_mafMfqThRems = new HashMap<>();
        m_mafMfqFelAreas = new HashMap<>();
        m_mafMfqFelRems = new HashMap<>();
        m_mafBeThSlash = new HashMap<>();
        m_mafBeFelSlash = new HashMap<>();
        m_mafThRsd = new HashMap<>();
        m_mafFelRsd = new HashMap<>();
        m_mafThRsdRem = new HashMap<>();
        m_mafFelRsdRem = new HashMap<>();
        m_mafNatMortDistr = new HashMap<>();
        m_mafDeadWoodDistr = new HashMap<>();

        m_pDistrLims = new ArrayList<>(16);

        for (int i=0;i<16;i++)
            m_pDistrLims.add((float)((i+1)*10.0));

        m_afStock = new ComArFlt();
        m_afCarbon = new ComArFlt();
        m_afStem = new ComArFlt();
        m_afBranches = new ComArFlt();
        m_afLeaves = new ComArFlt();
        m_afCroots = new ComArFlt();
        m_afFroots = new ComArFlt();

        m_afThinVolume = new ComArFlt();
        m_afFellVolume = new ComArFlt();

        m_afSoilCwl = new ComArFlt();
        m_afSoilFwl = new ComArFlt();
        m_afSoilNwl = new ComArFlt();
        m_afSoilCel = new ComArFlt();
        m_afSoilSol = new ComArFlt();
        m_afSoilLig = new ComArFlt();
        m_afSoilHm1 = new ComArFlt();
        m_afSoilHm2 = new ComArFlt();
        m_afSoilClost = new ComArFlt();
    }

    /**
     * Summarize values
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param maSrc source
     * @param ind index
     * @return summarized value
     */
    public float summarize (long lr, long lo, long lst, long lsp, HashMap<Long,ComArFlt<Float>> maSrc,
            int ind) {
        float retval = 0;
        long ulKey;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        ComArFlt<Float> pVector;
        for (Long uKey : maSrc.keySet())
        {
            pVector = maSrc.get(uKey);

            if (ind > pVector.getSize())
                return retval;

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                Float data = pVector.getData(ind);
                if (data != null)
                    retval+=data;
                }
            }
        return retval;
    }
    
    /**
     * Returns summarized first elements.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param maSrc source
     * @param ind index
     * @return summarized first elements
     */
    public float summarizeArrays (long lr, long lo, long lst, long lsp,
            HashMap<Long,ComArFlt<ArrayList<Float>>> maSrc, int ind) {
        float retval = 0;
        long ulKey;
        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        ComArFlt<ArrayList<Float>> pVector;
        for (Long uKey : maSrc.keySet())
        {
            pVector = maSrc.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                ArrayList<Float> pdest = pVector.getData(ind);
                if(pdest!=null)
                    retval += pdest.get(0);
            }
        }

        return retval;
    }
    
    /**
     * Returns summarized first elements.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param maSrc source
     * @param ind index
     * @return summarized first elements
     */
    public float summarizeArraysTotal (long lr, long lo, long lst, long lsp,
            HashMap<Long,ComArFlt<ArrayList<Float>>> maSrc, int ind) {
        ArrayList<Float> out = new ArrayList<>();
        float retval = 0;
        long ulKey;
        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        ComArFlt<ArrayList<Float>> pVector;
        for (Long uKey : maSrc.keySet())
        {
            pVector = maSrc.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                ArrayList<Float> pdest = pVector.getData(ind);
                if(pdest!=null)
                    for(float fl : pdest)
                        retval += fl;
            }
        }

        return retval;
    }

    /**
     * Update all the history data.
     * @return 1 if succesful
     */
    public int historyUpdateCount() {
        return historyUpdateCounter;
    }
    
    /**
     * Updates potential final felling area and volume after changing intensity
     * 
     */
    public void updatePotentialFellings() {
        ComArFlt<Float> pComar;
        for (Long uKey : m_mTables.keySet()) {
            GMFellings pFl = new GMFellings();
            pFl.setF_volume(0.0);
            pFl.setF_ratio(1.0);
            if (pFl.getF_stem() == 0)
                pFl.setF_stem(0.95);
            pFl = m_mTables.get(uKey).reportHarvest(pFl);
            pComar = m_mafPotentialFellingsArea.get(uKey);
            if (pComar != null) {
                if(pComar.getSize()>0) 
                    pComar.setData(pComar.getSize()-1, (float)pFl.getF_area()*m_FelInt);
                else pComar.setData(0, (float)pFl.getF_area() * m_FelInt);
                m_mafPotentialFellingsArea.put(uKey, pComar);
            }
            pComar = m_mafPotentialFellingsVolume.get(uKey);
            if (pComar != null) {
                if(pComar.getSize()>0) 
                    pComar.setData(pComar.getSize()-1,(float)pFl.getF_volume()*m_FelInt);
                else pComar.setData(0, (float)pFl.getF_volume() * m_FelInt);
                m_mafPotentialFellingsVolume.put(uKey, pComar);
            }
        }
    }
    
    public int updateHistory () throws GMParLocator.GMParLocatorException {
        historyUpdateCounter+=1;
        float grstock, val;
        grstock = 0.0f;
        GMMatrix pTable;
        ComArFlt<Float> pComarFloat;
        ComArFlt<Double> pComarDouble;
        GMCarbonAlloc pCAl;
        GMSoil pSl;
        //float grsprev = 0;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            val = pTable.getValue();
            pComarFloat = m_mafGrStock.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(val);
                m_mafGrStock.put(uKey, pComarFloat);
            }
            grstock+=val;
            val = pTable.getArea();
            pComarFloat = m_mafArea.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(val);
                m_mafArea.put(uKey, pComarFloat);
            }
            val = pTable.getIncrement();
            val = val/m_nStep;
            pComarFloat = m_mafIncrement.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(val);
                m_mafIncrement.put(uKey, pComarFloat);
            }
            // Deadwood
            pComarFloat = m_mafDeadWood.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(pTable.m_DeadWood);
                m_mafDeadWood.put(uKey, pComarFloat);
            }
            
            // Afforfund added by Janne 2012
            pComarFloat = m_mafAfforFund.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(pTable.m_BareArea);
                m_mafAfforFund.put(uKey, pComarFloat);
            }
            
            pComarFloat = m_mafBareArea.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(m_BareFund.getFund(uKey));
                m_mafBareArea.put(uKey, pComarFloat);
            }
     
            GMFellings pFl = new GMFellings();
            pFl.setF_volume(0.0);
            pFl.setF_ratio(1.0);
            if (pFl.getF_stem() == 0)
                pFl.setF_stem(0.95);
            pFl = pTable.reportHarvest(pFl);
            pComarFloat = m_mafPotentialFellingsArea.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData((float)pFl.getF_area() * m_FelInt);
                m_mafPotentialFellingsArea.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafPotentialFellingsVolume.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData((float)pFl.getF_volume() * m_FelInt);
                m_mafPotentialFellingsVolume.put(uKey, pComarFloat);
            }
            
            pCAl = new GMCarbonAlloc();
            pCAl.setCa_cstem(0.0);
            pCAl.setCa_cbranch(0.0);
            pCAl.setCa_ccroots(0.0);
            pCAl.setCa_cfroots(0.0);
            pCAl.setCa_cleaves(0.0);
            pCAl.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pCAl.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pCAl.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pCAl.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pCAl.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);

            //val = pTable.getBiomass(pCAl);
            //pComar = m_mafBiomass.get(uKey);
            //if (pComar != null)
            //	pComar.addData(val);

            // Compartments
            ArrayList<Float> pvalSt = new ArrayList<>(16);
            for (int i=0;i<16;i++) 
                pvalSt.add(0.0f);
            ArrayList<Float> pvalLv = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalLv.add(0.0f);
            ArrayList<Float> pvalBr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalBr.add(0.0f);
            ArrayList<Float> pvalCr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalCr.add(0.0f);
            ArrayList<Float> pvalFr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFr.add(0.0f);

            val = pTable.getBiomassDistr(pCAl,m_pDistrLims,pvalSt,pvalBr,pvalLv,pvalCr,pvalFr,16);

            pComarFloat = m_mafBiomass.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(val);
                m_mafBiomass.put(uKey, pComarFloat);
            }

            ComArFlt<ArrayList<Float>> pPComar;

            pPComar = m_mafCStem.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalSt);
                m_mafCStem.put(uKey, pPComar);
            }
            pPComar = m_mafCLeaves.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalLv);
                m_mafCLeaves.put(uKey, pPComar);
            }
            pPComar = m_mafCBranches.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalBr);
                m_mafCBranches.put(uKey, pPComar);
            }
            pPComar = m_mafCCRoots.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalCr);
                m_mafCCRoots.put(uKey, pPComar);
            }
            pPComar = m_mafCFRoots.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFr);
                m_mafCFRoots.put(uKey, pPComar);
            }

            // Areas and stocks distribution
            ArrayList<Float> pval = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pval.add(0.0f);
            pval = pTable.getAreaDistr(m_pDistrLims,pval,16);

            pPComar = m_mafAreas.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pval);
                m_mafAreas.put(uKey, pPComar);
            }
            ArrayList<Float> pvalStocks = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalStocks.add(0.0f);
            pvalStocks = pTable.getStockDistr(m_pDistrLims,pvalStocks,16);
            pPComar = m_mafStocks.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalStocks);
                m_mafStocks.put(uKey, pPComar);
            }

            // Mefique stuff!
            ArrayList<Float> pvalTha = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalTha.add(0.0f);
            ArrayList<Float> pvalThr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalThr.add(0.0f);
            ArrayList<Float> pvalFla = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFla.add(0.0f);
            ArrayList<Float> pvalFlr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFlr.add(0.0f);
            pTable.getMefiqueDistr(m_pDistrLims,pvalTha,pvalThr,pvalFla,pvalFlr,16);
            pPComar = m_mafMfqThAreas.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalTha);
                m_mafMfqThAreas.put(uKey, pPComar);
            }
            pPComar = m_mafMfqThRems.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThr);
                m_mafMfqThRems.put(uKey, pPComar);
            }
            pPComar = m_mafMfqFelAreas.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFla);
                m_mafMfqFelAreas.put(uKey, pPComar);
            }
            pPComar = m_mafMfqFelRems.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlr);
                m_mafMfqFelRems.put(uKey, pPComar);
            }
            // End Mefique!
            // Bioenergy stuff
            ArrayList<Float> pvalThSl = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalThSl.add(0.0f);
            ArrayList<Float> pvalFlSl = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFlSl.add(0.0f);
            pTable.getSlashDistr(m_pDistrLims,pvalThSl,pvalFlSl,16);
            pPComar = m_mafBeThSlash.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThSl);
                m_mafBeThSlash.put(uKey, pPComar);
            }
            pPComar = m_mafBeFelSlash.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlSl);
                m_mafBeFelSlash.put(uKey, pPComar);
            }
            // End bioenegy
            // Natural mortality 
            ArrayList<Float> pvalnm = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalnm.add(0.0f);
            pvalnm = pTable.getMortDistr(m_pDistrLims,pvalnm,16);

            pPComar = m_mafNatMortDistr.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalnm);
                m_mafNatMortDistr.put(uKey, pPComar);
            }
            ArrayList<Float> pvaldw = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvaldw.add(0.0f);

            pTable.getDwoodDistrFromPipe(m_pDistrLims,pvaldw,16);
            //for (int i=0;i<16;i++)
            //    pvaldw.add(0.0f);
            //pTable.getDwoodDistr(m_pDistrLims,pvaldw,16);

            pPComar = m_mafDeadWoodDistr.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvaldw);
                m_mafDeadWoodDistr.put(uKey, pPComar);
            }
            pComarFloat = m_mafAvrIncrement.get(uKey);
            if (pComarFloat != null) {
                float ccont = 0;
                float dens = 0;
                ccont = m_plCcont.getParameterValue(uKey,0);
                dens  = m_plWoodDens.getParameterValue(uKey,0);
                if (ccont==0.0) {
                    ccont = 0.5f;
                }
                if (dens==0.0) {
                    dens = 0.45f;
                }
                float cfactor = ccont*dens;
                //float grsprev = 0;
                Object ptemp;
                int i = this.historyUpdateCounter-1;
                //grs = pComar.getData(i);
                long ur = pTable.getRegionID();
                long uo = pTable.getOwnerID();
                long ust = pTable.getSiteID();
                long usp = pTable.getSpeciesID();

                //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                float hrvthin = 0;
                float hrvfel = 0;
                float hrvthinres = 0;
                float hrvfelres = 0;
                float grincr = 0;
                float grsav = 0;
                float felav = 0;
                float dwood = 0;
                float nmort = 0;
                float grs = summarize(ur,uo,ust,usp,m_mafGrStock,i);
                float garea = summarize(ur,uo,ust,usp,m_mafArea,i);
                ComArFlt<Float> pComarDW = m_mafDeadWood.get(uKey);
                ComArFlt<Float> pComarNM = m_mafNatMort.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarThRes = m_mafThRsd.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarFelRes = m_mafFelRsd.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarThResRem = m_mafThRsdRem.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarFelResRem = m_mafFelRsdRem.get(uKey);
                dwood = (Float)pComarDW.getData(i);
                if (i>0) {
                    nmort = (Float)pComarNM.getData(i-1);
                    hrvthin = summarize(ur,uo,ust,usp,m_mafThinnings,i-1);
                    hrvfel = summarize(ur,uo,ust,usp,m_mafFellings,i-1);
                    hrvthinres = pPComarThRes.getData(i-1).get(0);
                    hrvfelres = pPComarFelRes.getData(i-1).get(0);
                    // Adding topwood removals ...
                    hrvthinres += pPComarThResRem.getData(i-1).get(0); 
                    hrvfelres += pPComarFelResRem.getData(i-1).get(0);

                    float prev = grsprev.get(uKey);
                    grincr = (grs - prev + hrvthin + hrvfel + nmort
                            + hrvthinres/cfactor + hrvfelres/cfactor);

                }
                if (garea > 0) {
                    grsav = grs/garea;
                    felav = (hrvthin+hrvfel)/(garea*m_nStep);
                    grincr/=garea*m_nStep;
                }
                grsprev.put(uKey, grs);
                pComarFloat.addData(grincr);
                m_mafAvrIncrement.put(uKey, pComarFloat);
            }
        }
        m_afStock.addData(grstock);
        // Carbon history update
        GMCarbonAlloc pCarAl = new GMCarbonAlloc();
        pCarAl.setCa_cstem(0.0);
        pCarAl.setCa_cbranch(0.0);
        pCarAl.setCa_ccroots(0.0);
        pCarAl.setCa_cfroots(0.0);
        pCarAl.setCa_cleaves(0.0);

        float bioms = getCarbon(0,0,0,0,pCarAl);
        m_afCarbon.addData(bioms);
        m_afStem.addData(pCarAl.getCa_cstem());
        m_afLeaves.addData(pCarAl.getCa_cleaves());
        m_afBranches.addData(pCarAl.getCa_cbranch());
        m_afCroots.addData(pCarAl.getCa_ccroots());
        m_afFroots.addData(pCarAl.getCa_cfroots());

        // Soils update - general
        GMSoilComp scStock = new GMSoilComp();
        scStock.setSc_cwl(0.0f);
        scStock.setSc_fwl(0.0f);
        scStock.setSc_nwl(0.0f);
        scStock.setSc_sol(0.0f);
        scStock.setSc_cel(0.0f);
        scStock.setSc_lig(0.0f);
        scStock.setSc_hm1(0.0f);
        scStock.setSc_hm2(0.0f);
        scStock.setSc_clost(0.0f);
        scStock = getAllSoils(scStock);
        m_afSoilCwl.addData(scStock.getSc_cwl());
        m_afSoilFwl.addData(scStock.getSc_fwl());
        m_afSoilNwl.addData(scStock.getSc_nwl());
        m_afSoilSol.addData(scStock.getSc_sol());
        m_afSoilCel.addData(scStock.getSc_cel());
        m_afSoilLig.addData(scStock.getSc_lig());
        m_afSoilHm1.addData(scStock.getSc_hm1());
        m_afSoilHm2.addData(scStock.getSc_hm2());
        m_afSoilClost.addData(scStock.getSc_clost());
        // Soils update - main
        for (Long uKey : m_mSoils.keySet())
        {
            pSl = m_mSoils.get(uKey);
            scStock.setSc_cwl(0.0f);
            scStock.setSc_fwl(0.0f);
            scStock.setSc_nwl(0.0f);
            scStock.setSc_sol(0.0f);
            scStock.setSc_cel(0.0f);
            scStock.setSc_lig(0.0f);
            scStock.setSc_hm1(0.0f);
            scStock.setSc_hm2(0.0f);
            scStock.setSc_clost(0.0f);
            scStock = pSl.reportStocks(scStock);

            pComarFloat = m_mafSoilCwl.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_cwl());
                m_mafSoilCwl.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilFwl.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_fwl());
                m_mafSoilFwl.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilNwl.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_nwl());
                m_mafSoilNwl.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilSol.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_sol());
                m_mafSoilSol.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilCel.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_cel());
                m_mafSoilCel.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilLig.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_lig());
                m_mafSoilLig.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilHm1.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_hm1());
                m_mafSoilHm1.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilHm2.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_hm2());
                m_mafSoilHm2.put(uKey, pComarFloat);
            }
            pComarFloat = m_mafSoilClost.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(scStock.getSc_clost());
                m_mafSoilClost.put(uKey, pComarFloat);
            }
            pComarDouble = m_mafSoilCwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.addData(pSl.m_CwBasket);
                m_mafSoilCwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilFwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.addData(pSl.m_FwBasket);
                m_mafSoilFwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilNwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.addData(pSl.m_NwBasket);
                m_mafSoilNwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilInOut.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.addData(pSl.getInOut());
                m_mafSoilInOut.put(uKey, pComarDouble);
            }
            float ctotal = scStock.getSc_cwl() + scStock.getSc_cel() +
                    scStock.getSc_fwl() + scStock.getSc_hm1() +
                    scStock.getSc_hm2() + scStock.getSc_lig() +
                    scStock.getSc_nwl() + scStock.getSc_sol();
            pComarFloat = m_mafCSoil.get(uKey);
            if (pComarFloat != null) {
                pComarFloat.addData(ctotal);
                m_mafCSoil.put(uKey, pComarFloat);
            }
            pSl.setInOut(0.0);
            pSl.m_CwBasket = 0.0;
            pSl.m_FwBasket = 0.0;
            pSl.m_NwBasket = 0.0;
        }
        return 1;
    }
    /**
     * Reset history. For use in case something is changing after 
     * loading. Currently after matrices scaling
     * @return 1 if success
     */
       public int resetHistory () throws GMParLocator.GMParLocatorException {
        //if we are not in step zero do nothing
        if (historyUpdateCounter>1)
            return 0;
        float grstock, val;
        grstock = 0.0f;
        GMMatrix pTable;
        ComArFlt<Float> pComar;
        ComArFlt<Double> pComarDouble;
        GMCarbonAlloc pCAl;
        GMSoil pSl;
        //float grsprev = 0;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            val = pTable.getValue();
            pComar = m_mafGrStock.get(uKey);
            if (pComar != null) {
                pComar.setData(0,val);
                m_mafGrStock.put(uKey, pComar);
            }
            grstock+=val;
            val = pTable.getArea();
            pComar = m_mafArea.get(uKey);
            if (pComar != null) {
                pComar.setData(0,val);
                m_mafArea.put(uKey, pComar);
            }
            val = pTable.getIncrement();
            val = val/m_nStep;
            pComar = m_mafIncrement.get(uKey);
            if (pComar != null) {
                pComar.setData(0,val);
                m_mafIncrement.put(uKey, pComar);
            }
            // Deadwood
            pComar = m_mafDeadWood.get(uKey);
            if (pComar != null) {
                pComar.setData(0,pTable.m_DeadWood);
                m_mafDeadWood.put(uKey, pComar);
            }
            
            // Afforfund added 2012
            pComar = m_mafAfforFund.get(uKey);
            if (pComar != null) {
                pComar.setData(0,pTable.m_BareArea);
                m_mafAfforFund.put(uKey, pComar);
            }
            
            pComar = m_mafBareArea.get(uKey);
            if (pComar != null) {
                pComar.setData(0,m_BareFund.getFund(uKey));
                m_mafBareArea.put(uKey, pComar);
            }
     
            GMFellings pFl = new GMFellings();
            pFl.setF_volume(0.0);
            pFl.setF_ratio(1.0);
            if (pFl.getF_stem() == 0)
                pFl.setF_stem(0.95);
            pFl = pTable.reportHarvest(pFl);
            pComar = m_mafPotentialFellingsArea.get(uKey);
            if (pComar != null) {
                pComar.setData(0,(float)pFl.getF_area() * m_FelInt);
                m_mafPotentialFellingsArea.put(uKey, pComar);
            }
            pComar = m_mafPotentialFellingsVolume.get(uKey);
            if (pComar != null) {
                pComar.setData(0,(float)pFl.getF_volume() * m_FelInt);
                m_mafPotentialFellingsVolume.put(uKey, pComar);
            }
            
            pCAl = new GMCarbonAlloc();
            pCAl.setCa_cstem(0.0);
            pCAl.setCa_cbranch(0.0);
            pCAl.setCa_ccroots(0.0);
            pCAl.setCa_cfroots(0.0);
            pCAl.setCa_cleaves(0.0);
            pCAl.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pCAl.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pCAl.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pCAl.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pCAl.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pCAl.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);

            //val = pTable.getBiomass(pCAl);
            //pComar = m_mafBiomass.get(uKey);
            //if (pComar != null)
            //	pComar.addData(val);

            // Compartments
            ArrayList<Float> pvalSt = new ArrayList<>(16);
            for (int i=0;i<16;i++) 
                pvalSt.add(0.0f);
            ArrayList<Float> pvalLv = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalLv.add(0.0f);
            ArrayList<Float> pvalBr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalBr.add(0.0f);
            ArrayList<Float> pvalCr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalCr.add(0.0f);
            ArrayList<Float> pvalFr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFr.add(0.0f);

            val = pTable.getBiomassDistr(pCAl,m_pDistrLims,pvalSt,pvalBr,pvalLv,pvalCr,pvalFr,16);

            pComar = m_mafBiomass.get(uKey);
            if (pComar != null) {
                pComar.setData(0,val);
                m_mafBiomass.put(uKey, pComar);
            }

            ComArFlt<ArrayList<Float>> pPComar;

            pPComar = m_mafCStem.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalSt);
                m_mafCStem.put(uKey, pPComar);
            }
            pPComar = m_mafCLeaves.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalLv);
                m_mafCLeaves.put(uKey, pPComar);
            }
            pPComar = m_mafCBranches.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalBr);
                m_mafCBranches.put(uKey, pPComar);
            }
            pPComar = m_mafCCRoots.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalCr);
                m_mafCCRoots.put(uKey, pPComar);
            }
            pPComar = m_mafCFRoots.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalFr);
                m_mafCFRoots.put(uKey, pPComar);
            }

            // Areas and stocks distribution
            ArrayList<Float> pval = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pval.add(0.0f);
            pval = pTable.getAreaDistr(m_pDistrLims,pval,16);

            pPComar = m_mafAreas.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pval);
                m_mafAreas.put(uKey, pPComar);
            }
            ArrayList<Float> pvalStocks = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalStocks.add(0.0f);
            pvalStocks = pTable.getStockDistr(m_pDistrLims,pvalStocks,16);
            pPComar = m_mafStocks.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalStocks);
                m_mafStocks.put(uKey, pPComar);
            }

            // Mefique stuff!
            ArrayList<Float> pvalTha = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalTha.add(0.0f);
            ArrayList<Float> pvalThr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalThr.add(0.0f);
            ArrayList<Float> pvalFla = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFla.add(0.0f);
            ArrayList<Float> pvalFlr = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFlr.add(0.0f);
            pTable.getMefiqueDistr(m_pDistrLims,pvalTha,pvalThr,pvalFla,pvalFlr,16);
            pPComar = m_mafMfqThAreas.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalTha);
                m_mafMfqThAreas.put(uKey, pPComar);
            }
            pPComar = m_mafMfqThRems.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalThr);
                m_mafMfqThRems.put(uKey, pPComar);
            }
            pPComar = m_mafMfqFelAreas.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalFla);
                m_mafMfqFelAreas.put(uKey, pPComar);
            }
            pPComar = m_mafMfqFelRems.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalFlr);
                m_mafMfqFelRems.put(uKey, pPComar);
            }
            // End Mefique!
            // Bioenergy stuff
            ArrayList<Float> pvalThSl = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalThSl.add(0.0f);
            ArrayList<Float> pvalFlSl = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalFlSl.add(0.0f);
            pTable.getSlashDistr(m_pDistrLims,pvalThSl,pvalFlSl,16);
            pPComar = m_mafBeThSlash.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalThSl);
                m_mafBeThSlash.put(uKey, pPComar);
            }
            pPComar = m_mafBeFelSlash.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalFlSl);
                m_mafBeFelSlash.put(uKey, pPComar);
            }
            // End bioenegy
            // Natural mortality 
            ArrayList<Float> pvalnm = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvalnm.add(0.0f);
            pvalnm = pTable.getMortDistr(m_pDistrLims,pvalnm,16);

            pPComar = m_mafNatMortDistr.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvalnm);
                m_mafNatMortDistr.put(uKey, pPComar);
            }
            ArrayList<Float> pvaldw = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvaldw.add(0.0f);

            pTable.getDwoodDistrFromPipe(m_pDistrLims,pvaldw,16);
            //for (int i=0;i<16;i++)
            //    pvaldw.add(0.0f);
            //pTable.getDwoodDistr(m_pDistrLims,pvaldw,16);

            pPComar = m_mafDeadWoodDistr.get(uKey);
            if (pPComar != null) {
                pPComar.setData(0,pvaldw);
                m_mafDeadWoodDistr.put(uKey, pPComar);
            }
            pComar = m_mafAvrIncrement.get(uKey);
            if (pComar != null) {
                float ccont = 0;
                float dens = 0;
                ccont = m_plCcont.getParameterValue(uKey,0);
                dens  = m_plWoodDens.getParameterValue(uKey,0);
                if (ccont==0.0) {
                    ccont = 0.5f;
                }
                if (dens==0.0) {
                    dens = 0.45f;
                }
                float cfactor = ccont*dens;
                //float grsprev = 0;
                Object ptemp;
                int i = this.historyUpdateCounter-1;
                //grs = pComar.getData(i);
                long ur = pTable.getRegionID();
                long uo = pTable.getOwnerID();
                long ust = pTable.getSiteID();
                long usp = pTable.getSpeciesID();

                //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                float hrvthin = 0;
                float hrvfel = 0;
                float hrvthinres = 0;
                float hrvfelres = 0;
                float grincr = 0;
                float grsav = 0;
                float felav = 0;
                float dwood = 0;
                float nmort = 0;
                float grs = summarize(ur,uo,ust,usp,m_mafGrStock,i);
                float garea = summarize(ur,uo,ust,usp,m_mafArea,i);
                ComArFlt<Float> pComarDW = m_mafDeadWood.get(uKey);
                ComArFlt<Float> pComarNM = m_mafNatMort.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarThRes = m_mafThRsd.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarFelRes = m_mafFelRsd.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarThResRem = m_mafThRsdRem.get(uKey);
                ComArFlt<ArrayList<Float>> pPComarFelResRem = m_mafFelRsdRem.get(uKey);
                dwood = pComarDW.getData(i);
                if (i>0) {
                    nmort = pComarNM.getData(i-1);
                    hrvthin = summarize(ur,uo,ust,usp,m_mafThinnings,i-1);
                    hrvfel = summarize(ur,uo,ust,usp,m_mafFellings,i-1);
                    ArrayList<Float> arrayTemp = pPComarThRes.getData(i-1);
                    hrvthinres = arrayTemp.get(0);
                    arrayTemp = pPComarFelRes.getData(i-1);
                    hrvfelres = arrayTemp.get(0);
                    // Adding topwood removals ...
                    arrayTemp = pPComarThResRem.getData(i-1);
                    hrvthinres += arrayTemp.get(0);
                    arrayTemp = pPComarFelResRem.getData(i-1);
                    hrvfelres += arrayTemp.get(0);

                    float prev = grsprev.get(uKey);
                    grincr = (grs - prev + hrvthin + hrvfel + nmort
                            + hrvthinres/cfactor + hrvfelres/cfactor);

                }
                if (garea > 0) {
                    grsav = grs/garea;
                    felav = (hrvthin+hrvfel)/(garea*m_nStep);
                    grincr/=garea*m_nStep;
                }
                grsprev.put(uKey, grs);
                pComar.setData(0,grincr);
                m_mafAvrIncrement.put(uKey, pComar);
            }
        }
        m_afStock.setData(0,grstock);
        // Carbon history update
        GMCarbonAlloc pCarAl = new GMCarbonAlloc();
        pCarAl.setCa_cstem(0.0);
        pCarAl.setCa_cbranch(0.0);
        pCarAl.setCa_ccroots(0.0);
        pCarAl.setCa_cfroots(0.0);
        pCarAl.setCa_cleaves(0.0);

        float bioms = getCarbon(0,0,0,0,pCarAl);
        m_afCarbon.setData(0,bioms);
        m_afStem.setData(0,pCarAl.getCa_cstem());
        m_afLeaves.setData(0,pCarAl.getCa_cleaves());
        m_afBranches.setData(0,pCarAl.getCa_cbranch());
        m_afCroots.setData(0,pCarAl.getCa_ccroots());
        m_afFroots.setData(0,pCarAl.getCa_cfroots());

        // Soils update - general
        GMSoilComp scStock = new GMSoilComp();
        scStock.setSc_cwl(0.0f);
        scStock.setSc_fwl(0.0f);
        scStock.setSc_nwl(0.0f);
        scStock.setSc_sol(0.0f);
        scStock.setSc_cel(0.0f);
        scStock.setSc_lig(0.0f);
        scStock.setSc_hm1(0.0f);
        scStock.setSc_hm2(0.0f);
        scStock.setSc_clost(0.0f);
        scStock = getAllSoils(scStock);
        m_afSoilCwl.setData(0,scStock.getSc_cwl());
        m_afSoilFwl.setData(0,scStock.getSc_fwl());
        m_afSoilNwl.setData(0,scStock.getSc_nwl());
        m_afSoilSol.setData(0,scStock.getSc_sol());
        m_afSoilCel.setData(0,scStock.getSc_cel());
        m_afSoilLig.setData(0,scStock.getSc_lig());
        m_afSoilHm1.setData(0,scStock.getSc_hm1());
        m_afSoilHm2.setData(0,scStock.getSc_hm2());
        m_afSoilClost.setData(0,scStock.getSc_clost());
        // Soils update - main
        for (Long uKey : m_mSoils.keySet())
        {
            pSl = m_mSoils.get(uKey);
            scStock.setSc_cwl(0.0f);
            scStock.setSc_fwl(0.0f);
            scStock.setSc_nwl(0.0f);
            scStock.setSc_sol(0.0f);
            scStock.setSc_cel(0.0f);
            scStock.setSc_lig(0.0f);
            scStock.setSc_hm1(0.0f);
            scStock.setSc_hm2(0.0f);
            scStock.setSc_clost(0.0f);
            scStock = pSl.reportStocks(scStock);

            pComar = m_mafSoilCwl.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_cwl());
                m_mafSoilCwl.put(uKey, pComar);
            }
            pComar = m_mafSoilFwl.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_fwl());
                m_mafSoilFwl.put(uKey, pComar);
            }
            pComar = m_mafSoilNwl.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_nwl());
                m_mafSoilNwl.put(uKey, pComar);
            }
            pComar = m_mafSoilSol.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_sol());
                m_mafSoilSol.put(uKey, pComar);
            }
            pComar = m_mafSoilCel.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_cel());
                m_mafSoilCel.put(uKey, pComar);
            }
            pComar = m_mafSoilLig.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_lig());
                m_mafSoilLig.put(uKey, pComar);
            }
            pComar = m_mafSoilHm1.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_hm1());
                m_mafSoilHm1.put(uKey, pComar);
            }
            pComar = m_mafSoilHm2.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_hm2());
                m_mafSoilHm2.put(uKey, pComar);
            }
            pComar = m_mafSoilClost.get(uKey);
            if (pComar != null) {
                pComar.setData(0,scStock.getSc_clost());
                m_mafSoilClost.put(uKey, pComar);
            }
            pComarDouble = m_mafSoilCwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.setData(0,pSl.m_CwBasket);
                m_mafSoilCwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilFwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.setData(0,pSl.m_FwBasket);
                m_mafSoilFwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilNwlIn.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.setData(0,pSl.m_NwBasket);
                m_mafSoilNwlIn.put(uKey, pComarDouble);
            }
            pComarDouble = m_mafSoilInOut.get(uKey);
            if (pComarDouble != null) {
                pComarDouble.setData(0,pSl.getInOut());
                m_mafSoilInOut.put(uKey, pComarDouble);
            }
            float ctotal = scStock.getSc_cwl() + scStock.getSc_cel() +
                    scStock.getSc_fwl() + scStock.getSc_hm1() +
                    scStock.getSc_hm2() + scStock.getSc_lig() +
                    scStock.getSc_nwl() + scStock.getSc_sol();
            pComar = m_mafCSoil.get(uKey);
            if (pComar != null) {
                pComar.setData(0,ctotal);
                m_mafCSoil.put(uKey, pComar);
            }
            pSl.setInOut(0.0);
            pSl.m_CwBasket = 0.0;
            pSl.m_FwBasket = 0.0;
            pSl.m_NwBasket = 0.0;
        }
        return 1;
    }
/**
     * Reset history for given matrix ID
     * @param uKey key to the map
     * @return 1 if success
     */
    public int updateHistory (Long uKey) throws GMParLocator.GMParLocatorException {
        //historyUpdateCounter+=1;
        float grstock, val;
        grstock = 0.0f;
        GMMatrix pTable;
        ComArFlt<Float> pComar;
        GMCarbonAlloc pCAl;
        GMSoil pSl;
        //float grsprev = 0;
        pTable = m_mTables.get(uKey);
        val = pTable.getValue();
        pComar = m_mafGrStock.get(uKey);
        if (pComar != null) {
                pComar.setData(0,val);
                m_mafGrStock.put(uKey, pComar);
        }
        grstock+=val;
        val = pTable.getArea();
        pComar = m_mafArea.get(uKey);
        if (pComar != null) {
                pComar.setData(0,val);
                m_mafArea.put(uKey, pComar);
        }
        val = pTable.getIncrement();
        val = val/m_nStep;
        pComar = m_mafIncrement.get(uKey);
        if (pComar != null) {
                pComar.setData(0,val);
                m_mafIncrement.put(uKey, pComar);
        }
            // Deadwood
        pComar = m_mafDeadWood.get(uKey);
        if (pComar != null) {
                pComar.setData(0,pTable.m_DeadWood);
                m_mafDeadWood.put(uKey, pComar);
        }
            
            // Afforfund added by 2012
        pComar = m_mafAfforFund.get(uKey);
        if (pComar != null) {
                pComar.setData(0,pTable.m_BareArea);
                m_mafAfforFund.put(uKey, pComar);
        }
            
        pComar = m_mafBareArea.get(uKey);
        if (pComar != null) {
                pComar.setData(0,m_BareFund.getFund(uKey));
                m_mafBareArea.put(uKey, pComar);
        }
     
        GMFellings pFl = new GMFellings();
        pFl.setF_volume(0.0);
        pFl.setF_ratio(1.0);
        if (pFl.getF_stem() == 0)
                pFl.setF_stem(0.95);
        pFl = pTable.reportHarvest(pFl);
        pComar = m_mafPotentialFellingsArea.get(uKey);
        if (pComar != null) {
                pComar.setData(0,(float)pFl.getF_area() * m_FelInt);
                m_mafPotentialFellingsArea.put(uKey, pComar);
        }
        pComar = m_mafPotentialFellingsVolume.get(uKey);
        if (pComar != null) {
                pComar.setData(0,(float)pFl.getF_volume() * m_FelInt);
                m_mafPotentialFellingsVolume.put(uKey, pComar);
        }
            
        pCAl = new GMCarbonAlloc();
        pCAl.setCa_cstem(0.0);
        pCAl.setCa_cbranch(0.0);
        pCAl.setCa_ccroots(0.0);
        pCAl.setCa_cfroots(0.0);
        pCAl.setCa_cleaves(0.0);
        pCAl.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
        pCAl.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
        pCAl.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
        pCAl.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
        pCAl.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
        pCAl.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
        pCAl.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
        pCAl.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
        pCAl.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);

            //val = pTable.getBiomass(pCAl);
            //pComar = m_mafBiomass.get(uKey);
            //if (pComar != null)
            //	pComar.setData(0,val);

            // Compartments
        ArrayList<Float> pvalSt = new ArrayList<>(16);
        for (int i=0;i<16;i++) 
                pvalSt.add(0.0f);
        ArrayList<Float> pvalLv = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalLv.add(0.0f);
        ArrayList<Float> pvalBr = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalBr.add(0.0f);
        ArrayList<Float> pvalCr = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalCr.add(0.0f);
        ArrayList<Float> pvalFr = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalFr.add(0.0f);

        val = pTable.getBiomassDistr(pCAl,m_pDistrLims,pvalSt,pvalBr,pvalLv,pvalCr,pvalFr,16);

        pComar = m_mafBiomass.get(uKey);
        if (pComar != null) {
                pComar.setData(0,val);
                m_mafBiomass.put(uKey, pComar);
        }

        ComArFlt<ArrayList<Float>> pPComar;

        pPComar = m_mafCStem.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalSt);
                m_mafCStem.put(uKey, pPComar);
        }
        pPComar = m_mafCLeaves.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalLv);
                m_mafCLeaves.put(uKey, pPComar);
        }
        pPComar = m_mafCBranches.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalBr);
                m_mafCBranches.put(uKey, pPComar);
        }
        pPComar = m_mafCCRoots.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalCr);
                m_mafCCRoots.put(uKey, pPComar);
        }
        pPComar = m_mafCFRoots.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalFr);
                m_mafCFRoots.put(uKey, pPComar);
        }

            // Areas and stocks distribution
        ArrayList<Float> pval = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pval.add(0.0f);
        pval = pTable.getAreaDistr(m_pDistrLims,pval,16);

        pPComar = m_mafAreas.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pval);
                m_mafAreas.put(uKey, pPComar);
        }
        ArrayList<Float> pvalStocks = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalStocks.add(0.0f);
        pvalStocks = pTable.getStockDistr(m_pDistrLims,pvalStocks,16);
        pPComar = m_mafStocks.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalStocks);
                m_mafStocks.put(uKey, pPComar);
        }

            // Mefique stuff!
        ArrayList<Float> pvalTha = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalTha.add(0.0f);
        ArrayList<Float> pvalThr = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalThr.add(0.0f);
        ArrayList<Float> pvalFla = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalFla.add(0.0f);
        ArrayList<Float> pvalFlr = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalFlr.add(0.0f);
        pTable.getMefiqueDistr(m_pDistrLims,pvalTha,pvalThr,pvalFla,pvalFlr,16);
        pPComar = m_mafMfqThAreas.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalTha);
                m_mafMfqThAreas.put(uKey, pPComar);
        }
        pPComar = m_mafMfqThRems.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalThr);
                m_mafMfqThRems.put(uKey, pPComar);
        }
        pPComar = m_mafMfqFelAreas.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalFla);
                m_mafMfqFelAreas.put(uKey, pPComar);
        }
        pPComar = m_mafMfqFelRems.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalFlr);
                m_mafMfqFelRems.put(uKey, pPComar);
        }
            // End Mefique!
            // Bioenergy stuff
        ArrayList<Float> pvalThSl = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalThSl.add(0.0f);
        ArrayList<Float> pvalFlSl = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalFlSl.add(0.0f);
        pTable.getSlashDistr(m_pDistrLims,pvalThSl,pvalFlSl,16);
        pPComar = m_mafBeThSlash.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalThSl);
                m_mafBeThSlash.put(uKey, pPComar);
        }
        pPComar = m_mafBeFelSlash.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalFlSl);
                m_mafBeFelSlash.put(uKey, pPComar);
        }
            // End bioenegy
            // Natural mortality 
        ArrayList<Float> pvalnm = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvalnm.add(0.0f);
        pvalnm = pTable.getMortDistr(m_pDistrLims,pvalnm,16);

        pPComar = m_mafNatMortDistr.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvalnm);
                m_mafNatMortDistr.put(uKey, pPComar);
        }
        ArrayList<Float> pvaldw = new ArrayList<>(16);
        for (int i=0;i<16;i++)
                pvaldw.add(0.0f);

        pTable.getDwoodDistrFromPipe(m_pDistrLims,pvaldw,16);
            //for (int i=0;i<16;i++)
            //    pvaldw.add(0.0f);
            //pTable.getDwoodDistr(m_pDistrLims,pvaldw,16);

        pPComar = m_mafDeadWoodDistr.get(uKey);
        if (pPComar != null) {
                pPComar.setData(0,pvaldw);
                m_mafDeadWoodDistr.put(uKey, pPComar);
        }
        pComar = m_mafAvrIncrement.get(uKey);
        if (pComar != null) {
                float ccont = 0;
                float dens = 0;
                ccont = m_plCcont.getParameterValue(uKey,0);
                dens  = m_plWoodDens.getParameterValue(uKey,0);
                if (ccont==0.0) {
                    ccont = 0.5f;
                }
                if (dens==0.0) {
                    dens = 0.45f;
                }
                float cfactor = ccont*dens;
                //float grsprev = 0;
                Object ptemp;
                int i = this.historyUpdateCounter-1;
                //grs = pComar.getData(i);
                long ur = pTable.getRegionID();
                long uo = pTable.getOwnerID();
                long ust = pTable.getSiteID();
                long usp = pTable.getSpeciesID();

                //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                float hrvthin = 0;
                float hrvfel = 0;
                float hrvthinres = 0;
                float hrvfelres = 0;
                float grincr = 0;
                float grsav = 0;
                float felav = 0;
                float dwood = 0;
                float nmort = 0;
                float grs = summarize(ur,uo,ust,usp,m_mafGrStock,i);
                float garea = summarize(ur,uo,ust,usp,m_mafArea,i);
                ComArFlt pComarDW = m_mafDeadWood.get(uKey);
                ComArFlt pComarNM = m_mafNatMort.get(uKey);
                ComArFlt pPComarThRes = m_mafThRsd.get(uKey);
                ComArFlt pPComarFelRes = m_mafFelRsd.get(uKey);
                ComArFlt pPComarThResRem = m_mafThRsdRem.get(uKey);
                ComArFlt pPComarFelResRem = m_mafFelRsdRem.get(uKey);
                dwood = (Float)pComarDW.getData(i);
                if (garea > 0) {
                            grsav = grs/garea;
                            felav = (hrvthin+hrvfel)/(garea*m_nStep);
                            grincr/=garea*m_nStep;
                }
                grsprev.put(uKey, grs);
                pComar.setData(0,grincr);
                m_mafAvrIncrement.put(uKey, pComar);
        }
        
        m_afStock.setData(0,grstock);
        // Carbon history update
        GMCarbonAlloc pCarAl = new GMCarbonAlloc();
        pCarAl.setCa_cstem(0.0);
        pCarAl.setCa_cbranch(0.0);
        pCarAl.setCa_ccroots(0.0);
        pCarAl.setCa_cfroots(0.0);
        pCarAl.setCa_cleaves(0.0);

        float bioms = getCarbon(0,0,0,0,pCarAl);
        m_afCarbon.setData(0,bioms);
        m_afStem.setData(0,pCarAl.getCa_cstem());
        m_afLeaves.setData(0,pCarAl.getCa_cleaves());
        m_afBranches.setData(0,pCarAl.getCa_cbranch());
        m_afCroots.setData(0,pCarAl.getCa_ccroots());
        m_afFroots.setData(0,pCarAl.getCa_cfroots());

        // Soils update - general
        GMSoilComp scStock = new GMSoilComp();
        scStock.setSc_cwl(0.0f);
        scStock.setSc_fwl(0.0f);
        scStock.setSc_nwl(0.0f);
        scStock.setSc_sol(0.0f);
        scStock.setSc_cel(0.0f);
        scStock.setSc_lig(0.0f);
        scStock.setSc_hm1(0.0f);
        scStock.setSc_hm2(0.0f);
        scStock.setSc_clost(0.0f);
        scStock = getAllSoils(scStock);
        m_afSoilCwl.setData(0,scStock.getSc_cwl());
        m_afSoilFwl.setData(0,scStock.getSc_fwl());
        m_afSoilNwl.setData(0,scStock.getSc_nwl());
        m_afSoilSol.setData(0,scStock.getSc_sol());
        m_afSoilCel.setData(0,scStock.getSc_cel());
        m_afSoilLig.setData(0,scStock.getSc_lig());
        m_afSoilHm1.setData(0,scStock.getSc_hm1());
        m_afSoilHm2.setData(0,scStock.getSc_hm2());
        m_afSoilClost.setData(0,scStock.getSc_clost());
        // Soils update - main
//        for (Long uKey : m_mSoils.keySet())
//        {
//            pSl = m_mSoils.get(uKey);
//            scStock.setSc_cwl(0.0f);
//            scStock.setSc_fwl(0.0f);
//            scStock.setSc_nwl(0.0f);
//            scStock.setSc_sol(0.0f);
//            scStock.setSc_cel(0.0f);
//            scStock.setSc_lig(0.0f);
//            scStock.setSc_hm1(0.0f);
//            scStock.setSc_hm2(0.0f);
//            scStock.setSc_clost(0.0f);
//            scStock = pSl.reportStocks(scStock);
//
//            pComar = m_mafSoilCwl.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_cwl());
//                m_mafSoilCwl.put(uKey, pComar);
//            }
//            pComar = m_mafSoilFwl.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_fwl());
//                m_mafSoilFwl.put(uKey, pComar);
//            }
//            pComar = m_mafSoilNwl.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_nwl());
//                m_mafSoilNwl.put(uKey, pComar);
//            }
//            pComar = m_mafSoilSol.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_sol());
//                m_mafSoilSol.put(uKey, pComar);
//            }
//            pComar = m_mafSoilCel.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_cel());
//                m_mafSoilCel.put(uKey, pComar);
//            }
//            pComar = m_mafSoilLig.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_lig());
//                m_mafSoilLig.put(uKey, pComar);
//            }
//            pComar = m_mafSoilHm1.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_hm1());
//                m_mafSoilHm1.put(uKey, pComar);
//            }
//            pComar = m_mafSoilHm2.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_hm2());
//                m_mafSoilHm2.put(uKey, pComar);
//            }
//            pComar = m_mafSoilClost.get(uKey);
//            if (pComar != null) {
//                pComar.addData(scStock.getSc_clost());
//                m_mafSoilClost.put(uKey, pComar);
//            }
//            pComar = m_mafSoilCwlIn.get(uKey);
//            if (pComar != null) {
//                pComar.addData(pSl.m_CwBasket);
//                m_mafSoilCwlIn.put(uKey, pComar);
//            }
//            pComar = m_mafSoilFwlIn.get(uKey);
//            if (pComar != null) {
//                pComar.addData(pSl.m_FwBasket);
//                m_mafSoilFwlIn.put(uKey, pComar);
//            }
//            pComar = m_mafSoilNwlIn.get(uKey);
//            if (pComar != null) {
//                pComar.addData(pSl.m_NwBasket);
//                m_mafSoilNwlIn.put(uKey, pComar);
//            }
//            pComar = m_mafSoilInOut.get(uKey);
//            if (pComar != null) {
//                pComar.addData(pSl.getInOut());
//                m_mafSoilInOut.put(uKey, pComar);
//            }
//            float ctotal = scStock.getSc_cwl() + scStock.getSc_cel() +
//                    scStock.getSc_fwl() + scStock.getSc_hm1() +
//                    scStock.getSc_hm2() + scStock.getSc_lig() +
//                    scStock.getSc_nwl() + scStock.getSc_sol();
//            pComar = m_mafCSoil.get(uKey);
//            if (pComar != null) {
//                pComar.addData(ctotal);
//                m_mafCSoil.put(uKey, pComar);
//            }
//            pSl.setInOut(0.0);
//            pSl.m_CwBasket = 0.0;
//            pSl.m_FwBasket = 0.0;
//            pSl.m_NwBasket = 0.0;
//        }
        return 1;
    }

    /**
     * Find soil by given Matrixes ID, using to collect litter from fellings.
     * @param ulKey key to the map
     * @return soil by given id or null if not found
     */
    public GMSoil findSoil (long ulKey) {
        GMSoil pSol = null, pEl = null;
        long uKeyCopy, lr, lo, lst, lsp;
        for (Long uKey : m_mSoils.keySet())
        {
            pEl = m_mSoils.get(uKey);
            uKeyCopy = ulKey;
            lr = uKey>>24;
            lo = uKey&0xFF0000;
            lo = lo>>16;
            lst = uKey&0xFF00;
            lst = lst>>8;
            lsp = uKey&0xFF;
            if (lr == 0)  uKeyCopy = uKeyCopy & ~0xFF000000;
            if (lo == 0)  uKeyCopy = uKeyCopy & ~0xFF0000;
            if (lst == 0) uKeyCopy = uKeyCopy & ~0xFF00;
            if (lsp == 0) uKeyCopy = uKeyCopy & ~0xFF;
            if (uKeyCopy==uKey)
                pSol = pEl;
        }
        return pSol;
    }

    /**
     * @deprecated 
     * Harvest level reporting.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pFl fellings
     * @return harvest level
     */
    public GMFellings reportHarvestLevel (long lr, long lo, long lst, long lsp,
            GMFellings pFl) {
        //float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                //retval+=pTable.reportHarvest(pFl);
                pFl = pTable.reportHarvest(pFl);
        }
        return pFl;
    }

    /**
     * @deprecated 
     * Thinnigs level reporting.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pFl fellings
     * @return thinnings level
     */
    public GMFellings reportThinningsLevel (long lr, long lo, long lst, long lsp,
            GMFellings pFl) {
        //float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                pFl = pTable.reportThinnings(pFl);
        }
        return pFl;
    }

    /**
     * Harvest level reporting with removals definition.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pFl fellings
     * @param plRem removals
     * @return harvest level
     */
    public GMFellings reportHarvestLevel (long lr, long lo, long lst, long lsp,
            GMFellings pFl, GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        //float retval = 0;
        long ulKey, uSaveKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            uSaveKey = uKey;
            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                pFl.setF_stem(plRem.getParameterValue(uSaveKey,0));
                pFl.setF_branch(plRem.getParameterValue(uSaveKey,2));
                pFl.setF_leaves(plRem.getParameterValue(uSaveKey,3));
                //retval+=pTable.reportHarvest(pFl);
                pFl = pTable.reportHarvest(pFl);
            }
        }
        return pFl;
    }

    /**
     * Thinnings level reporting with removals definition.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pFl fellings
     * @param plRem removals
     * @return thinnings level
     */
    public GMFellings reportThinningsLevel (long lr, long lo, long lst, long lsp,
            GMFellings pFl, GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        //float retval = 0;
        long ulKey, uSaveKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            uSaveKey = uKey;
            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                pFl.setF_stem(plRem.getParameterValue(uSaveKey,5));
                pFl.setF_branch(plRem.getParameterValue(uSaveKey,7));
                pFl.setF_leaves(plRem.getParameterValue(uSaveKey,8));
                //retval+=pTable.reportThinnings(pFl);
                pFl = pTable.reportThinningsV4(pFl);
            }
        }
        return pFl;
    }

    /**
     * Performing Fellings with intensity common for all matrixes.
     * @param intens intensity
     * @return fellings volume
     */
    public float doFellings (float intens) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        if (intens>1.0)
            intens = 1.0f;
        if (intens<0)
            intens = 0.0f;
        pgmFel.setF_ratio(intens);
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.95);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0);
            pgmCa.setCa_cbranch(0.0);
            pgmCa.setCa_ccroots(0.0);
            pgmCa.setCa_cfroots(0.0);
            pgmCa.setCa_cleaves(0.0);

            float felval;
            felval = (float)pTable.makeFellings(pgmFel,pgmCa);
            ComArFlt pComar = m_mafFellings.get(uKey);
            if (pComar != null) {
                pComar.addData(felval);
                m_mafFellings.put(uKey, pComar);
            }
            m_BareFund.addArea(uKey,pgmFel.getF_area());
            // Residuals keeping
            ArrayList<Float> pvalFlRsd = new ArrayList<>();
            pvalFlRsd.add((float)pgmCa.getCa_cstem());
            pvalFlRsd.add((float)pgmCa.getCa_cbranch());
            pvalFlRsd.add((float)pgmCa.getCa_cleaves());
            pvalFlRsd.add((float)pgmCa.getCa_ccroots());
            
            ComArFlt pPComar = m_mafFelRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlRsd);
                /*pPComar.addData((float)pgmCa.getCa_cbranch());
                pPComar.addData((float)pgmCa.getCa_cleaves());
                pPComar.addData((float)pgmCa.getCa_ccroots()); // 3->4 (Uppsala 2009)*/
                m_mafFelRsd.put(uKey, pPComar);
            }
            // Resrems keeping! To integrity with scenarios
            ArrayList<Float> pvalFlRsdRem = new ArrayList<>();
            pvalFlRsdRem.add(0.0f);
            pvalFlRsdRem.add(0.0f);
            pvalFlRsdRem.add(0.0f);
            pvalFlRsdRem.add(0.0f);
            
            pPComar = m_mafFelRsdRem.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlRsdRem); //(float)pgmCa.getCa_cstem();
                /*pPComar.addData(0.0f); //(float)pgmCa.getCa_cbranch();
                pPComar.addData(0.0f); //(float)pgmCa.getCa_cleaves();
                pPComar.addData(0.0f); // 3->4 (Uppsala 2009)*/
                m_mafFelRsdRem.put(uKey, pPComar);
            }
            // Coarse roots management  January 2010
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            // Soil Litter update
            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                    (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                    (pgmCa.getCa_cstem()+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * @deprecated 
     * Performing Fellings with intensities in the plR Parlocator.
     * @param plR intensities
     * @return fellings volume
     */
    public float doFellingsEx (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.95);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // Carbon allocate structure filling
            pgmFel.setF_ratio(plR.getParameterValue(uKey,0));
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0);
            pgmCa.setCa_cbranch(0.0);
            pgmCa.setCa_ccroots(0.0);
            pgmCa.setCa_cfroots(0.0);
            pgmCa.setCa_cleaves(0.0);

            float felval;
            felval = (float)pTable.makeFellings(pgmFel,pgmCa);
            ComArFlt pComar = m_mafFellings.get(uKey);
            if (pComar != null) {
                pComar.addData(felval);
                m_mafFellings.put(uKey, pComar);
            }
            //pTable.makeFellings(pgmFel,pgmCa);
            m_BareFund.addArea(uKey,pgmFel.getF_area());
            // Residuals keeping
            ArrayList pvalFlRsd = new ArrayList();
            pvalFlRsd.add((float)pgmCa.getCa_cstem());
            pvalFlRsd.add((float)pgmCa.getCa_cbranch());
            pvalFlRsd.add((float)pgmCa.getCa_cleaves());
            pvalFlRsd.add((float)pgmCa.getCa_ccroots());
            
            ComArFlt pPComar = m_mafFelRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlRsd);
                /*pPComar.addData((float)pgmCa.getCa_cbranch());
                pPComar.addData((float)pgmCa.getCa_cleaves());
                pPComar.addData((float)pgmCa.getCa_ccroots()); // 3->4 (Uppsala 2009)*/
                m_mafFelRsd.put(uKey, pPComar);
            }
            // Coarse roots management  January 2010 
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            // Soil Litter update
            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                    (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                    (pgmCa.getCa_cstem()+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * Performing Fellings with intensities in the plR Parlocator.
     * @param plR intensities
     * @return fellings volume
     */
    public float doFellingsEx (GMParLocator plR, GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.95);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // Intencity retrieving
            pgmFel.setF_ratio(plR.getParameterValue(uKey,0));
            // Removals definition
            pgmFel.setF_stem(plRem.getParameterValue(uKey,0));	//0
            pgmFel.setF_branch(plRem.getParameterValue(uKey,2));	//1
            pgmFel.setF_leaves(plRem.getParameterValue(uKey,3));	//2
            pgmFel.setF_froots(plRem.getParameterValue(uKey,4));
            pgmFel.setF_croots(plRem.getParameterValue(uKey,10)); // coarse roots (Uppsala)
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0);
            pgmCa.setCa_cbranch(0.0);
            pgmCa.setCa_ccroots(0.0);
            pgmCa.setCa_cfroots(0.0);
            pgmCa.setCa_cleaves(0.0);

            float felval;
            felval = (float)pTable.makeFellings(pgmFel,pgmCa);
            ComArFlt pComar = m_mafFellings.get(uKey);
            if (pComar != null) {
                pComar.addData(felval);
                m_mafFellings.put(uKey, pComar);
            }
            //pTable.makeFellings(pgmFel,pgmCa);
            m_BareFund.addArea(uKey,pgmFel.getF_area());
            float stres,stresrem,brres,brresrem,lvres,lvresrem,crres,crresrem; //(Uppsala)
            stres = (float)pgmCa.getCa_cstem();
            stresrem = stres*plRem.getParameterValue(uKey,1);
            stres-=stresrem;
            
            brres = (float)pgmCa.getCa_cbranch();
            brresrem = 0.0f;
            if (pgmFel.getF_branch()<1.0)
                brresrem = (float)(pgmFel.getF_branch()*brres/(1-pgmFel.getF_branch()));
            lvres = (float)pgmCa.getCa_cleaves();
            lvresrem = 0.0f;
            if (pgmFel.getF_leaves()<1.0)
                lvresrem = (float)(pgmFel.getF_leaves()*lvres/(1-pgmFel.getF_leaves()));
            crres = (float)pgmCa.getCa_ccroots();
            crresrem = 0.0f;
            if (pgmFel.getF_croots()<1.0)
                crresrem = (float)(pgmFel.getF_croots()*crres/(1-pgmFel.getF_croots()));
            
            // Residuals keepings
            ArrayList pvalFlRsd = new ArrayList();
            pvalFlRsd.add((float)stres);
            pvalFlRsd.add((float)brres);
            pvalFlRsd.add((float)lvres);
            pvalFlRsd.add((float)crres);
            
            ComArFlt<ArrayList<Float>> pPComar = m_mafFelRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlRsd); //(float)pgmCa.getCa_cstem();
                /*pPComar.addData(brres); //(float)pgmCa.getCa_cbranch();
                pPComar.addData(lvres); //(float)pgmCa.getCa_cleaves();
                pPComar.addData(crres); //(float)pgmCa.getCa_ccroots(); (Uppsala)*/
                m_mafFelRsd.put(uKey, pPComar);
            }
            ArrayList<Float> pvalFlRsdRem = new ArrayList<>();
            pvalFlRsdRem.add(stresrem);
            pvalFlRsdRem.add(brresrem);
            pvalFlRsdRem.add(lvresrem);
            pvalFlRsdRem.add(crresrem);
            
            pPComar = m_mafFelRsdRem.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalFlRsdRem); //(float)pgmCa.setCa_cstem;
                /*pPComar.addData(brresrem); //(float)pgmCa.setCa_cbranch;
                pPComar.addData(lvresrem); //(float)pgmCa.setCa_cleaves;
                pPComar.addData(crresrem); //(float)pgmCa.setCa_ccroots; (Uppsala)*/
                m_mafFelRsdRem.put(uKey, pPComar);
            }
            // Coarse roots management  January 2010 
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            // Soil Litter update
            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                    (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                    (stres+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * Performing thinnings with same intensity for all Matrixes.
     * @param intens intensity
     * @return thinnings volume
     */
    public float doThinnings (float intens) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        if (intens>1.0)
            intens = 1.0f;
        if (intens<0)
            intens = 0.0f;
        pgmFel.setF_ratio(intens);
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.9);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0f);
            pgmCa.setCa_cbranch(0.0f);
            pgmCa.setCa_ccroots(0.0f);
            pgmCa.setCa_cfroots(0.0f);
            pgmCa.setCa_cleaves(0.0f);
            float thinval;
            //thinval = pTable.doThinning(pgmFel,pgmCa);
            thinval = (float)pTable.doThinningV4(pgmFel,pgmCa);
            ComArFlt<Float> pComar = m_mafThinnings.get(uKey);
            if (pComar != null) {
                pComar.addData(thinval);
                m_mafThinnings.put(uKey, pComar);
            }
            // Residuals keeping
            ArrayList<Float> pvalThRsd = new ArrayList<>();
            pvalThRsd.add((float)pgmCa.getCa_cstem());
            pvalThRsd.add((float)pgmCa.getCa_cbranch());
            pvalThRsd.add((float)pgmCa.getCa_cleaves());
            pvalThRsd.add((float)pgmCa.getCa_ccroots());
            
            ComArFlt<ArrayList<Float>> pPComar = m_mafThRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThRsd);
                /*pPComar.addData((float)pgmCa.getCa_cbranch());
                pPComar.addData((float)pgmCa.getCa_cleaves());
                pPComar.addData((float)pgmCa.getCa_ccroots()); // (Uppsala)*/
                m_mafThRsd.put(uKey, pPComar);
            }
            
            ArrayList<Float> pvalThRsdRem = new ArrayList<>();
            pvalThRsdRem.add(0.0f);
            pvalThRsdRem.add(0.0f);
            pvalThRsdRem.add(0.0f);
            pvalThRsdRem.add(0.0f);
            
            pPComar = m_mafThRsdRem.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThRsdRem); //(float)pgmCa.getCa_cstem();
                /*pPComar.addData(0.0f); //(float)pgmCa.getCa_cbranch();
                pPComar.addData(0.0f); //(float)pgmCa.getCa_cleaves();
                pPComar.addData(0.0f); //(Uppsala)*/
                m_mafThRsdRem.put(uKey, pPComar);
            }

            // Coarse roots management r January 2010 
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                        (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                        (pgmCa.getCa_cstem()+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * @deprecated 
     * Performing thinnings with intensity in the plR - ParLocator.
     * @param plR intensities
     * @return thinnings volume
     */
    public float doThinningsEx (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.9);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
			// Intensity retrieving
            pgmFel.setF_ratio(plR.getParameterValue(uKey,1));
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0f);
            pgmCa.setCa_cbranch(0.0f);
            pgmCa.setCa_ccroots(0.0f);
            pgmCa.setCa_cfroots(0.0f);
            pgmCa.setCa_cleaves(0.0f);
            float thinval;
            thinval = (float)pTable.doThinning(pgmFel,pgmCa);
            ComArFlt<Float> pComar = m_mafThinnings.get(uKey);
            if (pComar != null) {
                pComar.addData(thinval);
                m_mafThinnings.put(uKey, pComar);
            }
            // Residuals keeping
            ArrayList<Float> pvalThRsd = new ArrayList<>();
            pvalThRsd.add((float)pgmCa.getCa_cstem());
            pvalThRsd.add((float)pgmCa.getCa_cbranch());
            pvalThRsd.add((float)pgmCa.getCa_cleaves());
            pvalThRsd.add((float)pgmCa.getCa_ccroots());
            
            ComArFlt<ArrayList<Float>> pPComar = m_mafThRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThRsd);
                /*pPComar.addData((float)pgmCa.getCa_cbranch());
                pPComar.addData((float)pgmCa.getCa_cleaves());
                pPComar.addData((float)pgmCa.getCa_ccroots()); // (Uppsala)*/
                m_mafThRsd.put(uKey, pPComar);
            }
            // Coarse roots management January 2010
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                        (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                        (pgmCa.getCa_cstem()+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * Performing thinnings with intensity in the plR - ParLocator and plRem
     * - ParLocator with removals definition.
     * @param plR intensities
     * @return thinnings volume
     */
    public float doThinningsEx (GMParLocator plR, GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(0.9);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
			// Intensity retrieving
            pgmFel.setF_ratio(plR.getParameterValue(uKey,1));
            // Removals definition
            pgmFel.setF_stem(plRem.getParameterValue(uKey,5));	//3
            pgmFel.setF_branch(plRem.getParameterValue(uKey,7));	//4
            pgmFel.setF_leaves(plRem.getParameterValue(uKey,8));	//5
            pgmFel.setF_froots(plRem.getParameterValue(uKey,9));
            pgmFel.setF_croots(plRem.getParameterValue(uKey,11)); //coarse roots (Uppsala)
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0f);
            pgmCa.setCa_cbranch(0.0f);
            pgmCa.setCa_ccroots(0.0f);
            pgmCa.setCa_cfroots(0.0f);
            pgmCa.setCa_cleaves(0.0f);
            float thinval;
            thinval = (float)pTable.doThinningV4(pgmFel,pgmCa); // !!
            ComArFlt<Float> pComar = m_mafThinnings.get(uKey);
            if (pComar != null) {
                pComar.addData(thinval);
                m_mafThinnings.put(uKey, pComar);
            }
            float stres,stresrem,brres,brresrem,lvres,lvresrem,crres,crresrem;
            stres = (float)pgmCa.getCa_cstem();
            stresrem = stres*plRem.getParameterValue(uKey,6);
            stres-=stresrem;
            //removals
            brres = (float)pgmCa.getCa_cbranch();
            brresrem = 0.0f;
            if (pgmFel.getF_branch()<1.0)
                brresrem = (float)(pgmFel.getF_branch()*brres/(1-pgmFel.getF_branch()));
            lvres = (float)pgmCa.getCa_cleaves();
            lvresrem = 0.0f;
            if (pgmFel.getF_leaves()<1.0)
                lvresrem = (float)(pgmFel.getF_leaves()*lvres/(1-pgmFel.getF_leaves()));
            crres = (float)pgmCa.getCa_ccroots();
            crresrem = 0.0f;
            if (pgmFel.getF_croots()<1.0)
                crresrem = (float)(pgmFel.getF_croots()*crres/(1-pgmFel.getF_croots()));
            // End dirty part!
            // Residuals keeping
            ArrayList<Float> pvalThRsd = new ArrayList<>();
            pvalThRsd.add(stres);
            pvalThRsd.add(brres);
            pvalThRsd.add(lvres);
            pvalThRsd.add(crres);
            
            ComArFlt<ArrayList<Float>> pPComar = m_mafThRsd.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThRsd);
                /*pPComar.addData(brres);
                pPComar.addData(lvres);
                pPComar.addData(crres); // (Uppsala)*/
                m_mafThRsd.put(uKey, pPComar);
            }
            
            ArrayList<Float> pvalThRsdRem = new ArrayList<>();
            pvalThRsdRem.add(stresrem);
            pvalThRsdRem.add(brresrem);
            pvalThRsdRem.add(lvresrem);
            pvalThRsdRem.add(crresrem);
            
            pPComar = m_mafThRsdRem.get(uKey);
            if (pPComar != null) {
                pPComar.addData(pvalThRsdRem);//(float)pgmCa.setCa_cstem;
                /*pPComar.addData(brresrem);//(float)pgmCa.setCa_cbranch;
                pPComar.addData(lvresrem);//(float)pgmCa.setCa_cleaves;
                pPComar.addData(crresrem); //(Uppsala)*/
                m_mafThRsdRem.put(uKey, pPComar);
            }
            // Coarse roots management  January 2010 
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");
            // Soil Litter update
            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                        (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                        (stres+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * Performing afforestation (simple version).
     * @param afc afforestation coefficiency
     * @return amount of afforestation
     */
    public float doAfforSimple (double afc) {
        double ret,coeff,rest;
        ret = 0.0;
        coeff = afc;
        if (coeff>1.0) coeff = 1.0;
        if (coeff<0) coeff = 0.0;
        double afarea = 0.0;
        double arval;
        for (Long uKey : m_BareFund.m_mdFund.keySet())
        {
            arval = m_BareFund.m_mdFund.get(uKey);
            afarea = coeff*arval;
            GMMatrix pTable = m_mTables.get(uKey);
            if (pTable != null) {
                pTable.addToBare((float)afarea);
                rest = arval - afarea;
                m_BareFund.m_mdFund.put(uKey,rest);
                m_mTables.put(uKey, pTable);
                ret+=afarea;
            }
            afarea = 0.0;
        }
        return (float)ret;
    }

    /**
     * Performing Natural Mortality for all Matrixes.
     * @return fellings volume
     */
    public float doNaturalMortality () throws GMParLocator.GMParLocatorException {
        GMFellings pgmFel = new GMFellings();
        GMCarbonAlloc pgmCa = new GMCarbonAlloc();

        pgmFel.setF_ratio(1.0);
        pgmFel.setF_area(0.0);
        pgmFel.setF_volume(0.0);
        pgmFel.setF_stem(1.0);
        pgmFel.setF_branch(0.0);
        pgmFel.setF_croots(0.0);
        pgmFel.setF_froots(0.0);
        pgmFel.setF_leaves(0.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // Carbon allocate structure filling
            pgmCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pgmCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pgmCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pgmCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pgmCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pgmCa.setCa_cstem(0.0);
            pgmCa.setCa_cbranch(0.0);
            pgmCa.setCa_ccroots(0.0);
            pgmCa.setCa_cfroots(0.0);
            pgmCa.setCa_cleaves(0.0);
            ArrayList<Float> flims,frates;
            int ns;
            flims = null;
            frates = null;
            // Not now GMParArray pPar = null;
            // A bit dirty
            flims = m_plMortRateXvals.getParameter(uKey).m_Vals;
            frates = m_plMortRate.getParameter(uKey).m_Vals;
            ns = m_plMortRateXvals.getParameter(uKey).m_nSize;
            // Getting deadwood decay rate!
            pgmFel.setF_ratio(m_plDeadWoodDrate.getParameterValue(uKey,0));
            float dwood;
            dwood = (float)pTable.doNaturalMortality(pgmFel,pgmCa,flims,frates,ns);
            // Keeping history
            ComArFlt<Float> pComar = m_mafNatMort.get(uKey);
            if (pComar != null) {
                pComar.addData(dwood);
                m_mafNatMort.put(uKey, pComar);
            }
            // Adding litter to the soil
            // Coarse roots management  January 2010 
            float cr2wl = 1.0f;
            GMParArray paCr2Wl = null;
            paCr2Wl = m_plCroots2CWL.getParameter(uKey);
            if (paCr2Wl != null)
                cr2wl = paCr2Wl.m_Vals.get(0);
            else
                System.err.println("Could not locate the coarse woody litter share\nwill use 1.0");

            GMSoil pSol = findSoil(uKey);
            if (pSol != null) {
                pSol.addLitter((pgmCa.getCa_cleaves()+pgmCa.getCa_cfroots())/m_nStep,
                        (pgmCa.getCa_cbranch()+(1.0-cr2wl)*pgmCa.getCa_ccroots())/m_nStep,
                        (pgmCa.getCa_cstem()+cr2wl*pgmCa.getCa_ccroots())/m_nStep);
                //m_mSoils.put(uKey, pSol);
            }
            pgmFel.setF_area(0.0);
            m_mTables.put(uKey, pTable);
        }
        float ret = (float) pgmFel.getF_volume();
        return ret;
    }

    /**
     * Thinnings "history".
     * @return amount of matrixes
     */
    public int setThinHistory () throws GMParLocator.GMParLocatorException {
        int retval;
        retval = 0;

        GMMatrix pEl;
        float ratio;
        for (Long uKey : m_mTables.keySet())
        {
            pEl = m_mTables.get(uKey);
            ratio = m_plThHistory.getParameterValue(uKey,0);
            pEl.setThinHistory(ratio);
            m_mTables.put(uKey, pEl);
            retval+=1;
        }
        return retval;
    }

    /**
     * Performing Deadwood initialisation for all Matrixes.
     * @return total amount of deadwood
     */
    public float setDeadWood () throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float dwood, decay;
        GMMatrix pTable;
        int ns;
        ArrayList<Float> flims,frates;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // A bit dirty
            GMParArray tmpAr = m_plMortRateXvals.getParameter(uKey);
            flims = tmpAr.m_Vals;
            //flims = m_plMortRateXvals.getParameter(uKey).m_Vals;
            frates = m_plMortRate.getParameter(uKey).m_Vals;
            ns = m_plMortRateXvals.getParameter(uKey).m_nSize;
            // Getting deadwood decay rate!
            decay = m_plDeadWoodDrate.getParameterValue(uKey,0);
            dwood = (float)(pTable.initDeadWood(flims,frates,ns,decay));
            ComArFlt<Float> pComar;
            ComArFlt<ArrayList<Float>> pPComar;
            pComar = m_mafDeadWood.get(uKey);
            if (pComar != null) {
                pComar.setData(0,dwood);
                m_mafDeadWood.put(uKey, pComar);
            }
            ArrayList<Float> pvaldw = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvaldw.add(0.0f);
            pTable.getDwoodDistrFromPipe(m_pDistrLims,pvaldw,16);

            pPComar = m_mafDeadWoodDistr.get(uKey);
            if (pPComar != null) {
                //
                //pPComar.setData(0,pvaldw.get(0));
                pPComar.setData(0,pvaldw);
                m_mafDeadWoodDistr.put(uKey, pPComar);
            }
            ret+=dwood;
            m_mTables.put(uKey, pTable);
        }
        return ret;
    }
  
    /**
     * Performing Deadwood initialisation for all Matrixes.
     * Version with taking into account removals of deadwood during fellings.
     * @param plRem removals
     * @return total amount of deadwood
     */
    public float setDeadWoodEx (GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float dwood, decay, threm, felrem;
        GMMatrix pTable;
        int ns;
        ArrayList<Float> flims,frates;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            // A bit dirty
            flims = m_plMortRateXvals.getParameter(uKey).m_Vals;
            frates = m_plMortRate.getParameter(uKey).m_Vals;
            ns = m_plMortRateXvals.getParameter(uKey).m_nSize;
            // Getting deadwood decay rate!
            decay = m_plDeadWoodDrate.getParameterValue(uKey,0);
            threm = plRem.getParameterValue(uKey,9);
            felrem = plRem.getParameterValue(uKey,4);
            dwood = (float)(pTable.initDeadWoodEx(flims,frates,ns,decay,threm,felrem));
            ComArFlt<Float> pComar;
            ComArFlt<ArrayList<Float>> pPComar;
            pComar = m_mafDeadWood.get(uKey);
            if (pComar != null) {
                pComar.setData(0,dwood);
                m_mafDeadWood.put(uKey, pComar);
            }
            ArrayList<Float> pvaldw = new ArrayList<>(16);
            for (int i=0;i<16;i++)
                pvaldw.add(0.0f);
            pTable.getDwoodDistrFromPipe(m_pDistrLims,pvaldw,16);

            pPComar = m_mafDeadWoodDistr.get(uKey);
            if (pPComar != null) {
                // 
                //pPComar.setData(0,pvaldw.get(0));
                pPComar.setData(0,pvaldw);
                m_mafDeadWoodDistr.put(uKey, pPComar);
            }
            ret+=dwood;
            m_mTables.put(uKey, pTable);
        }
        return ret;
    }

    /**
     * Trying to distribute area changes aforestation.
     * Taking into account average soil input given in scenario as a realtive
     * of average stock.
     * Modified by EFI August 2010.
     * @param plR keeps data about absoulute values of aforestation - deforestation
     * @return 1 if succesful
     */
    public int distrAforestation (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        //GMParLocator plRat = new GMParLocator();
        double ar,arin,artot,armat;
        double cinput; //to get the ratio soil carbon input: August 2010
        GMParArray pEl;
        GMMatrix pTbl;
        for (Long uKey : m_BareFund.m_mdFund.keySet())
        {
            //ar = m_BareFund.m_mdFund.get(uKey);
            pEl = plR.getParameter(uKey);
            ar = 0;
            arin = pEl.m_Vals.get(0).floatValue();
            cinput = pEl.m_Vals.get(1);
            if (arin>0) {
                artot = getArea((long)pEl.getM_uRegion(),(long)pEl.getM_uOwner(),
                            (long)pEl.getM_uSite(),(long)pEl.getM_uSpecies());
                pTbl = m_mTables.get(uKey);
                armat = pTbl.getArea();

                if (artot > 0) {
                    ar = armat*arin/artot;
                }
                else {
                    int numtot = getNumMatr((long)pEl.getM_uRegion(),(long)pEl.getM_uOwner(),
                            (long)pEl.getM_uSite(),(long)pEl.getM_uSpecies());
                    if (numtot > 0)
                        ar = arin/numtot;
                }
            }
            m_BareFund.m_mdIncome.put(uKey,ar);
            GMSoil pSoil = findSoil(uKey);
            if (pSoil != null) {
                pSoil.addToPool(cinput*ar);
                //m_mSoils.put(uKey, pSoil);
            }
        }
        return 1;
    }

    /**
     * Trying to distribute area changes deforestation.
     * @param plR keeps data about absoulute values of aforestation - deforestation
     * @return 1 if succesful
     */
    public int distrDeforestation (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        //GMParLocator plRat = new GMParLocator();
        double ar,arin,artot,armat;
        GMParArray pEl;
        for (Long uKey : m_BareFund.m_mdFund.keySet())
        {
            armat = m_BareFund.m_mdFund.get(uKey);
            pEl = plR.getParameter(uKey);
            ar = 0;
            arin = pEl.m_Vals.get(0);
            if (arin>0) {
                artot = m_BareFund.getFund((long)pEl.getM_uRegion(),(long)pEl.getM_uOwner(),
                            (long)pEl.getM_uSite(),(long)pEl.getM_uSpecies());
                if (artot > 0)
                    ar = armat*arin/artot;
            }
            //check if area to be deforested exceed the area available
            //December 2013
            if (ar>armat) {
                System.out.format("Debug:Matrix: %d %d %d %d : deforestation asks more\n", m_mTables.get(uKey).getRegionID(),m_mTables.get(uKey).getOwnerID(),
                        m_mTables.get(uKey).getSiteID(),m_mTables.get(uKey).getSpeciesID());
                System.err.println("WARNING - deforestation asks more than possible");
                deferr = true;
                ar = armat;
            }
            m_BareFund.m_mdOutcome.put(uKey,ar);
            GMSoil pSoil = findSoil(uKey);
            if (pSoil != null) {
                pSoil.addToPool(-1.0*ar);
                //m_mSoils.put(uKey, pSoil);
            }
        }

        return 1;
    }

    /**
     * Species change implementation deforestation.
     * @param plR keeps data about relative aforestation - deforestation
     * @return 1 if succesful 0 otherwise
     */
    public int doSpecChange (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        //GMParLocator plRat = new GMParLocator();
        if (plR.getParameter(0) != null)
            return 0;
        long ulmID,ulID;
        int cr,co,cst,csp,cspdest;
        double ar,arin,artot,armat;
        //float arsumm;
        int ndest;
        GMParArray pEl;
        for (Long uKey : m_BareFund.m_mdFund.keySet())
        {
            armat = m_BareFund.m_mdFund.get(uKey);

            pEl = plR.getParameter(uKey);
            if (pEl == null)
                continue;
            cr = pEl.getM_uRegion();
            co = pEl.getM_uOwner();
            cst = pEl.getM_uSite();
            csp = pEl.getM_uSpecies();

            ulID = (long) cr;
            ulmID = ulID<<24;
            ulID = (long) co;
            ulmID = ulmID + (ulID<<16);
            ulID = (long) cst;
            ulmID = ulmID + (ulID<<8);

            //ulID = (long) csp;
            //ulmID = ulmID + ulID;
            //ulID = ulmID;

            ndest = pEl.m_nSize/2;

            artot = 0;
            for (int i=0;i<ndest;i++) {
                cspdest = pEl.getM_Vals().get(2*i).intValue();
                float ratio = pEl.getM_Vals().get(2*i+1);
                float destar = ratio*(float)armat;
                ulID = (long) cspdest;
                long ulKeyD;
                ulKeyD = ulmID + ulID;
                if (m_BareFund.m_mdIncome.containsKey(ulKeyD)) {
                    ar = m_BareFund.m_mdIncome.get(ulKeyD);
                    ar+=destar;
                    m_BareFund.m_mdIncome.put(ulKeyD,ar);
                    artot+=destar;
                }
            }
            m_BareFund.m_mdOutcome.put(uKey,artot);
            /*
            arin = pEl.getM_Vals.get(0);
            if (arin>0) {
                artot = m_BareFund.GetFund((long)pEl.getM_uRegion(),(long)pEl.getM_uOwner(),
                            (long)pEl.getM_uSite(),(long)pEl.getM_uSpecies());

                if (artot)
                    ar = armat*arin/artot;
            }
            m_BareFund.m_mdOutcome.put(uKey,ar);
            */
        }
        return 1;
    }

    /**
     * @deprecated
     * Step of simulation with scenario of management.
     * @param plR management
     * @return
     */
    public float goAheadEx (GMParLocator plR) throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float thh,fh;

        thh = doThinningsEx(plR);
        fh = doFellingsEx(plR);
        m_afThinVolume.addData(thh);
        m_afFellVolume.addData(fh);

        if (m_bIsStart)
            setDeadWood();

        // Natural mortality
        doNaturalMortality();

        if (m_bIsStart) {
            reInitSoilStocks(0);
            m_bIsStart = false;
        }

        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            if (pTable != null) {
                pTable.grow();
                ret += pTable.update();
                m_mTables.put(uKey, pTable);
            }
        }
        // Soils development
        soilGo();
        //GMSoil pSol;
        //for (Long uKey : m_mTables.keySet())
        //{
        //  pSol = m_mSoils.get(uKey);
        //	ret += pTable.update();
        //}
        // Just reforestation
        doAfforSimple(1.0);
        return ret;
    }

    /**
     * @deprecated 
     * Step of simulation with scenario of management and affor-deffor.
     * @param plR management
     * @param plAf afforestation
     * @param plDef deforestation
     * @return
     */
    public float goAheadEx (GMParLocator plR, GMParLocator plAf,
            GMParLocator plDef) throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float thh,fh;

        thh = doThinningsEx(plR);
        fh = doFellingsEx(plR);
        m_afThinVolume.addData(thh);
        m_afFellVolume.addData(fh);

        if (m_bIsStart)
            setDeadWood();

        // Natural mortality
        doNaturalMortality();

        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            if (pTable != null) {
                pTable.grow();
                ret += pTable.update();
                m_mTables.put(uKey, pTable);
            }
        }
        // Soils development
        if (m_bIsStart) {
            reInitSoilStocks(0);
            m_bIsStart = false;
        }
        soilGo();
        //GMSoil pSol;
        //for (Long uKey : m_mTables.keySet())
        //{
        //  pSol = m_mSoils.get(uKey);
        //	ret += pTable.update();
        //}
        distrAforestation(plAf);
        distrDeforestation(plDef);
        m_BareFund.applyChanges();
        m_BareFund.clearChanges();
        // Just reforestation
        doAfforSimple(1.0);
        return ret;
    }

    /**
     * @deprecated 
     * Step of simulation with scenario of management and affor-deffor and also
     * removals scenario.
     * @param plR management
     * @param plAf afforestation
     * @param plDef deforestation
     * @param plRem removals
     * @return
     */
    public float goAheadEx (GMParLocator plR, GMParLocator plAf,
            GMParLocator plDef, GMParLocator plRem) throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float thh,fh;

        thh = doThinningsEx(plR,plRem);
        fh = doFellingsEx(plR,plRem);
        m_afThinVolume.addData(thh);
        m_afFellVolume.addData(fh);

        if (m_bIsStart)
            setDeadWoodEx(plRem);

        if (m_bIsStart) {
            reInitSoilStocks(0);
            m_bIsStart = false;
        }
        // Fast bare lands grow 
        distrAforestation(plAf);
        distrDeforestation(plDef);
        m_BareFund.applyChanges();
        m_BareFund.clearChanges();
        // Just reforestation
        doAfforSimple(1.0);

        // Natural mortality
        doNaturalMortality();

        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            if (pTable != null) {
                pTable.growV4();
                ret += pTable.updateV4();
                m_mTables.put(uKey, pTable);
            }
        }
        // Soils development
        soilGo();
        //GMSoil pSol;
        //for (Long uKey : m_mTables.keySet())
        //{
        //  pSol = m_mSoils.get(uKey);
        //	ret += pTable.update();
        //}

        // Not Fast bare lands grow EFI!
        // Uncomment for not ALTERRA!
        /*
        distrAforestation(plAf);
        distrDeforestation(plDef);
        m_BareFund.applyChanges();
        m_BareFund.clearChanges();
        // Just reforestation
        doAfforSimple(1.0);
        */
        return ret;
    }

    /**
     * Step of simulation with scenario of management, affor-deffor,
     * removals of scenario and also with species change.
     * @param plR management
     * @param plAf afforestation
     * @param plDef deforestation
     * @param plRem removals
     * @param plSpc species change
     * @return
     */
    public float goAheadExRup (GMParLocator plR, GMParLocator plAf,
            GMParLocator plDef, GMParLocator plRem, GMParLocator plSpc) throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float thh,fh;

        thh = doThinningsEx(plR,plRem);
        fh = doFellingsEx(plR,plRem);
        m_afThinVolume.addData(thh);
        m_afFellVolume.addData(fh);

        if (m_bIsStart)
            setDeadWoodEx(plRem);

        // Fast bare lands grow 
        distrAforestation(plAf);
        distrDeforestation(plDef);
        doSpecChange(plSpc);
        // Soil stocks update due to affor/defor
        // August 2010
        changeSoilStocks();
        m_BareFund.applyChanges();
        m_BareFund.clearChanges();
        // Just reforestation
        doAfforSimple(1.0);

        // Natural mortality
        doNaturalMortality();
        
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            if (pTable != null) {
                pTable.growV4();
                ret += pTable.updateV4();
                m_mTables.put(uKey, pTable);
            }
        }
        // Soils development
        if (m_bIsStart) {
            reInitSoilStocks(0);
            m_bIsStart = false;
        }
        soilGo();
        //GMSoil pSol;
        //for (Long uKey : m_mTables.keySet())
        //{
        //  pSol = m_mSoils.get(uKey);
        //	ret += pTable.update();
        //}
        return ret;
    }

    /**
     * Step of simulation presentation.
     * @return
     */
    public float goAhead () throws GMParLocator.GMParLocatorException {
        float ret = 0;
        float thh,fh;
        thh = doThinnings(m_ThinInt);
        //thh = doThinningsV4(m_ThinInt);
        fh = doFellings(m_FelInt);
        m_afThinVolume.addData(thh);
        m_afFellVolume.addData(fh);

        if (m_bIsStart)
            setDeadWood();

        // Natural mortality
        doNaturalMortality();

        if (m_bIsStart) {
            reInitSoilStocks(0);
            m_bIsStart = false;
        }

        // Fast bare lands grow 
        doAfforSimple(1.0);
        GMMatrix pTable;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            if (pTable != null) {
                //pTable.grow();
                pTable.growV4();
                ret += pTable.updateV4();
                m_mTables.put(uKey, pTable);
            }
        }
        // Soils developmnet
        soilGo();
        //GMSoil pSol;
        //for (Long uKey : m_mTables.keySet())
        //{
        //  pSol = m_mTables.get(uKey);
        //	ret += pTable.update();
        //}

        // Not Fast bare lands grow EFI!
        //doAfforSimple(1.0);
        return ret;
    }
    
    /**
     * Soil development.
     * @return
     */
    public float soilGo () throws GMParLocator.GMParLocatorException {
        float ret = 0.0f;
        GMSoil pSol;
        for (Long uKey : m_mSoils.keySet())
        {
            pSol = m_mSoils.get(uKey);
            GMLitterCollect pLc = new GMLitterCollect();
            GMCarbonAlloc pCa = new GMCarbonAlloc();
            // Carbon allocate structure filling
            pCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pCa.setCa_cstem(0.0);
            pCa.setCa_cbranch(0.0);
            pCa.setCa_ccroots(0.0);
            pCa.setCa_cfroots(0.0);
            pCa.setCa_cleaves(0.0);
            // Litter collect structure filling
            pLc.setLc_pCalloc(pCa);
            pLc.setLc_nsize(m_plLtrCompXvals.getParameter(uKey).m_nSize);
            pLc.setLc_pxvals(m_plLtrCompXvals.getParameter(uKey).m_Vals);
            pLc.setLc_pstem(m_plLtrStemShare.getParameter(uKey).m_Vals);
            pLc.setLc_pbranch(m_plLtrBranchShare.getParameter(uKey).m_Vals);
            pLc.setLc_pcroots(m_plLtrCrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pfroots(m_plLtrFrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pleaves(m_plLtrLeavesShare.getParameter(uKey).m_Vals);
            pLc.setLc_cstem(0.0);
            pLc.setLc_cbranch(0.0);
            pLc.setLc_ccroots(0.0);
            pLc.setLc_cfroots(0.0);
            pLc.setLc_cleaves(0.0);
            pLc = collectLitter(uKey,pLc);
            double ccwl = 0,cfwl = 0,cnwl = 0;
            if (pLc != null) {
                ccwl = pLc.getLc_cstem();
                cfwl = pLc.getLc_cbranch() + pLc.getLc_ccroots();
                cnwl = pLc.getLc_cleaves() + pLc.getLc_cfroots();
            }
            pSol.addLitter(cnwl,cfwl,ccwl);
            pSol.m_CoutBasket = 0.0;
            for (int i=0;i<m_nStep;i++)
                ret += pSol.yearStep();
            //pSol.m_CwBasket = 0.0;
            //pSol.m_FwBasket = 0.0;
            //pSol.m_NwBasket = 0.0;
            m_mSoils.put(uKey, pSol);
        }
        return ret;
    }

    /**
     * Area reporting of subset of matrixes.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return total area
     */
    public float getArea (long lr, long lo, long lst, long lsp) {
        float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=pTable.getArea();
        }
        return retval;
    }

    /**
     * Increment reporting of subset of matrixes.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return total incerement
     */
    public float getIncrement (long lr, long lo, long lst, long lsp) {
        float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=pTable.getIncrement();
        }
        return retval/m_nStep;
    }

    /**
     * Area of Zero class (Afforestation fund) reporting of subset of matrixes.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return total barearea
     */
    public float getZeroClass (long lr, long lo, long lst, long lsp) {
        float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=pTable.m_BareArea;
        }
        return retval;
    }

    /**
     * @deprecated 
     * Value of Y reporting of subset of matrixes.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return total y value
     */
    public float getValue (long lr, long lo, long lst, long lsp) {
        float retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=pTable.getValue();
        }
        return retval;
    }

    /**
     * Value of Carbon reporting of subset of matrixes.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pCa carbon
     * @return total carbon biomass
     */
    public float getCarbon (long lr, long lo, long lst, long lsp,
            GMCarbonAlloc pCa) throws GMParLocator.GMParLocatorException {
        float retval = 0;
        long ulKey, uKeyCopy;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);
            uKeyCopy = uKey;
            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey) {
                // Carbon allocate structure filling
                pCa.setCa_ccont(m_plCcont.getParameterValue(uKeyCopy,0));
                pCa.setCa_dns(m_plWoodDens.getParameterValue(uKeyCopy,0));
                pCa.setCa_nsize(m_plCompXvals.getParameter(uKeyCopy).m_nSize);
                pCa.setCa_pxvals(m_plCompXvals.getParameter(uKeyCopy).m_Vals);
                pCa.setCa_pstem(m_plStemShare.getParameter(uKeyCopy).m_Vals);
                pCa.setCa_pbranch(m_plBranchShare.getParameter(uKeyCopy).m_Vals);
                pCa.setCa_pcroots(m_plCrootsShare.getParameter(uKeyCopy).m_Vals);
                pCa.setCa_pfroots(m_plFrootsShare.getParameter(uKeyCopy).m_Vals);
                pCa.setCa_pleaves(m_plLeavesShare.getParameter(uKeyCopy).m_Vals);

                retval+=pTable.getBiomass(pCa);
            }
        }
        return retval;
    }

    /**
     * Soil stocks reporting (only selected).
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @param pSc soil stocks
     * @return GMSoilComp containing the requested soil stocks
     */
    public GMSoilComp getSoilStocks (long lr, long lo, long lst, long lsp,
            GMSoilComp pSc) {
        long ulKey;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        GMSoil pSol = m_mSoils.get(ulKey);
        if (pSol != null) {
            pSc = pSol.reportStocks(pSc);
            m_mSoils.put(ulKey, pSol);
        }
        return pSc;
    }

    /**
     * Soil stocks reporting all soils in the Model.
     * @param pSc soil stocks
     * @return GMSoilComp containing the all soil stocks
     */
    public GMSoilComp getAllSoils (GMSoilComp pSc) {
        GMSoil pEl;
        for (Long uKey : m_mSoils.keySet())
        {
            pEl = m_mSoils.get(uKey);
            if (pEl != null) {
                pSc = pEl.reportStocks(pSc);
                m_mSoils.put(uKey, pEl);
            }
        }
        return pSc;
    }

    /**
     * Change soil stocks due to afforestation/deforestation.
     * August 2010
     * @return changes in stocks
     */
    public float changeSoilStocks () {
        float retval = 0;
        GMSoil pEl;
        for (Long uKey : m_mSoils.keySet())
        {
            pEl = m_mSoils.get(uKey);
            if (pEl != null) {
                if (pEl.getInOut() != 0.0) {
                    retval+=pEl.changeCarbon(getAreaBySoil(uKey));
                    m_mSoils.put(uKey, pEl);
                }
            }
        }
        return retval;
    }

    /**
     * Litter income reporting.
     * @param ulKey key for the litter
     * @param pLc collection of litter
     * @return GMLitterCollect containing the income litter
     */
    public GMLitterCollect collectLitter (long ulKey, GMLitterCollect pLc) {
        //float retval = 0;
        long lr,lo,lst,lsp;
        GMMatrix pTable;

        lr = ulKey>>24;
        lo = ulKey & 0xFF0000;
        lo = lo>>16;
        lst = ulKey & 0xFF00;
        lst = lst>>8;
        lsp = ulKey & 0xFF;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                //retval+=pTable.getLitter(pLc);
                pLc = pTable.getLitter(pLc);
        }
        return pLc;
    }

    /**
     * Gets the area of matrixes related to the soil. April 2010
     * @param ulKey key for the area
     * @return total area by soil
     */
    public float getAreaBySoil (long ulKey) {
        float retval = 0;
        long lr,lo,lst,lsp;
        GMMatrix pTable;

        lr = ulKey>>24;
        lo = ulKey & 0xFF0000;
        lo = lo>>16;
        lst = ulKey & 0xFF00;
        lst = lst>>8;
        lsp = ulKey & 0xFF;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=pTable.getArea();
        }
        return retval;
    }

    /**
     * Initialisation of initial stocks for soil.
     * @return total stock
     */
    public float initSoilStocks () throws GMParLocator.GMParLocatorException {
        float retval = 0;
        GMSoil pSol;
        GMLitterCollect pLc;
        GMCarbonAlloc pCa;

        for (Long uKey : m_mSoils.keySet())
        {
            pSol = m_mSoils.get(uKey);
            pLc = new GMLitterCollect();
            pCa = new GMCarbonAlloc();
            // Carbon allocate structure filling
            pCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pCa.setCa_cstem(0.0);
            pCa.setCa_cbranch(0.0);
            pCa.setCa_ccroots(0.0);
            pCa.setCa_cfroots(0.0);
            pCa.setCa_cleaves(0.0);
            // Litter collect structure filling
            pLc.setLc_pCalloc(pCa);
            pLc.setLc_nsize(m_plLtrCompXvals.getParameter(uKey).m_nSize);
            pLc.setLc_pxvals(m_plLtrCompXvals.getParameter(uKey).m_Vals);
            pLc.setLc_pstem(m_plLtrStemShare.getParameter(uKey).m_Vals);
            pLc.setLc_pbranch(m_plLtrBranchShare.getParameter(uKey).m_Vals);
            pLc.setLc_pcroots(m_plLtrCrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pfroots(m_plLtrFrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pleaves(m_plLtrLeavesShare.getParameter(uKey).m_Vals);
            pLc.setLc_cstem(0.0);
            pLc.setLc_cbranch(0.0);
            pLc.setLc_ccroots(0.0);
            pLc.setLc_cfroots(0.0);
            pLc.setLc_cleaves(0.0);
            pLc = collectLitter(uKey,pLc);
            double ccwl = 0,cfwl = 0,cnwl = 0;
            if (pLc != null) {
                ccwl = pLc.getLc_cstem();
                cfwl = pLc.getLc_cbranch() + pLc.getLc_ccroots();
                cnwl = pLc.getLc_cleaves() + pLc.getLc_cfroots();
            }
            retval+=pSol.initStocks(cnwl,cfwl,ccwl);
            m_mSoils.put(uKey, pSol);
        }
        return retval;
    }

    /**
     * Initialisation of initial stocks for soil.
     * Special version to take into account fellings - bug fixing!
     * Is being used only on the first step of simulation!
     * @param nstep number of steps
     * @return total stock
     */
    public float reInitSoilStocks (int nstep) throws GMParLocator.GMParLocatorException {
        GMSoilComp scStock = new GMSoilComp();
        GMSoil pSol;
        GMLitterCollect pLc;
        GMCarbonAlloc pCa;
        float retval = 0;

        for (Long uKey : m_mSoils.keySet())
        {
            pSol = m_mSoils.get(uKey);
            pLc = new GMLitterCollect();
            pCa = new GMCarbonAlloc();
            // Carbon allocate structure filling
            pCa.setCa_ccont(m_plCcont.getParameterValue(uKey,0));
            pCa.setCa_dns(m_plWoodDens.getParameterValue(uKey,0));
            pCa.setCa_nsize(m_plCompXvals.getParameter(uKey).m_nSize);
            pCa.setCa_pxvals(m_plCompXvals.getParameter(uKey).m_Vals);
            pCa.setCa_pstem(m_plStemShare.getParameter(uKey).m_Vals);
            pCa.setCa_pbranch(m_plBranchShare.getParameter(uKey).m_Vals);
            pCa.setCa_pcroots(m_plCrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pfroots(m_plFrootsShare.getParameter(uKey).m_Vals);
            pCa.setCa_pleaves(m_plLeavesShare.getParameter(uKey).m_Vals);
            pCa.setCa_cstem(0.0);
            pCa.setCa_cbranch(0.0);
            pCa.setCa_ccroots(0.0);
            pCa.setCa_cfroots(0.0);
            pCa.setCa_cleaves(0.0);
            // Litter collect structure filling
            pLc.setLc_pCalloc(pCa);
            pLc.setLc_nsize(m_plLtrCompXvals.getParameter(uKey).m_nSize);
            pLc.setLc_pxvals(m_plLtrCompXvals.getParameter(uKey).m_Vals);
            pLc.setLc_pstem(m_plLtrStemShare.getParameter(uKey).m_Vals);
            pLc.setLc_pbranch(m_plLtrBranchShare.getParameter(uKey).m_Vals);
            pLc.setLc_pcroots(m_plLtrCrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pfroots(m_plLtrFrootsShare.getParameter(uKey).m_Vals);
            pLc.setLc_pleaves(m_plLtrLeavesShare.getParameter(uKey).m_Vals);
            pLc.setLc_cstem(0.0);
            pLc.setLc_cbranch(0.0);
            pLc.setLc_ccroots(0.0);
            pLc.setLc_cfroots(0.0);
            pLc.setLc_cleaves(0.0);
            pLc = collectLitter(uKey,pLc);
            double ccwl = 0,cfwl = 0,cnwl = 0;
            if (pLc != null) {
                ccwl = pLc.getLc_cstem();
                cfwl = pLc.getLc_cbranch() + pLc.getLc_ccroots();
                cnwl = pLc.getLc_cleaves() + pLc.getLc_cfroots();
            }
            cnwl+=pSol.m_NwBasket;
            cfwl+=pSol.m_FwBasket;
            ccwl+=pSol.m_CwBasket;

            retval+=pSol.reInitStocks(cnwl,cfwl,ccwl);
            // All soils must be updated! 
            scStock.setSc_cwl(0.0f);
            scStock.setSc_fwl(0.0f);
            scStock.setSc_nwl(0.0f);
            scStock.setSc_sol(0.0f);
            scStock.setSc_cel(0.0f);
            scStock.setSc_lig(0.0f);
            scStock.setSc_hm1(0.0f);
            scStock.setSc_hm2(0.0f);
            scStock.setSc_clost(0.0f);

            scStock = pSol.reportStocks(scStock);

            ComArFlt<Float> pComar;
            pComar = m_mafSoilCwl.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_cwl());
                m_mafSoilCwl.put(uKey, pComar);
            }
            pComar = m_mafSoilFwl.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_fwl());
                m_mafSoilFwl.put(uKey, pComar);
            }
            pComar = m_mafSoilNwl.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_nwl());
                m_mafSoilNwl.put(uKey, pComar);
            }
            pComar = m_mafSoilSol.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_sol());
                m_mafSoilSol.put(uKey, pComar);
            }
            pComar = m_mafSoilCel.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_cel());
                m_mafSoilCel.put(uKey, pComar);
            }
            pComar = m_mafSoilLig.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_lig());
                m_mafSoilLig.put(uKey, pComar);
            }
            pComar = m_mafSoilHm1.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_hm1());
                m_mafSoilHm1.put(uKey, pComar);
            }
            pComar = m_mafSoilHm2.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_hm2());
                m_mafSoilHm2.put(uKey, pComar);
            }
            pComar = m_mafSoilClost.get(uKey);
            if (pComar != null) {
                pComar.setData(nstep,scStock.getSc_clost());
                m_mafSoilClost.put(uKey, pComar);
            }
            float ctotal = scStock.getSc_cwl() + scStock.getSc_cel() +
                    scStock.getSc_fwl() + scStock.getSc_hm1() +
                    scStock.getSc_hm2() + scStock.getSc_lig() +
                    scStock.getSc_nwl() + scStock.getSc_sol();
            pComar = m_mafCSoil.get(uKey);
            if (pComar != null) {
                pComar.addData(ctotal);
                m_mafCSoil.put(uKey, pComar);
            }

            m_mSoils.put(uKey, pSol);
        }
        // Soils update
        //GMSoilComp scStock;
        scStock.setSc_cwl(0.0f);
        scStock.setSc_fwl(0.0f);
        scStock.setSc_nwl(0.0f);
        scStock.setSc_sol(0.0f);
        scStock.setSc_cel(0.0f);
        scStock.setSc_lig(0.0f);
        scStock.setSc_hm1(0.0f);
        scStock.setSc_hm2(0.0f);
        scStock.setSc_clost(0.0f);
        scStock = getAllSoils(scStock);
        m_afSoilCwl.setData(nstep,scStock.getSc_cwl());
        m_afSoilFwl.setData(nstep,scStock.getSc_fwl());
        m_afSoilNwl.setData(nstep,scStock.getSc_nwl());
        m_afSoilSol.setData(nstep,scStock.getSc_sol());
        m_afSoilCel.setData(nstep,scStock.getSc_cel());
        m_afSoilLig.setData(nstep,scStock.getSc_lig());
        m_afSoilHm1.setData(nstep,scStock.getSc_hm1());
        m_afSoilHm2.setData(nstep,scStock.getSc_hm2());
        m_afSoilClost.setData(nstep,scStock.getSc_clost());
        return retval;
    }

    /**
     * Soil climate variables changing.
     * @param beta
     * @param gamma
     * @param tvar
     * @param pvar
     * @return number of soils
     */
    public float setSoilClimateVars (float beta, float gamma, float tvar,
            float pvar) {
        int retval = 0;
        GMClFunction pCfun = new GMClFunction();
        GMSoil pEl;

        pCfun.setCf_beta((double)beta);
        pCfun.setCf_gamma((double)gamma);
        pCfun.setCf_tav((double)tvar);
        pCfun.setCf_di((double)pvar);
        for (Long uKey : m_mSoils.keySet())
        {
            pEl = m_mSoils.get(uKey);
            pEl.setClFunction(pCfun);
            m_mSoils.put(uKey, pEl);
            retval+=1;
        }
        return retval;
    }

    /**
     * Sets the soil climate.
     * @param plSoilCl soil climate
     * @return number of soils
     */
    public float setSoilClimate (GMParLocator plSoilCl) throws GMParLocator.GMParLocatorException {
        int retval = 0;
        double tav,di;
        GMSoil pEl;

        for (Long uKey : m_mSoils.keySet())
        {
            pEl = m_mSoils.get(uKey);
            tav = (double)plSoilCl.getParameterValue(uKey,0);
            di  = (double)plSoilCl.getParameterValue(uKey,1);
            pEl.setClimate((float)tav,(float)di);
            m_mSoils.put(uKey, pEl);
            retval+=1;
        }
        return retval;
    }

    /**
     * Forest regrow by climate.
     * @param plForCl forest regrow
     * @return number of matrixes
     */
    public float setForestClimate (GMParLocator plForCl) throws GMParLocator.GMParLocatorException {
        int retval = 0;
        float ratio;
        GMMatrix pEl;

        for (Long uKey : m_mTables.keySet())
        {
            pEl = m_mTables.get(uKey);
            ratio = plForCl.getParameterValue(uKey,0);
            pEl.setClimGrow(ratio);
            m_mTables.put(uKey, pEl);
            retval+=1;
        }
        return retval;
    }

    /**
     * Forest regrow by Climate with ages and ratios.
     * @param plAgeLims ages
     * @param plRatios ratios
     * @return number of matrixes
     */
    public int setForestClimateV4 (GMParLocator plAgeLims,
            GMParLocator plRatios) throws GMParLocator.GMParLocatorException {
        int retval = 0;
        ArrayList<Float> pAlims, pRatios;
        int narsize;
        GMMatrix pEl;

        for (Long uKey : m_mTables.keySet())
        {
            pEl = m_mTables.get(uKey);
            pAlims = plAgeLims.getParameter(uKey).m_Vals;
            pRatios = plRatios.getParameter(uKey).m_Vals;
            narsize = plAgeLims.getParameter(uKey).m_nSize;
            //ratio = plForCl.getParameterValue(uKey,0);
            pEl.setClimGrowV4(pAlims,pRatios,narsize);
            m_mTables.put(uKey, pEl);
            retval+=1;
        }
        return retval;
    }

    /**
     * Number of matrixes (in selection) reporting.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return number of matrixes
     */
    public int getNumMatr (long lr, long lo, long lst, long lsp) {
        int retval;
        retval = 0;
        long ulKey;
        GMMatrix pTable;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mTables.keySet())
        {
            pTable = m_mTables.get(uKey);

            if (lr == 0)  uKey = uKey & ~0xFF000000;
            if (lo == 0)  uKey = uKey & ~0xFF0000;
            if (lst == 0) uKey = uKey & ~0xFF00;
            if (lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=1;
        }
        return retval;
    }

    /**
     * Add region, site, owner or species data to the given collection.
     * @param m_mCollection collection of data
     * @param pAdd data to added
     */
    public void addCollection (HashMap m_mCollection, GMCollection pAdd) {
        m_mCollection.put((long)pAdd.m_ucID, pAdd);
    }

    /**
     * Adds the given matrix to the current list of tables and initializes
     * the data keeping storages.
     * @param pAdd the matrix to be added
     */
    public void addTable (GMMatrix pAdd) {
        m_mTables.put(pAdd.m_wID,pAdd);
        m_BareFund.addArea(pAdd.m_wID,0.0);
        double arIn = 0.0;
        m_BareFund.m_mdIncome.put(pAdd.m_wID,arIn);
        m_BareFund.m_mdOutcome.put(pAdd.m_wID,arIn);

        // Data keeping storages initialization
        m_mafGrStock.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafThinnings.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafFellings.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafArea.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafIncrement.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafAvrIncrement.put(pAdd.m_wID,new ComArFlt<Float>());
        grsprev.put(pAdd.m_wID,0f);
        m_mafBiomass.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafDeadWood.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafNatMort.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafAfforFund.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafBareArea.put(pAdd.m_wID,new ComArFlt<Float>());   
        m_mafPotentialFellingsVolume.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafPotentialFellingsArea.put(pAdd.m_wID,new ComArFlt<Float>());
        
        // Areas and stocks distributions
        m_mafAreas.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafStocks.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        // Compartments
        m_mafCStem.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafCLeaves.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafCBranches.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafCCRoots.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafCFRoots.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        // Mefique stuff and Bioenergy!
        m_mafMfqFelAreas.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafMfqFelRems.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafMfqThAreas.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafMfqThRems.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafBeFelSlash.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafBeThSlash.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        // Residuals
        m_mafFelRsd.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(4));
        m_mafThRsd.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(4));
        m_mafFelRsdRem.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(4));
        m_mafThRsdRem.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(4));

        // Natural mortality Femke and MJ :-)
        m_mafNatMortDistr.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
        m_mafDeadWoodDistr.put(pAdd.m_wID,new ComArFlt<ArrayList<Float>>(16));
    }

    /**
     * Adds the given soil to the current list of soils.
     * @param pAdd the soil to be added
     */
    public void addSoil (GMSoil pAdd) {
        m_mSoils.put(pAdd.m_wID,pAdd);
        m_mafSoilCwl.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilNwl.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilFwl.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilCel.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilSol.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilLig.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilHm1.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilHm2.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilClost.put(pAdd.m_wID,new ComArFlt<Float>());
        m_mafSoilCwlIn.put(pAdd.m_wID,new ComArFlt<Double>());
        m_mafSoilFwlIn.put(pAdd.m_wID,new ComArFlt<Double>());
        m_mafSoilNwlIn.put(pAdd.m_wID,new ComArFlt<Double>());
        m_mafSoilInOut.put(pAdd.m_wID,new ComArFlt<Double>());
        m_mafCSoil.put(pAdd.m_wID,new ComArFlt<Float>());
    }
    
    /**
     * Get the hashmap of regions.
     * @return hashmap of regions
     */
    public HashMap getRegions () {
        return m_mRegions;
    }

    /**
     * Get the hashmap of owners.
     * @return hashmap of owners
     */
    public HashMap getOwners () {
        return m_mOwners;
    }

    /**
     * Get the hashmap of sites.
     * @return hashmap of sites
     */
    public HashMap getSites () {
        return m_mSites;
    }

    /**
     * Get the hashmap of species.
     * @return hashmap of species
     */
    public HashMap getSpecies () {
        return m_mSpecies;
    }

    /**
     * Get the hashmap of soils.
     * @return hashmap of soils
     */
    public HashMap getSoils () {
        return m_mSoils;
    }
    
    /**
     * Sets HarvestAge depending on step of the simulation if felling change
     * file is provided in scenario file
     * @param plFellAge 
     */
    public void setHarvestAge (GMParLocator plFellAge) throws GMParLocator.GMParLocatorException {
        if (!m_plHarvestAge.equals(plFellAge)) {
            m_plHarvestAge = plFellAge;
            GMParArray parAr;
            GMMatrix pTable;
            float ageh;
            float minageNetti, maxageNetti;
            for (Long uKey : m_mTables.keySet()) {
                pTable = m_mTables.get(uKey);
                parAr = m_plHarvestAge.getParameter(pTable.m_wID);
                if (parAr != null) {
                    if (parAr.m_nSize == 6) {
                        minageNetti = parAr.m_Vals.get(0);
                        maxageNetti = parAr.m_Vals.get(1);
                        float abage = (float) ((1.0 - parAr.m_Vals.get(5)) * parAr.m_Vals.get(0));
                        pTable.setFellingsRegimes(minageNetti, maxageNetti, parAr.m_Vals.get(2),
                                parAr.m_Vals.get(3), parAr.m_Vals.get(4), abage);

                    } else {
                        ageh = parAr.m_Vals.get(0);
                        pTable.setFellingsSimple(ageh);
                    }
                }
            }
        }
    }
    
    /**
     * Sets ThinRange depending on step of the simulation if thinning change
     * file is provided in scenario file
     * @param plThinAge 
     */
    public void setThinRange (GMParLocator plThinAge) throws GMParLocator.GMParLocatorException {
        if (!m_plThinRange.equals(plThinAge)) {
            m_plThinRange = plThinAge;
            GMMatrix pTable = null;
            Float ageh, agel;
            for (Long uKey : m_mTables.keySet()) {
                pTable = m_mTables.get(uKey);
                agel = m_plThinRange.getParameterValue(pTable.m_wID, 0);
                ageh = m_plThinRange.getParameterValue(pTable.m_wID, 1);
                if (agel < 5.) agel = 5.0f;
                pTable.setThinningsSimple(agel, ageh);
            }
        }
    }

}
