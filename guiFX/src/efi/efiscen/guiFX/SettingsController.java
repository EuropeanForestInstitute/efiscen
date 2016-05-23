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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controls the data of Settings class and Settings window of the
 * GUI. Loads and saves settings to a file named settings.txt.
 */
public class SettingsController implements Initializable {
    
    @FXML private TextField definputpath;
    @FXML private TextField defoutputpath;
    @FXML private TextField defusername;
    @FXML private TextField defpassword;
    @FXML private TextField defdbaddress;
    @FXML private TextField defdbname;
    @FXML private TextField defdbport;
    @FXML private Button savesettings;
    @FXML private ChoiceBox dbtype;
    private final String settingsfile = "settings.txt";
    private Settings settings;
    private ResourceBundle rb;
    private DirectoryChooser outputChooser;
    private DirectoryChooser inputChooser;
    private Stage thisWindow;
    
    
    /**
     * Parameterized constructor that sets Settings class for the controller
     * @param settings Settings object for the controller
     * @param thisWindow Stage where this window is located.
     */
    public SettingsController(Settings settings, Stage thisWindow) {
        this.settings = settings;
        this.thisWindow = thisWindow;
    }
    
    /**
     * Initializes the controller class. Loads settings.
     * @param url not in use
     * @param rb the given resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb=rb;
        loadSettigns();
        dbtype.getItems().addAll("ODBC","MySQL","PostgreSQL");
        inputChooser = new DirectoryChooser();
        inputChooser.setTitle(rb.getString("key.definputpath"));
        outputChooser = new DirectoryChooser();
        outputChooser.setTitle(rb.getString("key.defoutputpath"));
    }
    
    /**
     * Loads settings from the file settings.txt 
     */
    public void loadSettigns() {
        BufferedReader reader = null;
        File file = new File(settingsfile);
        if(!file.exists()) {
            System.err.println(rb.getString("key.error10"));
            return;
        }
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
        } catch (IOException ex) {
            System.err.println(rb.getString("key.error11"));
            return;
        }
        while(line!=null) {
            String[] temp = line.split("=");
            if(temp[0].equals("inputpath")) 
                if(temp.length > 1)
                settings.getDefaultInputPath().set(temp[1]);
                else settings.getDefaultInputPath().set(null);
            if(temp[0].equals("outputpath")) 
                if(temp.length > 1)
                settings.getDefaultOutputPath().set(temp[1]);
                else settings.getDefaultOutputPath().set(null);
            if(temp[0].equals("username")) 
                if(temp.length > 1)
                settings.getDefaultUsername().set(temp[1]);
                else settings.getDefaultUsername().set(null);
            if(temp[0].equals("password")) 
                if(temp.length > 1)
                settings.getDefaultPassword().set(temp[1]);
                else settings.getDefaultPassword().set(null);
            if(temp[0].equals("address")) 
                if(temp.length > 1)
                settings.getDefaultDBAddress().set(temp[1]);
                else settings.getDefaultDBAddress().set(null);
            if(temp[0].equals("dbname")) 
                if(temp.length > 1)
                settings.getDefaultDBName().set(temp[1]);
                else settings.getDefaultDBName().set(null);
            if(temp[0].equals("port")) 
                if(temp.length > 1)
                settings.getDefaultPort().set(temp[1]);
                else settings.getDefaultPort().set(null);
            if(temp[0].equals("type")) 
                if(temp.length > 1)
                settings.getDefaultDBType().set(temp[1]);
                else settings.getDefaultDBType().set(null);
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                break;
            }
        }
        if(settings.getDefaultInputPath().get()!=null) definputpath.setText(settings.getDefaultInputPath().get());
        if(settings.getDefaultOutputPath().get()!=null) defoutputpath.setText(settings.getDefaultOutputPath().get());
        if(settings.getDefaultUsername().get()!=null) defusername.setText(settings.getDefaultUsername().get());
        if(settings.getDefaultPassword().get()!=null) defpassword.setText(settings.getDefaultPassword().get());
        if(settings.getDefaultDBAddress().get()!=null) defdbaddress.setText(settings.getDefaultDBAddress().get());
        if(settings.getDefaultDBName().get()!=null) defdbname.setText(settings.getDefaultDBName().get());
        if(settings.getDefaultPort().get()!=null) defdbport.setText(settings.getDefaultPort().get());
        if(settings.getDefaultDBType().get()!=null) dbtype.setValue(settings.getDefaultDBType().get());
    }
    
    /**
     * Saves settings to the settings.txt file.
     * @param evt Event that triggered this method.
     */
    @FXML
    public void saveSettings(ActionEvent evt) {
        savesettings.setDisable(true);
        new Thread(new Runnable() {

            @Override
            public void run() {
                settings.getDefaultInputPath().set(definputpath.getText());
                settings.getDefaultOutputPath().set(defoutputpath.getText());
                settings.getDefaultUsername().set(defusername.getText());
                settings.getDefaultPassword().set(defpassword.getText());
                settings.getDefaultDBAddress().set(defdbaddress.getText());
                settings.getDefaultDBName().set(defdbname.getText());
                settings.getDefaultPort().set(defdbport.getText());
                if (dbtype.getSelectionModel().getSelectedItem()!=null)
                    settings.getDefaultDBType().set(dbtype.getSelectionModel().getSelectedItem().toString());
                File file = new File(settingsfile);
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file));
                    String inputpath = settings.getDefaultInputPath().get();
                    if(inputpath!=null && inputpath.length()!=0) {
                        writer.write("inputpath=" + inputpath);
                        writer.newLine();
                    }
                    String outputpath = settings.getDefaultOutputPath().get();
                    if(outputpath!=null && outputpath.length()!=0) {
                        writer.write("outputpath=" + outputpath);
                        writer.newLine();
                    }
                    String username = settings.getDefaultUsername().get();
                    if(username!=null && username.length()!=0) {
                        writer.write("username=" + username);
                        writer.newLine();
                    }
                    String password = settings.getDefaultPassword().get();   
                    if(password!=null && password.length()!=0) {
                        writer.write("password=" + password);
                        writer.newLine();
                    }
                    String dbaddress = settings.getDefaultDBAddress().get();
                    if(dbaddress!=null && dbaddress.length()!=0) {
                        writer.write("address=" + dbaddress);
                        writer.newLine();
                    }
                    String dbname = settings.getDefaultDBName().get();
                    if(dbname!=null && dbname.length()!=0) {
                        writer.write("dbname=" + dbname);
                        writer.newLine();
                    }
                    String dbport = settings.getDefaultPort().get();
                    if(dbport!=null && dbport.length()!=0) {
                        writer.write("port=" + dbport);
                        writer.newLine();
                    }
                    String dbtype = settings.getDefaultDBType().get();
                    if(dbtype!=null && dbtype.length()!=0) {
                        writer.write("type=" + dbtype);
                    }
                    writer.close();
                    
                } catch (IOException ex) {
                    System.err.println(rb.getString("key.error12"));
                } finally {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            savesettings.setDisable(false);
                        }
                        
                    });
                }
            }
            
        }).start();
        System.out.println("Settings saved to " + settingsfile);
        thisWindow.close();
    }
    
    /**
     * unused method
     */
    public void loadSettings() {
        
    }
    
    /**
     * Opens the window to choose an input file for settings. Sets the users selection
     * as default input path.
     */
    @FXML
    public void chooseInput(){
        if(definputpath.getText().length()>0) {
            File path = new File(definputpath.getText());
            if(path.isDirectory()){
                inputChooser.setInitialDirectory(path);
            }else{
                path = new File(settings.getDefaultInputPath().get());
                if(path.isDirectory())
                    inputChooser.setInitialDirectory(path);
            }
        }
        File file = inputChooser.showDialog(null);
        if(file!=null){
            definputpath.setText(file.getAbsolutePath());
        }
    }
    
    /**
     * Opens the window to choose an output file for settings. Sets the users selection
     * as default output path.
     */
    @FXML
    public void chooseOutput(){
        if(defoutputpath.getText().length()>0) {
            File path = new File(defoutputpath.getText());
            if(path.isDirectory()){
                outputChooser.setInitialDirectory(path);
            }else{
                path = new File(settings.getDefaultOutputPath().get());
                if(path.isDirectory())
                    outputChooser.setInitialDirectory(path);
            }
        }
        File file = outputChooser.showDialog(null);
        if(file!=null){
            defoutputpath.setText(file.getAbsolutePath());
        }
    }
}
