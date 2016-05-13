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

/**
 * Exception for parsing the file.
 * 
 */
public class EFISCENFileParsingException extends EFISCENException{
    
    /**
     * Parameterized constructor
     * @param log the used logger
     * @param file the name of the input file that caused the error
     */
    public EFISCENFileParsingException(Logger log,String file) {
        super("File parsing error",log.getNumErrorsLogged() + " errors logged " 
                + " in file " + file);
    }
}
