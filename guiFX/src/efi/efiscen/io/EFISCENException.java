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
 * General exception.
 * 
 */
public class EFISCENException extends Throwable{
    
    private String error;
    private String description;
    
    /**
     * Parameterized constructor
     * @param error the given error
     * @param description description of the error
     */
    public EFISCENException(String error,String description) {
        this.error = error;
        this.description = description;
    }
    
    /**
     * Returns a String representation of the object 
     * in format error : description.
     * @return String representation of the error and it's description
     */
    public String toString() {
        String rVal = error + " : " + description;
        return rVal;
    }
}
