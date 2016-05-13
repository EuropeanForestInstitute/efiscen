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
package int_.efi.efiscen.cli;

import efi.efiscen.cli.EfiscenCLI;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * 
 */
public class EfiscenCLITest extends TestCase {
    
    public EfiscenCLITest(String testName) {
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
    
    public void testFileWritingSpeed() {
        System.out.println("file writing speed");
        long currentTimeMillis = System.currentTimeMillis();
        for(int i=0;i<50;i++) {
            String[] args = new String[6];
            args[0] = "1";
            args[1] = "1";
            args[2] = "1";
            args[3] = "C:\\Documents and Settings\\jakiljun\\My Documents\\NetBeansProjects\\java\\AUT\\Austria.efs";
            args[4] = "1";
            args[5] = "C:\\Documents and Settings\\jakiljun\\My Documents\\NetBeansProjects\\java\\AUT out\\Austria.csv";
            EfiscenCLI.main(args);
        }
        Long time = System.currentTimeMillis() - currentTimeMillis;
        System.out.println("running time: " + time);
    }
}