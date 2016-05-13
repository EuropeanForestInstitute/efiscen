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
package efi.efiscen.database;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class for features
 * 
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Features feats = new Features(DriverType.MySql, Writer.class);
        System.out.println(feats.toString());
        Features feats2 = new Features(DriverType.MySql, Reader.class);
        System.out.println(feats2.toString());
    }
}
