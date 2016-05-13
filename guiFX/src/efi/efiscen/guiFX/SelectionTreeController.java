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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Controller for the selection tree containing matrix identifiers. Selection tree
 * is used to select on which regions, owners, sites and species to show data on
 * dataPanel. Selections are stored on Selections-class.
 * 
 */
public class SelectionTreeController implements ChangeListener, ListChangeListener {
    
    private Selections selections;
    private ROSSNames names;
    private TreeView selectionTree;
    private TreeItem<TreeData> regions; 
    private TreeItem<TreeData> owners; 
    private TreeItem<TreeData> sites; 
    private TreeItem<TreeData> species;
    
    /**
     * Updates Selections when some selection of the SelectionTree has changed.
     * @param change object representing change made. Not used by the method itself.
     */
    @Override
    public void onChanged(Change change) {
        selections.getRegions().clear();
        selections.getOwners().clear();
        selections.getSites().clear();
        selections.getSpecies().clear();
        ObservableList<TreeItem<TreeData>> selectedItems = 
                selectionTree.getSelectionModel().getSelectedItems();
        for(TreeItem<TreeData> item : selectedItems) {
            if(regions.getChildren().contains(item)) {
                selections.getRegions().add(item.getValue().getId());
            }
            if(owners.getChildren().contains(item)) {
                selections.getOwners().add(item.getValue().getId());
            }
            if(sites.getChildren().contains(item)) {
                selections.getSites().add(item.getValue().getId());
            }
            if(species.getChildren().contains(item)) {
                selections.getSpecies().add(item.getValue().getId());
            }
        }
        if(selections.getRegions().isEmpty()) selections.getRegions().add(0l);
        if(selections.getOwners().isEmpty()) selections.getOwners().add(0l);
        if(selections.getSites().isEmpty()) selections.getSites().add(0l);
        if(selections.getSpecies().isEmpty()) selections.getSpecies().add(0l);
        selections.setChanged(true);
    }
    
    /**
     * Contains the name and id data of single selection element of regions, owners
     * species or sites.
     */
    private static class TreeData {
        String name;
        Long id;

        /**
         * Parameterized constructor
         * @param name name of the selected element
         * @param id id of the selected element
         */
        public TreeData(String name, Long id) {
            this.name = name;
            this.id = id;
        }

        /**
         * Returns the name of TreeData object
         * @return name of TreeData object
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the id of TreeData object
         * @return id of TreeData object
         */
        public Long getId() {
            return id;
        }
        
        /**
         * Returns String representation of the TreeData object
         * @return String representation of the TreeData object
         */
        @Override
        public String toString() {
            return name;
        }
    }
    
    /**
     * Parameterized constructor.
     * @param selections Selections object containing the selected elements 
     * @param names ROSSnames element containing Region, owner, site and species names storage
     * @param selectionTree How the data is shown in the selection tree for the user 
     */
    public SelectionTreeController(Selections selections,ROSSNames names,TreeView selectionTree) {
        this.selections = selections;
        this.names = names;
        this.selectionTree = selectionTree;
        TreeItem<TreeData> root = new TreeItem<>(new TreeData("Selections",-1l));
        root.setExpanded(true);
        selectionTree.setRoot(root);
        selectionTree.setShowRoot(false);
        selectionTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        regions = new TreeItem<>(new TreeData("Regions",0l));
        owners = new TreeItem<>(new TreeData("Owners",0l));
        sites = new TreeItem<>(new TreeData("Sites",0l));
        species = new TreeItem<>(new TreeData("Species",0l));
        root.getChildren().addAll(regions,owners,sites,species);
        selectionTree.getSelectionModel().getSelectedItems().addListener(this);
    }
    
    /**
     * Updates selection tree with new items after clearing it.
     */
    private void updateSelectionTree() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                TreeItem root = selectionTree.getRoot();
                if(root!=null) {
                    ObservableList<TreeItem<String>> children = root.getChildren();
                    regions.getChildren().clear();
                    owners.getChildren().clear();
                    sites.getChildren().clear();
                    species.getChildren().clear();
                    for(Long id : names.getRegionNames().keySet()) {
                        String name = names.getRegionNames().get(id);
                        regions.getChildren().add(new TreeItem<>(new TreeData(name,id)));
                    }
                    for(Long id : names.getOwnerNames().keySet()) {
                        String name = names.getOwnerNames().get(id);
                        owners.getChildren().add(new TreeItem<>(new TreeData(name,id)));
                    }
                    for(Long id : names.getSiteNames().keySet()) {
                        String name = names.getSiteNames().get(id);
                        sites.getChildren().add(new TreeItem<>(new TreeData(name,id)));
                    }
                    for(Long id : names.getSpeciesNames().keySet()) {
                        String name = names.getSpeciesNames().get(id);
                        species.getChildren().add(new TreeItem<>(new TreeData(name,id)));
                    }
                }
            }
        });
    }

    /**
     * Updates SelectionTree if some selection has a new value.
     * @param ov Value that is observed if it has changed
     * @param oldVal Old value
     * @param newVal New value
     */
    @Override
    public void changed(ObservableValue ov, Object oldVal, Object newVal) {
        if(ov == names.getChangedProperty() && (Boolean)newVal) {
            updateSelectionTree();
        }
    }
}
