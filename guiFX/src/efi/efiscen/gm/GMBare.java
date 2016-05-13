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
import java.util.HashMap;

/**
 * A class to implement the keeping track of "bare lands". Keeps track of areas
 * temporarily de-forested during final felling and serve afforestation,
 * deforestation and tree species change.
 * 
 * 
 */
public class GMBare implements Serializable {

    /**
     * Map of bare fund
     */
    protected HashMap<Long,Double> m_mdFund;
    /**
     * Map of inputs to bare
     */
    protected HashMap<Long,Double> m_mdIncome;
    /**
     * Map of outputs from bare
     */
    protected HashMap<Long,Double> m_mdOutcome;

    /**
     * Default constructor. 
     */
    public GMBare () {
        m_mdFund = new HashMap<>();
        m_mdIncome = new HashMap<>();
        m_mdOutcome = new HashMap<>();
    }

    /**
     * Getter to bare fund
     * @return map of bare areas
     */
    public HashMap<Long, Double> getM_mdFund() {
        return m_mdFund;
    }

    /**
     * Getter to income bare fund
     * @return map of income
     */
    public HashMap<Long, Double> getM_mdIncome() {
        return m_mdIncome;
    }

    /**
     * Getter to outcome bare fund 
     * @return map of outcome
     */
    public HashMap<Long, Double> getM_mdOutcome() {
        return m_mdOutcome;
    }

    /**
     * Setter for bare fund
     * @param m_mdFund value to be assigned
     */
    public void setM_mdFund(HashMap<Long, Double> m_mdFund) {
        this.m_mdFund = m_mdFund;
    }

    /**
     * Setter for income fund
     * @param m_mdIncome value to be assigned
     */
    public void setM_mdIncome(HashMap<Long, Double> m_mdIncome) {
        this.m_mdIncome = m_mdIncome;
    }

    /**
     * Setter for outcome fund
     * @param m_mdOutcome value to be assigned
     */
    public void setM_mdOutcome(HashMap<Long, Double> m_mdOutcome) {
        this.m_mdOutcome = m_mdOutcome;
    }

    /**
     * Clears the changes made to income/outcome.
     * @return 1 if successful
     */
    public int clearChanges () {
        for (Long uKey : m_mdFund.keySet())
        {
            Double arSet = 0.0;
            m_mdOutcome.put(uKey,arSet);
            m_mdIncome.put(uKey,arSet);
        }
        return 1;
    }

    /**
     * Applies the changes to the fund based on income/outcome funds
     * @return true if successful else false
     */
    public boolean applyChanges () {
        for (Long uKey : m_mdIncome.keySet())
        {
            Double diff,ar,arIn,arOut;
            ar = 0.0;
            arIn = 0.0;
            arOut = 0.0;

            arIn = m_mdIncome.get(uKey);
            ar = m_mdFund.get(uKey);
            arOut = m_mdOutcome.get(uKey);

            if (arIn == null || ar == null || arOut == null)
                return false;

            if (arOut>ar) arOut = ar;
            diff = ar + arIn - arOut;
            if (diff<0.0) diff = 0.0;
            m_mdFund.put(uKey,diff);

        }
        return true;
    }

    /**
     * Adds the given value to the bare fund map at the given key.
     * @param ulkey key to the map
     * @param area value to be added
     * @return added value
     */
    public double addArea (long ulkey, double area) {
        Double val = m_mdFund.get(ulkey);
        if (val != null)
            val += area;
        else
            val = new Double(area);
        m_mdFund.put(ulkey, val);
        return val;
    }

    /**
     * Gets the bare fund aggregated by the given distributed index.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return aggregated fund
     */
    public float getFund (long lr, long lo, long lst, long lsp) {
        double retval;
        retval = 0;
        long ulKey;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mdFund.keySet())
        {
            long uKeyCopy = uKey;
            if(lr == 0)  uKeyCopy = uKeyCopy & ~0xFF000000;
            if(lo == 0)  uKeyCopy = uKeyCopy & ~0xFF0000;
            if(lst == 0) uKeyCopy = uKeyCopy & ~0xFF00;
            if(lsp == 0) uKeyCopy = uKeyCopy & ~0xFF;
            if (ulKey==uKeyCopy)
                retval+=m_mdFund.get(uKey);
        }
        return (float)retval;
    }

    /**
     * Gets the number of "bare lands" aggregated by the given distributed index.
     * @param lr region
     * @param lo owner
     * @param lst site
     * @param lsp species
     * @return aggregated number of "bare lands"
     */
    public int getNumBares (long lr, long lo, long lst, long lsp) {
        int retval;
        retval = 0;
        long ulKey;

        ulKey = (lr<<24) + (lo<<16) + (lst<<8) + lsp;
        for (Long uKey : m_mdFund.keySet())
        {
            if(lr == 0)  uKey = uKey & ~0xFF000000;
            if(lo == 0)  uKey = uKey & ~0xFF0000;
            if(lst == 0) uKey = uKey & ~0xFF00;
            if(lsp == 0) uKey = uKey & ~0xFF;
            if (ulKey==uKey)
                retval+=1;

        }
        return retval;
    }

    /**
     * Get the fund at the given key index.
     * @param ukey key to the map
     * @return fund value
     */
    public float getFund (long ukey) {
        Double retval = m_mdFund.get(ukey);
        if (retval != null)
            return new Float(retval);
        return -3.1415926f;
    }

}

