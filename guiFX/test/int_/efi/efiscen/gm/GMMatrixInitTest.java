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

import efi.efiscen.gm.GMMatrixInit;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class GMMatrixInitTest extends TestCase {
    
    public GMMatrixInitTest(String testName) {
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
     * Test of getMi_id method, of class GMMatrixInit.
     */
    public void testGetMi_id() {
        System.out.println("getMi_id");
        GMMatrixInit instance = new GMMatrixInit();
        long expResult = 0L;
        long result = instance.getMi_id();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setMi_id method, of class GMMatrixInit.
     */
    public void testSetMi_id() {
        System.out.println("setMi_id");
        long val = 8L;
        GMMatrixInit instance = new GMMatrixInit();
        long expResult = 0L;
        long result = instance.getMi_id();
        assertEquals(expResult, result);
        instance.setMi_id(val);
        result = instance.getMi_id();
        assertEquals(val, result);
        
    }

    /**
     * Test of getMi_xb method, of class GMMatrixInit.
     */
    public void testGetMi_xb() {
        System.out.println("getMi_xb");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_xb();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of setMi_xb method, of class GMMatrixInit.
     */
    public void testSetMi_xb() {
        System.out.println("setMi_xb");
        float val = 0.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_xb(val);
        assertEquals(val,instance.getMi_xb());
        
    }

    /**
     * Test of getMi_xs method, of class GMMatrixInit.
     */
    public void testGetMi_xs() {
        System.out.println("getMi_xs");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_xs();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of setMi_xs method, of class GMMatrixInit.
     */
    public void testSetMi_xs() {
        System.out.println("setMi_xs");
        float val = 5.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_xs(val);
        assertEquals(val,instance.getMi_xs());
       
    }

    /**
     * Test of getMi_xt method, of class GMMatrixInit.
     */
    public void testGetMi_xt() {
        System.out.println("getMi_xt");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_xt();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of setMi_xt method, of class GMMatrixInit.
     */
    public void testSetMi_xt() {
        System.out.println("setMi_xt");
        float val = 110.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_xt(val);
        assertEquals(val,instance.getMi_xt());
        
    }

    /**
     * Test of getMi_yb method, of class GMMatrixInit.
     */
    public void testGetMi_yb() {
        System.out.println("getMi_yb");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_yb();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of setMi_yb method, of class GMMatrixInit.
     */
    public void testSetMi_yb() {
        System.out.println("setMi_yb");
        float val = 0.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_yb(val);
        assertEquals(val,instance.getMi_yb());
        
    }

    /**
     * Test of getMi_ys method, of class GMMatrixInit.
     */
    public void testGetMi_ys() {
        System.out.println("getMi_ys");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_ys();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of setMi_ys method, of class GMMatrixInit.
     */
    public void testSetMi_ys() {
        System.out.println("setMi_ys");
        float val = 1.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_ys(val);
        assertEquals(val,instance.getMi_ys());
    }

    /**
     * Test of getMi_yt method, of class GMMatrixInit.
     */
    public void testGetMi_yt() {
        System.out.println("getMi_yt");
        GMMatrixInit instance = new GMMatrixInit();
        float expResult = 0.0F;
        float result = instance.getMi_yt();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setMi_yt method, of class GMMatrixInit.
     */
    public void testSetMi_yt() {
        System.out.println("setMi_yt");
        float val = 2.0F;
        GMMatrixInit instance = new GMMatrixInit();
        instance.setMi_yt(val);
        assertEquals(val,instance.getMi_yt());
    }
}
