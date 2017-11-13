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
package int_.efi.efiscen.gm;

import efi.efiscen.gm.GMCarbonAlloc;
import efi.efiscen.gm.GMCellInit;
import efi.efiscen.gm.GMMatrix;
import efi.efiscen.gm.GMLitterCollect;
import efi.efiscen.gm.GMMatrixInit;
import efi.efiscen.gm.GMCell;
import efi.efiscen.gm.GMFellings;
import efi.efiscen.gm.GMGrFunction;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class GMMatrixTest extends TestCase {
    
    public GMMatrixTest(String testName) {
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
     * Test of getIncrement method, of class GMMatrix.
     */
    public void testGetIncrement() {
        System.out.println("getIncrement");
        GMMatrix instance = new GMMatrix(5,5);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2);
        float expResult = 40.0F;
        float result = instance.getIncrement();
        assertEquals(expResult, result);
        float farea = instance.getArea();
        System.out.println(farea);
        instance.growV4();
        assertEquals(farea,instance.getArea());
        instance.updateV4();
        assertEquals(farea,instance.getArea());
        
    }

    /**
     * Test of setFellingsRegimes method, of class GMMatrix.
     */
    public void testSetFellingsRegimes() {
        System.out.println("setFellingsRegimes");
        float minage = 2.0F;
        float maxage = 2.0F;
        float mintr = 2.0F;
        float maxtr = 2.0F;
        float belowtr = 2.0F;
        float belowage = 2.0F;
        GMMatrix instance = new GMMatrix(1,1);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2);
        ArrayList<GMCell> expResult = new ArrayList<>(2);
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        cell.setM_FellingsShare(2);
        expResult.add(cell);
        expResult.add(cell);
        ArrayList<GMCell> result = instance.setFellingsRegimes(minage, maxage, mintr, maxtr, belowtr, belowage);
        //for (GMCell c : result)
        //    System.out.println(c);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i).getM_FellingsShare(), result.get(i).getM_FellingsShare());
    }

    /**
     * Test of setClimGrow method, of class GMMatrix.
     */
    public void testSetClimGrow() {
        System.out.println("setClimGrow");
        float ratio = 2.0F;
        GMMatrix instance = new GMMatrix(1,1);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2);
        ArrayList<GMCell> expResult = new ArrayList<>(2);
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        expResult.add(cell);
        expResult.add(cell);
        ArrayList<GMCell> result = instance.setClimGrow(ratio);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i).getM_MoveByXY(), result.get(i).getM_MoveByXY());
    }

    /**
     * Test of doThinning method, of class GMMatrix.
     */
    public void testDoThinning() {
        System.out.println("doThinning");
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(2,2f,2f,2.0,2.0,2.0,2.0,2.0);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(1.0f);
        pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(1.0f);
        pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(1.0f);
        pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(1.0f);
        croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(1.0f);
        froots.add(1.0f);
        pCa.setCa_pfroots(froots);

        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);

        /*
        ArrayList<GMCell> expResult = new ArrayList<GMCell>(2);
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        expResult.add(cell);
        expResult.add(cell); */
        double result = instance.doThinning(pFr, pCa);
        double expResult2 = 8.0;
        //for (GMCell c : instance.m_Cells)
        //    System.out.println(c);
        //for (int i = 0; i < result.size(); i++)
        assertEquals(expResult2, result);
    }

    /**
     * Test of doNaturalMortality method, of class GMMatrix.
     */
    public void testDoNaturalMortality() {
        System.out.println("doNaturalMortality");
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(2,2f,2f,2.0,2.0,2.0,2.0,2.0);
        ArrayList<Float> pLims = new ArrayList<>();
        pLims.add(1.0f);
        pLims.add(1.0f);
        ArrayList<Float> pRat = new ArrayList<>();
        pRat.add(1.0f);
        pRat.add(1.0f);
        int nsize = 2;

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(1.0f);
        pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(1.0f);
        pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(1.0f);
        pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(1.0f);
        croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(1.0f);
        froots.add(1.0f);
        pCa.setCa_pfroots(froots);

        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);

        double expResult = 8.0;
        double result = instance.doNaturalMortality(pFr, pCa, pLims, pRat, nsize);
        assertEquals(expResult, result);
    }

    /**
     * Test of initDeadWood method, of class GMMatrix.
     */
    public void testInitDeadWood() {
        System.out.println("initDeadWood");
        ArrayList<Float> pLims = new ArrayList<>();
        pLims.add(1.0f);
        pLims.add(1.0f);
        ArrayList<Float> pRat = new ArrayList<>();
        pRat.add(1.0f);
        pRat.add(1.0f);
        int nsize = 2;
        float dec = 1.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        
        double expResult = 8.0;
        double result = instance.initDeadWood(pLims, pRat, nsize, dec);
        assertEquals(expResult, result);
    }

    /**
     * Test of initDeadWoodEx method, of class GMMatrix.
     */
    public void testInitDeadWoodEx() {
        System.out.println("initDeadWoodEx");
        ArrayList<Float> pLims = new ArrayList<>();
        pLims.add(1.0f);
        pLims.add(1.0f);
        ArrayList<Float> pRat = new ArrayList<>();
        pRat.add(1.0f);
        pRat.add(1.0f);
        int nsize = 2;
        float decay = 1.0F;
        float threm = 1.0F;
        float felrem = 1.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        
        double expResult = 8.0;
        double result = instance.initDeadWoodEx(pLims, pRat, nsize, decay, threm, felrem);
        assertEquals(expResult, result);
    }

    /**
     * Test of reportHarvest method, of class GMMatrix.
     */
    public void testReportHarvest() {
        System.out.println("reportHarvest");
        GMFellings pFl = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        instance.setFellingsSimple(0);

        GMFellings expResult = new GMFellings(2f,18f,130f,2f,2f,2f,2f,2f);
        GMFellings result = instance.reportHarvest(pFl);
        //System.out.println(result);
        assertEquals(expResult.getF_volume(), result.getF_volume());
        assertEquals(expResult.getF_area(), result.getF_area());
    }

    /**
     * Test of reportThinnings method, of class GMMatrix.
     */
    public void testReportThinnings() {
        System.out.println("reportThinnings");
        GMFellings pFl = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        instance.setFellingsSimple(0);

        GMFellings expResult = new GMFellings(2f,6f,10f,2f,2f,2f,2f,2f);
        GMFellings result = instance.reportThinnings(pFl);
        //System.out.println(result);
        assertEquals(expResult.getF_volume(), result.getF_volume());
        assertEquals(expResult.getF_area(), result.getF_area());
    }

    /**
     * Test of reportThinningsV4 method, of class GMMatrix.
     */
    public void testReportThinningsV4() {
        System.out.println("reportThinningsV4");
        GMFellings pFl = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        instance.setFellingsSimple(0);

        GMFellings expResult = new GMFellings(2f,6f,18f,2f,2f,2f,2f,2f);
        GMFellings result = instance.reportThinningsV4(pFl);
        //System.out.println(result);
        assertEquals(expResult.getF_volume(), result.getF_volume());
        assertEquals(expResult.getF_area(), result.getF_area());
    }

    /**
     * Test of doThinningV4 method, of class GMMatrix.
     */
    public void testDoThinningV4() {
        System.out.println("doThinningV4");
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(2,2f,2f,2.0,2.0,2.0,2.0,2.0);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(1.0f);
        pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(1.0f);
        pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(1.0f);
        pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(1.0f);
        croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(1.0f);
        froots.add(1.0f);
        pCa.setCa_pfroots(froots);

        instance.initRegular(init);
        instance.fillRegular(2);
        instance.setThinningsSimple(0,5);
        instance.setFellingsSimple(0);

        double expResult = 16.0;
        double result = instance.doThinningV4(pFr, pCa);
        assertEquals(expResult, result);
    }

    /**
     * Test of growV4 method, of class GMMatrix.
     */
    public void testGrowV4() {
        System.out.println("growV4");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(2,2f,2f,2.0,2.0,2.0,2.0,2.0);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(1.0f);
        pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(1.0f);
        pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(1.0f);
        pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(1.0f);
        croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(1.0f);
        froots.add(1.0f);
        pCa.setCa_pfroots(froots);

        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setThinningsSimple(0,5);
        instance.setFellingsSimple(0);
        instance.setThinHistory(2.0f);
        instance.doThinningV4(pFr, pCa);
        //instance.doThinning(pFr, pCa);

        ArrayList<GMCell> expResult = new ArrayList<>(4);
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        expResult.add(cell);
        expResult.add(cell);
        cInit = new GMCellInit(2,1,0,2f,2f,2f,2f,2f,2f,2f);
        cell = new GMCell(cInit);
        cell.setM_Income(-1.5f);
        cell.setM_MoveAway(0.6f);
        expResult.add(cell);
        cInit = new GMCellInit(2,2,0,2f,2f,2f,2f,2f,2f,2f);
        cell = new GMCell(cInit);
        cell.setM_Income(4.9f);
        cell.setM_MoveAway(1.0f);
        expResult.add(cell);
        ArrayList<GMCell> result = instance.growV4();
        //for (GMCell c : result)
        //    System.out.println(c);
        for (int i = 0; i < result.size(); i++) {
            //System.out.println(result.get(i).getM_Income());
            assertEquals(expResult.get(i).getM_Income(), result.get(i).getM_Income());
            //System.out.println(result.get(i).getM_MoveAway());
            assertEquals(expResult.get(i).getM_MoveAway(), result.get(i).getM_MoveAway());
        }
    }

    /**
     * Test of updateV4 method, of class GMMatrix.
     */
    public void testUpdateV4() {
        System.out.println("updateV4");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);

        instance.initRegular(init);
        instance.fillRegular(2.0f);

        float expResult = 32.0F;
        float result = instance.updateV4();
        assertEquals(expResult, result);
    }

    /**
     * Test of setClimGrowV4 method, of class GMMatrix.
     */
    public void testSetClimGrowV4() {
        System.out.println("setClimGrowV4");
        ArrayList<Float> pLims = new ArrayList<>();
        pLims.add(1.0f);
        pLims.add(1.0f);
        ArrayList<Float> pRat = new ArrayList<>();
        pRat.add(1.0f);
        pRat.add(1.0f);
        int nsize = 2;

        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        int expResult = 0;
        int result = instance.setClimGrowV4(pLims, pRat, nsize);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLitter method, of class GMMatrix.
     */
    public void testGetLitter() {
        System.out.println("getLitter");
        GMLitterCollect pLc = new GMLitterCollect(2,2,2,2,2);
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);

        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        pLc.setLc_nsize(1);
        ArrayList<Float> tmp = new ArrayList<>(2);
        tmp.add(2f);
        //tmp.add(2f);
        pLc.setLc_pbranch(tmp);
        pLc.setLc_pcroots(tmp);
        pLc.setLc_pfroots(tmp);
        pLc.setLc_pleaves(tmp);
        pLc.setLc_pstem(tmp);
        pLc.setLc_pxvals(tmp);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        //pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        //pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        //pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        //croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        //froots.add(1.0f);
        pCa.setCa_pfroots(froots);

        pLc.setLc_pCalloc(pCa);
        
        double expResult = 514.0; // total litter
        GMLitterCollect result = instance.getLitter(pLc);
        assertEquals(expResult, result.getLc_cbranch());
    }

    /**
     * Test of setFellingsSimple method, of class GMMatrix.
     */
    public void testSetFellingsSimple() {
        System.out.println("setFellingsSimple");
        float age = 0.0F;
        GMMatrix instance = new GMMatrix(1,1);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        ArrayList<GMCell> expResult = new ArrayList<>();
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        cell.setM_FellingsShare(1.0f);
        expResult.add(cell);
        ArrayList<GMCell> result = instance.setFellingsSimple(age);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i).getM_FellingsShare(), result.get(i).getM_FellingsShare());
    }

    /**
     * Test of setThinningsSimple method, of class GMMatrix.
     */
    public void testSetThinningsSimple() {
        System.out.println("setThinningsSimple");
        float agel = 0.0F;
        float ageh = 5.0F;
        GMMatrix instance = new GMMatrix(1,1);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        ArrayList<GMCell> expResult = new ArrayList<>();
        GMCellInit cInit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cInit);
        cell.setM_bThinned(true);
        expResult.add(cell);
        ArrayList<GMCell> result = instance.setThinningsSimple(agel, ageh);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i).isM_bThinned(), result.get(i).isM_bThinned());
    }

    /**
     * Test of getBiomass method, of class GMMatrix.
     */
    public void testGetBiomass() {
        System.out.println("getBiomass");
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        //pxvals.add(1.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        //pbranch.add(1.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        //pleaves.add(1.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        //croots.add(1.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        //froots.add(1.0f);
        pCa.setCa_pfroots(froots);
        
        float expResult = 1152.0F;
        float result = instance.getBiomass(pCa);
        assertEquals(expResult, result);
    }

    /**
     * Test of getBiomassDistr method, of class GMMatrix.
     */
    public void testGetBiomassDistr() {
        System.out.println("getBiomassDistr");
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        ArrayList<Float> pSt = new ArrayList<>(nsize);
        ArrayList<Float> pBr = new ArrayList<>(nsize);
        ArrayList<Float> pLv = new ArrayList<>(nsize);
        ArrayList<Float> pCr = new ArrayList<>(nsize);
        ArrayList<Float> pFr = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        pSt.add(2.0f);
        pSt.add(2.0f);
        pBr.add(2.0f);
        pBr.add(2.0f);
        pLv.add(2.0f);
        pLv.add(2.0f);
        pCr.add(2.0f);
        pCr.add(2.0f);
        pFr.add(2.0f);
        pFr.add(2.0f);

        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        pxvals.add(2.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        pbranch.add(2.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        pleaves.add(2.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        croots.add(2.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        froots.add(2.0f);
        pCa.setCa_pfroots(froots);

        float expResult = 1152.0F;
        float result = instance.getBiomassDistr(pCa, pLims, pSt, pBr, pLv, pCr, pFr, nsize);
        assertEquals(expResult, result);
    }

    /**
     * Test of makeFellings method, of class GMMatrix.
     */
    public void testMakeFellings() {
        System.out.println("makeFellings");
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setFellingsSimple(0);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        pxvals.add(2.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        pbranch.add(2.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        pleaves.add(2.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        croots.add(2.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        froots.add(2.0f);
        pCa.setCa_pfroots(froots);

        instance.updateV4();

        double expResult = 128.0;
        double result = instance.makeFellings(pFr, pCa);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSpeciesID method, of class GMMatrix.
     */
    public void testGetSpeciesID() {
        System.out.println("getSpeciesID");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit((1 << 24)+(1 << 16)+(1 << 8)+1,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        int expResult = 1;
        int result = instance.getSpeciesID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSiteID method, of class GMMatrix.
     */
    public void testGetSiteID() {
        System.out.println("getSiteID");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit((1 << 24)+(1 << 16)+(1 << 8)+1,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        int expResult = 1;
        int result = instance.getSiteID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOwnerID method, of class GMMatrix.
     */
    public void testGetOwnerID() {
        System.out.println("getOwnerID");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit((1 << 24)+(1 << 16)+(1 << 8)+1,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        int expResult = 1;
        int result = instance.getOwnerID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRegionID method, of class GMMatrix.
     */
    public void testGetRegionID() {
        System.out.println("getRegionID");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit((1 << 24)+(1 << 16)+(1 << 8)+1,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        int expResult = 1;
        int result = instance.getRegionID();
        assertEquals(expResult, result);
    }

    /**
     * Test of scaleArea method, of class GMMatrix.
     */
    public void testScaleArea() {
        System.out.println("scaleArea");
        float scf = 2.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 16.0F;
        float result = instance.scaleArea(scf);
        assertEquals(expResult, result);
    }

    /**
     * Test of getArea method, of class GMMatrix.
     */
    public void testGetArea() {
        System.out.println("getArea");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 8.0F;
        float result = instance.getArea();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class GMMatrix.
     */
    public void testGetValue() {
        System.out.println("getValue");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 32.0F;
        float result = instance.getValue();
        assertEquals(expResult, result);
    }

     /**
     * Test of getIds methods (reg,owner,site,spec), of class GMMatrix.
     */
    public void testGetIds() {
        System.out.println("getIds");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        long cr = 16;
        long co = 2;
        long cst = 4;
        long csp = 56;
        long key = (cr<<24) + (co<<16) + (cst<<8) + csp;
        init.setMi_id(key);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        int expResult = 56;
        int result = instance.getSpeciesID();
        assertEquals(expResult, result);
        expResult = 2;
        result = instance.getOwnerID();
        assertEquals(expResult, result);
        expResult = 16;
        result = instance.getRegionID();
        assertEquals(expResult, result);
        result = instance.getSiteID();
        expResult = 4;
        assertEquals(expResult, result);
    }

    /**
     * Test of getRegion,getOwner,getSite,getSpecies method, of class GMMatrix.
     */
    public void testGetValueByX() {
        System.out.println("getValueByX");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 32.0F;
        float result = instance.getValueByX();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMeanX method, of class GMMatrix.
     */
    public void testGetMeanX() {
        System.out.println("getMeanX");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 4.0F;
        float result = instance.getMeanX();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAreaDistr method, of class GMMatrix.
     */
    public void testGetAreaDistr() {
        System.out.println("getAreaDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        ArrayList<Float> pDest = new ArrayList<>(nsize);
        pDest.add(2.0f);
        pDest.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        //float expResult = 8.0F;
        ArrayList<Float> expResult = new ArrayList<>(2);
        expResult.add(2.0f);
        expResult.add(10.0f);
        ArrayList result = instance.getAreaDistr(pLims, pDest, nsize);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getStockDistr method, of class GMMatrix.
     */
    public void testGetStockDistr() {
        System.out.println("getStockDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        ArrayList<Float> pDest = new ArrayList<>(nsize);
        pDest.add(2.0f);
        pDest.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        //float expResult = 32.0F;
        ArrayList<Float> expResult = new ArrayList<>(2);
        expResult.add(2.0f);
        expResult.add(34.0f);
        ArrayList result = instance.getStockDistr(pLims, pDest, nsize);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getMortDistr method, of class GMMatrix.
     * TODO: rewrite this!
     */
    public void testGetMortDistr() {
        System.out.println("getMortDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        ArrayList<Float> pDest = new ArrayList<>(nsize);
        pDest.add(2.0f);
        pDest.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        //float expResult = 0.0F;
        ArrayList<Float> expResult = new ArrayList<>(2);
        expResult.add(2.0f);
        expResult.add(2.0f);
        ArrayList result = instance.getMortDistr(pLims, pDest, nsize);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getDwoodDistr method, of class GMMatrix.
     */
    public void testGetDwoodDistr() {
        System.out.println("getDwoodDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        ArrayList<Float> pDest = new ArrayList<>(nsize);
        pDest.add(2.0f);
        pDest.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.initDeadWood(pLims, pDest, nsize, nsize);

        //float expResult = 4.0F;
        ArrayList<Float> expResult = new ArrayList<>(2);
        expResult.add(2.0f);
        expResult.add(6.0f);
        ArrayList result = instance.getDwoodDistr(pLims, pDest, nsize);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getDwoodDistrFromPipe method, of class GMMatrix.
     */
    public void testGetDwoodDistrFromPipe() {
        System.out.println("getDwoodDistrFromPipe");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        ArrayList<Float> pDest = new ArrayList<>(nsize);
        pDest.add(2.0f);
        pDest.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.initDeadWood(pLims, pDest, nsize, nsize);
        //float expResult = 4.0F;
        ArrayList<Float> expResult = new ArrayList<>(2);
        expResult.add(2.0f);
        expResult.add(6.0f);
      /*  ArrayList result = instance.getDwoodDistrFromPipe(pLims, pDest, nsize);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));*/
    }

    /**
     * Test of getMefiqueDistr method, of class GMMatrix.
     * TODO: redo this
     */
    public void testGetMefiqueDistr() {
        System.out.println("getMefiqueDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        /*
        ArrayList<Float> pDestTha = null;
        ArrayList<Float> pDestThr = null;
        ArrayList<Float> pDestFla = null;
        ArrayList<Float> pDestFlr = null;
        */
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setFellingsSimple(0);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        pxvals.add(2.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        pbranch.add(2.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        pleaves.add(2.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        croots.add(2.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        froots.add(2.0f);
        pCa.setCa_pfroots(froots);

        instance.doThinningV4(pFr, pCa);
        //float expResult = 0.0F;
        ArrayList expResult = new ArrayList(4);
        expResult.add(pLims);
        expResult.add(pLims);
        expResult.add(pLims);
        expResult.add(pLims);
      /*  ArrayList result = instance.getMefiqueDistr(pLims, pLims, pLims, pLims, pLims, nsize);
        assertEquals(expResult, result);*/
    }

    /**
     * Test of getSlashDistr method, of class GMMatrix.
     * TODO: redo this too
     */
    public void testGetSlashDistr() {
        System.out.println("getSlashDistr");
        int nsize = 2;
        ArrayList<Float> pLims = new ArrayList<>(nsize);
        pLims.add(2.0f);
        pLims.add(2.0f);
        GMFellings pFr = new GMFellings(2f,2f,2f,2f,2f,2f,2f,2f);
        GMCarbonAlloc pCa = new GMCarbonAlloc(1,2f,2f,2.0,2.0,2.0,2.0,2.0);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setFellingsSimple(0);

        ArrayList pxvals = pCa.getCa_pxvals();
        pxvals.add(2.0f);
        pxvals.add(2.0f);
        pCa.setCa_pxvals(pxvals);
        ArrayList pbranch = pCa.getCa_pbranch();
        pbranch.add(2.0f);
        pbranch.add(2.0f);
        pCa.setCa_pbranch(pbranch);
        ArrayList pleaves = pCa.getCa_pleaves();
        pleaves.add(2.0f);
        pleaves.add(2.0f);
        pCa.setCa_pleaves(pleaves);
        ArrayList croots = pCa.getCa_pcroots();
        croots.add(2.0f);
        croots.add(2.0f);
        pCa.setCa_pcroots(croots);
        ArrayList froots = pCa.getCa_pfroots();
        froots.add(2.0f);
        froots.add(2.0f);
        pCa.setCa_pfroots(froots);

        instance.doThinningV4(pFr, pCa);

        //float expResult = 0.0F;
        ArrayList expResult = new ArrayList(2);
        expResult.add(pLims);
        expResult.add(pLims);
     /*   ArrayList result = instance.getSlashDistr(pLims, pLims, pLims, nsize);
        assertEquals(expResult, result);*/
    }

    /**
     * Test of calcTransitions method, of class GMMatrix.
     */
    public void testCalcTransitions() {
        System.out.println("calcTransitions");
        ArrayList<Float> pvols = new ArrayList<>();
        pvols.add(2.0f);
        pvols.add(2.0f);
        ArrayList<Float> pinvols = new ArrayList<>();
        pinvols.add(2.0f);
        pinvols.add(102.0f);
        int ntop = 2;
        float beta = 2.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(1,0,100,0,10,50,5);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setGrFunction(new GMGrFunction());
        ArrayList<GMCell> expResult = new ArrayList<>();
        GMCellInit cinit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell cell = new GMCell(cinit);
        cell.setM_MoveByXOrg(1.0f);
        cell.setM_MoveByXYOrg(0.0f);
        expResult.add(cell);
        expResult.add(cell);
        GMCell cell2 = new GMCell();
        cell2.setM_MoveByXOrg(0.5f);
        cell2.setM_MoveByXYOrg(0.0f);
        expResult.add(cell2);
        expResult.add(cell2);
        ArrayList<GMCell> result = instance.calcTransitions(pvols, pinvols, ntop, beta);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(expResult.get(i).getM_MoveByXOrg(), result.get(i).getM_MoveByXOrg());
            //System.out.println(result.get(i).getM_MoveByXOrg());
            assertEquals(expResult.get(i).getM_MoveByXYOrg(), result.get(i).getM_MoveByXYOrg());
            //System.out.println(result.get(i).getM_MoveByXYOrg());
        }
    }

    /**
     * Test of update method, of class GMMatrix.
     */
    public void testUpdate() {
        System.out.println("update");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 32.0F;
        float result = instance.update();
        assertEquals(expResult, result);
    }

    /**
     * Test of grow method, of class GMMatrix.
     */
    public void testGrow() {
        System.out.println("grow");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        float expResult = 0.0F;
        float result = instance.grow();
        assertEquals(expResult, result);
    }

    /**
     * Test of initRegular method, of class GMMatrix.
     */
    public void testInitRegular() {
        System.out.println("initRegular");
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        Long expResult = new Long(2);
        instance.initRegular(init);
        assertEquals(expResult, instance.m_wID);
    }

    /**
     * Test of fillRegular method, of class GMMatrix.
     */
    public void testFillRegular() {
        System.out.println("fillRegular");
        float carea = 0.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        //ArrayList<GMCell> expResult = new ArrayList<GMCell>();
        float expResult = 2.0f;
        ArrayList<GMCell> result = instance.fillRegular(carea);
        assertEquals(expResult, result.get(0).getM_Xmin());
    }

    /**
     * Test of fillRegularByX method, of class GMMatrix.
     */
    public void testFillRegularByX() {
        System.out.println("fillRegularByX");
        float carea = 0.0F;
        ArrayList<Float> volims = new ArrayList<>();
        volims.add(2.0f);
        volims.add(2.0f);
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        float expResult = 2.0f;
        ArrayList<GMCell> result = instance.fillRegularByX(carea, volims);
        assertEquals(expResult, result.get(0).getM_Xmin());
    }

    /**
     * Test of findCell method, of class GMMatrix.
     */
    public void testFindCell() {
        System.out.println("findCell");
        int nCol = 1;
        int nRow = 1;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        GMCellInit cinit = new GMCellInit(1,1,0,2f,2f,2f,2f,2f,2f,2f);
        GMCell expResult = new GMCell(cinit);
        GMCell result = instance.findCell(nCol, nRow);
        assertEquals(expResult.m_wID, result.m_wID);
    }

    /**
     * Test of setThinHistory method, of class GMMatrix.
     */
    public void testSetThinHistory() {
        System.out.println("setThinHistory");
        float ratio = 2.0F;
        GMMatrix instance = new GMMatrix(2,2);
        GMMatrixInit init = new GMMatrixInit(2,2,2,2,2,2,2);
        instance.initRegular(init);
        instance.fillRegular(2.0f);
        instance.setThinningsSimple(0, 5);
        float expResult = 4.0f;
        ArrayList<GMCell> result = instance.setThinHistory(ratio);
        assertEquals(expResult, result.get(0).getM_ThinArea());
    }
}
