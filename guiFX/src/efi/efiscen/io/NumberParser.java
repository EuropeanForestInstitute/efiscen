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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;

/**
 * Is used  to convert a string into integer, float or double.
 * 
 */
public class NumberParser {
    
    /**
     * Message for number parsing error.
     */
    public static final String errorParsingNumber = "Number parsing error";
    
     /**
     * Tries to convert a string into an integer. If the method fails to convert
     * string to integer then an error message will be logged to a log file with 
     * errorLogger Logger object.
     * @param str string to convert
     * @param reader reader that reads the value from a file
     * @param errorLogger Logger object that logs errors to a log file
     * @return input strings value as an integer, or 0 if the conversion is unsuccessful
     * @throws NumberFormatException is thrown when converting fails
     */
    public static Integer convertInt(String str,
            LineReader reader,Logger errorLogger) throws NumberFormatException {
        try{
            str = str.trim();
            str = str.replace(",", "");
            Number num = null;
            try {
                num = NumberFormat.getInstance().parse(str);
            } catch (ParseException ex) {}
            return num.intValue();
        }catch(NumberFormatException e) {
            if( errorLogger != null ) {
                if(str.equals("")) str = "empty";
                errorLogger.logEntry(errorParsingNumber, "parameter required to"
                        + " be integer, " + str + " was found. File " 
                        + reader.getFileName() + " line " + reader.getLineNumber());
                //e.printStackTrace();
            }
            throw e;
        }
    }
    
    /**
     * Tries to convert a string into a float. If the method fails to convert
     * string into a float then error message will be logged to a log file with 
     * errorLogger Logger object.
     * @param str string to convert
     * @param reader reader that reads the value from a file.
     * @param errorLogger Logger object that logs errors to a log file
     * @return input strings value as a float, or 0.0f if conversion unsuccessful
     * @throws NumberFormatException is thrown when converting fails
     */
    public static Float convertFloat(String str,LineReader reader,
            Logger errorLogger) throws NumberFormatException {
        try{
            Number num = null;
                if(str.startsWith(".")){
                    str = "0" + str;
                }
                num = Float.parseFloat(str);
            return num.floatValue();
        }catch(NumberFormatException e) {
            if( errorLogger != null ) {
                if(str.equals("")) str = "empty";
                errorLogger.logEntry(errorParsingNumber, "parameter required is"
                        + " a float, " + str + " was found. File " 
                        + reader.getFileName() + " line " + reader.getLineNumber());
                //e.printStackTrace();
            }
            throw e;
        }
    }
    
    /**
     * Tries to convert a string into a double. If the method fails to convert
     * string into a double then error message will be logged to a log file with 
     * errorLogger Logger object.
     * @param str string to convert
     * @param reader reader that reads the value from a file
     * @param errorLogger Logger object that logs errors to a log file
     * @return input strings value as an integer, or 0 if conversion unsuccessful
     * @throws NumberFormatException is thrown when converting fails
     */
    public static Double convertDouble(String str,LineReader reader,
            Logger errorLogger) throws NumberFormatException {
        Double rValue = null;
        try{
            str = str.trim();
            rValue = Double.parseDouble(str);
        }catch(NumberFormatException e) {           
            if( errorLogger != null ) {
                if(str.equals("")) str = "empty";
                errorLogger.logEntry(errorParsingNumber, "parameter required is"
                        + " a float, " + str + " was found. File " 
                        + reader.getFileName() + " line " + reader.getLineNumber());
                //e.printStackTrace();
                throw e;
            }
        }
        return rValue;
    }
}
