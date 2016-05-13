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
 * Class to implement default growth function in EFISCEN
 * function: <i> f(x) = a0 + a1/x + a2/x² defined on interval [x_left,x_right]
 *          where a0,a1,a2 - parameters and x is age of the forest </i>
 * 
 */
public class GMGrFunDefault extends GMGrFunction {

    //private GMGrFunction func;

    // Interval to apply
    private double m_Xleft;
    private double m_Xright;

    // Coefficients
    private double m_a0;
    private double m_a1;
    private double m_a2;

    /**
     * Empty constructor. Sets function to constant zero
     * with interval [10,100]
     */
    public GMGrFunDefault () {
        //func = new GMGrFunction();
        super();
        m_Xleft = 10.0;
        m_Xright = 100.0;
        m_a0 = m_a1 = m_a2 = 0;
    }

    /**
     * Calculates the value of the function
     * @param x the argument
     * @return the calculated value
     */
    @Override
    public double calculate (double x) {
        if (x == 0) return -999.;
        double a = x;
        if (a<m_Xleft) a = m_Xleft;
        if (a>m_Xright) a = m_Xright;
        m_value = m_a0 + m_a1/a + m_a2/(a*a);
        return m_value;
    }

    /**
     * Set the coefficients according to the given values.
     * @param a0 coefficient <i>a0</i> 
     * @param a1 coefficient <i>a1</i>
     * @param a2 coefficient <i>a2</i>
     * @return 1 if successful
     */
    public int setCoeff (double a0, double a1, double a2) {
        m_Xleft = 10.0;
        m_Xright = 100.0;
        m_a0 = a0;
        m_a1 = a1;
        m_a2 = a2;
        return 1;
    }

    /**
     * Set the coefficients and limits according to the given values.
     * @param a0 coefficient <i>a0</i>
     * @param a1 coefficient <i>a1</i>
     * @param a2 coefficient <i>a2</i>
     * @param xl left point of interval <i>x_left</i>
     * @param xr right point of interval <i>x_right</i>
     * @return 1 if successful
     */
    public int setCoeffEx (double a0, double a1, double a2, double xl,
            double xr) {
        m_Xleft = xl;
        m_Xright = xr;
        m_a0 = a0;
        m_a1 = a1;
        m_a2 = a2;
        return 1;
    }

}

