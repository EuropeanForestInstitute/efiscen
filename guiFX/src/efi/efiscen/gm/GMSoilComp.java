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

/**
 * A class for storing soil compartments data.
 * Serves initialization of soil object and reporting soil data 
 * 
 */
public class GMSoilComp {

    private float sc_cwl;
    private float sc_fwl;
    private float sc_nwl;
    private float sc_sol;
    private float sc_cel;
    private float sc_lig;
    private float sc_hm1;
    private float sc_hm2;
    private float sc_clost;

    /**
     * Default (empty) constructor
     */
    public GMSoilComp () {
    }

    /**
     * Parameterized constructor
     * @param sc_cwl coarse woody litter
     * @param sc_fwl fine woody litter
     * @param sc_nwl non woody litter
     * @param sc_sol soluble compartment
     * @param sc_cel cellulose compartment
     * @param sc_lig lignin compartment
     * @param sc_hm1 humus1 compartment
     * @param sc_hm2 humus2 compartment
     * @param sc_clost lose of Carbon
     */
    public GMSoilComp (float sc_cwl, float sc_fwl, float sc_nwl, float sc_sol,
            float sc_cel, float sc_lig, float sc_hm1, float sc_hm2,
            float sc_clost) {
        this.sc_cwl = sc_cwl;
        this.sc_fwl = sc_fwl;
        this.sc_nwl = sc_nwl;
        this.sc_sol = sc_sol;
        this.sc_cel = sc_cel;
        this.sc_lig = sc_lig;
        this.sc_hm1 = sc_hm1;
        this.sc_hm2 = sc_hm2;
        this.sc_clost = sc_clost;
    }

    /**
     * Getter for cellulose
     * @return cellulose
     */
    public float getSc_cel () {
        return sc_cel;
    }

    /**
     * Setter for cellulose
     * @param val value to be assigned
     */
    public void setSc_cel (float val) {
        this.sc_cel = val;
    }

    /**
     * Getter for Carbon lose
     * @return carbon lost
     */
    public float getSc_clost () {
        return sc_clost;
    }

    /**
     * Setter for Carbon lose
     * @param val value to be assigned
     */
    public void setSc_clost (float val) {
        this.sc_clost = val;
    }

    /**
     * Getter for coarse woody litter
     * @return coarse woody litter
     */
    public float getSc_cwl () {
        return sc_cwl;
    }

    /**
     * Setter for coarse woody litter
     * @param val value to be assigned
     */
    public void setSc_cwl (float val) {
        this.sc_cwl = val;
    }

    /**
     * Getter for fine woody litter
     * @return fine woody litter
     */
    public float getSc_fwl () {
        return sc_fwl;
    }

    /**
     * Setter for fine woody litter
     * @param val value to be assigned
     */
    public void setSc_fwl (float val) {
        this.sc_fwl = val;
    }

    /**
     * Getter for humus1
     * @return humus1
     */
    public float getSc_hm1 () {
        return sc_hm1;
    }

    /**
     * Setter for humus1
     * @param val value to be assigned
     */
    public void setSc_hm1 (float val) {
        this.sc_hm1 = val;
    }

    /**
     * Getter for humus2 
     * @return humus2
     */
    public float getSc_hm2 () {
        return sc_hm2;
    }

    /**
     * Setter for humus2
     * @param val value to be assigned
     */
    public void setSc_hm2 (float val) {
        this.sc_hm2 = val;
    }

    /**
     * Getter for lignin
     * @return lignin
     */
    public float getSc_lig () {
        return sc_lig;
    }

    /**
     * Setter for lignin
     * @param val value to be assigned
     */
    public void setSc_lig (float val) {
        this.sc_lig = val;
    }

    /**
     * Getter for non woody litter
     * @return non woody litter
     */
    public float getSc_nwl () {
        return sc_nwl;
    }

    /**
     * Setter for non woody litter
     * @param val value to be assigned
     */
    public void setSc_nwl (float val) {
        this.sc_nwl = val;
    }

    /**
     * Getter for soluble compartment
     * @return soluble compartment
     */
    public float getSc_sol () {
        return sc_sol;
    }

    /**
     * Setter for soluble compartment
     * @param val value to be assigned
     */
    public void setSc_sol (float val) {
        this.sc_sol = val;
    }

}

