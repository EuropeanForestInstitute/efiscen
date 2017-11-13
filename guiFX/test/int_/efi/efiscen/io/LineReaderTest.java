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
import efi.efiscen.io.LineReader;
import efi.efiscen.io.Logger;
import java.io.File;
import junit.framework.TestCase;

/**
 *
 * EFI
 */
public class LineReaderTest extends TestCase {
    public String inputFolder;
    public LineReaderTest(String testName) {
        super(testName);
        String userfolder = System.getProperty("user.home");
        String separator = File.separator;
        inputFolder = userfolder + separator + "EFISCEN" + separator;
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
     * Test of readLine method, of class LineReader.
     */
    public void testReadLine_0args() throws EFISCENFileNotFoundException {
        System.out.println("readLine");
        Logger log = new Logger("errorLog.txt");
        LineReader instance = new LineReader(new File(inputFolder+"simpleTestFile.txt"),log);
        String expResult = "this is the first line read";
        String result = instance.readLine();
        assertEquals(expResult, result);
    }

    /**
     * Test of readLine method, of class LineReader.
     */
    public void testReadLine_File() throws EFISCENFileNotFoundException {
        System.out.println("readLine");
        File file = new File(inputFolder+"simpleTestFile.txt");
        Logger log = new Logger("errorLog.txt");
        LineReader instance = new LineReader(file,log);
        String expResult = "this is the first line read";
        String result = instance.readLine(file);
        assertEquals(expResult, result);
    }
}
