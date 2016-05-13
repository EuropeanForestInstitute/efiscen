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

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Stores user selections on what data to show in the GUI.
 * 
 */
public class Selections {
    
    private Set<Long> regions;
    private Set<Long> owners;
    private Set<Long> sites;
    private Set<Long> species;
    
    private BooleanProperty changed = new SimpleBooleanProperty();
    
    /**
     * Default constructor
     */
    public Selections() {
        regions = new HashSet<>();
        owners = new HashSet<>();
        sites = new HashSet<>();
        species = new HashSet<>();
        regions.add(0l);
        owners.add(0l);
        sites.add(0l);
        species.add(0l);
    }
    
    /**
     * Returns boolean value of changed. If selections have changed, returns
     * true, false if not.
     * @return boolean value of changed. True if changed.
     */
    public boolean getChanged() {
        return changed.get();
    }
    
    /**
     * Sets boolean value for changed. True if changed.
     * @param val New boolean value for changed
     */
    public void setChanged(boolean val) {
        changed.set(val);
    }
    
    /**
     * Returns BooleanProperty value of changed. If selections have changed, changed is
     * true, false if not.
     * @return BooleanProperty value of changed
     */
    public BooleanProperty getChangedProperty() {
        return changed;
    }
    
    /**
     * Returns the Set of regions selections.
     * @return
     */
    public Set<Long> getRegions() {
        return regions;
    }
    
    /**
     * Returns the Set of owners selections.
     * @return owners selections
     */
    public Set<Long> getOwners() {
        return owners;
    }
    
    /**
     * Returns the Set of sites selections.
     * @return sites selections
     */
    public Set<Long> getSites() {
        return sites;
    }
    
    /**
     * Returns the Set of owners selections.
     * @return species selections
     */
    public Set<Long> getSpecies() {
        return species;
    }
}
