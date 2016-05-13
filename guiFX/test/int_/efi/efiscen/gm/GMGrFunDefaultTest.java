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

import efi.efiscen.gm.GMGrFunDefault;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class GMGrFunDefaultTest extends TestCase {
    
    public GMGrFunDefaultTest(String testName) {
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
     * Test of calculate method, of class GMGrFunDefault.
     */
    public void testCalculate() {
        System.out.println("calculate");
        double x = 42.0;
        GMGrFunDefault instance = new GMGrFunDefault();
        instance.setCoeff(x, x, x*x);
        double expResult = 44.0;
        double result = instance.calculate(x);
        assertEquals(expResult, result);
    }

}
