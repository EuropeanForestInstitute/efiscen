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
 * A class for storing information about initializing the matrix.
 * 
 */
public class GMMatrixInit {

    private long mi_id;

    // Lower value by X
    private float mi_xb;
    // Upper value by X
    private float mi_xt;
    // Lower value by Y
    private float mi_yb;
    // Upper value by Y
    private float mi_yt;
    // Step by X
    private float mi_xs;
    // Step by Y
    private float mi_ys;

    public GMMatrixInit () {
    }

    public GMMatrixInit (long mi_id, float mi_xb, float mi_xt, float mi_yb,
            float mi_yt, float mi_xs, float mi_ys) {
        this.mi_id = mi_id;
        this.mi_xb = mi_xb;
        this.mi_xt = mi_xt;
        this.mi_yb = mi_yb;
        this.mi_yt = mi_yt;
        this.mi_xs = mi_xs;
        this.mi_ys = mi_ys;
    }

    public long getMi_id () {
        return mi_id;
    }

    public void setMi_id (long val) {
        this.mi_id = val;
    }

    public float getMi_xb () {
        return mi_xb;
    }

    public void setMi_xb (float val) {
        this.mi_xb = val;
    }

    public float getMi_xs () {
        return mi_xs;
    }

    public void setMi_xs (float val) {
        this.mi_xs = val;
    }

    public float getMi_xt () {
        return mi_xt;
    }

    public void setMi_xt (float val) {
        this.mi_xt = val;
    }

    public float getMi_yb () {
        return mi_yb;
    }

    public void setMi_yb (float val) {
        this.mi_yb = val;
    }

    public float getMi_ys () {
        return mi_ys;
    }

    public void setMi_ys (float val) {
        this.mi_ys = val;
    }

    public float getMi_yt () {
        return mi_yt;
    }

    public void setMi_yt (float val) {
        this.mi_yt = val;
    }

}

