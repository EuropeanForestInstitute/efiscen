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
package efi.efiscen.io;

import java.util.ArrayList; 

/**
 * Parser for getting floats out of strings.
 * 
 */
public class StringParser {

    /**
     * Parses an array of floats from a string. The first value should be the
     * size of this array.
     * @param strIn String containing the floats
     * @param del separator character
     * @param reader Reader that was used for reading the line. Used for reporting
     * filenames in case of error.
     * @param errorLogger Logger to report errors.
     * @return ArrayList of the parsed floats or null if parsing was
     * unsuccessful
     */
    public static ArrayList<Float> getFlArFromString (String strIn, String del, 
            LineReader reader, Logger errorLogger) {
        int size = 10;
        String[] all = strIn.split(del);
        if (all.length == 0)
            return null;
        try {
            size = NumberParser.convertInt(all[0],reader,errorLogger);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return null;
        }
        ArrayList<Float> list = new ArrayList<>(size);
        for (int i = 1; i < all.length; i++) {
            try {
                list.add(NumberParser.convertFloat(all[i],reader,errorLogger));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return null;
            }
        }
        return list;
    }

    /**
     * Parses an array of floats from a string with certain number of elements.
     * @param strIn String containing the floats
     * @param del separator character
     * @param nsize the number of elements
     * @return ArrayList of the parsed floats or null if parsing was
     * unsuccessful
     */
    public static ArrayList<Float> getFlArFromStringEx (String strIn,
            String del, int nsize, LineReader reader, Logger errorLogger) {
        //int size = 10;
        String[] all = strIn.split(del);
        if (all.length == 0)
            return null;
        /*try {
            size = Integer.parseInt(all[0]);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return null;
        }*/
        // What for is this here, really?
        //if (nsize > size)
        //    return null;
        ArrayList<Float> list = new ArrayList<>(nsize);
        for (int i = 0; i < nsize; i++) {
            try {
                list.add(NumberParser.convertFloat(all[i],reader,errorLogger));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                list.add(0.F);
            }catch (ArrayIndexOutOfBoundsException nfe) {
                list.add(0.f);
                return list;
            }
        }
        return list;
    }

    /**
     * Parses an array of integers from a string with certain number of
     * @param strIn String containing the integers
     * @param del separator character
     * @param nsize the number of elements
     * @return ArrayList of the parsed integers or null if parsing was unsuccessful
     */
    public static ArrayList<Integer> getIntArFromStringEx (String strIn,
            String del, int nsize, LineReader reader, Logger errorLogger) {
        //int size = 10;
        String[] all = strIn.split(del);
        if (all.length == 0)
            return null;
        /*try {
            size = Integer.parseInt(all[0]);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return null;
        }*/
        //if (nsize > size)
        //    return null;
        ArrayList<Integer> list = new ArrayList<>(nsize);
        for (int i = 0; i < nsize; i++) {
            try {
                list.add(NumberParser.convertInt(all[i],reader,errorLogger));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return null;
            }
        }
        return list;
    }

}

