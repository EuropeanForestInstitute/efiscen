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

import efi.efiscen.gm.GMEfiscen;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Labels that show data values, buttons to select graphs and sends
 * selections to chartController. 
 * 
 */
public class DataPanelController implements ChangeListener, Initializable {

    @FXML private Label jAreaValue;
    @FXML private Label jDeadWoodValue;
    @FXML private Label jIncrAvValue;
    @FXML private Label jNatMortValue;
    @FXML private Label jThinAreaValue;
    @FXML private Label jFFellAreaValue;
    @FXML private Label jThinRemsValue;
    @FXML private Label jFellRemsValue;
    @FXML private Label jFellAreaValue;
    @FXML private Label jFellVolumeValue;
    @FXML private Label jBareAreaValue;
    @FXML private Label jAfforFundValue;
    @FXML private Label jVolumeValue;
    @FXML private Label jAvVolumeValue;
    @FXML private Label jNwlValue;
    @FXML private Label jFwlValue;
    @FXML private Label jCwlValue;
    @FXML private Label jSolValue;
    @FXML private Label jCelValue;
    @FXML private Label jLigValue;
    @FXML private Label jHum1Value;
    @FXML private Label jHum2Value;
    @FXML private Label jCoutValue;
    @FXML private Label jCSoilValue;
    @FXML private Label jStemValue;
    @FXML private Label jCRootsValue;
    @FXML private Label jFRootsValue;
    @FXML private Label jBranchesValue;
    @FXML private Label jLeavesValue;
    @FXML private Label jTotalValue;
    @FXML private LineChart historyChart;
    @FXML private VBox datapanelgroup;
    @FXML private Label matNum;
    
    @FXML private ToggleButton Area;
    @FXML private ToggleButton DeadWood;
    @FXML private ToggleButton IncrAv;
    @FXML private ToggleButton NatMort;
    @FXML private ToggleButton ThinArea;
    @FXML private ToggleButton FFarea;
    @FXML private ToggleButton ThinRems;
    @FXML private ToggleButton FelRems;
    @FXML private ToggleButton PotentFellArea;
    @FXML private ToggleButton PotentFellVol;
    @FXML private ToggleButton BareArea;
    @FXML private ToggleButton AfforFund;
    @FXML private ToggleButton Vol;
    @FXML private ToggleButton avgVolume;
    @FXML private ToggleButton NWL;
    @FXML private ToggleButton FWL;
    @FXML private ToggleButton CWL;
    @FXML private ToggleButton SOL;
    @FXML private ToggleButton CEL;
    @FXML private ToggleButton LIG;
    @FXML private ToggleButton HUM1;
    @FXML private ToggleButton HUM2;
    @FXML private ToggleButton COUT;
    @FXML private ToggleButton CSoil;
    @FXML private ToggleButton Stem;
    @FXML private ToggleButton CRoots;
    @FXML private ToggleButton FRoots;
    @FXML private ToggleButton Branches;
    @FXML private ToggleButton Leaves;
    @FXML private ToggleButton tCarbon;
    
    private final EfiscenModel model;
    private final Set<Long> regions = new HashSet<>();
    private final Set<Long> owners = new HashSet<>();
    private final Set<Long> sites = new HashSet<>();
    private final Set<Long> species = new HashSet<>();
    private final Selections selections;
    private ChartController chartController;
    private DataParsers dataParsers;
    
    
    /**
     * Constructor for DataPanelController.
     * @param model Model to read data from
     * @param selections Selections for what data to show on labels.
     */
    public DataPanelController(EfiscenModel model,Selections selections) {
        this.model = model;
        this.selections = selections;
        this.dataParsers = new DataParsers(model);
        if(selections==null) {
            regions.add(0l);
            owners.add(0l);
            sites.add(0l);
            species.add(0l);
        }
    }
    
    /**
     * Initializes chartController
     * @param url url
     * @param rb resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(selections!=null) {
            chartController = new ChartController(historyChart,model,selections,rb);
            selections.getChangedProperty().addListener(chartController);
        }
        else {
            chartController = new ChartController(historyChart,model,null,rb);
        }
        datapanelgroup.setDisable(true);
    }
        
    /**
     * Updates values of the graph data of regions, owners, sites and species
     * based on EfiscenModel model LoadedProperty and newVal and disables or 
     * enables datapanelgroup of the values depending on data loading or 
     * simulation running.
     * @param ov observable value
     * @param oldVal old value 
     * @param newVal new value
     */
    @Override
    public void changed(ObservableValue ov, Object oldVal, Object newVal) {
        if(ov == model.getLoadedProperty() && (Boolean)newVal) {
            if(selections!=null) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        datapanelgroup.setDisable(false);
                        setValues();
                        int nRegions = (int) model.getEfiscen().m_mRegions.size();
                        int nOwners = (int) model.getEfiscen().m_mOwners.size();
                        int nSites = (int) model.getEfiscen().m_mSites.size();
                        int nSpecies = (int) model.getEfiscen().m_mSpecies.size();
                        matNum.setText(""+nRegions*nOwners*nSites*nSpecies);
                        chartController.disableAll();
                        releaseButtons();
                        chartController.updateGraphData();
                    }
                });
            }
            else {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        datapanelgroup.setDisable(false);
                        setValues();
                        int nRegions =  model.getEfiscen().m_mRegions.size();
                        int nOwners =  model.getEfiscen().m_mOwners.size();
                        int nSites = model.getEfiscen().m_mSites.size();
                        int nSpecies = model.getEfiscen().m_mSpecies.size();
                        matNum.setText(""+nRegions*nOwners*nSites*nSpecies);
                        chartController.disableAll();
                        releaseButtons();
                        chartController.updateGraphData();
                    }
                });
            }
        }
        if(ov == model.getLoadedProperty() && !(Boolean)newVal) {
            Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        datapanelgroup.setDisable(true);
                    }
                });
        }
        if(ov == model.getRunningProperty()) {
            if(!(Boolean)newVal)
            {
                if(selections!=null) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setValues();
                            chartController.updateGraphData();
                            datapanelgroup.setDisable(false);
                        }
                    });
                }
                else {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setValues();
                            chartController.updateGraphData();
                            datapanelgroup.setDisable(false);
                        }
                    });
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        datapanelgroup.setDisable(true);
                    }
                });
            }
        }
        if(selections!=null && ov == selections.getChangedProperty() && (Boolean)newVal) {
            regions.clear();
            owners.clear();
            sites.clear();
            species.clear();
            regions.addAll(selections.getRegions());
            owners.addAll(selections.getOwners());
            sites.addAll(selections.getSites());
            species.addAll(selections.getSpecies());
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    setValues();
                }
            });
            //selections.setChanged(false);
        }
    }
    
    
    /**
     * Listener for graph buttons. Calls chartController.toggleGraphState() when
     * button is pushed.
     * @param evt event
     */
    public void listenGraphButtons(ActionEvent evt) {
        ToggleButton btn = (ToggleButton)evt.getSource();
        chartController.toggleGraphState(btn.getId());
    }
    
    /**
     * Sets data for "Selected" tab
     */
    protected void setValues() {
        
        GMEfiscen gmefiscen = model.getEfiscen();
        //General Area
        float areaf = dataParsers.getLatest(regions, owners, sites, species, "Area");
        areaf = new BigDecimal(areaf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jAreaValue.setText(Float.toString(areaf));
        
        float dA = dataParsers.getLatest(regions, owners, sites, species, "PotentFellArea");
        dA = new BigDecimal(dA).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFellAreaValue.setText(Float.toString(dA));
        
        float dV = dataParsers.getLatest(regions, owners, sites, species, "PotentFellVol");
        dV = new BigDecimal(dV).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFellVolumeValue.setText(Float.toString(dV));
        
        float deadWoodf = dataParsers.getLatest(regions, owners, sites, species, "DeadWood");
        deadWoodf = new BigDecimal(deadWoodf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jDeadWoodValue.setText(Float.toString(deadWoodf));
        
        
        float incrAvf = dataParsers.getLatest(regions, owners, sites, species, "IncrAv");
        incrAvf = new BigDecimal(incrAvf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jIncrAvValue.setText(Float.toString(incrAvf));
        
        float natMortf = dataParsers.getLatest(regions, owners, sites, species, "NatMort");
        natMortf = new BigDecimal(natMortf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jNatMortValue.setText(Float.toString(natMortf));
        
        float thinAreaf = dataParsers.getLatest(regions, owners, sites, species, "ThinArea");
        thinAreaf = new BigDecimal(thinAreaf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jThinAreaValue.setText(Float.toString(thinAreaf));
        
        float fellAreaf = dataParsers.getLatest(regions, owners, sites, species, "FFarea");
        fellAreaf = new BigDecimal(fellAreaf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFFellAreaValue.setText(Float.toString(fellAreaf));
        
        float thinVolf = dataParsers.getLatest(regions, owners, sites, species, "ThinRems");
        thinVolf = new BigDecimal(thinVolf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jThinRemsValue.setText(Float.toString(thinVolf));
        
        float fellVolf = dataParsers.getLatest(regions, owners, sites, species, "FelRems");
        fellVolf = new BigDecimal(fellVolf).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFellRemsValue.setText(Float.toString(fellVolf));
        
        // Bare Area
        float bareArea = 0;
        for (Long r : regions ) {
            for (Long o : owners ) {
                for (Long st : sites ) {
                    for (Long sp : species ) {
                        bareArea += gmefiscen.m_BareFund.getFund(r, o, st, sp);
                    }
                }
            }
        }
        
        bareArea = new BigDecimal(bareArea).setScale(1, RoundingMode.HALF_UP).floatValue();
        jBareAreaValue.setText(Float.toString(bareArea));
        
        // Affor Fund
        float afforFund = 0.00f;
        for (Long r : regions ) {
            for (Long o : owners ) {
                for (Long st : sites ) {
                    for (Long sp : species ) {
                        afforFund += gmefiscen.getZeroClass(r, o, st, sp);
                    }
                }
            }
        }
        afforFund = new BigDecimal(afforFund).setScale(1, RoundingMode.HALF_UP).floatValue();
        jAfforFundValue.setText(Float.toString(afforFund));
        
        // General Volume
        Float val = 0.00f;
        val = dataParsers.getLatest(regions, owners, sites, species, "Vol");
        Float valBig = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jVolumeValue.setText(Float.toString(valBig));
        
        // Average Volume
        Float avgGrowthStock = 0.0f;
        if(areaf>0.f) avgGrowthStock = val / areaf;
        avgGrowthStock = new BigDecimal(avgGrowthStock).setScale(1, RoundingMode.HALF_UP).floatValue();
         this.jAvVolumeValue.setText(Float.toString(avgGrowthStock));
        
        // Soils
        val = dataParsers.getLatest(regions, owners, sites, species, "NWL");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jNwlValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "FWL");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFwlValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "CWL");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jCwlValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "SOL");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jSolValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "CEL");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jCelValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "LIG");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jLigValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "HUM1");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jHum1Value.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "HUM2");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jHum2Value.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "COUT");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jCoutValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "CSoil");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jCSoilValue.setText(val.toString());
        
        // Carbons
        val = dataParsers.getLatest(regions, owners, sites, species, "Stem");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jStemValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "CRoots");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jCRootsValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "FRoots");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jFRootsValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "Branches");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jBranchesValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "Leaves");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jLeavesValue.setText(val.toString());
        val = dataParsers.getLatest(regions, owners, sites, species, "tCarbon");
        val = new BigDecimal(val).setScale(1, RoundingMode.HALF_UP).floatValue();
        jTotalValue.setText(val.toString());
    }
    
    /**
     * Sets all selected data to false.
     */
    public void releaseButtons(){
            Area.setSelected(false);
            DeadWood.setSelected(false);
            IncrAv.setSelected(false);
            NatMort.setSelected(false);
            ThinArea.setSelected(false);
            FFarea.setSelected(false);
            ThinRems.setSelected(false);
            FelRems.setSelected(false);
            PotentFellArea.setSelected(false);
            PotentFellVol.setSelected(false);
            BareArea.setSelected(false);
            AfforFund.setSelected(false);
            Vol.setSelected(false);
            avgVolume.setSelected(false);
            NWL.setSelected(false);
            FWL.setSelected(false);
            CWL.setSelected(false);
            SOL.setSelected(false);
            CEL.setSelected(false);
            LIG.setSelected(false);
            HUM1.setSelected(false);
            HUM2.setSelected(false);
            COUT.setSelected(false);
            CSoil.setSelected(false);
            Stem.setSelected(false);
            CRoots.setSelected(false);
            FRoots.setSelected(false);
            Branches.setSelected(false);
            Leaves.setSelected(false);
            tCarbon.setSelected(false);
    }
}
