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
package int_.efi.efiscen.com;

import efi.efiscen.com.ComFltPipe;
import efi.efiscen.com.ComFltPipeElement;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * 
 */
public class ComFltPipeTest extends TestCase {
    
    public ComFltPipeTest(String testName) {
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
     * Test of shift method, of class ComFltPipe.
     */
    public void testShift() {
        System.out.println("shift");
        int size = 5;
        ComFltPipe instance = new ComFltPipe(size);
        for (int i=0; i<size; i++)
            instance.getElement(i).setCfp_value(i);
        ArrayList<ComFltPipeElement> expResult = new ArrayList<>();
        expResult.add(new ComFltPipeElement(1,size-1,0,0));
        for (int k=1; k<5; k++) {
            expResult.add(new ComFltPipeElement(k+1,k-1,0,0));
        }
        ArrayList<ComFltPipeElement> result = instance.shift();
        for (int i=0; i < result.size(); i++)
            assertTrue(((ComFltPipeElement)expResult.get(i)).equals((ComFltPipeElement)result.get(i)));
    }

}
