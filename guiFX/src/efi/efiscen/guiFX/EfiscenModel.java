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
import efi.efiscen.gm.GMScenario;
import efi.efiscen.gm.GMSimulation;
import efi.efiscen.io.EFISCENException;
import efi.efiscen.io.EFISCENFileNotFoundException;
import efi.efiscen.io.InputLoader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

/**
 * EfiscenModel stores simulation, scenario, experiment on 
 * GMSimulation, GMSimulation and GMEfiscen objects.
 * Reads experiment and scenario with InputLoader from files. Runs simulations
 * by calling GMimulation.onGo().
 * Boolean values running, loaded and loading keep track of simulation running 
 * and scenario or experiment loading and loaded. 
 * 
 */
public class EfiscenModel {
    private GMSimulation simulation;
    private GMScenario scenario;
    private GMEfiscen efiscen;
    private final InputLoader inputLoader; 
    private String experimentName;
    private String scenarioName;
    private final int startingStep = 0;
    private final ExecutorService executor;
    private final BooleanProperty running = new SimpleBooleanProperty();
    private final BooleanProperty loaded = new SimpleBooleanProperty();
    private final BooleanProperty loading = new SimpleBooleanProperty();
    private final BooleanProperty error = new SimpleBooleanProperty();
    private String name;
    private final Map<String,String> filenames;
    
    /**
     * Constructor for EfiscenModel. 
     */
    public EfiscenModel() {
        executor = Executors.newSingleThreadExecutor();
        experimentName = null;
        scenarioName = null;
        inputLoader = new InputLoader();
        inputLoader.setM_scaleAreas(1.0f);
        scenario = inputLoader.getM_Scenario();
        efiscen = inputLoader.getM_pExperiment();
        simulation = null;
        setRunning(false);
        setLoaded(false);
        setLoading(false);
        error.set(false);
        filenames = new HashMap<>();
    }
    
    /**
     * Returns InputLoader used for loading scenarios and experiments.
     * @return InputLoader 
     */
    public InputLoader getInputLoader() { 
        return inputLoader;
    }
    
    /**
     * Returns loading value. Tells whether experiment file is being loaded or not. 
     * True if loading and false if not.
     * @return loading. True if loading and false if not. 
     */
    public boolean getLoading() {
        return loading.get();
    }
    
    /**
     * Sets loading value. True if loading and false if not.
     * @param val loading value. True if loading and false if not.
     */
    public void setLoading(boolean val) {
        loading.set(val);
    }
    
    /**
     * Returns running property object representing running state. Tells whether 
     * simulation is running or not.
     * @return Booleanproperty loading state. True if running and false if not.
     */
    public BooleanProperty getLoadingProperty() {
        return loading;
    }
        
    /**
     * Updates GMEfiscen potential final fellings. 
     * Sets loaded false before updating potential final fellings. 
     * After updating them sets loaded true.
     */
    public void reinit() {
        setLoaded(false);
        efiscen.updatePotentialFellings();
        setLoaded(true);
    }
    
    /**
     * Returns error property. Tells if any errors have occurred.
     * @return BooleanProperty error. True in case of error, false otherwise.
     */
    public BooleanProperty getErrorProperty() {
        return error;
    }
    
    /**
     * Returns a list of filenames that were loaded by the loadExperiment-
     * method.
     * @return 
     */
    public Map<String,String> getLoadedFiles() {
        return filenames;
    }
    
    /**
     * Loads experiment.
     * While loading experiment, set loading true and loaded false. 
     * After experiment has been loaded, set loading false and loaded true.
     * Tells if loading of experiment was successful. Otherwise gives error 
     * message. 
     * @param experimentName Name of the experiment to be loaded.
     * @param scn Is scenario also loaded with experiment file.
     */
    public void loadExperiment(final String experimentName,final boolean scn) {
        this.experimentName = experimentName;
        simulation = null;
        executor.submit(new Task() {

            @Override
            protected Object call() throws Exception {
                setLoading(true);
                setLoaded(false);
                AtomicInteger errors = new AtomicInteger(0);
                try {
                    efiscen = inputLoader.loadExperiment(experimentName, errors, filenames);
                    if(efiscen==null) {
                        System.err.println("Efiscen experiment could not be loaded");
                        getErrorProperty().set(true);
                        return null;
                    }
                } catch (EFISCENException ex) {
                    System.err.println("Error when loading experiment");
                } finally {
                    setLoading(false);
                }
                if(errors.get()>0) {
                    System.err.println("Errors were present in the experiment files");
                }
                String dname = experimentName.substring(experimentName.lastIndexOf(File.separator)+1, experimentName.length());
                System.out.println("Datafile loaded " + dname);
                if(!scn){
                   setLoading(false);
                    setLoaded(true); 
                }
                return null;
            }
        });
        
        String fname = experimentName.substring(experimentName.lastIndexOf(File.separator)+1, experimentName.length());

    }
    
    /**
     * Loads scenario.
     * While loading experiment, set loading true and loaded false. 
     * After experiment has been loaded, set loading false and loaded true.
     * Tells if loading of scenario was successful. Otherwise gives error 
     * message. 
     * @param scenarioName Name of the scenario to be loaded.
     * @throws Exception Thrown when there are errors during loading. Error message
     * is printed to System.err.
     */
    public void loadScenario(final String scenarioName) throws Exception{
        this.scenarioName = scenarioName;
        Callable<Float> t = new Callable<Float>() {

            @Override
            public Float call() throws Exception{
                setLoading(true);
                setLoaded(false);
                AtomicInteger errors = new AtomicInteger(0);
                try {
                    scenario = inputLoader.loadScenario(scenarioName, errors);
                    if(scenario==null) {
                        System.err.println("Efiscen scenario could not be loaded");
                        getErrorProperty().set(true);
                        setLoaded(false);
                        return null;
                    }
                } catch (EFISCENFileNotFoundException ex) {
                    System.err.println("Error when loading scenario");
                } finally {
                    setLoading(false);
                    setLoaded(true);
                }
                if(errors.get()>0) {
                    System.err.println("Errors were present in the scenario files");
                }
                String sname = scenarioName.substring(scenarioName.lastIndexOf(File.separator)+1, scenarioName.length());
                System.out.println("Scenariofile loaded " + sname);
                return null;
            }
        };
        try {
            executor.submit(t).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new Exception();
        }
        
        String fname = scenarioName.substring(scenarioName.lastIndexOf(File.separator)+1, scenarioName.length());
    }
    
    /**
     * Runs simulation by calling GMSimulation.onGo().
     * Set running true, while running simulation
     * and set running false after running of simulation ends. 
     * @param numSteps Number of steps to run simulation.
     */
    public void runSimulation(int numSteps) {
        if(simulation==null) {
            simulation = new GMSimulation(efiscen,scenario,startingStep);
        }
        if(running.get()) {
            System.err.println("simulation running");
            return;
        }
        simulation.setTimeSteps(numSteps);
        simulation.getM_pExperiment().deferr = false;
        System.out.println("Simulation started");
        executor.submit(new Task() {

            @Override
            protected Object call() throws Exception {
                running.set(true);
                simulation.onGo();
                running.set(false);
                if(simulation.getM_pExperiment().deferr) System.err.println("Not enough area to perform deforestation (Check deforestation scenario)");
                System.out.println("Simulation ended");
                return null;
            }
        });
    }
    
    /**
     * Tells if simulation is running or not.
     * @return Running state of simulation. False if not running and true if running.
     */
    public boolean getRunning() {
        return running.get();
    }
    
    /**
     * Set state of running. True if  simulation is running and false if not 
     * running.
     * @param value Running value. True if running and false if not.
     */
    public void setRunning(boolean value) {
        running.set(value);
    }
    
    /**
     * Returns running property object representing running state.
     * @return running property object representing running state. 
     * True if running, false if not.
     */
    public BooleanProperty getRunningProperty() {
        return running;
    }
    
   /**
    * Tells loaded state. Tells whether experiment file is loaded or not.
    * Gives true if loaded and false if not.
    * @return Experiment file loaded state. True if loaded and false if not.
    */
    public boolean getLoaded() {
        return loaded.get();
    }
    
    /**
     * Set loading state. Tells whether experiment file is loading or not. 
     * Gives true if loaded and false if not.
     * @param value loaded value. True if loaded and false if not.
     */
    public void setLoaded(boolean value) {
        loaded.set(value);
    }
    
    /**
     * Returns BooleanProperty object representing loaded state. Tells whether 
     * experiment file is loaded or not.
     * @return loaded
     */
    public BooleanProperty getLoadedProperty() {
        return loaded;
    }
    
    /**
     * Reloads name of experiment and name of scenario. Reloads names from previously 
     * loaded experiment.
     * @throws Exception when scenario files had errors.
     */
    public void reload() throws Exception {
//      Commented  EFI. We take care on loaded and loading properties in loading experiment and scenario
        //TODO: delete after tesing!
//        Platform.runLater(new Runnable() {
//
//            @Override
//            public void run() {
//                //loaded.set(false);
//            }
//        });
        boolean scn = false;
        if(getScenarioName()!=null) scn = true;
        if(getExperimentName()!=null) loadExperiment(getExperimentName(),scn);
        if(getScenarioName()!=null) loadScenario(getScenarioName());
    }

    /**
     * Returns scenario.
     * @return GMScenario object representing scenario. Null if no scenario is
     * loaded.
     */
    public GMScenario getScenario() {
        return scenario;
    }

    /**
     * Returns current experiment. 
     * @return GMEfiscen model representing experiment.
     */
    public GMEfiscen getEfiscen() {
        return efiscen;
    }

    /**
     * Returns name of experiment.
     * @return the experimentName
     */
    public String getExperimentName() {
        return experimentName;
    }

    /**
     * Returns name of scenario.
     * @return Name of scenariofile.
     */
    public String getScenarioName() {
        return scenarioName;
    }
    
   /**
    * Return name.
    * @return null 
    */
    public String getName(){
        return name;
    }
    
    /**
     * Returns simulation.
     * @return GMSimulation object representing current simulation.
     */
    public GMSimulation getSimulation(){
        return simulation;
    }
}
