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

import efi.efiscen.gm.GMBare;
import junit.framework.TestCase;

/**
 * Test class for GMBare.
 * EFI
 */
public class GMBareTest extends TestCase {
    
    public GMBareTest(String testName) {
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
     * Test of clearChanges method, of class GMBare.
     */
    public void testClearChanges() {
        System.out.println("clearChanges");
        GMBare instance = new GMBare();
        int result = instance.clearChanges();
        assertEquals(1, result);
        for (Long uKey : instance.getM_mdFund().keySet()) {
            assertEquals(instance.getM_mdIncome().get(uKey), 0.0);
            assertEquals(instance.getM_mdOutcome().get(uKey), 0.0);
        }
    }

    /**
     * Test of applyChanges method, of class GMBare.
     */
    public void testApplyChanges() {
        System.out.println("applyChanges");
        GMBare instance = new GMBare();
        Long ukey = new Long(Math.abs(new java.util.Random().nextLong()));
        java.util.HashMap m = instance.getM_mdIncome();
        m.put(ukey, new Double(1.0));
        instance.setM_mdIncome(m);
        java.util.HashMap m2 = instance.getM_mdOutcome();
        m2.put(ukey, new Double(2.0));
        instance.setM_mdOutcome(m2);
        instance.addArea(ukey, 3.0);
        boolean expResult = true;
        boolean result = instance.applyChanges();
        assertEquals(expResult, result);
    }

    /**
     * Test of addArea method, of class GMBare.
     */
    public void testAddArea() {
        System.out.println("addArea");
        Long ulkey = new Long(Math.abs(new java.util.Random().nextLong()));
        double area = 2.0;
        GMBare instance = new GMBare();
        float expResult = (float)area;
        instance.addArea(ulkey, area);
        float result = instance.getFund(ulkey);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFund method, of class GMBare.
     */
    public void testGetFund_4args() {
        System.out.println("getFund");
        long lr = 2L;
        long lo = 2L;
        long lst = 2L;
        long lsp = 2L;
        GMBare instance = new GMBare();
        Float val = new Float(1.0F);
        Long key = new Long(33686018);
        instance.addArea(key, val);
        float expResult = 1.0F;
        float result = instance.getFund(lr, lo, lst, lsp);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNumBares method, of class GMBare.
     */
    public void testGetNumBares() {
        System.out.println("getNumBares");
        long lr = 2L;
        long lo = 2L;
        long lst = 2L;
        long lsp = 2L;
        GMBare instance = new GMBare();
        Float val = new Float(1.0F);
        Long key = new Long(33686018);
        instance.addArea(key, val);
        int expResult = 1;
        int result = instance.getNumBares(lr, lo, lst, lsp);
        assertEquals(expResult, result);
    }

}
