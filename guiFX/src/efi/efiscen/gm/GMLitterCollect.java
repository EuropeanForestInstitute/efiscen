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
 * A class for keeping litter production parameters and collecting litter from the matrix.
 * 
 */
public class GMLitterCollect implements Serializable {

    private GMCarbonAlloc lc_pCalloc;

    // Size of all arrays
    private int lc_nsize;

    // ArrayList with x limits
    private ArrayList<Float> lc_pxvals;
    // ArrayList of stem's shares of litter
    private ArrayList<Float> lc_pstem;
    // ArrayList of branches shares of litter
    private ArrayList<Float> lc_pbranch;
    // Array of coarce roots shares
    private ArrayList<Float> lc_pcroots;
    // Array of fine roots shares
    private ArrayList<Float> lc_pfroots;
    // Array of leaves shares
    private ArrayList<Float> lc_pleaves;

    // To collect litter from stem
    private double lc_cstem;
    // To collect litter from branches
    private double lc_cbranch;
    // To collect litter from coarce roots
    private double lc_ccroots;
    // To collect litter from fine roots
    private double lc_cfroots;
    // To collect litter from leaves/needles
    private double lc_cleaves;

    /**
     * Default constructor. Initialize lists
     */
    public GMLitterCollect () {
        lc_pCalloc = new GMCarbonAlloc();
        lc_pxvals = new ArrayList<>();
        lc_pstem = new ArrayList<>();
        lc_pbranch = new ArrayList<>();
        lc_pcroots = new ArrayList<>();
        lc_pfroots = new ArrayList<>();
        lc_pleaves = new ArrayList<>();
    }

    /**
     * Parameterized constructor. Initializes buffers for litter collection.
     * (Not used in the model. For testing purpose only!)
     * @param lc_cstem litter from stem
     * @param lc_cbranch litter from branches
     * @param lc_ccroots litter from coarse roots
     * @param lc_cfroots litter from fine roots
     * @param lc_cleaves litter from leaves
     */
    public GMLitterCollect (double lc_cstem, double lc_cbranch, double lc_ccroots,
            double lc_cfroots, double lc_cleaves) {
        this.lc_cstem = lc_cstem;
        this.lc_cbranch = lc_cbranch;
        this.lc_ccroots = lc_ccroots;
        this.lc_cfroots = lc_cfroots;
        this.lc_cleaves = lc_cleaves;
        
        lc_pCalloc = new GMCarbonAlloc();
        lc_pxvals = new ArrayList<>();
        lc_pstem = new ArrayList<>();
        lc_pbranch = new ArrayList<>();
        lc_pcroots = new ArrayList<>();
        lc_pfroots = new ArrayList<>();
        lc_pleaves = new ArrayList<>();
    }

    /**
     * Getter for branches litter
     * @return litter from branches
     */
    public double getLc_cbranch () {
        return lc_cbranch;
    }

    /**
     * Setter for branches litter
     * @param val value to be assigned
     */
    public void setLc_cbranch (double val) {
        this.lc_cbranch = val;
    }

    /**
     * Getter for coarse roots litter
     * @return litter from coarse roots
     */
    public double getLc_ccroots () {
        return lc_ccroots;
    }

    /**
     * Setter for coarse roots litter
     * @param val value to be assigned
     */
    public void setLc_ccroots (double val) {
        this.lc_ccroots = val;
    }

    /**
     * Getter for fine roots litter
     * @return litter from fine roots
     */
    public double getLc_cfroots () {
        return lc_cfroots;
    }

    /**
     * Setter for fine roots litter
     * @param val value to be assigned
     */
    public void setLc_cfroots (double val) {
        this.lc_cfroots = val;
    }

    /**
     * Getter for leaves litter
     * @return litter from leaves
     */
    public double getLc_cleaves () {
        return lc_cleaves;
    }

    /**
     * Setter for leaves litter
     * @param val value to be assigned
     */
    public void setLc_cleaves (double val) {
        this.lc_cleaves = val;
    }

    /**
     * Getter for stem litter
     * @return litter from stem
     */
    public double getLc_cstem () {
        return lc_cstem;
    }

    /**
     * Setter for stem litter
     * @param val value to be assigned
     */
    public void setLc_cstem (double val) {
        this.lc_cstem = val;
    }

    /**
     * Getter for list of parameters size
     * @return size of parameters list
     */
    public int getLc_nsize () {
        return lc_nsize;
    }

    /**
     * Setter for list parameters size
     * @param val value to be assigned
     */
    public void setLc_nsize (int val) {
        this.lc_nsize = val;
    }

    /**
     * Getter for Carbon allocate object 
     * @return carbon allocate object
     */
    public GMCarbonAlloc getLc_pCalloc () {
        return lc_pCalloc;
    }

    /**
     * Setter for Carbon allocate object
     * @param val value to be assigned 
     */
    public void setLc_pCalloc (GMCarbonAlloc val) {
        this.lc_pCalloc = val;
    }

    /**
     * Getter for list of litter production parameters for branches
     * @return list of litter production parameters for branches
     */
    public ArrayList<Float> getLc_pbranch () {
        return lc_pbranch;
    }

    /**
     * Setter for list of litter production parameters for branches
     * @param val value to be assigned
     */
    public void setLc_pbranch (ArrayList<Float> val) {
        this.lc_pbranch = val;
    }

    /**
     * Getter for list of litter production parameters for coarse roots
     * @return list of litter production parameters for branches
     */
    public ArrayList<Float> getLc_pcroots () {
        return lc_pcroots;
    }

    /**
     * Setter for list of litter production parameters for coarse roots
     * @param val value to be assigned
     */
    public void setLc_pcroots (ArrayList<Float> val) {
        this.lc_pcroots = val;
    }

    /**
     * Getter for list of litter production parameters for fine roots
     * @return list of litter production parameters for fine roots
     */
    public ArrayList<Float> getLc_pfroots () {
        return lc_pfroots;
    }

    /**
     * Setter for list of litter production parameters for fine roots
     * @param val value to be assigned
     */
    public void setLc_pfroots (ArrayList<Float> val) {
        this.lc_pfroots = val;
    }

    /**
     * Getter for list of litter production parameters for leaves
     * @return list of litter production parameters for leaves
     */
    public ArrayList<Float> getLc_pleaves () {
        return lc_pleaves;
    }

    /**
     * Setter for list of litter production parameters for leaves
     * @param val value to be assigned
     */
    public void setLc_pleaves (ArrayList<Float> val) {
        this.lc_pleaves = val;
    }

    /**
     * Getter for list of litter production parameters for stem
     * @return list of litter production parameters for stem
     */
    public ArrayList<Float> getLc_pstem () {
        return lc_pstem;
    }

    /**
     * Setter for list of litter production parameters for stem
     * @param val value to be assigned
     */
    public void setLc_pstem (ArrayList<Float> val) {
        this.lc_pstem = val;
    }

    /**
     * Getter for list of age limits for all parameters
     * @return list of age limits for all parameters
     */
    public ArrayList<Float> getLc_pxvals () {
        return lc_pxvals;
    }

    /**
     * Setter for list of age limits for all parameters
     * @param val value to be assigned
     */
    public void setLc_pxvals (ArrayList<Float> val) {
        this.lc_pxvals = val;
    }

}

