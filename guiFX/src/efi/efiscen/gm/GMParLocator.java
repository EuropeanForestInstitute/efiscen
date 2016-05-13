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
 * This class will find an appropriate parameter in it's m_mElements map
 * by using the idea : if parameter does not depend on one of the four objects
 * (Region::Owner::Site::Species) corresponding subkey should be set to 0.
 * Look at GMMatrix to key construction idea short decription.
 * 
 */
public class GMParLocator implements Serializable {
    
    public static class GMParLocatorException extends Exception {
        
        private final String desc;
        private final String name;
        private final long r,o,st,sp;

        public GMParLocatorException(String desc, String name, long r, long o, long st, long sp) {
            this.desc = desc;
            this.name = name;
            this.r = r;
            this.o = o;
            this.st = st;
            this.sp = sp;
        }

        @Override
        public String toString() {
            return desc + ": param " + name + " matrix : " + r + "," + o + ","
                    + st + "," + sp;
        }
    }
    
    /**
     * Name of ParLocator, especially useful for error reporting
     */
    public String m_sName;

    protected HashMap<Long,GMParArray> m_mElements;

    private int m_bRegion;
    private int m_bOwner;
    private int m_bSite;
    private int m_bSpecies;

    /**
     * Default constructor.
     */
    public GMParLocator () {
        m_sName = "undefined";
        m_mElements = new HashMap<>();
        m_bRegion = 0;
        m_bOwner = 0;
        m_bSite = 0;
        m_bSpecies = 0;
    }
    
    public GMParLocator (String name) {
        m_sName = name;
        m_mElements = new HashMap<>();
        m_bRegion = 0;
        m_bOwner = 0;
        m_bSite = 0;
        m_bSpecies = 0;
    }
    
    public int getNumElements() {
        return m_mElements.size();
    }
    
    public HashMap<Long,GMParArray> getElements() {
        return m_mElements;
    }

    /**
     * Adds the given pararray to the hashmap.
     * @param pPar added pararray
     * @return key to the map
     */
    public long addParameter (GMParArray pPar) {
        long ulKey,ulR,ulO,ulSt,ulSp;

        ulR = (long) pPar.m_uRegion;
        ulR = ulR<<24;
        ulO = (long) pPar.m_uOwner;
        ulO = ulO<<16;
        ulSt = (long) pPar.m_uSite;
        ulSt = ulSt<<8;
        ulSp = (long) pPar.m_uSpecies;
        ulKey = ulR + ulO + ulSt + ulSp;
        m_mElements.put(ulKey,pPar);

        return ulKey;
    }

    /**
     * Gets the pararray corresponding to the key from the hashmap.
     * @param ulkey key to the array
     * @return GMParArray object if succesful else null
     * @throws efi.efiscen.gm.GMParLocator.GMParLocatorException
     */
    public GMParArray getParameter (long ulkey) throws GMParLocatorException {
        GMParArray pRet = null;
        long inkey;
        for (Long uKey : m_mElements.keySet())
        {
            GMParArray pEl;
            inkey = ulkey;
            pEl = m_mElements.get(uKey);
            if (pEl.m_uRegion == 0)  inkey = inkey & ~0xFF000000;
            if (pEl.m_uOwner == 0)   inkey = inkey & ~0xFF0000;
            if (pEl.m_uSite == 0)    inkey = inkey & ~0xFF00;
            if (pEl.m_uSpecies == 0) inkey = inkey & ~0xFF;
            
            if (uKey==inkey) {
                pRet = pEl;
                return pRet;
            }
        }
        if (pRet == null) {
            /*throw new GMParLocatorException("could not locate param",m_sName,
                    m_bRegion,m_bOwner,m_bSite,m_bSpecies);*/
            long lr,lo,lst,lsp;
            lr = ulkey>>24;
            lo = ulkey&0xFF0000;
            lo = lo>>16;
            lst = ulkey&0xFF00;
            lst = lst>>8;
            lsp = ulkey&0xFF;
            System.out.println("Debug:could not locate param "+m_sName+" for key "+ulkey+
                    ",reg: "+lr+",own: "+lo+",site: "+lst+",sp: "+lsp);
            System.out.println("Debug:Please check parameters files");
        }
        return pRet;
    }

    /**
     * Gets the value from GMParArray at given index from the hashmap at the
     * given key.
     * @param ulkey key to the hashmap
     * @param nindex index to the GMParArray
     * @return value at the index
     * @throws efi.efiscen.gm.GMParLocator.GMParLocatorException
     */
    public float getParameterValue (long ulkey, int nindex) throws GMParLocatorException {
        GMParArray pAr = getParameter(ulkey);
        if (pAr == null) {
            long lr,lo,lst,lsp;
            lr = ulkey>>24;
            lo = ulkey&0xFF0000;
            lo = lo>>16;
            lst = ulkey&0xFF00;
            lst = lst>>8;
            lsp = ulkey&0xFF;
            System.out.println("Debug:could not locate param "+m_sName+","+
                    lr+","+lo+","+lst+","+lsp);
            System.out.println("Debug:Please check parameters files");
        }
        if (nindex<0 || nindex>=pAr.m_Vals.size()) {
            long lr,lo,lst,lsp;
            lr = ulkey>>24;
            lo = ulkey&0xFF0000;
            lo = lo>>16;
            lst = ulkey&0xFF00;
            lst = lst>>8;
            lsp = ulkey&0xFF;
            System.err.println("index out of bounds "+m_sName+","+
                    lr+","+lo+","+lst+","+lsp);
            System.err.println("Please check parameters files");
        }

        return pAr.m_Vals.get(nindex);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GMParLocator)) return false;
        else {
            GMParLocator temp = (GMParLocator)obj;
            if(this.m_mElements.size() != temp.m_mElements.size()) return false;
            for(Long key : this.m_mElements.keySet()) {
                try {
                    if(!this.m_mElements.get(key).equals(temp.getParameter(key)))
                        return false;
                } catch (GMParLocatorException ex) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return the m_bRegion
     */
    public int getM_bRegion() {
        return m_bRegion;
    }

    /**
     * @return the m_bOwner
     */
    public int getM_bOwner() {
        return m_bOwner;
    }

    /**
     * @return the m_bSite
     */
    public int getM_bSite() {
        return m_bSite;
    }

    /**
     * @return the m_bSpecies
     */
    public int getM_bSpecies() {
        return m_bSpecies;
    }
}

