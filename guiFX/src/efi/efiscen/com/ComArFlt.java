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
package efi.efiscen.com;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Collection of data. Stores objects to an array.
 * 
 */
public class ComArFlt<E> implements Serializable {

    /**
     * Container for elements.
     */
        protected ArrayList<E> m_caData;

    /**
     * Default constructor. Creates an empty ArrayList
     */
    public ComArFlt () {
        m_caData = new ArrayList<>();
    }

    /**
     * Constructor with the defined size.
     * @param nsize number of variables in container
     */
    public ComArFlt (int nsize) {
        m_caData = new ArrayList<>(nsize);
    }

    /**
     * Set the data at given index. PrintStackTrace() in case of exception.
     * @param nid array index
     * @param val value to be stored
     * @return 1 if successful -1 else
     */
    public int setData (int nid, E val) {
        try {
            m_caData.set(nid, val);
        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
            return -1;
        }
        return 1;
    }

    /**
     * Add data to the array.
     * @param val float value to be added
     * @return index of the top element in the array
     */
    public int addData (E val) {
        m_caData.add(val);
        return m_caData.lastIndexOf(val);
    }

    /**
     * Return data at given index.
     * @param nid array index
     * @return data at given index or null if not found
     */
    public E getData (int nid) {
        try {
            return m_caData.get(nid);
        } catch (IndexOutOfBoundsException iob) {
            //iob.printStackTrace();
        }
        return null;
    }

    /**
     * Get the size of the array.
     * @return Size of the array where floats are stored.
     */
    public int getSize () {
        return m_caData.size();
    }

}

