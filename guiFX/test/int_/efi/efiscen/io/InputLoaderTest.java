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
package int_.efi.efiscen.io;

import efi.efiscen.io.EFISCENFileNotFoundException;
import efi.efiscen.io.EFISCENException;
import efi.efiscen.io.InputLoader;
import efi.efiscen.io.EFISCENFileParsingException;
import efi.efiscen.gm.GMScenario;
import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMParLocator;
import efi.efiscen.gm.GMParArray;
import efi.efiscen.gm.GMEfiscenario;
import efi.efiscen.gm.GMSoil;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class InputLoaderTest extends TestCase {
    
    public InputLoaderTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of loadExperiment method, of class InputLoader.
     */
    public void testLoadExperiment() throws EFISCENFileNotFoundException, EFISCENException {
       /* System.out.println("loadExperiment");
        String sFileIn = "testutopia.efs";
        InputLoader instance = new InputLoader();
        AtomicInteger numErrors = new AtomicInteger();
        GMEfiscen result = instance.loadExperiment(sFileIn,numErrors);
        GMEfiscen expectedResult = new GMEfiscen();
        expectedResult.m_nBaseYear = 1990;
        expectedResult.m_sName = "Utopia";
        expectedResult.m_mTables.put(0l, new GMMatrix());
        assertEquals(result.m_nBaseYear, expectedResult.m_nBaseYear);
        assertEquals(result.m_sName, expectedResult.m_sName);
        for( long li : result.m_mTables.keySet()) {
            assertTrue(result.m_mTables.get(li)!=null);
        }*/
        //assertEquals(result.m_mRegions, expectedResult.m_mRegions);
    }

    /**
     * Test of loadScenario method, of class InputLoader.
     */
    public void testLoadScenario() throws EFISCENFileNotFoundException {
        System.out.println("loadScenario");
        String sFileIn = "testutopia.scn";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        expResult.m_sName = "Utopia base (business as ususal harvest)";
        AtomicInteger numErrors = new AtomicInteger();
        GMScenario result = instance.loadScenario(sFileIn,numErrors);
        assertTrue(result.m_sName.equals(expResult.m_sName));
    }

    /**
     * Test of loadBioParameters method, of class InputLoader.
     */
    public void testLoadBioParameters() throws EFISCENFileNotFoundException {
        System.out.println("loadBioParameters");
        String sFileIn = "bio-utopia.txt";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.5f);
        expResult.m_plCcont.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.4f);
        expResult.m_plWoodDens.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plCompXvals.addParameter(pPar);
        
        //0.557 0.569 0.581 0.59 0.598 0.605 0.611
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.557f);
        pPar.m_Vals.add(0.569f);
        pPar.m_Vals.add(0.581f);
        pPar.m_Vals.add(0.59f);
        pPar.m_Vals.add(0.598f);
        pPar.m_Vals.add(0.605f);
        pPar.m_Vals.add(0.611f);
        expResult.m_plStemShare.addParameter(pPar);
        
        //0.15 0.143 0.138 0.134 0.131 0.128 0.125
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.15f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.138f);
        pPar.m_Vals.add(0.134f);
        pPar.m_Vals.add(0.131f);
        pPar.m_Vals.add(0.128f);
        pPar.m_Vals.add(0.125f);
        expResult.m_plBranchShare.addParameter(pPar);
        
        //0.133 0.143 0.151 0.157 0.163 0.167 0.171
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.133f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.151f);
        pPar.m_Vals.add(0.157f);
        pPar.m_Vals.add(0.163f);
        pPar.m_Vals.add(0.167f);
        pPar.m_Vals.add(0.171f);
        expResult.m_plCrootsShare.addParameter(pPar);
        
        //0.072 0.067 0.062 0.058 0.054 0.051 0.049
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.072f);
        pPar.m_Vals.add(0.067f);
        pPar.m_Vals.add(0.062f);
        pPar.m_Vals.add(0.058f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.051f);
        pPar.m_Vals.add(0.049f);
        expResult.m_plFrootsShare.addParameter(pPar);
        
        //0.088 0.078 0.068 0.061 0.054 0.049 0.044
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.088f);
        pPar.m_Vals.add(0.078f);
        pPar.m_Vals.add(0.068f);
        pPar.m_Vals.add(0.061f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.049f);
        pPar.m_Vals.add(0.044f);
        expResult.m_plLeavesShare.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plLtrCompXvals.addParameter(pPar);
        
        //0.0043 0.0043 0.0043 0.0043 0.0043 0.0043 0.0043
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        expResult.m_plLtrStemShare.addParameter(pPar);
        
        //0.0614 0.0463 0.0266 0.0142 0.0091 0.0076 0.0071
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrBranchShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrCrootsShare.addParameter(pPar);
        
        //0.4906 0.4906 0.4906 0.4906 0.4906 0.4906 0.4906
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.4906f);
        expResult.m_plLtrFrootsShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.21f);
        expResult.m_plLtrLeavesShare.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.0f);
        expResult.m_plCroots2CWL.addParameter(pPar);
        
        instance.loadBioParameters(sFileIn);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("ccont", expResult.m_plCcont.equals(efiscen.m_plCcont));
        assertTrue("wooddens", expResult.m_plWoodDens.equals(efiscen.m_plWoodDens));
        assertTrue("compxvals", expResult.m_plCompXvals.equals(efiscen.m_plCompXvals));
        assertTrue("stemshare", expResult.m_plStemShare.equals(efiscen.m_plStemShare));
        /**
         * TODO these don't work yet
         */
     /*   assertTrue("branchshare", expResult.m_plBranchShare.equals(efiscen.m_plBranchShare));
        assertTrue("crootsshare", expResult.m_plCrootsShare.equals(efiscen.m_plCrootsShare));
        assertTrue("frootsshare", expResult.m_plFrootsShare.equals(efiscen.m_plFrootsShare));
        assertTrue("leavesshare", expResult.m_plLeavesShare.equals(efiscen.m_plLeavesShare));*/
        
        assertTrue("ltrcompxvals", expResult.m_plLtrCompXvals.equals(efiscen.m_plLtrCompXvals));
        assertTrue("m_plLtrStemShare", expResult.m_plLtrStemShare.equals(efiscen.m_plLtrStemShare));
        assertTrue("m_plLtrBranchShare", expResult.m_plLtrBranchShare.equals(efiscen.m_plLtrBranchShare));
        assertTrue("m_plLtrCrootsShare", expResult.m_plLtrCrootsShare.equals(efiscen.m_plLtrCrootsShare));
        assertTrue("m_plLtrFrootsShare", expResult.m_plLtrFrootsShare.equals(efiscen.m_plLtrFrootsShare));
        assertTrue("m_plLtrLeavesShare", expResult.m_plLtrLeavesShare.equals(efiscen.m_plLtrLeavesShare));
        assertTrue("m_plCroots2CWL", expResult.m_plCroots2CWL.equals(efiscen.m_plCroots2CWL));
    }
    
    public void testLoadBioParametersExperiment() throws EFISCENFileNotFoundException {
        System.out.println("loadBioParameters");
        String sFileIn = "bio-utopia.txt";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.5f);
        expResult.m_plCcont.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.4f);
        expResult.m_plWoodDens.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plCompXvals.addParameter(pPar);
        
        //0.557 0.569 0.581 0.59 0.598 0.605 0.611
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.557f);
        pPar.m_Vals.add(0.569f);
        pPar.m_Vals.add(0.581f);
        pPar.m_Vals.add(0.59f);
        pPar.m_Vals.add(0.598f);
        pPar.m_Vals.add(0.605f);
        pPar.m_Vals.add(0.611f);
        expResult.m_plStemShare.addParameter(pPar);
        
        //0.15 0.143 0.138 0.134 0.131 0.128 0.125
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.15f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.138f);
        pPar.m_Vals.add(0.134f);
        pPar.m_Vals.add(0.131f);
        pPar.m_Vals.add(0.128f);
        pPar.m_Vals.add(0.125f);
        expResult.m_plBranchShare.addParameter(pPar);
        
        //0.133 0.143 0.151 0.157 0.163 0.167 0.171
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.133f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.151f);
        pPar.m_Vals.add(0.157f);
        pPar.m_Vals.add(0.163f);
        pPar.m_Vals.add(0.167f);
        pPar.m_Vals.add(0.171f);
        expResult.m_plCrootsShare.addParameter(pPar);
        
        //0.072 0.067 0.062 0.058 0.054 0.051 0.049
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.072f);
        pPar.m_Vals.add(0.067f);
        pPar.m_Vals.add(0.062f);
        pPar.m_Vals.add(0.058f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.051f);
        pPar.m_Vals.add(0.049f);
        expResult.m_plFrootsShare.addParameter(pPar);
        
        //0.088 0.078 0.068 0.061 0.054 0.049 0.044
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.088f);
        pPar.m_Vals.add(0.078f);
        pPar.m_Vals.add(0.068f);
        pPar.m_Vals.add(0.061f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.049f);
        pPar.m_Vals.add(0.044f);
        expResult.m_plLeavesShare.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plLtrCompXvals.addParameter(pPar);
        
        //0.0043 0.0043 0.0043 0.0043 0.0043 0.0043 0.0043
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        expResult.m_plLtrStemShare.addParameter(pPar);
        
        //0.0614 0.0463 0.0266 0.0142 0.0091 0.0076 0.0071
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrBranchShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrCrootsShare.addParameter(pPar);
        
        //0.4906 0.4906 0.4906 0.4906 0.4906 0.4906 0.4906
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.4906f);
        expResult.m_plLtrFrootsShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.21f);
        expResult.m_plLtrLeavesShare.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.0f);
        expResult.m_plCroots2CWL.addParameter(pPar);
        
        instance.loadBioParameters(sFileIn);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("ccont", expResult.m_plCcont.equals(efiscen.m_plCcont));
        assertTrue("wooddens", expResult.m_plWoodDens.equals(efiscen.m_plWoodDens));
        assertTrue("compxvals", expResult.m_plCompXvals.equals(efiscen.m_plCompXvals));
        assertTrue("stemshare", expResult.m_plStemShare.equals(efiscen.m_plStemShare));
        /**
         * TODO these don't work yet
         */
     /*   assertTrue("branchshare", expResult.m_plBranchShare.equals(efiscen.m_plBranchShare));
        assertTrue("crootsshare", expResult.m_plCrootsShare.equals(efiscen.m_plCrootsShare));
        assertTrue("frootsshare", expResult.m_plFrootsShare.equals(efiscen.m_plFrootsShare));
        assertTrue("leavesshare", expResult.m_plLeavesShare.equals(efiscen.m_plLeavesShare));*/
        
        assertTrue("ltrcompxvals", expResult.m_plLtrCompXvals.equals(efiscen.m_plLtrCompXvals));
        assertTrue("m_plLtrStemShare", expResult.m_plLtrStemShare.equals(efiscen.m_plLtrStemShare));
        assertTrue("m_plLtrBranchShare", expResult.m_plLtrBranchShare.equals(efiscen.m_plLtrBranchShare));
        assertTrue("m_plLtrCrootsShare", expResult.m_plLtrCrootsShare.equals(efiscen.m_plLtrCrootsShare));
        assertTrue("m_plLtrFrootsShare", expResult.m_plLtrFrootsShare.equals(efiscen.m_plLtrFrootsShare));
        assertTrue("m_plLtrLeavesShare", expResult.m_plLtrLeavesShare.equals(efiscen.m_plLtrLeavesShare));
        assertTrue("m_plCroots2CWL", expResult.m_plCroots2CWL.equals(efiscen.m_plCroots2CWL));
    }
    
    /**
     * loads all data and checks that bio-parameter data is no
     * overwritten.
     */
    public void testLoadBioParametersFull() throws EFISCENFileNotFoundException, EFISCENException {
       /* System.out.println("loadBioParameters");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.5f);
        expResult.m_plCcont.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.4f);
        expResult.m_plWoodDens.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plCompXvals.addParameter(pPar);
        
        //0.557 0.569 0.581 0.59 0.598 0.605 0.611
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.557f);
        pPar.m_Vals.add(0.569f);
        pPar.m_Vals.add(0.581f);
        pPar.m_Vals.add(0.59f);
        pPar.m_Vals.add(0.598f);
        pPar.m_Vals.add(0.605f);
        pPar.m_Vals.add(0.611f);
        expResult.m_plStemShare.addParameter(pPar);
        
        //0.15 0.143 0.138 0.134 0.131 0.128 0.125
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.15f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.138f);
        pPar.m_Vals.add(0.134f);
        pPar.m_Vals.add(0.131f);
        pPar.m_Vals.add(0.128f);
        pPar.m_Vals.add(0.125f);
        expResult.m_plBranchShare.addParameter(pPar);
        
        //0.133 0.143 0.151 0.157 0.163 0.167 0.171
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.133f);
        pPar.m_Vals.add(0.143f);
        pPar.m_Vals.add(0.151f);
        pPar.m_Vals.add(0.157f);
        pPar.m_Vals.add(0.163f);
        pPar.m_Vals.add(0.167f);
        pPar.m_Vals.add(0.171f);
        expResult.m_plCrootsShare.addParameter(pPar);
        
        //0.072 0.067 0.062 0.058 0.054 0.051 0.049
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.072f);
        pPar.m_Vals.add(0.067f);
        pPar.m_Vals.add(0.062f);
        pPar.m_Vals.add(0.058f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.051f);
        pPar.m_Vals.add(0.049f);
        expResult.m_plFrootsShare.addParameter(pPar);
        
        //0.088 0.078 0.068 0.061 0.054 0.049 0.044
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.088f);
        pPar.m_Vals.add(0.078f);
        pPar.m_Vals.add(0.068f);
        pPar.m_Vals.add(0.061f);
        pPar.m_Vals.add(0.054f);
        pPar.m_Vals.add(0.049f);
        pPar.m_Vals.add(0.044f);
        expResult.m_plLeavesShare.addParameter(pPar);
        
        //20 40 60 80 100 120 1000
        pPar = new GMParArray(7);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(40f);
        pPar.m_Vals.add(60f);
        pPar.m_Vals.add(80f);
        pPar.m_Vals.add(100f);
        pPar.m_Vals.add(120f);
        pPar.m_Vals.add(1000f);
        expResult.m_plLtrCompXvals.addParameter(pPar);
        
        //0.0043 0.0043 0.0043 0.0043 0.0043 0.0043 0.0043
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        pPar.m_Vals.add(0.0043f);
        expResult.m_plLtrStemShare.addParameter(pPar);
        
        //0.0614 0.0463 0.0266 0.0142 0.0091 0.0076 0.0071
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrBranchShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.0614f);
        pPar.m_Vals.add(0.0463f);
        pPar.m_Vals.add(0.0266f);
        pPar.m_Vals.add(0.0142f);
        pPar.m_Vals.add(0.0091f);
        pPar.m_Vals.add(0.0076f);
        pPar.m_Vals.add(0.0071f);
        expResult.m_plLtrCrootsShare.addParameter(pPar);
        
        //0.4906 0.4906 0.4906 0.4906 0.4906 0.4906 0.4906
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.4906f);
        expResult.m_plLtrFrootsShare.addParameter(pPar);
        
        pPar = new GMParArray(7);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        for(int i = 0; i < 7; i++)
            pPar.m_Vals.add(0.21f);
        expResult.m_plLtrLeavesShare.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.0f);
        expResult.m_plCroots2CWL.addParameter(pPar);
        
        AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        sFileIn = "utopia.scn";
        instance.loadScenario(sFileIn,numErrors);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("ccont", expResult.m_plCcont.equals(efiscen.m_plCcont));
        assertTrue("wooddens", expResult.m_plWoodDens.equals(efiscen.m_plWoodDens));
        assertTrue("compxvals", expResult.m_plCompXvals.equals(efiscen.m_plCompXvals));
        assertTrue("stemshare", expResult.m_plStemShare.equals(efiscen.m_plStemShare));*/
        /**
         * TODO these don't work yet
         */
     /*   assertTrue("branchshare", expResult.m_plBranchShare.equals(efiscen.m_plBranchShare));
        assertTrue("crootsshare", expResult.m_plCrootsShare.equals(efiscen.m_plCrootsShare));
        assertTrue("frootsshare", expResult.m_plFrootsShare.equals(efiscen.m_plFrootsShare));
        assertTrue("leavesshare", expResult.m_plLeavesShare.equals(efiscen.m_plLeavesShare));*/
        
       /* assertTrue("ltrcompxvals", expResult.m_plLtrCompXvals.equals(efiscen.m_plLtrCompXvals));
        assertTrue("m_plLtrStemShare", expResult.m_plLtrStemShare.equals(efiscen.m_plLtrStemShare));
        assertTrue("m_plLtrBranchShare", expResult.m_plLtrBranchShare.equals(efiscen.m_plLtrBranchShare));
        assertTrue("m_plLtrCrootsShare", expResult.m_plLtrCrootsShare.equals(efiscen.m_plLtrCrootsShare));
        assertTrue("m_plLtrFrootsShare", expResult.m_plLtrFrootsShare.equals(efiscen.m_plLtrFrootsShare));
        assertTrue("m_plLtrLeavesShare", expResult.m_plLtrLeavesShare.equals(efiscen.m_plLtrLeavesShare));
        assertTrue("m_plCroots2CWL", expResult.m_plCroots2CWL.equals(efiscen.m_plCroots2CWL));*/
    }

    /**
     * Test of loadSoils method, of class InputLoader.
     */
    public void testLoadSoils() throws EFISCENFileNotFoundException {
       /* System.out.println("loadSoils");
        String sFileIn = "soil_utopia.par";
        InputLoader instance = new InputLoader();
        GMEfiscen expResult = new GMEfiscen();
        instance.loadSoils(sFileIn);
        
        GMSoil soil = new GMSoil( 0, 0, 0, 0, 0, 0, 0, 0,
                0.053, 0.54, 1.0, 0.48, 0.3, 0.22, 0.012, 0.0012,
                0.2, 0.2, 0.2, 0.2, 0.69, 0.03, 0.65, 0.03, 0.51, 
                0.27, 0.6, 0.36 );
        
        long ulID, ulRes;
        ulID = (long) 1;
        ulRes = ulID<<24;
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<16);
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<8);
        ulID = (long) 1;
        ulRes = ulRes + ulID;
        soil.m_wID = ulRes;
        
        expResult.addSoil(soil);
        
        instance.loadSoils(sFileIn);
        GMEfiscen result = instance.getM_pExperiment();
        assertTrue("m_mSoils", expResult.m_mSoils.equals(result.m_mSoils));*/
    }
    
    public void testLoadSoilsExperiment() throws EFISCENFileNotFoundException, EFISCENException {
       /* System.out.println("loadSoils");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        GMEfiscen expResult = new GMEfiscen();
        instance.loadSoils(sFileIn);
        
        GMSoil soil = new GMSoil( 0, 0, 0, 0, 0, 0, 0, 0,
                0.053, 0.54, 1.0, 0.48, 0.3, 0.22, 0.012, 0.0012,
                0.2, 0.2, 0.2, 0.2, 0.69, 0.03, 0.65, 0.03, 0.51, 
                0.27, 0.6, 0.36 );
        
        long ulID, ulRes;
        ulID = (long) 1;
        ulRes = ulID<<24;
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<16);
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<8);
        ulID = (long) 1;
        ulRes = ulRes + ulID;
        soil.m_wID = ulRes;
        
        expResult.addSoil(soil);
        AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        GMEfiscen result = instance.getM_pExperiment();
        assertTrue("m_mSoils", expResult.m_mSoils.equals(result.m_mSoils));*/
    }
    
    /**
     * Load all data and check that parameter-data is not 
     * overwritten.
     */
    public void testLoadSoilsFull() throws EFISCENFileNotFoundException, EFISCENException {
        System.out.println("loadSoils");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        GMEfiscen expResult = new GMEfiscen();
        instance.loadSoils(sFileIn);
        
        GMSoil soil = new GMSoil( 0, 0, 0, 0, 0, 0, 0, 0,
                0.053, 0.54, 1.0, 0.48, 0.3, 0.22, 0.012, 0.0012,
                0.2, 0.2, 0.2, 0.2, 0.69, 0.03, 0.65, 0.03, 0.51, 
                0.27, 0.6, 0.36 );
        
        long ulID, ulRes;
        ulID = (long) 1;
        ulRes = ulID<<24;
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<16);
        ulID = (long) 0;
        ulRes = ulRes + (ulID<<8);
        ulID = (long) 1;
        ulRes = ulRes + ulID;
        soil.m_wID = ulRes;
        
        expResult.addSoil(soil);
        float beta = 0.105f;
        float gamma = 0.00274f;
        float tvar = 4f;
        float pvar = -50f;
        expResult.setSoilClimateVars(beta, gamma, tvar, pvar);
        
 /*       GMEfiscenario pSc = new GMEfiscenario();
        pSc.setEs_nStep(pData.get(0).intValue());
        GMParArray pPar = new GMParArray(2);
        int ind = 4*i;
        pPar.m_uRegion = (int)pIDs.get(ind);
        pPar.m_uOwner  = (int)pIDs.get(ind+1);
        pPar.m_uSite   = (int)pIDs.get(ind+2);
        pPar.m_uSpecies = (int)pIDs.get(ind+3);
        pPar.m_Vals.add(pData.get(2*i+1));
        pPar.m_Vals.add(pData.get(2*i+2));
        pSc.getEs_paData().addParameter(pPar);
        m_Scenario.m_plSoilClim.add(pSc);*/
      /*  AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        sFileIn = "utopia.scn";
        instance.loadScenario(sFileIn,numErrors);
        GMEfiscen result = instance.getM_pExperiment();
        assertTrue("m_mSoils", expResult.m_mSoils.equals(result.m_mSoils));*/
    }

    /**
     * Test of loadParameters method, of class InputLoader.
     */
    public void testLoadParameters() throws EFISCENFileNotFoundException, EFISCENFileParsingException {
        System.out.println("loadParameters");
        String sFileIn = "utopia.prs";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        expResult.m_nStep = 5;
        expResult.m_nBaseYear = 1990;
        
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeClasses.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolClasses.addParameter(pPar);
        
        pPar = new GMParArray(3);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals = new ArrayList<>();
        pPar.m_Vals.add(-2.0384f);
        pPar.m_Vals.add(1604.33f);
        pPar.m_Vals.add(-10256.0f);
        expResult.m_plGrCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.6f);
        expResult.m_plYoungCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plRegrowCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(75f);
        expResult.m_plHarvestAge.addParameter(pPar);
        
        pPar = new GMParArray(2);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(70f);
        expResult.m_plThinRange.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plBeta.addParameter(pPar);
        
        pPar = new GMParArray(9);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        for(int i = 1; i <= 9; i++) {
            pPar.m_Vals.add(20f*i);
        }
        expResult.m_plAgeLims.addParameter(pPar);
        
        pPar = new GMParArray(15);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(14f);
        pPar.m_Vals.add(89f);
        pPar.m_Vals.add(158f);
        pPar.m_Vals.add(183f);
        pPar.m_Vals.add(200f);
        pPar.m_Vals.add(205f);
        pPar.m_Vals.add(210f);
        pPar.m_Vals.add(218f);
        pPar.m_Vals.add(226f);
        for(int i = 0; i < 6; i++)
            pPar.m_Vals.add(228f);
        expResult.m_plVolSers.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.02f);
        expResult.m_plMortRate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
        instance.loadParameters(sFileIn);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("step", expResult.m_nStep == efiscen.m_nStep);
        assertTrue("agenu,", expResult.m_plAgeNum.equals(efiscen.m_plAgeNum));
        assertTrue("ageclasses", expResult.m_plAgeClasses.equals(efiscen.m_plAgeClasses));
        assertTrue("volnum", expResult.m_plVolNum.equals(efiscen.m_plVolNum));
        assertTrue("volclasses", expResult.m_plVolClasses.equals(efiscen.m_plVolClasses));
        assertTrue("grcoeff.", expResult.m_plGrCoeff.equals(efiscen.m_plGrCoeff));
        assertTrue("youngcoeff.", expResult.m_plYoungCoeff.equals(efiscen.m_plYoungCoeff));
        assertTrue("regrowcoeff.", expResult.m_plRegrowCoeff.equals(efiscen.m_plRegrowCoeff));
        assertTrue("harvest age", expResult.m_plHarvestAge.equals(efiscen.m_plHarvestAge));
        assertTrue("thin range", expResult.m_plThinRange.equals(efiscen.m_plThinRange));
        assertTrue("beta", expResult.m_plBeta.equals(efiscen.m_plBeta));
        assertTrue("age limits", expResult.m_plAgeLims.equals(efiscen.m_plAgeLims));
        assertTrue("volsers", expResult.m_plVolSers.equals(efiscen.m_plVolSers));
        assertTrue("mortratexvals", expResult.m_plMortRateXvals.equals(efiscen.m_plMortRateXvals));
        assertTrue("mortrate", expResult.m_plMortRate.equals(efiscen.m_plMortRate));
        assertTrue("deadwood", expResult.m_plDeadWoodDrate.equals(efiscen.m_plDeadWoodDrate));
    }
    
    public void testLoadParametersExperiment() throws EFISCENFileNotFoundException, EFISCENException {
        System.out.println("loadParameters");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        expResult.m_nStep = 5;
        expResult.m_nBaseYear = 1990;
        
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeClasses.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolClasses.addParameter(pPar);
        
        pPar = new GMParArray(3);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals = new ArrayList<>();
        pPar.m_Vals.add(-2.0384f);
        pPar.m_Vals.add(1604.33f);
        pPar.m_Vals.add(-10256.0f);
        expResult.m_plGrCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.6f);
        expResult.m_plYoungCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plRegrowCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(75f);
        expResult.m_plHarvestAge.addParameter(pPar);
        
        pPar = new GMParArray(2);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(70f);
        expResult.m_plThinRange.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plBeta.addParameter(pPar);
        
        pPar = new GMParArray(9);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        for(int i = 1; i <= 9; i++) {
            pPar.m_Vals.add(20f*i);
        }
        expResult.m_plAgeLims.addParameter(pPar);
        
        pPar = new GMParArray(15);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(14f);
        pPar.m_Vals.add(89f);
        pPar.m_Vals.add(158f);
        pPar.m_Vals.add(183f);
        pPar.m_Vals.add(200f);
        pPar.m_Vals.add(205f);
        pPar.m_Vals.add(210f);
        pPar.m_Vals.add(218f);
        pPar.m_Vals.add(226f);
        for(int i = 0; i < 6; i++)
            pPar.m_Vals.add(228f);
        expResult.m_plVolSers.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.02f);
        expResult.m_plMortRate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
       /* AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("step", expResult.m_nStep == efiscen.m_nStep);
        assertTrue("agenu,", expResult.m_plAgeNum.equals(efiscen.m_plAgeNum));
        assertTrue("ageclasses", expResult.m_plAgeClasses.equals(efiscen.m_plAgeClasses));
        assertTrue("volnum", expResult.m_plVolNum.equals(efiscen.m_plVolNum));
        assertTrue("volclasses", expResult.m_plVolClasses.equals(efiscen.m_plVolClasses));
        assertTrue("grcoeff.", expResult.m_plGrCoeff.equals(efiscen.m_plGrCoeff));
        assertTrue("youngcoeff.", expResult.m_plYoungCoeff.equals(efiscen.m_plYoungCoeff));
        assertTrue("regrowcoeff.", expResult.m_plRegrowCoeff.equals(efiscen.m_plRegrowCoeff));
        assertTrue("harvest age", expResult.m_plHarvestAge.equals(efiscen.m_plHarvestAge));
        assertTrue("thin range", expResult.m_plThinRange.equals(efiscen.m_plThinRange));
        assertTrue("beta", expResult.m_plBeta.equals(efiscen.m_plBeta));
        assertTrue("age limits", expResult.m_plAgeLims.equals(efiscen.m_plAgeLims));
        assertTrue("volsers", expResult.m_plVolSers.equals(efiscen.m_plVolSers));
        assertTrue("mortratexvals", expResult.m_plMortRateXvals.equals(efiscen.m_plMortRateXvals));
        assertTrue("mortrate", expResult.m_plMortRate.equals(efiscen.m_plMortRate));
        assertTrue("deadwood", expResult.m_plDeadWoodDrate.equals(efiscen.m_plDeadWoodDrate));*/
    }
    
    public void testLoadParametersFull() throws EFISCENFileNotFoundException, EFISCENException {
        System.out.println("loadParameters");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        expResult.m_nStep = 5;
        expResult.m_nBaseYear = 1990;
        
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plAgeClasses.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolNum.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        expResult.m_plVolClasses.addParameter(pPar);
        
        pPar = new GMParArray(3);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals = new ArrayList<>();
        pPar.m_Vals.add(-2.0384f);
        pPar.m_Vals.add(1604.33f);
        pPar.m_Vals.add(-10256.0f);
        expResult.m_plGrCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 1;
        pPar.m_Vals.add(0.6f);
        expResult.m_plYoungCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plRegrowCoeff.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(75f);
        expResult.m_plHarvestAge.addParameter(pPar);
        
        pPar = new GMParArray(2);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(20f);
        pPar.m_Vals.add(70f);
        expResult.m_plThinRange.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.4f);
        expResult.m_plBeta.addParameter(pPar);
        
        pPar = new GMParArray(9);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        for(int i = 1; i <= 9; i++) {
            pPar.m_Vals.add(20f*i);
        }
        expResult.m_plAgeLims.addParameter(pPar);
        
        pPar = new GMParArray(15);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(14f);
        pPar.m_Vals.add(89f);
        pPar.m_Vals.add(158f);
        pPar.m_Vals.add(183f);
        pPar.m_Vals.add(200f);
        pPar.m_Vals.add(205f);
        pPar.m_Vals.add(210f);
        pPar.m_Vals.add(218f);
        pPar.m_Vals.add(226f);
        for(int i = 0; i < 6; i++)
            pPar.m_Vals.add(228f);
        expResult.m_plVolSers.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1000f);
        expResult.m_plMortRateXvals.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.02f);
        expResult.m_plMortRate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.3867f);
        expResult.m_plDeadWoodDrate.addParameter(pPar);
        
       /* AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        sFileIn = "utopia.scn";
        instance.loadScenario(sFileIn,numErrors);
        GMEfiscen efiscen = instance.getM_pExperiment();
        assertTrue("step", expResult.m_nStep == efiscen.m_nStep);
        assertTrue("agenu,", expResult.m_plAgeNum.equals(efiscen.m_plAgeNum));
        assertTrue("ageclasses", expResult.m_plAgeClasses.equals(efiscen.m_plAgeClasses));
        assertTrue("volnum", expResult.m_plVolNum.equals(efiscen.m_plVolNum));
        assertTrue("volclasses", expResult.m_plVolClasses.equals(efiscen.m_plVolClasses));
        assertTrue("grcoeff.", expResult.m_plGrCoeff.equals(efiscen.m_plGrCoeff));
        assertTrue("youngcoeff.", expResult.m_plYoungCoeff.equals(efiscen.m_plYoungCoeff));
        assertTrue("regrowcoeff.", expResult.m_plRegrowCoeff.equals(efiscen.m_plRegrowCoeff));
        assertTrue("harvest age", expResult.m_plHarvestAge.equals(efiscen.m_plHarvestAge));
        assertTrue("thin range", expResult.m_plThinRange.equals(efiscen.m_plThinRange));
        assertTrue("beta", expResult.m_plBeta.equals(efiscen.m_plBeta));
        assertTrue("age limits", expResult.m_plAgeLims.equals(efiscen.m_plAgeLims));
        assertTrue("volsers", expResult.m_plVolSers.equals(efiscen.m_plVolSers));
        assertTrue("mortratexvals", expResult.m_plMortRateXvals.equals(efiscen.m_plMortRateXvals));
        assertTrue("mortrate", expResult.m_plMortRate.equals(efiscen.m_plMortRate));
        assertTrue("deadwood", expResult.m_plDeadWoodDrate.equals(efiscen.m_plDeadWoodDrate));*/
    }

    /**
     * Test of loadYLimits method, of class InputLoader.
     */
    public void testLoadYLimits() throws EFISCENFileNotFoundException, EFISCENFileParsingException {
        System.out.println("loadYLimits");
        String sFileIn = "utopiatest.vcl";
        InputLoader instance = new InputLoader();
        
        GMParLocator expResult = new GMParLocator();
        GMParArray pPar = new GMParArray(10);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 1;
        pPar.m_uSite   = 1;
        pPar.m_uSpecies = 1;
        /*
         55.
        110.
        165.
        220.
        275.
        330.
        385.
        440.
        495.
        550.*/
        pPar.m_Vals.add(55f);
        pPar.m_Vals.add(110f);
        pPar.m_Vals.add(165f);
        pPar.m_Vals.add(220f);
        pPar.m_Vals.add(275f);
        pPar.m_Vals.add(330f);
        pPar.m_Vals.add(385f);
        pPar.m_Vals.add(440f);
        pPar.m_Vals.add(495f);
        pPar.m_Vals.add(550f);
        expResult.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(10f);
        
        GMEfiscen efiscen = instance.getM_pExperiment();
        efiscen.m_plVolNum.addParameter(pPar);
        
        GMParLocator result = instance.loadYLimits(sFileIn);
        assertEquals(expResult, result);
    }
    
    public void testLoadYLimitsExperiment() throws EFISCENFileNotFoundException, EFISCENException {
        System.out.println("loadYLimits");
        String sFileIn = "utopia.efs";
        InputLoader instance = new InputLoader();
        
        GMEfiscen expResult = new GMEfiscen();
        GMParLocator pGMPar = new GMParLocator();
        GMParArray pPar = new GMParArray(10);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 1;
        pPar.m_uSite   = 1;
        pPar.m_uSpecies = 1;
        /*
         55.
        110.
        165.
        220.
        275.
        330.
        385.
        440.
        495.
        550.*/
       /* pPar.m_Vals.add(55f);
        pPar.m_Vals.add(110f);
        pPar.m_Vals.add(165f);
        pPar.m_Vals.add(220f);
        pPar.m_Vals.add(275f);
        pPar.m_Vals.add(330f);
        pPar.m_Vals.add(385f);
        pPar.m_Vals.add(440f);
        pPar.m_Vals.add(495f);
        pPar.m_Vals.add(550f);
        pGMPar.addParameter(pPar);
        
        pPar = new GMParArray(1);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(10f);
        
        expResult.m_plVolNum.addParameter(pPar);
        
        AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        GMEfiscen result = instance.getM_pExperiment();
       // efiscen.m_plVolNum.addParameter(pPar); 
        assertEquals(expResult.m_plVolNum,result.m_plVolNum);
        assertEquals(expResult.m_plVolNum,result.m_plVolNum);*/
    }

    /**
     * Test of loadForClim method, of class InputLoader.
     */
/*    public void testLoadForClim() {
        System.out.println("loadForClim");
        String sFileIn = "uto_defsoil.csv";
        InputLoader instance = new InputLoader();
        boolean expResult = false;
        boolean result = instance.loadForClim(sFileIn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of registerListener method, of class InputLoader.
     */
/*    class InputListenerStub implements InputLoaderListener {

        public void onLoadClimateHeader(String name) {
            boolean r = true;
            assertTrue(r);
        }

        public void onLoadCuttingHeader(String name) {
            boolean r = true;
            assertTrue(r);
        }
        
    }
    public void testRegisterListener() {
        System.out.println("registerListener");
        InputLoaderListener listener = new InputListenerStub();
        InputLoader instance = new InputLoader();
        instance.registerListener(listener);
        instance.loadExperiment("utopia.efs");
    }*/

    /**
     * Test of loadForClimV4 method, of class InputLoader.
     */
    public void testLoadForClimV4() throws EFISCENFileNotFoundException {
        System.out.println("loadForClimV4");
        String sFileIn = "uto_defgrowtest.csv";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        //100,0.95,0,0.0,0,0.0,0,0.9,0,0.0,0,0.0,0
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion  = 0;
        pPar.m_uOwner   = 0;
        pPar.m_uSite    = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(300f);
        expResult.m_plClimAgeLims.addParameter(pPar);
        
        GMEfiscenario pSc = new GMEfiscenario();
        pPar = new GMParArray(1);
        pPar.m_uRegion  = 0;
        pPar.m_uOwner   = 0;
        pPar.m_uSite    = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(1f);
        pSc.getEs_paData().addParameter(pPar);
        pSc.setEs_nStep(1000);
        expResult.m_plForClim.add(pSc);
        
        instance.loadForClimV4(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue("1", result.m_plForClim.equals(expResult.m_plForClim));
        assertTrue("2", result.m_plClimAgeLims.equals(expResult.m_plClimAgeLims));
    }
    
    

    /**
     * Test of loadSoilClim method, of class InputLoader.
     */
    public void testLoadSoilClim() throws EFISCENFileNotFoundException {
        System.out.println("loadSoilClim");
        String sFileIn = "uto_defsoil.csv";
        InputLoader instance = new InputLoader();      
        GMScenario expResult = new GMScenario();
        
        GMEfiscenario pSc = new GMEfiscenario();
        pSc.setEs_nStep(100);
        //expResult.setSoilClimateVars(0.105f,0.00274f,4f,-50f);
        GMParArray pPar = new GMParArray(2);
        pPar.m_uRegion = 1;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        //3.180942545,-59.56649925
        //pPar.m_Vals.add(100f);
        pPar.m_Vals.add(3.180942545f);
        pPar.m_Vals.add(-59.56649925f);
        pSc.getEs_paData().addParameter(pPar);
        
        expResult.m_plSoilClim.add(pSc);
        
        instance.loadSoilClim(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue("m_plSoilClim", 
                result.m_plSoilClim.equals(expResult.m_plSoilClim));
    }

    /**
     * Test of loadFelProps method, of class InputLoader.
     */
/*    public void testLoadFelProps() {
        System.out.println("loadFelProps");
        String sFileIn = "";
        InputLoader instance = new InputLoader();
        boolean expResult = false;
        boolean result = instance.loadFelProps(sFileIn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of loadFelPropsEx method, of class InputLoader.
     */
    public void testLoadFelPropsEx() throws EFISCENFileNotFoundException {
        System.out.println("loadFelPropsEx");
        String sFileIn = "uto_defremstest.csv";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        //100,0.95,0,0.0,0,0.0,0,0.9,0,0.0,0,0.0,0
        GMEfiscenario pSc = new GMEfiscenario();
        GMParArray pPar = new GMParArray(12);
        //100, 0.95, 0, 0, 0, 0, 0, 0.9, 0, 0, 0, 0, 0
        pPar.m_uRegion  = 0;
        pPar.m_uOwner   = 0;
        pPar.m_uSite    = 0;
        pPar.m_uSpecies = 0;
        pPar.m_Vals.add(0.95f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0.9f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        pSc.setEs_nStep(100);
        pSc.getEs_paData().addParameter(pPar);
        expResult.m_plCutProps.add(pSc);
        
        instance.loadFelPropsEx(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue("test1",expResult.m_plCutProps.equals(result.m_plCutProps));
    }

    /**
     * Test of loadBusiness method, of class InputLoader.
     */
    public void testLoadBusiness() throws EFISCENFileNotFoundException {
        System.out.println("loadBusiness");
        String sFileIn = "uto_defcuttest.csv";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        for(int i = 0; i < 8; i++) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(i+1);
            //49425.66016,8978.420898,
            GMParArray pPar = new GMParArray(2);
            pPar.m_uRegion  = 0;
            pPar.m_uOwner   = 0;
            pPar.m_uSite    = 0;
            pPar.m_uSpecies = 0;
            switch(i) {
                case 0 :{
                    //pPar.m_Vals.add(1f);
                    pPar.m_Vals.add(49425.66016f);
                    pPar.m_Vals.add(8978.420898f);
                    break;
                }
               case 1 :{
                   //pPar.m_Vals.add(2f);
                    pPar.m_Vals.add(32515.86133f);
                    pPar.m_Vals.add(6503.633301f);
                    break;
                }
                case 2 :{
                    //pPar.m_Vals.add(3f);
                    pPar.m_Vals.add(23468.51953f);
                    pPar.m_Vals.add(6709.018066f);
                    break;
                }
                case 3 :{
                    //pPar.m_Vals.add(4f);
                    pPar.m_Vals.add(19241.44922f);
                    pPar.m_Vals.add(7189.809082f);
                    break;
                }
                case 4 :{
                    //pPar.m_Vals.add(5f);
                    pPar.m_Vals.add(15334.39551f);
                    pPar.m_Vals.add(7654.942383f);
                    break;
                }
                case 5 :{
                    //pPar.m_Vals.add(6f);
                    pPar.m_Vals.add(13852.08691f);
                    pPar.m_Vals.add(10798.77637f);
                    break;
                }
                case 6 :{
                    //pPar.m_Vals.add(7f);
                    pPar.m_Vals.add(13723.27441f);
                    pPar.m_Vals.add(12759.62012f);
                    break;
                }
                case 7 :{
                    //pPar.m_Vals.add(8f);
                    pPar.m_Vals.add(14350.2627f);
                    pPar.m_Vals.add(13377.79785f);
                    break;
                }
            }
            pSc.getEs_paData().addParameter(pPar);
            expResult.m_plCuttings.add(pSc);
        }
        GMParArray pPar = new GMParArray(2);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        // First value is fellings, second one is thínnings!
        pPar.m_Vals.add(1.0f);
        pPar.m_Vals.add(1.0f);
        expResult.m_plCutRatios.addParameter(pPar);
            
        instance.loadBusiness(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue("test1",result.m_plCuttings.equals(expResult.m_plCuttings));
        assertTrue("test2",result.m_plCutRatios.equals(expResult.m_plCutRatios));
    }
    
    /**
     * Test of loadBusiness method, of class InputLoader.
     */
    public void testLoadBusinessFull() throws EFISCENFileNotFoundException, EFISCENException {
        System.out.println("loadBusiness"); 
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        for(int i = 0; i < 8; i++) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(i+1);
            //49425.66016,8978.420898,
            GMParArray pPar = new GMParArray(2);
            pPar.m_uRegion  = 0;
            pPar.m_uOwner   = 0;
            pPar.m_uSite    = 0;
            pPar.m_uSpecies = 0;
            switch(i) {
                case 0 :{
                    //pPar.m_Vals.add(1f);
                    pPar.m_Vals.add(49425.66016f);
                    pPar.m_Vals.add(8978.420898f);
                    break;
                }
               case 1 :{
                   //pPar.m_Vals.add(2f);
                    pPar.m_Vals.add(32515.86133f);
                    pPar.m_Vals.add(6503.633301f);
                    break;
                }
                case 2 :{
                    //pPar.m_Vals.add(3f);
                    pPar.m_Vals.add(23468.51953f);
                    pPar.m_Vals.add(6709.018066f);
                    break;
                }
                case 3 :{
                    //pPar.m_Vals.add(4f);
                    pPar.m_Vals.add(19241.44922f);
                    pPar.m_Vals.add(7189.809082f);
                    break;
                }
                case 4 :{
                    //pPar.m_Vals.add(5f);
                    pPar.m_Vals.add(15334.39551f);
                    pPar.m_Vals.add(7654.942383f);
                    break;
                }
                case 5 :{
                    //pPar.m_Vals.add(6f);
                    pPar.m_Vals.add(13852.08691f);
                    pPar.m_Vals.add(10798.77637f);
                    break;
                }
                case 6 :{
                    //pPar.m_Vals.add(7f);
                    pPar.m_Vals.add(13723.27441f);
                    pPar.m_Vals.add(12759.62012f);
                    break;
                }
                case 7 :{
                    //pPar.m_Vals.add(8f);
                    pPar.m_Vals.add(14350.2627f);
                    pPar.m_Vals.add(13377.79785f);
                    break;
                }
            }
            pSc.getEs_paData().addParameter(pPar);
            expResult.m_plCuttings.add(pSc);
        }
        GMParArray pPar = new GMParArray(2);
        pPar.m_uRegion = 0;
        pPar.m_uOwner  = 0;
        pPar.m_uSite   = 0;
        pPar.m_uSpecies = 0;
        // First value is fellings, second one is thínnings!
        pPar.m_Vals.add(1.0f);
        pPar.m_Vals.add(1.0f);
        expResult.m_plCutRatios.addParameter(pPar);
        
    /*    String sFileIn = "utopia.efs";
        AtomicInteger numErrors = new AtomicInteger();
        instance.loadExperiment(sFileIn,numErrors);
        sFileIn = "utopia.scn";
        instance.loadScenario(sFileIn,numErrors);
        GMScenario result = instance.getM_Scenario();
        assertTrue("test1",result.m_plCuttings.equals(expResult.m_plCuttings));
        assertTrue("test2",result.m_plCutRatios.equals(expResult.m_plCutRatios));*/
    }

    /**
     * Test of loadAforestation method, of class InputLoader.
     */
    public void testLoadAforestation() throws EFISCENFileNotFoundException {
        System.out.println("loadAforestation");
        String sFileIn = "no_affortest.csv";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        GMEfiscenario pSc = new GMEfiscenario();
        GMParArray pPar = new GMParArray(2);
        pPar.m_uRegion  = 0;
        pPar.m_uOwner   = 0;
        pPar.m_uSite    = 0;
        pPar.m_uSpecies = 0;
        //pPar.m_Vals.add(100f);
        pPar.m_Vals.add(0f);
        pPar.m_Vals.add(0f);
        
        pSc.setEs_nStep(100);
        pSc.getEs_paData().addParameter(pPar);
        expResult.m_plAfor.add(pSc);
        
        instance.loadAforestation(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue(result.m_plAfor.equals(expResult.m_plAfor));
        
    }

    /**
     * Test of loadDeforestation method, of class InputLoader.
     */
    public void testLoadDeforestation() throws EFISCENFileNotFoundException {
        System.out.println("loadDeforestation");
        String sFileIn = "no_defotestr.csv";
        InputLoader instance = new InputLoader();
        GMScenario expResult = new GMScenario();
        
        GMEfiscenario pSc = new GMEfiscenario();
        GMParArray pPar = new GMParArray(1);
        pPar.m_uRegion  = 0;
        pPar.m_uOwner   = 0;
        pPar.m_uSite    = 0;
        pPar.m_uSpecies = 0;
        //pPar.m_Vals.add(100f);
        pPar.m_Vals.add(0f);
        pSc.setEs_nStep(100);
        pSc.getEs_paData().addParameter(pPar);
        expResult.m_plDefor.add(pSc);
        
        instance.loadDeforestation(sFileIn);
        GMScenario result = instance.getM_Scenario();
        assertTrue(result.m_plDefor.equals(expResult.m_plDefor));
    }

    /**
     * Test of loadData method, of class InputLoader.
     */
/*    public void testLoadData() {
        System.out.println("loadData");
        String sFileIn = "utopiatest.aer";
        InputLoader instance = new InputLoader();
        GMMatrix expResult = new GMMatrix();
        
        Long id = (1<<24) + (1<<16) + (1<<8) + 1L;
        int na = 1;
        int nv = 1;
        int agew = 9;
        int volw = 10;
        int pagew = 20;
        float upperAge = 20;
            
        GMMatrix result = instance.loadData(sFileIn,1);
        assertTrue(expResult.equals(result));
    }*/

    /**
     * Test of loadSpecChange method, of class InputLoader.
     */
/*    public void testLoadSpecChange() {
        System.out.println("loadSpecChange");
        String sFileIn = "";
        InputLoader instance = new InputLoader();
        boolean expResult = false;
        boolean result = instance.loadSpecChange(sFileIn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    } */
}
