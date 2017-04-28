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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller class for frame.fmxl. Contains all the elements in the main window
 * besides elements in the dataPanel (graph and graph buttons). 
 * Loads dataPanel.fxml and dataPanelController when initialized. 
 * 
 */
public class FrameController implements Initializable, ChangeListener {
    
    @FXML private TextField thinint;
    @FXML private TextField fellint;
    @FXML private TextField scaling;
    @FXML private Label thinning;
    @FXML private Label felling;
    @FXML private Label climate;
    @FXML private Label management;
    @FXML private Label currentYear;
    @FXML private Label baseYear;
    @FXML private Label yearsPerStep;
    @FXML private Label country;
    @FXML private TextField steps;
    @FXML private Text warning;
    @FXML private Text warning2;
    @FXML private TreeView selectionTree;
    @FXML private TextArea console;
    @FXML private Tab datapanel;
    @FXML private Tab selecteddatapanel;
    @FXML private GridPane scenariobox;
    @FXML private Button runSimulation;
    @FXML private Button openLogs;
    @FXML private MenuItem outputbtn;
    @FXML private MenuItem exitbtn;
    @FXML private ProgressIndicator working;
    private EfiscenModel model;
    private FileChooser filechooser;
    private int numSteps;
    private ROSSNames names;
    private Selections selections;
    private ConsolePrinter printer;
    private boolean scenario = false;
    private SelectionTreeController selController;
    private ResourceBundle rb;
    private Stage settingsWindow;
    private Stage outputWindow;
    private Stage logsWindow;
    private LogsController logsController;
    private Stage aboutWindow;
    private Settings settings;
    private OutputController outputController;
    private DataPanelController selecteddataController;
    
    /**
     * Initializes the controller class. Loads all the fxml files (dataPanel.fxml,
     * settings.fxml, output.fxml, logs.fxml, about.fxml) and sets
     * the controllers for them. 
     * @param url Not in use
     * @param rb The given resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb=rb;
        filechooser = new FileChooser();
        model = new EfiscenModel();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.efs, *.scn","*.efs","*.scn"));
        names = new ROSSNames(model);
        model.getLoadedProperty().addListener(names);
        model.getRunningProperty().addListener(this);
        printer = new ConsolePrinter(console);
        
        ScenarioBoxController scenarioBoxController = new ScenarioBoxController(
                model,thinning,felling,thinint,fellint,scaling,scenariobox);
        model.getLoadingProperty().addListener(scenarioBoxController);
        model.getRunningProperty().addListener(scenarioBoxController);
        
        selections = new Selections();
        selController = new SelectionTreeController(selections,names,selectionTree);
        names.getChangedProperty().addListener(selController);
        
        DataPanelController dataController  = new DataPanelController(model,null);
        model.getLoadedProperty().addListener(dataController);
        model.getRunningProperty().addListener(dataController);
        model.getLoadedProperty().addListener(this);
        model.getLoadingProperty().addListener(this);
        Pane pane = loadFXML("dataPanel.fxml",dataController,rb);
        datapanel.setContent(pane);
        
        selecteddataController  = new DataPanelController(model,selections);
        model.getLoadedProperty().addListener(selecteddataController);
        model.getRunningProperty().addListener(selecteddataController);
        selections.getChangedProperty().addListener(selecteddataController);
        pane = loadFXML("dataPanel.fxml",selecteddataController,rb);
        selecteddatapanel.setContent(pane);
        
        settingsWindow = new Stage();
        settings = new Settings();
        SettingsController settingsController = new SettingsController(settings, settingsWindow);
        pane = loadFXML("settings.fxml",settingsController,rb);
        settingsWindow.setResizable(false);
        settingsWindow.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(pane);
        settingsWindow.setScene(scene);
        settingsWindow.setTitle(rb.getString("key.settings"));
        
        outputWindow = new Stage();
        outputController = new OutputController(model,settings, outputWindow, working, runSimulation, scenariobox);
        pane = loadFXML("output.fxml",outputController,rb);
        outputWindow.setResizable(false);
        outputWindow.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(pane);
        outputWindow.setScene(scene);
        outputWindow.setTitle(rb.getString("key.output"));
        
        logsController  = new LogsController();
        pane = loadFXML("logs.fxml",logsController,rb);
        logsWindow = new Stage();
        logsWindow.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(pane);
        logsWindow.setScene(scene);
        logsWindow.sizeToScene();
        logsWindow.setTitle(rb.getString("key.logs"));
        
        pane = loadFXML("about.fxml",null,rb);
        //pane.getChildren().
        
        aboutWindow = new Stage();
        aboutWindow.initModality(Modality.APPLICATION_MODAL);
        scene = new Scene(pane);
        Label vlabel = (Label) scene.lookup("#versioninfo");
        String sversion = vlabel.getText();
        sversion = sversion+" "+Main.getRbToken("Application.revision")+" : ";
        sversion += rb.getString("key.build")+" "+ Main.getRbToken("Application.build_date");
        vlabel.setText(sversion);
        aboutWindow.setScene(scene);
        aboutWindow.setTitle(rb.getString("key.about"));
        aboutWindow.setResizable(false);
        
        aboutWindow.initModality(Modality.APPLICATION_MODAL);
        printer.start();
        
        exitbtn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });
    }
        
    /**
     * Loads FXML file and it's controller.
     * @param filename the given fxml file
     * @param controller the given controller for the FXML file
     * @param rb the given resource bundle
     * @return Panel containing elements from fxml file
     */
    private Pane loadFXML(String filename,Initializable controller,ResourceBundle rb) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(rb);
        if(controller!=null) fxmlLoader.setController(controller);
        Pane pane = null;
        try {
             pane = (Pane) fxmlLoader.load(this.getClass().getResource(filename).openStream());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println(rb.getString("key.error1"));
            //System.exit(-1);
        }
        return pane;
    }
    
   /**
    * Shows "About"-window
    * @param evt Event that triggers this method
    */
    public void showAboutWindow(ActionEvent evt) {
        aboutWindow.showAndWait();
    }
   
    /**
     * Sets disable selection tree to false
     * @param evt Event that triggers this method
     */
    public void selectedTab(Event evt) {
        if(selectionTree!=null) selectionTree.setDisable(false);
    }
    
    /**
     * Disables selection tree 
     * @param evt Event that triggers this method
     */
    public void totalTab(Event evt) {
        if(selectionTree!=null) selectionTree.setDisable(true);
    }
    
    /**
     * Shows "Settings"-window
     * @param evt Event that triggers this method
     */
    public void showSettings(ActionEvent evt) {
        settingsWindow.showAndWait();
    }
    
    /**
     * Shows "Output"-window and updates it
     * @param evt Event that triggers this method
     */
    public void showOutputDialog(ActionEvent evt) {
        outputController.update();
        outputWindow.showAndWait();
    }
    
    /**
     * Shows "Logs"-window and updates it
     */
    public void showLogWindow(){
        logsController.showLogs(model.getInputLoader());
        logsWindow.showAndWait();
    }
   
    /**
     * Loads thinning intensity value from thinint-textbox to GMEfiscen and
     * updates experiment by calling updatePotentialFellings().
     * @param evt Event that triggers this method
     */
    public void thinint(ActionEvent evt) {
        float thin = 1.f;
        try {
            thin = Float.parseFloat(thinint.getText());
        } catch(NumberFormatException ex) {
            warning2.setText("Thinning intensity must be a number");
            warning2.setVisible(true);
            return;
        }
        warning2.setVisible(false);
        model.getInputLoader().getM_pExperiment().m_ThinInt = thin;
        model.getInputLoader().getM_pExperiment().updatePotentialFellings();
    }
    
    /**
     * Reads thinning intensity, felling intensity and scaling value from textboxes
     * and loads them to model. Shows errors if there is something wrong with the values.
     */
    public void calculate() {
        if(model.getLoaded()){
            float scale = model.getInputLoader().getM_scaleAreas();
        try{
            scale = Float.parseFloat(scaling.getText().replace(",", "."));
            scaling.setText(Float.toString(scale));
        }catch(NumberFormatException e) {
            scaling.setText("1.0");
            warning2.setText(rb.getString("key.error2"));
            warning2.setVisible(true);
            return;
        }
        Float fell = 1.0f;
        Float thin = 1.0f;
        if (model.getEfiscen() != null && !scenario) {
            fell = model.getEfiscen().m_FelInt;
            thin = model.getEfiscen().m_ThinInt;
            float felling = 1f;
            try{
                felling = Float.parseFloat(fellint.getText().replace(",", "."));
                if(felling > 1.0f || felling < 0.0f) {
                    warning2.setText(rb.getString("key.error3"));
                    warning2.setVisible(true);
                    return;
                } else{
                    fell = felling;
                    warning2.setVisible(false);
                }
            }catch(NumberFormatException e) {
                fellint.setText("1.0");
                warning2.setText(rb.getString("key.error4"));
                warning2.setVisible(true);
                return;
            }
            float thinning = 1f;
            try{
                thinning = Float.parseFloat(thinint.getText().replace(",", "."));
                if(thinning > 1.0f || thinning < 0.0f) {
                    warning2.setText(rb.getString("key.error5"));
                    warning2.setVisible(true);
                    return;
                } else{
                    thin = thinning;
                    warning2.setVisible(false);
                }
            }catch(NumberFormatException e) {
                thinint.setText("1.0");
                warning2.setText(rb.getString("key.error6"));
                warning2.setVisible(true);
                return;
            }
        }
        if (scale != model.getInputLoader().getM_scaleAreas()) {
            model.getInputLoader().setM_scaleAreas(scale);
            System.out.println(rb.getString("key.scalingapplied")+" "+scale);
            try {
                model.reload();
            } catch (Exception ex) {
                System.out.println("reload after scaling"+ ex.getMessage());
            }
        }
        if (model.getEfiscen() != null && !scenario) {
            model.getEfiscen().m_FelInt = fell;
            model.getEfiscen().m_ThinInt = thin;
            model.reinit();
        } else if (scenario) {
            model.getEfiscen().m_FelInt = 1;
            model.getEfiscen().m_ThinInt = 1;
        }
       }
    }
    
    /**
     * Runs simulation with steps stated in steps-textbox. Shows errors if 
     * number of steps is lower than zero, over hundred or null.
     * @param evt Event that triggers this method
     */
    public void runSimulation(ActionEvent evt) {
        try {
            if(steps.getText()!= null) {
                numSteps = Integer.parseInt(steps.getText());
                if (numSteps > 0 && numSteps <= 100) {
                    warning.setVisible(false);
                    if(model.getLoaded()){
                        model.runSimulation(numSteps);
                    }
                }else{
                    warning.setText(rb.getString("key.error7"));
                    warning.setVisible(true);
                }
            } else {
                warning.setText(rb.getString("key.error7"));
                warning.setVisible(true);
            }
        }catch(NumberFormatException e) {
            steps.setText("5");
            warning.setText(rb.getString("key.error8"));
            warning.setVisible(true);
        }
    }

    /**
     * Shows file chooser and loads files. After user has chosen .efs and .scn
     * files loads experiment and scenario to model.
     * @param evt Event that triggers this method
     */
    public void loadFiles(ActionEvent evt) {
        String get = settings.getDefaultInputPath().get();
        if(get!=null) {
            File folder = new File(get);
            if(folder.exists()){
                filechooser.setInitialDirectory(folder);
            }
        }
        List<File> files = filechooser.showOpenMultipleDialog(null);
        if(files!=null && !files.isEmpty()) {
            String dataFile = null;
            String scenarioFile = null;
            Iterator<File> iterator = files.iterator();
            while(iterator.hasNext()) {
                File next = iterator.next();
                if( next.getName().endsWith(".efs")) {
                    dataFile = next.getAbsolutePath();
                }
                if( next.getName().endsWith(".scn")) {
                    scenarioFile = next.getAbsolutePath();
                }
            }
            String path = files.get(0).getPath();
            if(dataFile == null){
                System.err.println(rb.getString("key.error9"));
            }else{
                restoreFields();
                model.getInputLoader().setM_scaleAreas((float) 1.0);
                runSimulation.setDisable(true);
                scenariobox.setDisable(true);
                boolean scn =false;
                if(scenarioFile != null){
                    scn = true;
                }
                model.loadExperiment(dataFile,scn);
                //country.setText(model.getName());
                if(scenarioFile != null){
                    try { 
                        model.loadScenario(scenarioFile);
                        scenario = true;
                    } catch (Exception e) {
                        
                    }
                }else{
                    scenario = false;
                }
            }
            
        }
    }

    /**
     * Shows and hides elements depending on data loading or simulation running.
     * Sets elements that are disabled in the beginning to enabled state. 
     * Shows thinning intensity and felling intensity boxes if scenario is not 
     * loaded. Shows and hides working icon depending on model loading and 
     * simulation running.
     * @param ov Value that is observed if it has changed
     * @param t Old value
     * @param t1 New value
     */
    @Override
    public void changed(ObservableValue ov, Object t, Object t1) {
        if(ov == model.getLoadedProperty() && (Boolean)t1) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    //files loaded
                    country.setText(model.getEfiscen().m_sName);
                    currentYear.setText(""+model.getEfiscen().m_nBaseYear);
                    baseYear.setText(""+model.getEfiscen().m_nBaseYear);
                    yearsPerStep.setText(""+model.getEfiscen().m_nStep);
                    runSimulation.setDisable(false);
                    scenariobox.setDisable(false);
                    outputbtn.setDisable(false);
                    selections.setChanged(true);
                    openLogs.setDisable(false);
                    if(scenario){
                        thinint.setDisable(true);
                        thinint.setVisible(false);
                        fellint.setDisable(true);
                        fellint.setVisible(false);
                        climate.setVisible(true);
                        management.setVisible(true);
                        thinning.setText(model.getScenario().climName);
                        felling.setText(model.getScenario().manName);
                    }else{
                        climate.setVisible(false);
                        management.setVisible(false);
                        fellint.setDisable(false);
                        thinint.setDisable(false);
                        fellint.setVisible(true);
                        thinint.setVisible(true);
                    }
                }
            });
        }
         if(ov == model.getLoadingProperty()) {
            if((Boolean)t1) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        working.setVisible(true);
                        scenariobox.setDisable(true);
                        runSimulation.setDisable(true);
                    }
                });
            } else {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        working.setVisible(false);
                        scenariobox.setDisable(false);
                        runSimulation.setDisable(false);
                    }
                });
            }
        }
        if(ov == model.getRunningProperty()) {
            if(!(Boolean)t1) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        working.setVisible(false);
                        int year = Integer.parseInt(currentYear.getText())
                            +model.getSimulation().getM_pExperiment().m_nStep*numSteps;
                        currentYear.setText(""+year);
                        scenariobox.setDisable(false);
                        runSimulation.setDisable(false);
                    }
                });
            } else {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        working.setVisible(true);
                        scenariobox.setDisable(true);
                        runSimulation.setDisable(true);
                    }
                });
            }
       }
    }
    
    /**
     * Puts predetermined values to thinning, felling, scaling
     * and steps -fields.
     */
    public void restoreFields(){
        thinint.setText("1.0");
        fellint.setText("1.0");
        scaling.setText("1.0");
        steps.setText("5");
    }
    
}
