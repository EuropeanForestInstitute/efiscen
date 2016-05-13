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

import efi.efiscen.io.StringParser;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class StringParserTest extends TestCase {
    
    public StringParserTest(String testName) {
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
     * Test of getFlArFromString method, of class StringParser.
     */
    public void testGetFlArFromString() {
        System.out.println("getFlArFromString");
        String strIn = "5 15.45 15.31 13.51 19.33 16.53";
        String del = "\\s+";
        ArrayList<Float> expResult = new ArrayList<>(5);
        expResult.add(15.45f);
        expResult.add(15.31f);
        expResult.add(13.51f);
        expResult.add(19.33f);
        expResult.add(16.53f);
        ArrayList<Float> result = StringParser.getFlArFromString(strIn, del,null,null);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getFlArFromStringEx method, of class StringParser.
     */
    public void testGetFlArFromStringEx() {
        System.out.println("getFlArFromStringEx");
        String strIn = "5 15.45 15.31 13.51 19.33 16.53";
        String del = "\\s+";
        int nsize = 4;
        ArrayList<Float> expResult = new ArrayList<>(4);
        expResult.add(5f);
        expResult.add(15.45f);
        expResult.add(15.31f);
        expResult.add(13.51f);
        //expResult.add(19.33f);
        ArrayList<Float> result = StringParser.getFlArFromStringEx(strIn, del, nsize,null,null);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

    /**
     * Test of getIntArFromStringEx method, of class StringParser.
     */
    public void testGetIntArFromStringEx() {
        System.out.println("getIntArFromStringEx");
        String strIn = "5 1 2 3 4 5";
        String del = "\\s+";
        int nsize = 4;
        ArrayList<Integer> expResult = new ArrayList<>(4);
        expResult.add(5);
        expResult.add(1);
        expResult.add(2);
        expResult.add(3);
        //expResult.add(4);
        ArrayList<Integer> result = StringParser.getIntArFromStringEx(strIn, del, nsize,null,null);
        for (int i = 0; i < result.size(); i++)
            assertEquals(expResult.get(i), result.get(i));
    }

}
