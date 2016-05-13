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
 * A class for storing harvesting information. Used to provide harvesting parameters
 * to matrix and to collect harvesting results from a matrix. 
 * 
 */
public class GMFellings implements Serializable {

    // External share of fellings (to decrease "native" level of cell's share)
    private double f_ratio;
    // Cutted area summator
    private double f_area;
    // Cutted volume summator
    private double f_volume;
    // Part of stem biomass going away
    private double f_stem;
    // Part of branch biomass going away
    private double f_branch;
    // Part of leaves biomass going away
    private double f_leaves;
    // Part of roots biomass going away
    private double f_croots;
    // Part of roots biomass going away
    private double f_froots;

    /**
     * Default (empty) constructor
     */
    public GMFellings () {
    }

    /**
     * Parameterized constructor
     * @param f_ratio harvest ratio
     * @param f_area felled area
     * @param f_volume felled volume
     * @param f_stem part of stem wood removals
     * @param f_branch part of branches removals
     * @param f_leaves part of leaves removals
     * @param f_croots part of coarse roots removals
     * @param f_froots part of coarse roots removals
     */
    public GMFellings (double f_ratio, double f_area, double f_volume,
            double f_stem, double f_branch, double f_leaves, double f_croots,
            double f_froots) {
        this.f_ratio = f_ratio;
        this.f_area = f_area;
        this.f_volume = f_volume;
        this.f_stem = f_stem;
        this.f_branch = f_branch;
        this.f_leaves = f_leaves;
        this.f_croots = f_croots;
        this.f_froots = f_froots;
    }

    /**
     * Getter for harvested area
     * @return harvested area
     */
    public double getF_area () {
        return f_area;
    }

    /**
     * Setter for harvested area
     * @param val value to be assigned
     */
    public void setF_area (double val) {
        this.f_area = val;
    }

    /**
     * Getter for branches ratio removals
     * @return branches removals ratio
     */
    public double getF_branch () {
        return f_branch;
    }

    /**
     * Setter for branches ratio removals
     * @param val value to be assigned
     */
    public void setF_branch (double val) {
        this.f_branch = val;
    }

    /**
     * Getter for coarse roots ratio removals 
     * @return coarse roots removals ratio
     */
    public double getF_croots () {
        return f_croots;
    }

    /**
     * Setter for coarse roots ratio removals
     * @param val value to be assigned
     */
    public void setF_croots (double val) {
        this.f_croots = val;
    }

    /**
     * Getter for fine roots ratio removals
     * @return fine roots removals ratio
     */
    public double getF_froots () {
        return f_froots;
    }

    /**
     * Setter for fine roots removals ratio
     * @param val value to be assigned
     */
    public void setF_froots (double val) {
        this.f_froots = val;
    }

    /**
     * Getter for leaves ratio removals
     * @return leaves removals ratio
     */
    public double getF_leaves () {
        return f_leaves;
    }

    /**
     * Setter for leaves ratio removals
     * @param val value to be assigned
     */
    public void setF_leaves (double val) {
        this.f_leaves = val;
    }

    /**
     * Getter for harvest ratio
     * @return harvest ratio
     */
    public double getF_ratio () {
        return f_ratio;
    }

    /**
     * Setter for harvest ratio
     * @param val value to be assigned
     */
    public void setF_ratio (double val) {
        this.f_ratio = val;
    }

    /**
     * Getter for stem ratio removals
     * @return stem removals ratio
     */
    public double getF_stem () {
        return f_stem;
    }

    /** 
     * Setter for stem ratio removals
     * @param val value to be assigned
     */
    public void setF_stem (double val) {
        this.f_stem = val;
    }

    /**
     * Getter for harvested wood
     * @return harvested wood
     */
    public double getF_volume () {
        return f_volume;
    }

    /** 
     * Setter for harvested wood
     * @param val value to be assigned
     */
    public void setF_volume (double val) {
        this.f_volume = val;
    }

    @Override
    public String toString() {
        return this.f_area + " " + this.f_branch + " " + this.f_croots + " "
                + this.f_froots + " " + this.f_leaves + " " + this.f_ratio + " "
                + this.f_stem + " " + this.f_volume;
    }

}

