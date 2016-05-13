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

import efi.efiscen.database.DriverType;
import efi.efiscen.db.DBException;
import efi.efiscen.db.DatabaseExporter;
import efi.efiscen.io.FileSaver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controller for output.fxml. The window has two tabs. First one controls the choosing 
 * of the variables that can be saved to a file. The other tab has the database option 
 * to save the data to a database that requires the user to give database type, login 
 * info, ISO country code and session id.
 * 
 */
public class OutputController implements Initializable {
    
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField address;
    @FXML private TextField database;
    @FXML private TextField iso;
    @FXML private TextField sid;
    @FXML private TextField pid;
    @FXML private TextField port;
    @FXML private Button savedatabase;
    @FXML private ChoiceBox<String> type;
    @FXML private TreeView outputselection;
    @FXML private TreeView databaseTree;
    @FXML private TextField filename;
    @FXML private TextField filepath;
    @FXML private Button filesave;
    @FXML private Button choose;
    @FXML private Label warning;
    @FXML private Label warning2;
    @FXML private ProgressIndicator fileprogress;
    @FXML private ProgressIndicator dbprogress;
    private final EfiscenModel model;
    private final Settings settings;
    private ResourceBundle rb;
    private DirectoryChooser chooser;
    private final ExecutorService executor;
    private final Stage thisWindow;
    final private ProgressIndicator progress;
    final private Button runButton;
    final private GridPane scenarioBox;
    
    
    /**
     * Constructor for Output window.
     * @param model EfiscenModel that will be saved.
     * @param settings Loaded pathnames, usernames and passwords.
     * @param thisWindow
     * @param progress
     */
    public OutputController(EfiscenModel model,Settings settings, 
            Stage thisWindow, ProgressIndicator progress, Button runBtn,
            GridPane scenarioBox) {
        this.thisWindow = thisWindow;
        this.model = model;
        this.settings = settings;
        executor = Executors.newSingleThreadExecutor();
        this.progress = progress;
        this.runButton = runBtn;
        this.scenarioBox = scenarioBox;
    }
    
    /**
     * Initializer for OutputController. Call constructor before initializing.
     * Initializes elements and loads settings.
     * @param url not in use
     * @param rb the given resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb=rb;
        //type.getItems().addAll("ODBC","MySQL","PostgreSQL");
        for(DriverType driver : DriverType.values()) {
            type.getItems().add(driver.toString());
        }
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(rb.getString("key.alloutputs"));
        rootItem.setExpanded(true);
        outputselection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        outputselection.setCellFactory(CheckBoxTreeCell.<String>forTreeView());    
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.base")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.carboncountry")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.carbonsoil")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.fellingmatrix")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.fellingresidues")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.naturalmortality")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.thinningmatrix")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.thinningresidues")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.treecarbon")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.generalregions")));
        rootItem.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.generalspecies")));
        rootItem.selectedProperty().set(true);
        
        outputselection.setRoot(rootItem);
        CheckBoxTreeItem<String> rootItem2 = new CheckBoxTreeItem<>(rb.getString("key.alloutputs"));
        rootItem2.setExpanded(true);
        databaseTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        databaseTree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());    
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.base")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.carboncountry")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.carbonsoil")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.deadwood")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.fellingmatrix")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.fellingresidues")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.naturalmortality")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.thinningmatrix")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.thinningresidues")));
        rootItem2.getChildren().add(new CheckBoxTreeItem<>(rb.getString("key.treecarbon")));
        rootItem2.selectedProperty().set(true);
        
        databaseTree.setRoot(rootItem2);
        rootItem.selectedProperty().set(true);
        update();
        chooser = new DirectoryChooser();
        chooser.setTitle(rb.getString("key.outputpath"));
    }
    
    /**
     * Saves outputs to the chosen database type mySQL, odbc or postgreSQL.
     * @param evt Event that triggers this method
     */
    public void saveDatabase(ActionEvent evt) {
        //savedatabase.setDisable(true);
        final String user = username.getText();
        final String pass = password.getText();
        final String addr = address.getText();
        final String db = database.getText();
        final int prt = Integer.parseInt(port.getText());
        final String selectedItem = (String)type.getSelectionModel().getSelectedItem();
        int cisoTemp = 0;
        int nSidTemp = 0;
        int nPidTemp = 0;
        //check if parameters in fields are correct
        if(address.equals("")){
            warning.setText(rb.getString("key.error30"));
            warning.setVisible(true);
            return;
        }
        if(addr.length() > 200){
            warning.setText(rb.getString("key.error34"));
            warning.setVisible(true);
            return;
        }
        if(selectedItem.equals("")){
            warning.setText(rb.getString("key.error31"));
            warning.setVisible(true);
            return;
        }
        try {
            cisoTemp = Integer.parseInt(iso.getText());
        } catch(NumberFormatException ex) {
            warning.setText(rb.getString("key.error13"));
            warning.setVisible(true);
            return;
        }
        try {
            nSidTemp = Integer.parseInt(sid.getText());
        } catch(NumberFormatException ex) {
            warning.setText(rb.getString("key.error14"));
            warning.setVisible(true);
            return;
        }
        try {
            nPidTemp = Integer.parseInt(pid.getText());
        } catch(NumberFormatException ex) {
            warning.setText(rb.getString("key.error37"));
            warning.setVisible(true);
            return;
        }
        //start writing to db
        warning.setVisible(false);
        final int ciso = cisoTemp;
        final int nSid = nSidTemp;
        final int nPid = nPidTemp;
        DriverType driver = DriverType.MySql;
        if(selectedItem.equals("MySQL")){
            driver = DriverType.MySql;
        }else if(selectedItem.equals("Odbc")){
            driver = DriverType.Odbc;
        }else if(selectedItem.equals("PostgreSQL")) {
            driver = DriverType.PostgreSQL;
        }
        String pword = pass.replaceAll(" ", "");
        ObservableList<CheckBoxTreeItem> children = databaseTree.getRoot().getChildren();
        DatabaseExporter temp = null;
        try {
                temp = new DatabaseExporter(addr,db,prt,driver,user,pword,model.getEfiscen());
        } catch (DBException ex) {
            System.out.println(ex.method + ": " + ex.msg);
            System.err.println("saveDatabase: "+ rb.getString("key.error15"));
            return;
        } catch (efi.efiscen.database.DatabaseComponentsException ex) {
            System.out.println(ex.toString());
            System.err.println("saveDatabase: "+ rb.getString("key.error15"));
            return;
        }
        final DatabaseExporter saver = temp;

        //if(!children.isEmpty())
        //    saver.updateMatrixIds(ciso);
        final List<String> selected = new LinkedList<>();
        for(CheckBoxTreeItem item : children) {
            if(item.selectedProperty().get())
                selected.add(item.getValue().toString());
        }
        progress.setVisible(true);
        runButton.setDisable(true);
        scenarioBox.setDisable(true);
        Thread th = new Thread() {

            public void run() {
                try {
                    int simulationID = -1;
                    saver.saveMatrices(ciso);
                    try {
                        simulationID = saver.saveSimulation(nSid, ciso, nPid, 
                                model.getLoadedFiles().get("parameters"));
                    } catch (DBException ex) {
                        System.err.println("error when saving simulation table");
                    }
                    for(String item : selected) {
                        if(item.equals(rb.getString("key.base"))) {
                            try {
                                saver.saveBase(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error16"));
                            }
                        }
                        if(item.equals(rb.getString("key.carboncountry"))) {
                            try {
                                saver.saveCarbonCountry(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error17"));
                            }
                        }
                        if(item.equals(rb.getString("key.carbonsoil"))) {
                            try {
                                saver.saveCarbonSoil(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error18"));
                            }
                        }
                        if(item.equals(rb.getString("key.deadwood"))) {
                            try {
                                saver.saveDeadwood(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error19"));
                            }
                        }
                        if(item.equals(rb.getString("key.fellingmatrix"))) {
                            try {
                                saver.saveFellingMatrix(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error20"));
                            }
                        }
                        if(item.equals(rb.getString("key.fellingresidues"))) {
                            try {
                                saver.saveFellResidues(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error21"));
                            }
                        }
                        if(item.equals(rb.getString("key.naturalmortality"))) {
                            try {
                                saver.saveNatMort(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error22"));
                            }
                        }
                        if(item.equals(rb.getString("key.thinningmatrix"))) {
                            try {
                                saver.saveThinningMatrix(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error23"));
                            }
                        }
                        if(item.equals(rb.getString("key.thinningresidues"))) {
                            try {
                                saver.saveThinResidues(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error24"));
                            }
                        }

                        if(item.equals(rb.getString("key.treecarbon"))) {
                            try {
                                saver.saveTreeC(simulationID);
                            } catch (DBException ex) {
                                System.err.println(rb.getString("key.error24"));
                            }
                        }
                    }
                    System.out.println(rb.getString("key.dbsaved") + simulationID);
                } catch (SQLException ex) {
                   System.err.println(ex);
                }
                Platform.runLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        progress.setVisible(false);
                        runButton.setDisable(false);
                        scenarioBox.setDisable(false);
                    }
                });
            }
        };
        th.start();
        thisWindow.close();
    }
    
    /**
     * Saves files chosen in TreeView ouputselection. 
     * @param evt Event that triggers this method
     */
    public void saveFiles(ActionEvent evt) {
        disableAll();
        fileprogress.setVisible(true);
        final String prefix = filename.getText();
        final String path = filepath.getText();
        if(prefix==null || prefix.equals("")){
            warning2.setText(rb.getString("key.error32"));
            warning2.setVisible(true);
            return;
        }
        if(path==null || path.equals("")){
            warning2.setText(rb.getString("key.error33"));
            warning2.setVisible(true);
            return;
        }
        warning2.setVisible(false);
        ObservableList<CheckBoxTreeItem> children = outputselection.getRoot().getChildren();
        final List<String> selected = new LinkedList<>();
        for(CheckBoxTreeItem item : children) {
            if(item.isSelected())
                selected.add(item.getValue().toString());
        }
        runButton.setDisable(true);
        scenarioBox.setDisable(true);
        Thread th = new Thread() {

            @Override
            public void run() {
                for(String item : selected) {
                    if(item.equals("Base")) {
                        String file = path + File.separator + prefix + ".csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportMain(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.fellingmatrix"))) {
                        String file = path + File.separator + prefix + "_fell_matr.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportMefiqFelRems(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.fellingresidues"))) {
                        String file = path + File.separator + prefix + "_fell_residues.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportBeFelSlash(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.thinningmatrix"))) {
                        String file = path + File.separator + prefix + "_thin_matr.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportMefiqThinRems(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.thinningresidues"))) {
                        String file = path + File.separator + prefix + "_thin_residues.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportBeThinSlash(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.treecarbon"))) {
                        String file = path + File.separator + prefix + "_treeC_matr.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportMainCarbon(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.carboncountry"))) {
                        String file = path + File.separator + prefix + "_carbon_country.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportGenSoil(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }

                    if(item.equals(rb.getString("key.carbonsoil"))) {
                        String file = path + File.separator + prefix + "_carbon_soil.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportMainSoil(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    if(item.equals(rb.getString("key.naturalmortality"))) {
                        String file = path + File.separator + prefix + "_natmort.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportNatMort(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }

                    if(item.equals(rb.getString("key.generalspecies"))) {
                        String file = path + File.separator + prefix + "_gspec.csv";
                        FileSaver filesaver = new FileSaver(model.getEfiscen());
                        try {
                            filesaver.exportGeneralBySpec(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }

                    if(item.equals(rb.getString("key.generalregions"))) {
                        try {
                            String file = path + File.separator + prefix + "_gdat.csv";
                            FileSaver filesaver = new FileSaver(model.getEfiscen());
                            filesaver.exportGeneralByRegs(file);
                        } catch (IOException ex) {
                            System.err.println("error happened when saving to table "+item);
                        }
                    }
                    Platform.runLater(new Runnable() {
                    
                        @Override
                        public void run() {
                            progress.setVisible(false);
                            runButton.setDisable(false);
                            scenarioBox.setDisable(false);
                        }
                    });
                }
            }
        };
        progress.setVisible(true);
        th.start();
        System.out.println(rb.getString("key.filesaved"));
        thisWindow.close();
        fileprogress.setVisible(false);
        enableAll();
    }
    
    /**
     * Resets default texts for fields. Resets default texts for username, 
     * password, database, filepath, databasetype.
     */
    public void update(){
        String user = settings.getDefaultUsername().get();
        if(user != null && !user.equals("null"))
            username.setText(user);
        
        String pass = settings.getDefaultPassword().get();
        if(pass != null && !pass.equals("null"))
            password.setText(pass);
        
        String db = settings.getDefaultDBAddress().get();
        if(db != null && !db.equals("null"))
            database.setText(db);
        
        String port = settings.getDefaultPort().get();
        if(port != null && !port.equals("null")) {
            this.port.setText(port);
        }
        filepath.setText(settings.getDefaultOutputPath().get());
        type.setValue(settings.getDefaultDBType().get());
    }
    
    /**
     * Opens folder chooser for output path. Sets chosen file path to text field.
     */
    public void choosePath(){
        File path = new File(settings.getDefaultOutputPath().get());
        if(path.isDirectory())
            chooser.setInitialDirectory(path);
        File file = chooser.showDialog(thisWindow);
        if(file!=null){
            filepath.setText(file.getAbsolutePath());
        }
    }
    
    /**
     * Disables all Buttons, Fields and SelectionTrees in output window.
     */
    public void disableAll(){
        savedatabase.setDisable(true);
        type.setDisable(true);
        outputselection.setDisable(true);
        databaseTree.setDisable(true);
        filename.setDisable(true);
        filepath.setDisable(true);
        filesave.setDisable(true);
        username.setDisable(true);
        password.setDisable(true);
        database.setDisable(true);
        iso.setDisable(true);
        sid.setDisable(true);
        pid.setDisable(true);
        choose.setDisable(true);
    }
    
    /**
     * Enables all Buttons, Fields and SelectionTrees in output window.
     */
    public void enableAll(){
        savedatabase.setDisable(false);
        type.setDisable(false);
        outputselection.setDisable(false);
        databaseTree.setDisable(false);
        filename.setDisable(false);
        filepath.setDisable(false);
        filesave.setDisable(false);
        username.setDisable(false);
        password.setDisable(false);
        database.setDisable(false);
        iso.setDisable(false);
        sid.setDisable(false);
        choose.setDisable(false);
    }
}
