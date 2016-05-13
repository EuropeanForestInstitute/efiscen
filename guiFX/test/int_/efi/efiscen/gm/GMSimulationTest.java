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

import efi.efiscen.gm.GMScenario;
import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMSimulation;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class GMSimulationTest extends TestCase {
    
    public GMSimulationTest(String testName) {
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
     * Test of setTimeSteps method, of class GMSimulation.
     */
    public void testSetTimeSteps() {
        int timesteps = 5;
        GMSimulation instance = new GMSimulation(new GMEfiscen(),new GMScenario(), 5 );
        instance.setTimeSteps(timesteps);
        assertEquals(timesteps,instance.getTimeSteps());
    }

    /**
     * Test of getTimeSteps method, of class GMSimulation.
     */
    public void testGetTimeSteps() {
        int timesteps = 5;
        GMSimulation instance = new GMSimulation(new GMEfiscen(),new GMScenario(), 5 );
        instance.setTimeSteps(timesteps);
        assertEquals(timesteps,instance.getTimeSteps());
    }

    /**
     * Test of stepScenario method, of class GMSimulation.
     */
    public void testStepScenario() {
        System.out.println("stepScenario");
        GMSimulation instance = null;
        GMEfiscen expResult = null;
        GMEfiscen result = instance.stepScenario();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getM_Scenario method, of class GMSimulation.
     */
    public void testGetM_Scenario() {
        System.out.println("getM_Scenario");
        GMSimulation instance = null;
        GMScenario expResult = new GMScenario();
        instance = new GMSimulation(new GMEfiscen(),expResult,0);
        GMScenario result = instance.getM_Scenario();
        assertEquals(expResult, result);
    }

    /**
     * Test of getM_TotalVolume method, of class GMSimulation.
     */
    public void testGetM_TotalVolume() {
        System.out.println("getM_TotalVolume");
        GMSimulation instance = null;
        float expResult = 0.0F;
        float result = instance.getM_TotalVolume();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getM_nStep method, of class GMSimulation.
     */
    public void testGetM_nStep() {
        System.out.println("getM_nStep");
        GMSimulation instance = new GMSimulation(new GMEfiscen(),new GMScenario(),0);
        int expResult = 0;
        int result = instance.getM_nStep();
        assertEquals(expResult, result);
    }

    /**
     * Test of getM_nStepsByClick method, of class GMSimulation.
     */
    public void testGetM_nStepsByClick() {
        System.out.println("getM_nStepsByClick");
        GMSimulation instance = new GMSimulation(new GMEfiscen(),new GMScenario(),0);
        int expResult = 1;
        int result = instance.getM_nStepsByClick();
        assertEquals(expResult, result);
    }

    /**
     * Test of getM_pExperiment method, of class GMSimulation.
     */
    public void testGetM_pExperiment() {
        System.out.println("getM_pExperiment");
        GMEfiscen expResult = new GMEfiscen();
        GMSimulation instance = new GMSimulation(expResult,new GMScenario(),0);
        GMEfiscen result = instance.getM_pExperiment();
        assertEquals(expResult, result);
    }
}
