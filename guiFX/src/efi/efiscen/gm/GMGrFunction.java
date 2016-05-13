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
 * Base class to implement growth function in EFISCEN
 * 
 */
public class GMGrFunction implements Serializable {

    /**
     * Calculated value
     */
    protected double m_value;

    /**
     * Default constructor. Dummy (constant)function
     */
    public GMGrFunction () {
        m_value = -999.;
    }

    /**
     * (Over writable).Calculates function by given argument
     * <i> constant function in base class realization </i> 
     * @param x argument
     * @return calculated value
     */
    public double calculate (double x) {
        return m_value;
    }

}

