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
 * A class for storing biomass allocation parameters.
 * Keeps allocation parameters (depended on tree age given in x-limits vector) in vectors and some 
 * additional parameters as scalars.
 * Serves recalculation growing stock to biomass (in Carbon units) and keeps track of 
 * current state of biomass in different tree compartments
 * 
 */
public class GMCarbonAlloc implements Serializable {

    // Carbon content of dry mass (ratio)
    private float ca_ccont;
    // Density of stem wood (Mg/m3)
    private float ca_dns;
    // Size of all arrays
    private int ca_nsize;

    // ArrayList with x limits
    private ArrayList<Float> ca_pxvals;
    // ArrayList of stem's shares
    private ArrayList<Float> ca_pstem;
    // ArrayList of branches shares
    private ArrayList<Float> ca_pbranch;
    // ArrayList of coarce roots shares
    private ArrayList<Float> ca_pcroots;
    // ArrayList of fine roots shares
    private ArrayList<Float> ca_pfroots;
    // ArrayList of leaves shares
    private ArrayList<Float> ca_pleaves;

    // To collect carbon in stem
    private double ca_cstem;
    // To collect carbon in branches
    private double ca_cbranch;
    // To collect carbon in coarce roots
    private double ca_ccroots;
    // To collect carbon in fine roots
    private double ca_cleaves;
    // To collect carbon in leaves/needles
    private double ca_cfroots;

    /**
     * Default constructor, allocation of all vectors 
     */
    public GMCarbonAlloc () {
        ca_pxvals = new ArrayList<>();
        ca_pstem = new ArrayList<>();
        ca_pbranch = new ArrayList<>();
        ca_pcroots = new ArrayList<>();
        ca_pfroots = new ArrayList<>();
        ca_pleaves = new ArrayList<>();
    }

    /**
     * Parameterized constructor, allocate all vectors with given size
     * @param nsize size of vectors
     */
    public GMCarbonAlloc (int nsize) {
        this.ca_nsize = nsize;
        ca_pxvals = new ArrayList<>(nsize);
        ca_pstem = new ArrayList<>(nsize);
        ca_pbranch = new ArrayList<>(nsize);
        ca_pcroots = new ArrayList<>(nsize);
        ca_pfroots = new ArrayList<>(nsize);
        ca_pleaves = new ArrayList<>(nsize);
    }

    /**
     * Parameterized constructor, allocate all vectors with given size
     * and scalars by provided values.
     * @param nsize size for vectors
     * @param ca_ccont carbon content of dry wood
     * @param ca_dns density of wood
     * @param ca_cstem biomass in stem
     * @param ca_cbranch biomass in branches
     * @param ca_ccroots biomass in coarse roots
     * @param ca_cleaves biomass in leaves/needles
     * @param ca_cfroots biomass in fine roots
     */
    public GMCarbonAlloc (int nsize, float ca_ccont, float ca_dns, double ca_cstem,
            double ca_cbranch, double ca_ccroots, double ca_cleaves, double ca_cfroots) {
        this.ca_nsize = nsize;
        this.ca_ccont = ca_ccont;
        this.ca_dns = ca_dns;
        this.ca_cstem = ca_cstem;
        this.ca_cbranch = ca_cbranch;
        this.ca_ccroots = ca_ccroots;
        this.ca_cfroots = ca_cfroots;
        this.ca_cleaves = ca_cleaves;
        ca_pxvals = new ArrayList<>(nsize);
        ca_pstem = new ArrayList<>(nsize);
        ca_pbranch = new ArrayList<>(nsize);
        ca_pcroots = new ArrayList<>(nsize);
        ca_pfroots = new ArrayList<>(nsize);
        ca_pleaves = new ArrayList<>(nsize);
    }

    /**
     * Getter for brunches biomass
     * @return biomass in branches
     */
    public double getCa_cbranch () {
        return ca_cbranch;
    }

    /**
     * Setter for brunches biomass
     * @param val value to be assigned
     */
    public void setCa_cbranch (double val) {
        this.ca_cbranch = val;
    }

    /**
     * Getter for Carbon content
     * @return carbon content
     */
    public float getCa_ccont () {
        return ca_ccont;
    }

    /**
     *  Setter for Carbon content
     * @param val value to be assigned
     */
    public void setCa_ccont (float val) {
        this.ca_ccont = val;
    }

    /**
     * Getter for biomass in coarse roots 
     * @return biomass in coarse roots
     */
    public double getCa_ccroots () {
        return ca_ccroots;
    }

    /**
     * Setter for coarse roots biomass
     * @param val value to be assigned
     */
    public void setCa_ccroots (double val) {
        this.ca_ccroots = val;
    }

    /**
     * Getter for fine roots biomass
     * @return biomass in fine roots
     */
    public double getCa_cfroots () {
        return ca_cfroots;
    }

    /**
     * Setter for fine roots biomass
     * @param val value to be assigned
     */
    public void setCa_cfroots (double val) {
        this.ca_cfroots = val;
    }

    /**
     * Getter for leaves biomass
     * @return biomass in leaves
     */
    public double getCa_cleaves () {
        return ca_cleaves;
    }

    /**
     * Setter for leaves biomass
     * @param val value to be assigned
     */
    public void setCa_cleaves (double val) {
        this.ca_cleaves = val;
    }

    /**
     * Getter for stem biomass
     * @return biomass in stem  
     */
    public double getCa_cstem () {
        return ca_cstem;
    }

    /**
     * Setter for stem biomass
     * @param val value to be assigned
     */
    public void setCa_cstem (double val) {
        this.ca_cstem = val;
    }

    /**
     * Getter for wood density
     * @return wood density
     */
    public float getCa_dns () {
        return ca_dns;
    }

    /**
     * Setter for wood density
     * @param val value to be assigned
     */
    public void setCa_dns (float val) {
        this.ca_dns = val;
    }

    /**
     * Getter for size of vectors
     * @return size of vectors
     */
    public int getCa_nsize () {
        return ca_nsize;
    }

    /**
     * Setter for size of vectors
     * @param val value to be assigned
     */
    public void setCa_nsize (int val) {
        this.ca_nsize = val;
    }

    /**
     * Getter for branches biomass
     * @return biomass in branches
     */
    public ArrayList<Float> getCa_pbranch () {
        return ca_pbranch;
    }

    /**
     * Setter for branches biomass
     * @param val value to be assigned
     */
    public void setCa_pbranch (ArrayList<Float> val) {
        this.ca_pbranch = val;
    }

    /**
     * Getter for coarse roots biomass
     * @return biomass in coarse roots
     */
    public ArrayList<Float> getCa_pcroots () {
        return ca_pcroots;
    }

    /**
     * Setter for coarse roots biomass
     * @param val value to be assigned
     */
    public void setCa_pcroots (ArrayList<Float> val) {
        this.ca_pcroots = val;
    }

    /**
     * Getter for fine roots allocation
     * @return fine roots allocation vector
     */
    public ArrayList<Float> getCa_pfroots () {
        return ca_pfroots;
    }

    /**
     * Setter for fine roots allocation
     * @param val value to be assigned
     */
    public void setCa_pfroots (ArrayList<Float> val) {
        this.ca_pfroots = val;
    }

    /**
     * Getter for leaves allocation
     * @return leaves allocation vector
     */
    public ArrayList<Float> getCa_pleaves () {
        return ca_pleaves;
    }

    /**
     * Setter for leaves allocation
     * @param val value to be assigned
     */
    public void setCa_pleaves (ArrayList<Float> val) {
        this.ca_pleaves = val;
    }

    /**
     * Getter for stem allocation
     * @return stem allocation vector
     */
    public ArrayList<Float> getCa_pstem () {
        return ca_pstem;
    }

    /**
     * Setter for stem allocation
     * @param val value to be assigned
     */
    public void setCa_pstem (ArrayList<Float> val) {
        this.ca_pstem = val;
    }

    /**
     * Getter for x limits
     * @return x-limits vector
     */
    public ArrayList<Float> getCa_pxvals () {
        return ca_pxvals;
    }

    /**
     * Setter for x limits
     * @param val value to be assigned
     */
    public void setCa_pxvals (ArrayList<Float> val) {
        this.ca_pxvals = val;
    }

}

