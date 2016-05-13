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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;



/**
 * Controller class for Graph panel. Controls chart that shows
 * graphs of selected variables. Listens selections for regions, owners, sites
 * and species and shows data according to selections. 
 * 
 *
 */

public class ChartController implements ChangeListener {
    
    private LineChart<Float,Float> panel;
    private EfiscenModel efiscenModel = null;
    private TreeMap<String,Boolean> graphsEnabled = new TreeMap<>();
    private Map<String,XYChart.Series<Float,Float>> allSeries = new HashMap<>();
    private Selections selections;
    private final Set<Long> regions;
    private final Set<Long> owners;
    private final Set<Long> sites;
    private final Set<Long> species;
    private DataParsers dataParsers;
    private ResourceBundle rb;
        
    /**
     * Constructor for ChartController
     * @param panel chart where graphs are printed 
     * @param efiscenModel Model containing data that is shown in the graph
     * @param selections selections for regions, owners, sites and species  
     * @param rb ResourceBundle containing localization.
     */
    public ChartController(LineChart<Float,Float> panel, EfiscenModel efiscenModel,
            Selections selections, ResourceBundle rb) {
        this.selections = selections;
        this.panel = panel;
        this.efiscenModel = efiscenModel;
        this.rb = rb;
        dataParsers = new DataParsers(efiscenModel);
        ((ValueAxis)panel.getXAxis()).setTickLabelFormatter(new StringConverter() {

            @Override
            public String toString(Object t) {
                Number n = (Number) t;
                if(n.intValue()!=n.doubleValue()) return "..";
                return ""+n.intValue();
            }

            @Override
            public Object fromString(String string) {
                return Double.parseDouble(string);
            }
        });
        regions = new HashSet<>();
        owners = new HashSet<>();
        sites = new HashSet<>();
        species = new HashSet<>();
        regions.add(0l);
        owners.add(0l);
        sites.add(0l);
        species.add(0l);
        
        //get all parsable values from DataParsers
        //set all graphs disabled
        for(String key : dataParsers.getIds()) {
            graphsEnabled.put(key, Boolean.FALSE);
        }
        
        GMEfiscen efiscen = efiscenModel.getEfiscen();
        if(efiscen==null) efiscen = new GMEfiscen();
        
    }
    
    /**
     * Gets whether a certain graph is shown or not.
     * @param id id of a graph to get information of the graph state
     * @return false if graph id not found otherwise graph's state. True if graph
     * is enabled, false otherwise.
     */
    public Boolean getGraphState(String id) {
        if( graphsEnabled.containsKey(id))
            return graphsEnabled.get(id);
        else return false;
    }
    
    /**
     * Shows/hides a certain graph.
     * @param id id of a graph to show or hide graph
     */
    public void toggleGraphState(String id) {
        GMEfiscen efiscen = efiscenModel.getEfiscen();
        if(efiscen==null) {
            System.err.println("Efiscen instance is null");
            return;
        }
        if(graphsEnabled.get(id) == null || !graphsEnabled.get(id)) {
            XYChart.Series<Float,Float> serie = null;
            serie = dataParsers.getSeries(
                    regions, owners, sites, species, id);
            String key = "key."+id+"Label";
            String seriename = rb.getString("key."+id+"Label");
            serie.setName(rb.getString("key."+id+"Label"));
            panel.getData().add(serie);
            allSeries.put(id, serie);
            graphsEnabled.put(id, Boolean.TRUE);
        } else {
            panel.getData().remove(allSeries.get(id));
            graphsEnabled.put(id, Boolean.FALSE);
        }
    }
    
    /**
     * Gets latest updated data values from the model and adds them to visible
     * graphs.
     */
    public void addLatest() {
        GMEfiscen efiscen = efiscenModel.getEfiscen();
        if(efiscen==null) efiscen = new GMEfiscen();
        for(String id : graphsEnabled.keySet()) {
            if(graphsEnabled.get(id)) {
                XYChart.Series<Float,Float> series = allSeries.get(id);
                XYChart.Data<Float,Float> point = new XYChart.Data<>((float)series.getData().size(),
                        dataParsers.getLatest(regions, owners, sites, species, id));
                series.getData().add(point);
            }
        }
    }
    
    /**
     * Updates graph. Removes data that is not selected and adds new selected data.
     * Modified by EFI October 2013 - bug with color fixing
     */
    void updateGraphData() {
        //TODO: refactor to make more elegant!!!!! EFI 2013 October
        for(String id : graphsEnabled.keySet()) {
            if(graphsEnabled.get(id)) {
                XYChart.Series<Float,Float> serie = allSeries.get(id);
                serie.setData(dataParsers.getSeries(regions, owners,
                        sites, species, id).getData());
                    serie.setName(id);
                    allSeries.put(id, serie); 
                    graphsEnabled.put(id,Boolean.TRUE);
                if(!panel.getData().contains(serie)) panel.getData().add(serie);
            }
            else {
                XYChart.Series serie = allSeries.get(id);
                if(serie!=null) panel.getData().remove(serie);
                graphsEnabled.put(id,Boolean.FALSE);
            }
        }
    }
    
    /**
     * Clears all selections and removes all graphs and clears them.
     */
    public void clearData() {
        if(selections!=null) {
            this.regions.clear();
            this.owners.clear();
            this.sites.clear();
            this.species.clear();
            if(!allSeries.isEmpty()) {
                for(String tag : allSeries.keySet()) {
                    XYChart.Series<Float,Float> serie = allSeries.get(tag);
                    panel.getData().remove(serie);
                    serie.getData().clear();
                }
                allSeries.clear();
            }
        }
    }
    
   /**
    * Clears all selections and initializes them and removes all graphs and clears them.
    */
    public void clearAll() {
        if(selections!=null) {
            this.regions.clear();
            this.owners.clear();
            this.sites.clear();
            this.species.clear();
            this.regions.add(new Long(0));
            this.owners.add(new Long(0));
            this.sites.add(new Long(0));
            this.species.add(new Long(0));
        }
        if(!allSeries.isEmpty()) {
            for(String tag : allSeries.keySet()) {
                XYChart.Series<Float,Float> serie = allSeries.get(tag);
                panel.getData().remove(serie);
                serie.getData().clear();
            }
            allSeries.clear();
        }
    }

    /**
     * If selections have changed, updates the graph.
     * @param ov Value that is observed if it has changed.
     * @param t Old value
     * @param t1 New value
     */
    @Override
    public void changed(ObservableValue ov, Object t, Object t1) {
        if(ov == selections.getChangedProperty() && (Boolean)t1) {
            if(selections!=null) {
             /*   Platform.runLater(new Runnable() {

                    @Override
                    public void run() {*/
                        regions.clear();
                        owners.clear();
                        sites.clear();
                        species.clear();
                        for(long r : selections.getRegions())
                            regions.add(r);
                        for(long o : selections.getOwners())
                            owners.add(o);
                        for(long st : selections.getSites())
                            sites.add(st);
                        for(long sp : selections.getSpecies())
                            species.add(sp);
                        panel.getData().clear();
                        updateGraphData();
                        selections.setChanged(false);
                  /*  }
                    
                });*/
            }
        }
    }
    
    /**
     * Hides all graphs.
     * Disables all graphs by putting their enabled state to false. 
     */
    
    public void disableAll(){
        for(String key : graphsEnabled.keySet())
            graphsEnabled.put(key, Boolean.FALSE);
    }
}
