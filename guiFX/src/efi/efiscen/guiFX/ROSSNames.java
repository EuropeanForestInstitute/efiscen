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
package efi.efiscen.guiFX;

import efi.efiscen.gm.GMCollection;
import efi.efiscen.gm.GMEfiscen;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Region, owner, site and species names storage. Contains
 * a changed-property that can be listened.
 * 
 */
public class ROSSNames implements ChangeListener {
    
    private Map<Long,String> regionNames;
    private Map<Long,String> siteNames;
    private Map<Long,String> ownerNames;
    private Map<Long,String> speciesNames;
    private BooleanProperty changed = new SimpleBooleanProperty();
    private EfiscenModel model;
    
    /**
     * Parameterized constructor. 
     * @param model Model from which names for regions, sites, owners and species
     * are read.
     */
    public ROSSNames(EfiscenModel model) {
        regionNames = new HashMap<>();
        siteNames = new HashMap<>();
        ownerNames = new HashMap<>();
        speciesNames = new HashMap<>();
        this.model = model;
    }
    
    /** 
     * Returns whether attributes have changed. Returns true if there are changes
     * in regions, sites, owners or species names.
     * @return Boolean value of changed. True if names have changed.
     */
    public boolean getChanged() {
        return changed.get();
    }
    
    /**
     * Sets boolean value for changed. Returns whether there are changes
     * in regions, sites, owners or species names. True if there are changes, false if not.
     * @param val Boolean value for changed. True if there are changes, false if not.
     */
    public void setChanged(boolean val) {
        changed.set(val);
    }
    
    /**
     * Returns BooleanProperty value of changed. Returns whether there are changes
     * in regions, sites, owners or species names. True if there are changes, false if not.
     * @return the BooleanProperty value of changed. True if there are changes, false if not.
     */
    public BooleanProperty getChangedProperty() {
        return changed;
    }

    /**
     * Returns the region names.
     * @return Region names.
     */
    public Map<Long,String> getRegionNames() {
        return regionNames;
    }

    /**
     * Returns the site names. 
     * @return Site names.
     */
    public Map<Long,String> getSiteNames() {
        return siteNames;
    }

    /**
     * Returns the owner names.
     * @return Owner names.
     */
    public Map<Long,String> getOwnerNames() {
        return ownerNames;
    }

    /**
     * Returns the species names.
     * @return Species names.
     */
    public Map<Long,String> getSpeciesNames() {
        return speciesNames;
    }

    /**
     * Changed-method for model.getLoadedLroperty(). If the loaded property has changed to 
     * true the method loads region names, owner names, site names and 
     * species names from GMEfiscen model. Else it clears the data of the listed attributes.
     * @param ov Observable value of changed
     * @param oldVal Old value of changed
     * @param newVal New value of changed
     */
    @Override
    public void changed(ObservableValue ov, Object oldVal, Object newVal) {
        if(ov == model.getLoadedProperty()) {
            if((Boolean)newVal) {
                setChanged(false);
                GMEfiscen efiscen = model.getEfiscen();
                for(Long id : efiscen.m_mRegions.keySet()) {
                    GMCollection get = efiscen.m_mRegions.get(id);
                    regionNames.put(id, get.m_sName);
                }
                for(Long id : efiscen.m_mOwners.keySet()) {
                    GMCollection get = efiscen.m_mOwners.get(id);
                    ownerNames.put(id, get.m_sName);
                }
                for(Long id : efiscen.m_mSites.keySet()) {
                    GMCollection get = efiscen.m_mSites.get(id);
                    siteNames.put(id, get.m_sName);
                }
                for(Long id : efiscen.m_mSpecies.keySet()) {
                    GMCollection get = efiscen.m_mSpecies.get(id);
                    speciesNames.put(id, get.m_sName);
                }
                setChanged(true);
            }
            if(!(Boolean)newVal) {
                setChanged(false);
                regionNames.clear();
                ownerNames.clear();
                siteNames.clear();
                speciesNames.clear();
                setChanged(true);
            }
        }
    }
}
