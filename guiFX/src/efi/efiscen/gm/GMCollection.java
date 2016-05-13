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
package efi.efiscen.gm;

import java.io.Serializable;

/**
 * Class to introduce a simple object - name - id pair
 * used to keep information on owners, regions, sites and species.
 * 
 * @param <E> type of object
 * 
 */
public class GMCollection<E> implements Serializable {

    /**
     * name
     */
    public String m_sName;
    /**
     * identifier
     */
    public int m_ucID;
    /**
     * long identifier (used in region only for linkage with GIS data)
     */
    public long m_lISOID;

    /**
     * Default (empty) constructor
     */
    public GMCollection () {
    }
    
    /**
     * Parameterized  constructor
     * @param name name
     * @param id identifier
     */
    public GMCollection (String name, int id) {
        this.m_sName = name;
        this.m_ucID = id;
    }

}
