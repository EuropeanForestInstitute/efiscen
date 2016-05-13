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
 * ComFltPipe is a common class to implement "pipe" functionality: FIFO
 * by moving elements in the array by one index.
 * First usage to monitor standing deadwood development in the EFISCEN
 * - Hans request
 *  
 */
public class ComFltPipe implements Serializable {

    /**
     * Size of ArrayList.
     */
    public int m_nSize; 

    /**
     * Placeholder for the elements.
     */
    protected ArrayList<ComFltPipeElement> m_pData;

    /**
     * Default constructor. Creates a null ArrayList.
     */
    public ComFltPipe () {
        m_pData = null;
        m_nSize = 0;
    }

    /**
     * Parametrized constuctor which initializes the array of comfltpipelements.
     * @param nsize size of array
     */
    public ComFltPipe (int nsize) {
        this();
        if (nsize > 0) {
            m_nSize = nsize;
            m_pData = new ArrayList<>(nsize);
            // Creating id's: starting from 1
            for (int i=0;i<m_nSize;i++)
                m_pData.add(new ComFltPipeElement((i+1),0,0,0));
        }
    }

    /**
     * Shifts the elements in the arraylist.
     * @return m_pData arraylist containing the elements or null if shift was
     * not possible.
     */
    public ArrayList<ComFltPipeElement> shift () {
        float val2keep, val2store;

        if (m_nSize == 0)
            return null;

        val2store = m_pData.get(m_nSize-1).getCfp_value();
        for (int i=0; i<m_nSize; i++) {
            ComFltPipeElement e = m_pData.get(i);
            val2keep = e.getCfp_value();
            e.setCfp_value(val2store);
            m_pData.set(i, e);
            val2store = val2keep;
        }
        return m_pData;
    }

    /**
     * Reporting data element by array index not by cfp_nind!
     * @param nind array index
     * @return the element at the desired index or null if not found
     */
    public ComFltPipeElement getElement (int nind) {
        try {
            return m_pData.get(nind);
        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
        }
        return null;
    }

    /**
     * Setting element to the array.
     * @param nind array index
     * @param element added element
     * @return the previous element or null if wrong index
     */
    public Object setElement (int nind, ComFltPipeElement element) {
        try {
            return m_pData.set(nind, element);
        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
        }
        return null;
    }

}

