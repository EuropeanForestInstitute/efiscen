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
import java.util.ArrayList; 

/**
 * A class to keep various parameters of simulation, which
 * can depend on different combinations of Regs::Owners::Sites::Species.
 * We are limited here only scalar or 1-dimensional parameters.
 * 
 */
public class GMParArray implements Serializable {

    public ArrayList<Float> m_Vals;
   // public String m_sName;
    public int m_nSize;
    
    public int m_uRegion;
    public int m_uOwner;
    public int m_uSite;
    public int m_uSpecies;

    /**
     * Default constructor.
     */
    public GMParArray () {
        //m_sName = "undefined";
        m_Vals = new ArrayList<>();
        m_nSize = 0;
        m_uRegion = 0;
        m_uOwner = 0;
        m_uSite = 0;
        m_uSpecies = 0;
    }

    public GMParArray (int nsize) {
       // m_sName = "undefined";
        m_nSize = nsize;
        m_Vals = new ArrayList<>(nsize);
        m_uRegion = 0;
        m_uOwner = 0;
        m_uSite = 0;
        m_uSpecies = 0;
    }
    
    public GMParArray (int nsize, String name) {
        //m_sName = name;
        m_nSize = nsize;
        m_Vals = new ArrayList<>(nsize);
        m_uRegion = 0;
        m_uOwner = 0;
        m_uSite = 0;
        m_uSpecies = 0;
    }

    public int getM_nSize() {
        return m_nSize;
    }

    public void setM_nSize(int m_nSize) {
        this.m_nSize = m_nSize;
    }

    public ArrayList<Float> getM_Vals() {
        return m_Vals;
    }

    public void setM_Vals(ArrayList<Float> m_Vals) {
        this.m_Vals = m_Vals;
    }

    /*public String getM_sName() {
        return m_sName;
    }

    public void setM_sName(String m_sName) {
        this.m_sName = m_sName;
    }*/

    public int getM_uOwner() {
        return m_uOwner;
    }

    public void setM_uOwner(int m_uOwner) {
        this.m_uOwner = m_uOwner;
    }

    public int getM_uRegion() {
        return m_uRegion;
    }

    public void setM_uRegion(int m_uRegion) {
        this.m_uRegion = m_uRegion;
    }

    public int getM_uSite() {
        return m_uSite;
    }

    public void setM_uSite(int m_uSite) {
        this.m_uSite = m_uSite;
    }

    public int getM_uSpecies() {
        return m_uSpecies;
    }

    public void setM_uSpecies(int m_uSpecies) {
        this.m_uSpecies = m_uSpecies;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GMParArray))
            return false;
        else {
            GMParArray temp = (GMParArray)obj;
            //if(!this.m_sName.equals(temp.m_sName)) return false;
            if(this.m_nSize != temp.m_nSize) return false;
            if(this.m_uSpecies != temp.m_uSpecies) return false;
            if(this.m_uSite != temp.m_uSite) return false;
            if(this.m_uRegion != temp.m_uRegion) return false;
            if(this.m_uOwner != temp.m_uOwner) return false;
            for(int i = 0; i < this.m_Vals.size(); i++) {
                if(!this.m_Vals.get(i).equals(temp.m_Vals.get(i)))
                    return false;
            }
        }
        return true;
    }
}

