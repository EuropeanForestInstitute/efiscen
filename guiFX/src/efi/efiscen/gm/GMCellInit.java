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
 * A class for storing information for GMCell initialization.
 * 
 */
public class GMCellInit {

    /**x-coordinate (zero+1 based index of column in matrix) */
    private int ci_wx;
    /** y-coordinate (zero+1 based index in of row in matrix) */
    private int ci_wy;
    /** id of the cell (reserved) */
    private int ci_id;
    /** minimal value of x (age) */
    private float ci_xmin;
    /** maximal value of x (age) */
    private float ci_xmax;
    /** middle (average) value of x (age) */
    private float ci_x;
    /** minimal value of y (volume) */
    private float ci_ymin;
    /** maximal value of y (volume) */
    private float ci_ymax;
    /** middle (average) value of y (volume) */
    private float ci_y;
    /** forest area to be assigned to the cell*/
    private float ci_area;
    /** Owner matrix */
    private GMMatrix ci_powner;
    /**
     * Default (empty) constructor.
     */
    public GMCellInit () {
    }
    /**
     * Parametrized constructor.
     * @param ci_wx x-coordinate (zero+1 based index of column in matrix)
     * @param ci_wy y-coordinate (zero+1 based index in of row in matrix)
     * @param ci_id id of the cell (reserved)
     * @param ci_xmin minimal value of x (age)
     * @param ci_xmax maximal value of x (age)
     * @param ci_x middle (average) value of x (age)
     * @param ci_ymin minimal value of y (volume)
     * @param ci_ymax maximal value of y (volume)
     * @param ci_y middle (average) value of y (volume)
     * @param ci_area forest area to be assigned to the cell
     */
    public GMCellInit (int ci_wx, int ci_wy, int ci_id, float ci_xmin,
            float ci_xmax, float ci_x, float ci_ymin, float ci_ymax,
            float ci_y, float ci_area) {
        this.ci_wx = ci_wx;
        this.ci_wy = ci_wy;
        this.ci_id = ci_id;
        this.ci_xmin = ci_xmin;
        this.ci_xmax = ci_xmax;
        this.ci_x = ci_x;
        this.ci_ymin = ci_ymin;
        this.ci_ymax = ci_ymax;
        this.ci_y = ci_y;
        this.ci_area = ci_area;
    }
    /**
     * Getter for area
     * @return area
     */
    public float getCi_area () {
        return ci_area;
    }
    /**
     * Setter for area
     * @param val  assigned value
     */
    public void setCi_area (float val) {
        this.ci_area = val;
    }
    /**
     * Getter for id
     * @return id
     */
    public int getCi_id () {
        return ci_id;
    }
    /**
     * Setter for id
     * @param val  assigned value
     */
    public void setCi_id (int val) {
        this.ci_id = val;
    }
    /**
     * Getter for cell owner (GMMatrix)
     * @return owner 
     */
    public GMMatrix getCi_powner () {
        return ci_powner;
    }
    /**
     * Setter for cell owner (GMMatrix)
     * @param val  assigned value
     */
    public void setCi_powner (GMMatrix val) {
        this.ci_powner = val;
    }
    /**
     * Getter for x-coordinate
     * @return x index
     */
    public int getCi_wx () {
        return ci_wx;
    }
     /**
     * Setter for x-coordinate
     * @param val  assigned value
     */
    public void setCi_wx (int val) {
        this.ci_wx = val;
    }
    /**
     * Getter for y-coordinate
     * @return y index
     */
    public int getCi_wy () {
        return ci_wy;
    }
    /**
     * Setter for y-coordinate
     * @param val  assigned value
     */
    public void setCi_wy (int val) {
        this.ci_wy = val;
    }
    /**
     * Getter for x-value (middle)
     * @return x value
     */
    public float getCi_x () {
        return ci_x;
    }
    /**
     * Setter for x-value (middle)
     * @param val  assigned value
     */
    public void setCi_x (float val) {
        this.ci_x = val;
    }
    /**
     * Getter for x-value (maximal)
     * @return x max value
     */
    public float getCi_xmax () {
        return ci_xmax;
    }
    /**
     * Setter for x-value (maximal)
     * @param val  assigned value
     */
    public void setCi_xmax (float val) {
        this.ci_xmax = val;
    }
    /**
     * Getter for x-value (minimal)
     * @return x min value
     */
    public float getCi_xmin () {
        return ci_xmin;
    }
    /**
     * Setter for x-value (minimal)
     * @param val  assigned value
     */
    public void setCi_xmin (float val) {
        this.ci_xmin = val;
    }
    /**
     * Getter for y-value (middle)
     * @return y value
     */
    public float getCi_y () {
        return ci_y;
    }
    /**
     * Setter for y-value (middle)
     * @param val  assigned value
     */
    public void setCi_y (float val) {
        this.ci_y = val;
    }
     /**
     * Getter for y-value (maximal)
     * @return y max value
     */
    public float getCi_ymax () {
        return ci_ymax;
    }
    /**
     * Setter for y-value (maximal)
     * @param val  assigned value
     */
    public void setCi_ymax (float val) {
        this.ci_ymax = val;
    }
    /**
     * Getter for y-value (minimal)
     * @return y min value
     */
    public float getCi_ymin () {
        return ci_ymin;
    }
    /**
     * Setter for y-value (minimal)
     * @param val  assigned value
     */
    public void setCi_ymin (float val) {
        this.ci_ymin = val;
    }

}

