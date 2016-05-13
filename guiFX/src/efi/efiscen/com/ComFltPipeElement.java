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

/**
 * Element of ComFltPipe. Data container class for the floats. Keeps four floats:
 * uplim, value, threm, felrem.
 * 
 * 
 */
public class ComFltPipeElement implements Serializable {

    private int cfp_nind;
    private float cfp_uplim;
    private float cfp_value;
    private float cfp_threm;
    private float cfp_felrem;
    
    /**
     * Default constructor. Does nothing.
     */
    public ComFltPipeElement () {
    }
    
    /**
     * Creates new container with given values.
     * @param cfp_nind index of the element
     * @param cfp_value value for this index
     * @param cfp_threm input/output buffer1
     * @param cfp_felrem input/output buffer2
     */
    public ComFltPipeElement (int cfp_nind, float cfp_value, float cfp_threm,
            float cfp_felrem) {
        this.cfp_nind = cfp_nind;
        this.cfp_value = cfp_value;
        this.cfp_threm = cfp_threm;
        this.cfp_felrem = cfp_felrem;
    }
    
    /**
     * Getter for felrem
     * @return felrem
     */
    public float getCfp_felrem () {
        return cfp_felrem;
    }
    
    /**
     * Setter for felrem
     * @param val new felrem
     */
    public void setCfp_felrem (float val) {
        this.cfp_felrem = val;
    }

    /**
     * Getter for nind
     * @return nind
     */
    public int getCfp_nind () {
        return cfp_nind;
    }
    
    /**
     * Setter for nind
     * @param val new nind
     */
    public void setCfp_nind (int val) {
        this.cfp_nind = val;
    }

    /**
     * Getter for threm
     * @return current trhem
     */
    public float getCfp_threm () {
        return cfp_threm;
    }
    
    /**
     * Setter for threm
     * @param val new threm
     */
    public void setCfp_threm (float val) {
        this.cfp_threm = val;
    }
    
    /**
     * Getter for uplim
     * @return uplim
     */
    public float getCfp_uplim () {
        return cfp_uplim;
    }

    /**
     * setter for uplim
     * @param val new uplim
     */
    public void setCfp_uplim (float val) {
        this.cfp_uplim = val;
    }

    /**
     * getter for value
     * @return value
     */
    public float getCfp_value () {
        return cfp_value;
    }
    
    /**
     * setter for value
     * @param val new value
     */
    public void setCfp_value (float val) {
        this.cfp_value = val;
    }

    @Override
    public String toString() {
        return cfp_nind+","+cfp_uplim+","+cfp_value+","+cfp_threm+","+cfp_felrem;
    }

    /**
     * Compares given ComFltPipeElement to this
     * @param target ComFltPipeElement to compare
     * @return true if two containers equal, false otherwise
     */
    public boolean equals (ComFltPipeElement target) {
        if (target.getCfp_nind() == cfp_nind && target.getCfp_uplim() == cfp_uplim
                && target.getCfp_value() == cfp_value && target.getCfp_threm() == cfp_threm
                && target.cfp_felrem == cfp_felrem)
            return true;
        return false;
    }

}

