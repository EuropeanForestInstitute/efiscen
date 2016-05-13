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

import efi.efiscen.gm.GMSoil;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class GMSoilTest extends TestCase {
    
    public GMSoilTest(String testName) {
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
     * Test of changeCarbon method, of class GMSoil.
     */
    public void testChangeCarbon() {
        System.out.println("changeCarbon");
        double tarea = 1.0;
        GMSoil instance = new GMSoil(10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10);
        instance.setInOut(1.0);
        double expResult = 80.0;
        double result = instance.changeCarbon(tarea);
        assertEquals(expResult, result);
    }

    /**
     * Test of reInitStocks method, of class GMSoil.
     */
    public void testReInitStocks() {
        System.out.println("reInitStocks");
        double nw = 2.0;
        double fw = 2.0;
        double cw = 2.0;
        GMSoil instance = new GMSoil();
        double expResult = 60000.0;
        double result = instance.reInitStocks(nw, fw, cw);
        assertEquals(expResult, result);
    }

    /**
     * Test of initStocks method, of class GMSoil.
     */
    public void testInitStocks() {
        System.out.println("initStocks");
        double nw = 1.0;
        double fw = 1.0;
        double cw = 1.0;
        GMSoil instance = new GMSoil();
        double expResult = 30000.0;
        double result = instance.initStocks(nw, fw, cw);
        assertEquals(expResult, result);
    }

    /**
     * Test of initStocksEx method, of class GMSoil.
     */
    public void testInitStocksEx() {
        System.out.println("initStocksEx");
        double nw = 1.0;
        double fw = 1.0;
        double cw = 1.0;
        double t = 1.0;
        double pe = 1.0;
        GMSoil instance = new GMSoil();
        double expResult = 30000.0;
        double result = instance.initStocksEx(nw, fw, cw, t, pe);
        assertEquals(expResult, result);
    }

    /**
     * Test of yearStep method, of class GMSoil.
     */
    public void testYearStep() {
        System.out.println("yearStep");
        GMSoil instance = new GMSoil(10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10);
        double expResult = -3500.0;
        double result = instance.yearStep();
        assertEquals(expResult, result);
    }

}
