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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Controls ScenarioBox of the GUI. If scenario is loaded, ScenarioBox is shown 
 * on the left side of gui under the SelectionTree.
 * 
 */
public class ScenarioBoxController implements ChangeListener {
    
    private final Label thinning;
    private final Label felling;
    private final TextField thinint;
    private final TextField fellint;
    private final TextField scaling;
    private final GridPane scenariobox;
    private final EfiscenModel model;
    
    /**
     * Constructor for ScenarioBoxController.
     * RunningProperty and LoadedProperty is checked from the EfiscenModel model given as the parameter.
     * @param model EfiscenModel model used by the class to check RunningProperty and LoadedProperty
     * @param thinning Thinning intensity Label
     * @param felling Felling intensity Label
     * @param thinint Thinning intensity TextField
     * @param fellint Felling intensity TextField
     * @param scaling Scaling TextField
     * @param scenariobox GridPane containing scenariobox elements
     */
    public ScenarioBoxController(EfiscenModel model,Label thinning, Label felling, TextField thinint, 
            TextField fellint, TextField scaling, GridPane scenariobox) {
        this.thinning = thinning;
        this.felling = felling;
        this.thinint = thinint;
        this.fellint = fellint;
        this.scaling = scaling;
        this.scenariobox = scenariobox;
        this.model = model;
        scenariobox.setDisable(true);
    }
    
    /**
     * Disables and enables scenariobox depending on data loading or EfiscenModel
     * simulation running. If scenario is running, ScenarioBox is disabled.
     * @param ov Value that is observed if it has changed
     * @param oldVal Old value
     * @param newVal New value
     */
    @Override
    public void changed(ObservableValue ov, Object oldVal, Object newVal) {
        if(ov==model.getRunningProperty() && ov==model.getLoadedProperty()) {
            if((Boolean)newVal) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scenariobox.setDisable(true);
                    }
                });
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scenariobox.setDisable(false);
                    }
                });
            }
        }
    }
}
