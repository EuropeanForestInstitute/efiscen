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
 * Container class for parlocator.
 * 
 */
public class GMEfiscenario implements Serializable {

    private int es_nStep;
    private GMParLocator es_paData;

    public GMEfiscenario () {
        es_nStep = 0;
        es_paData = new GMParLocator();
    }
    
    public GMEfiscenario (String name) {
        es_nStep = 0;
        es_paData = new GMParLocator(name);
    }

    public GMEfiscenario (int es_nStep, GMParLocator es_paData) {
        this.es_nStep = es_nStep;
        this.es_paData = es_paData;
    }

    public int getEs_nStep () {
        return es_nStep;
    }

    public void setEs_nStep (int val) {
        this.es_nStep = val;
    }

    public GMParLocator getEs_paData () {
        return es_paData;
    }

    public void setEs_paData (GMParLocator val) {
        this.es_paData = val;
    }
    
    public boolean equals(Object obj) {
        if(!(obj instanceof GMEfiscenario))
            return false;
        else {
            GMEfiscenario temp = (GMEfiscenario)obj;
            if(this.getEs_nStep()!=temp.getEs_nStep()) return false;
            if(!this.getEs_paData().equals(temp.getEs_paData())) return false;
        }
        return true;
    }

}

