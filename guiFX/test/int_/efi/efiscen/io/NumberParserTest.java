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

import efi.efiscen.io.LineReader;
import efi.efiscen.io.NumberParser;
import efi.efiscen.io.Logger;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class NumberParserTest extends TestCase {
    
    public NumberParserTest(String testName) {
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
     * Test of convertInt method, of class NumberParser.
     */
    public void testConvertInt() {
        System.out.println("convertInt");
        String str = "4";
        LineReader reader = null;
        Logger errorLogger = null;
        Integer expResult = 4;
        Integer result = NumberParser.convertInt(str, reader, errorLogger);
        assertEquals(expResult, result);
    }

    /**
     * Test of convertFloat method, of class NumberParser.
     */
    public void testConvertFloat() {
        System.out.println("convertFloat");
        String str = "1.0";
        LineReader reader = null;
        Logger errorLogger = null;
        Float expResult = 1.0f;
        Float result = NumberParser.convertFloat(str, reader, errorLogger);
        assertEquals(expResult, result);
    }

    /**
     * Test of convertDouble method, of class NumberParser.
     */
    public void testConvertDouble() {
        System.out.println("convertDouble");
        String str = "8.33311";
        LineReader reader = null;
        Logger errorLogger = null;
        Double expResult = 8.33311;
        Double result = NumberParser.convertDouble(str, reader, errorLogger);
        assertEquals(expResult, result);
    }
}
