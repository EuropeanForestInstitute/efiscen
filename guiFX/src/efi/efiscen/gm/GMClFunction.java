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
 * A class to keep parameters of function describing dependence of soil decomposition
 * rates on climate.
 * <p><i>r(T,D)=1+beta*(T-Tav)+gamma*(D-Di)<br>
 *  where: T,D - current temperature and drought index<br>
 *          Tav,Di - reference values for temperature and drought<br>
 *          beta,gamma - parameters </i></p>
 * For details refer the user manual to EFISCEN model
 * 
 */
public class GMClFunction implements Serializable {

    private double cf_beta;
    private double cf_gamma;
    private double cf_tav;
    private double cf_di;

    /**
     * Default (empty) constructor
     */
    public GMClFunction () {
    }

    /**
     * Parameterized constructor 
     * @param cf_beta beta parameter
     * @param cf_gamma gamma parameter
     * @param cf_tav reference temperature
     * @param cf_di reference drought
     */
    public GMClFunction (double cf_beta, double cf_gamma, double cf_tav,
            double cf_di) {
        this.cf_beta = cf_beta;
        this.cf_gamma = cf_gamma;
        this.cf_tav = cf_tav;
        this.cf_di = cf_di;
    }

    /**
     * Getter for beta 
     * @return beta parameter
     */
    public double getCf_beta () {
        return cf_beta;
    }

    /**
     * Setter for beta
     * @param val value to be assigned
     */
    public void setCf_beta (double val) {
        this.cf_beta = val;
    }

    /**
     * Getter for drought
     * @return reference drought
     */
    public double getCf_di () {
        return cf_di;
    }

    /**
     * Setter for drought
     * @param val value to be assigned
     */
    public void setCf_di (double val) {
        this.cf_di = val;
    }

    /**
     * Getter for gamma
     * @return gamma parameter
     */
    public double getCf_gamma () {
        return cf_gamma;
    }

    /**
     * Setter for gamma
     * @param val value to be assigned
     */
    public void setCf_gamma (double val) {
        this.cf_gamma = val;
    }

    /**
     * Getter for temperature
     * @return reference temperature
     */
    public double getCf_tav () {
        return cf_tav;
    }

    /**
     * Setter for temperature
     * @param val value to be assigned
     */
    public void setCf_tav (double val) {
        this.cf_tav = val;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GMClFunction))
            return false;
        else {
            GMClFunction temp = (GMClFunction)obj;
            if(this.getCf_beta() != temp.getCf_beta()) return false;
            if(this.getCf_di() != temp.getCf_di()) return false;
            if(this.getCf_gamma() != temp.getCf_gamma()) return false;
            if(this.getCf_tav() != temp.getCf_tav()) return false;
        }
        return true;
    }

}

