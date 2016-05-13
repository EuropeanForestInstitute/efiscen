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
 * The "smallest" unit of simulation; realization of a single cell of an
 * EFISCEN matrix. Keeps all necessary information about forest area of giving
 * X,Y (age class, volume class in current version) in any step of simulation
 * and responsible for dynamic changes during simulation.
 * 
 */
public class GMCell implements Serializable {

    /** x coordinate in the Matrix (I-index) */
    public int m_wX;
    /** y coordinate in the Matrix (J index) */
    public int m_wY;
    /** Just ID */
    public long m_wID;

    // Group below are about coordinates in "real world" - not in the Matrix
    private float m_Xmin; // lowest value of x
    private float m_Xmax; // highest value of x
    private float m_Xval; // middle value of x
    private float m_Ymin; // same things for y coordinates
    private float m_Ymax;
    private float m_Yval;

    // Mefique stuff - for a while!
    private float m_ThArea;
    private float m_ThRem;
    private float m_FelArea;
    private float m_FelRem;

    // Bioenergy purposes - slash keeping
    private float m_ThSlash;
    private float m_FelSlash;

    // Natural mortality stuff - thanks to Femke and MJ :-)
    private float m_NatMrt;
    private float m_DWood;

    // Version 3.2! MJ - natural disturbances!
    private float m_FireReplSus;
    private float m_FireNonReplSus;
    private float m_WindReplSus;
    private float m_WindNonReplSus;
    private float m_InsReplSus;
    private float m_InsNonReplSus;

    private float m_Area; // The main driving variable
    private float m_ThinArea; // Thinned area e.g. area thinned at previous step
    private float m_MoveAsThin; // Thinned area which will go to m_ThinArea of
                                // another cell
    private float m_MoveByX; // What part of the area will move by x axis
    private float m_MoveByY;
    private float m_MoveByXY;
    private float m_MoveByXOrg; // Same for calculated - original values
    private float m_MoveByYOrg;
    private float m_MoveByXYOrg;
    private float m_Move; // How much area will go away by all axis
    private float m_MoveAway; // How much is going away during cutting,
                              // for example (actually by natural mortality!)
    private float m_FellingsShare; // Part of area could be felled
    private float m_ThinShare; // Part of area could be thinned
    private float m_Income; // How much will come from "abroad"
    private boolean m_bThinned; // Could be thinned or not

    private GMMatrix m_pmOwner; // The matrix object to witch this cell belongs

    /**
     * Default constructor.
     * Set to zero all members
     */
    public GMCell () {
        m_pmOwner = null;
        m_MoveByX = m_MoveByXOrg = 0.0f;
        m_MoveByY = m_MoveByYOrg = 0.0f;
        m_MoveByXY = m_MoveByXYOrg = 0.0f;
        m_MoveAway = 0.0f;
        m_Move = 0.0f;
        m_Income = 0.0f;
        m_bThinned = false;
        m_FellingsShare = 0.0f;
        m_ThinShare = 0.0f;
        m_ThinArea = 0.0f;
        m_MoveAsThin = 0.0f;
        // Mefique stuff!
        m_ThArea = 0.0f;
        m_ThRem = 0.0f;
        m_FelArea = 0.0f;
        m_FelRem = 0.0f;
        // Bioenergy stuff
        m_ThSlash = 0.0f;
        m_FelSlash = 0.0f;
        // Mortality and deadwood
        m_NatMrt = 0.0f;
        m_DWood = 0.0f;
        // MJ - disturbances
        m_FireReplSus = 0.0f;
        m_FireNonReplSus = 0.0f;
        m_WindReplSus = 0.0f;
        m_WindNonReplSus = 0.0f;
        m_InsReplSus = 0.0f;
        m_InsNonReplSus = 0.0f;
    }

    /**
     * Parametrized constructor.
     * @param pGmi prepared GMCellInit object
     */
    public GMCell (GMCellInit pGmi) {
        m_wID	= pGmi.getCi_id();
        m_wX	= pGmi.getCi_wx();
        m_wY	= pGmi.getCi_wy();
        m_Xmin	= pGmi.getCi_xmin();
        m_Xmax	= pGmi.getCi_xmax();
        m_Xval	= pGmi.getCi_x();
        m_Ymin	= pGmi.getCi_ymin();
        m_Ymax	= pGmi.getCi_ymax();
        m_Yval	= pGmi.getCi_y();
        m_Area	= pGmi.getCi_area();
        m_pmOwner = pGmi.getCi_powner();

        m_pmOwner = null;
        m_MoveByX = m_MoveByXOrg = 0.0f;
        m_MoveByY = m_MoveByYOrg = 0.0f;
        m_MoveByXY = m_MoveByXYOrg = 0.0f;
        m_MoveAway = 0.0f;
        m_Move = 0.0f;
        m_Income = 0.0f;
        m_bThinned = false;
        m_FellingsShare = 0.0f;
        m_ThinShare = 0.0f;
        m_ThinArea = 0.0f;
        m_MoveAsThin = 0.0f;
        // Mefique stuff!
        m_ThArea = 0.0f;
        m_ThRem = 0.0f;
        m_FelArea = 0.0f;
        m_FelRem = 0.0f;
        // Bioenergy stuff
        m_ThSlash = 0.0f;
        m_FelSlash = 0.0f;
        // Femke and MJ :-)
        m_NatMrt = 0.0f;
        m_DWood = 0.0f;
        // MJ - disturbances
        m_FireReplSus = 0.0f;
        m_FireNonReplSus = 0.0f;
        m_WindReplSus = 0.0f;
        m_WindNonReplSus = 0.0f;
        m_InsReplSus = 0.0f;
        m_InsNonReplSus = 0.0f;
    }

    /**
     * Getter for deadwood
     * @return amount of deadwood
     */
    public float getM_DWood() {
        return m_DWood;
    }

    /**
     * Getter for felled area
     * @return area of final felling
     */
    public float getM_FelArea() {
        return m_FelArea;
    }

    /**
     * Getter for felling removals
     * @return removal volume of final felling
     */
    public float getM_FelRem() {
        return m_FelRem;
    }

    /**
     * Getter for felling residues
     * @return felling residues 
     */
    public float getM_FelSlash() {
        return m_FelSlash;
    }

    /**
     * Getter for felling share
     * @return share of felling
     */
    public float getM_FellingsShare() {
        return m_FellingsShare;
    }

    /**
     * Getter for fire non stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to fire non stand replacing
     */
    public float getM_FireNonReplSus() {
        return m_FireNonReplSus;
    }

    /**
     * Getter for fire stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to fire stand replacing
     */
    public float getM_FireReplSus() {
        return m_FireReplSus;
    }

    /**
     * Getter for "income" area buffer
     * @return income area
     */
    public float getM_Income() {
        return m_Income;
    }

    /**
     * Getter for insects non stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to insects non stand replacing
     */
    public float getM_InsNonReplSus() {
        return m_InsNonReplSus;
    }

    /**
     * Getter for insects stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to insects stand replacing
     */
    public float getM_InsReplSus() {
        return m_InsReplSus;
    }

    /**
     * Getter for area which will be moved from the cell in next step of simulations
     * @return area to be moved
     */
    public float getM_Move() {
        return m_Move;
    }

    /**
     * Getter for area which will be moved from the cell in next step of simulations
     * because of thinning
     * @return area to be moved as thinned
     */
    public float getM_MoveAsThin() {
        return m_MoveAsThin;
    }

    /**
     * Getter for area which will be moved from the cell in next step of simulations
     * because of final felling or natural mortality
     * @return area to be moved as felled
     */
    public float getM_MoveAway() {
        return m_MoveAway;
    }

    /**
     * Getter for current share of area which will be moved from the cell in next step of simulations
     * by x axe (no growth in volume)
     * @return current share of area to be moved by x
     */
    public float getM_MoveByX() {
        return m_MoveByX;
    }

    /**
     * Getter for share of area which will be moved from the cell in next step of simulations
     * by x axe (no growth in volume) (original value)
     * @return share of area to be moved by x (original)
     */
    public float getM_MoveByXOrg() {
        return m_MoveByXOrg;
    }

    /**
     * Getter for current share of area which will be moved from the cell in next step of simulations
     * by x and y axis (growth in volume)
     * @return current share of area to be moved by x,y
     */
    public float getM_MoveByXY() {
        return m_MoveByXY;
    }

    /**
      * Getter for share of area which will be moved from the cell in next step of simulations
     * by x and y axis (growth in volume) (original value)
     * @return share of area to be moved by x,y (original)
     */
    public float getM_MoveByXYOrg() {
        return m_MoveByXYOrg;
    }

    /**
    * Getter for current share of area which will be moved from the cell in next step of simulations
     * by y axe (no growth in age)
     * @return current share of area to be moved by y
     */
    public float getM_MoveByY() {
        return m_MoveByY;
    }

    /**
     * Getter for share of area which will be moved from the cell in next step of simulations
     * by y axe (no growth in age) (original value)
     * @return share of area to be moved by y (original)
     */
    public float getM_MoveByYOrg() {
        return m_MoveByYOrg;
    }

    /**
     * Getter for total amount of natural mortality (volume)
     * @return amount of natural mortality
     */
    public float getM_NatMrt() {
        return m_NatMrt;
    }

    /**
     * Getter for are thinned
     * @return area thinned
     */
    public float getM_ThArea() {
        return m_ThArea;
    }

    /**
     * Getter for thinning removals
     * @return  thinning removals
     */
    public float getM_ThRem() {
        return m_ThRem;
    }

    /**
     * Getter for thinning residues
     * @return thinning residues
     */
    public float getM_ThSlash() {
        return m_ThSlash;
    }

    /**
     * Getter for thinning area
     * @return thinned area
     */
    public float getM_ThinArea() {
        return m_ThinArea;
    }

    /**
     * Getter for share of area which could be thinned
     * @return share of thinning
     */
    public float getM_ThinShare() {
        return m_ThinShare;
    }

    /**
     * Getter for wind non stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to wind non stand replacing
     */
    public float getM_WindNonReplSus() {
        return m_WindNonReplSus;
    }

    /**
     * Getter for wind stand replace susceptibility
     * (reserved for use in Disturbances version)
     * @return susceptibility to wind stand replacing
     */
    public float getM_WindReplSus() {
        return m_WindReplSus;
    }

    /**
     * Getter for maximal value by x
     * @return maximal value by x
     */
    public float getM_Xmax() {
        return m_Xmax;
    }

    /**
     * Getter for minimal value by x
     * @return minimal value by x
     */
    public float getM_Xmin() {
        return m_Xmin;
    }

    /**
     * Getter for middle value by x
     * @return middle value by x
     */
    public float getM_Xval() {
        return m_Xval;
    }

    /**
     * Getter for maximal value by y
     * @return maximal value by y
     */
    public float getM_Ymax() {
        return m_Ymax;
    }

    /**
     * Getter for minimal value by y
     * @return minimal value by y
     */
    public float getM_Ymin() {
        return m_Ymin;
    }

    /**
     * Getter for middle value by y
     * @return middle value by y
     */
    public float getM_Yval() {
        return m_Yval;
    }

    /**
     * Getter for thinning status
     * @return true if cell could be thinned
     */
    public boolean isM_bThinned() {
        return m_bThinned;
    }

    /**
     * Getter for matrix to which this cell belongs
     * @return "owner" matrix
     */
    public GMMatrix getM_pmOwner() {
        return m_pmOwner;
    }

    /**
     * Getter for cell area
     * @return area of the cell
     */
    public float getArea () {
        return m_Area;
    }

    /**
     * Setter for cell area
     * @param m_Area value to be assigned
     */
    public void setM_Area(float m_Area) {
        this.m_Area = m_Area;
    }

    /**
     * Setter for dead wood
     * @param m_DWood value to be assigned
     */
    public void setM_DWood(float m_DWood) {
        this.m_DWood = m_DWood;
    }

    /**
     * Setter for area felled 
     * @param m_FelArea value to be assigned
     */
    public void setM_FelArea(float m_FelArea) {
        this.m_FelArea = m_FelArea;
    }

    /**
     * Setter for felling removals
     * @param m_FelRem value to be assigned
     */
    public void setM_FelRem(float m_FelRem) {
        this.m_FelRem = m_FelRem;
    }

    /**
     * Setter for felling residues
     * @param m_FelSlash value to be assigned
     */
    public void setM_FelSlash(float m_FelSlash) {
        this.m_FelSlash = m_FelSlash;
    }

    /**
     * Setter for felling share
     * @param m_FellingsShare value to be assigned
     */
    public void setM_FellingsShare(float m_FellingsShare) {
        this.m_FellingsShare = m_FellingsShare;
    }

    /**
     * Setter for fire non stand replace susceptibility
     * @param m_FireNonReplSus value to be assigned
     */
    public void setM_FireNonReplSus(float m_FireNonReplSus) {
        this.m_FireNonReplSus = m_FireNonReplSus;
    }

    /**
     * Setter for fire stand replace susceptibility
     * @param m_FireReplSus value to be assigned
     */
    public void setM_FireReplSus(float m_FireReplSus) {
        this.m_FireReplSus = m_FireReplSus;
    }

    /**
     * Setter for income area
     * @param m_Income value to be assigned
     */
    public void setM_Income(float m_Income) {
        this.m_Income = m_Income;
    }

    /**
     * Setter for insects non stand replace susceptibility
     * @param m_InsNonReplSus value to be assigned
     */
    public void setM_InsNonReplSus(float m_InsNonReplSus) {
        this.m_InsNonReplSus = m_InsNonReplSus;
    }

    /**
     * Setter for insects stand replace susceptibility
     * @param m_InsReplSus value to be assigned
     */
    public void setM_InsReplSus(float m_InsReplSus) {
        this.m_InsReplSus = m_InsReplSus;
    }

    /**
     * Setter for area to be moved
     * @param m_Move value to be assigned
     */
    public void setM_Move(float m_Move) {
        this.m_Move = m_Move;
    }

    /**
     * Setter for area to be moved as thinned
     * @param m_MoveAsThin value to be assigned
     */
    public void setM_MoveAsThin(float m_MoveAsThin) {
        this.m_MoveAsThin = m_MoveAsThin;
    }

    /**
     * Setter for total area moved away
     * @param m_MoveAway value to be assigned
     */
    public void setM_MoveAway(float m_MoveAway) {
        this.m_MoveAway = m_MoveAway;
    }

    /**
     * Setter for share to move by x (current)
     * @param m_MoveByX value to be assigned
     */
    public void setM_MoveByX(float m_MoveByX) {
        this.m_MoveByX = m_MoveByX;
    }

    /**
     * Setter for share to move by x (original)
     * @param m_MoveByXOrg value to be assigned
     */
    public void setM_MoveByXOrg(float m_MoveByXOrg) {
        this.m_MoveByXOrg = m_MoveByXOrg;
    }

    /**
     * Setter for share to move by x,y (current)
     * @param m_MoveByXY value to be assigned
     */
    public void setM_MoveByXY(float m_MoveByXY) {
        this.m_MoveByXY = m_MoveByXY;
    }

    /**
     * Setter for share to move by x,y (original)
     * @param m_MoveByXYOrg value to be assigned
     */
    public void setM_MoveByXYOrg(float m_MoveByXYOrg) {
        this.m_MoveByXYOrg = m_MoveByXYOrg;
    }

    /**
     * Setter for share to move by y (current)
     * @param m_MoveByY value to be assigned
     */
    public void setM_MoveByY(float m_MoveByY) {
        this.m_MoveByY = m_MoveByY;
    }

    /**
     * Setter for share to move by y (original)
     * @param m_MoveByYOrg value to be assigned
     */
    public void setM_MoveByYOrg(float m_MoveByYOrg) {
        this.m_MoveByYOrg = m_MoveByYOrg;
    }

    /**
     * Setter for natural mortality
     * @param m_NatMrt value to be assigned
     */
    public void setM_NatMrt(float m_NatMrt) {
        this.m_NatMrt = m_NatMrt;
    }

    /**
     * Setter for thinned area
     * @param m_ThArea value to be assigned
     */
    public void setM_ThArea(float m_ThArea) {
        this.m_ThArea = m_ThArea;
    }

    /**
     * Setter for thinning removals
     * @param m_ThRem value to be assigned
     */
    public void setM_ThRem(float m_ThRem) {
        this.m_ThRem = m_ThRem;
    }

    /**
     * Setter for thinning residues
     * @param m_ThSlash value to be assigned
     */
    public void setM_ThSlash(float m_ThSlash) {
        this.m_ThSlash = m_ThSlash;
    }

    /**
     * Setter for area thinned
     * @param m_ThinArea value to be assigned
     */
    public void setM_ThinArea(float m_ThinArea) {
        this.m_ThinArea = m_ThinArea;
    }

    /**
     * Setting for share of thinning
     * @param m_ThinShare value to be assigned
     */
    public void setM_ThinShare(float m_ThinShare) {
        this.m_ThinShare = m_ThinShare;
    }

    /**
     * Setter for wind non stand replace susceptibility
     * @param m_WindNonReplSus value to be assigned
     */
    public void setM_WindNonReplSus(float m_WindNonReplSus) {
        this.m_WindNonReplSus = m_WindNonReplSus;
    }

    /**
     * Setter for wind stand replace susceptibility
     * @param m_WindReplSus value to be assigned
     */
    public void setM_WindReplSus(float m_WindReplSus) {
        this.m_WindReplSus = m_WindReplSus;
    }

    /**
     * Setter for maximal value by x
     * @param m_Xmax value to be assigned
     */
    public void setM_Xmax(float m_Xmax) {
        this.m_Xmax = m_Xmax;
    }

    /**
     * Setter for minimal value by x
     * @param m_Xmin value to be assigned
     */
    public void setM_Xmin(float m_Xmin) {
        this.m_Xmin = m_Xmin;
    }

    /**
     * Setter for middle value by x
     * @param m_Xval value to be assigned
     */
    public void setM_Xval(float m_Xval) {
        this.m_Xval = m_Xval;
    }

    /**
     * Setter for maximal value by y
     * @param m_Ymax value to be assigned
     */
    public void setM_Ymax(float m_Ymax) {
        this.m_Ymax = m_Ymax;
    }

    /**
     * Setter for minimal value by y
     * @param m_Ymin value to be assigned
     */
    public void setM_Ymin(float m_Ymin) {
        this.m_Ymin = m_Ymin;
    }

    /**
     * Setter for middle value by y
     * @param m_Yval value to be assigned
     */
    public void setM_Yval(float m_Yval) {
        this.m_Yval = m_Yval;
    }

    /**
     * Setter for thinning status
     * @param m_bThinned value to be assigned
     */
    public void setM_bThinned(boolean m_bThinned) {
        this.m_bThinned = m_bThinned;
    }

    /**
     * Setter for "owner" matrix
     * @param m_pmOwner value to be assigned
     */
    public void setM_pmOwner(GMMatrix m_pmOwner) {
        this.m_pmOwner = m_pmOwner;
    }

    /**
     * Reserved
     * @return
     */
    public float growStep () {
        return 0.0f;
    }

    /**
     * Reserved
     * @return
     */
    public int update () {
        return 0;
    }

    @Override
    public String toString() {
        return m_wID + " " + m_wX + " " + m_wY + " " + m_FellingsShare
                + " " + m_bThinned;
    }
    
    public boolean equals(Object obj) {
        if(!(obj instanceof GMCell))
            return false;
        else {
            GMCell temp = (GMCell)obj;
            if(this.m_wID != temp.m_wID) return false;
            if(this.m_wX != temp.m_wX) return false;
            if(this.m_wY != temp.m_wY) return false;
            if(this.getArea() != temp.getArea()) return false;
            if(this.getM_DWood() != temp.getM_DWood()) return false;
            if(this.getM_FelArea() != temp.getM_FelArea()) return false;
            if(this.getM_FelRem() != temp.getM_FelRem()) return false;
            if(this.getM_FelSlash() != temp.getM_FelSlash()) return false;
            if(this.getM_FellingsShare() != temp.getM_FellingsShare()) return false;
            if(this.getM_FireNonReplSus() != temp.getM_FireNonReplSus()) return false;
            if(this.getM_FireReplSus() != temp.getM_FireReplSus()) return false;
            if(this.getM_Income() != temp.getM_Income()) return false;
            if(this.getM_InsNonReplSus() != temp.getM_InsNonReplSus()) return false;
            if(this.getM_InsReplSus() != temp.getM_InsReplSus()) return false;
            if(this.getM_Move() != temp.getM_Move()) return false;
            if(this.getM_MoveAsThin() != temp.getM_MoveAsThin()) return false;
            if(this.getM_MoveAway() != temp.getM_MoveAway()) return false;
            if(this.getM_MoveByX() != temp.getM_MoveByX()) return false;
            if(this.getM_MoveByXOrg() != temp.getM_MoveByXOrg()) return false;
            if(this.getM_MoveByY() != temp.getM_MoveByY()) return false;
            if(this.getM_MoveByYOrg() != temp.getM_MoveByYOrg()) return false;
            if(this.getM_NatMrt() != temp.getM_NatMrt()) return false;
            if(this.getM_ThArea() != temp.getM_ThArea()) return false;
            if(this.getM_ThRem() != temp.getM_ThRem()) return false;
            if(this.getM_ThSlash() != temp.getM_ThSlash()) return false;
            if(this.getM_ThinArea() != temp.getM_ThinArea()) return false;
            if(this.getM_ThinShare() != temp.getM_ThinShare()) return false;
            if(this.getM_WindNonReplSus() != temp.getM_WindNonReplSus()) return false;
            if(this.getM_WindReplSus() != temp.getM_WindReplSus()) return false;
            if(this.getM_Xmax() != temp.getM_Xmax()) return false;
            if(this.getM_Xmin() != temp.getM_Xmin()) return false;
            if(this.getM_Xval() != temp.getM_Xval()) return false;
            if(this.getM_Ymax() != temp.getM_Ymax()) return false;
            if(this.getM_Ymin() != temp.getM_Ymin()) return false;
            if(this.getM_Yval() != temp.getM_Yval()) return false;
            if(this.getM_pmOwner() != temp.getM_pmOwner()) return false;
        }
        return true;
    }
}

