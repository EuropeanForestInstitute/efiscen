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

/**
 * General database connection exception.
 * 
 */
public class DatabaseComponentsException extends Exception{
    
    private String method;
    private String msg;
    
    /**
     * Creates new exception.
     * @param method Method in which the exception occured.
     * @param msg Information about the cause of the exception.
     */
    public DatabaseComponentsException(String method,String msg) {
        this.method = method;
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return "Exception in method " + method + "\nmessage: " + msg;
    }
}
