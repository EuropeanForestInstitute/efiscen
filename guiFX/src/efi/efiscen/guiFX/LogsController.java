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

import efi.efiscen.io.InputLoader;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * Controller class for the log window. Controls logs.fxml Label and TextArea
 * areas. Log window opens the log files and prints them to a TextArea.
 * 
 */
public class LogsController implements Initializable {
    
    @FXML private Label errorLabel;
    @FXML private Label eventLabel;
    @FXML private TextArea errorBox;
    @FXML private TextArea eventBox;
    
    private String errorLogName;
    private String eventLogName;
    private ResourceBundle rb;
    
    /**
     * Default constructor
     */
    public LogsController() {
        
    }
    
    /**
     * Initializer for LogsController
     * @param url not used
     * @param rb New ResourceBundle for LogsController
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb=rb;
    }
    
    /**
     * Shows names of the log files. Loads names of the logfiles to labels above
     * textboxes.
     * @param input InputLoader for log files.
     */
    public void showLogs(InputLoader input){
        errorLogName = input.getErrorLogName();
        eventLogName = input.getEventLogName();
        if(errorLogName==null){
            errorLabel.setText(rb.getString("key.error26"));
        }else{
            boolean fileExists = loadErrorLog(errorBox);
            if(fileExists){
                errorLabel.setText(errorLogName);
            }else{
                errorLabel.setText(rb.getString("key.error26"));
            }
            
        }
        if(eventLogName==null){
            eventLabel.setText(rb.getString("key.error27"));
        }else{
            boolean fileExists = loadEventLog(eventBox);
            if(fileExists){
                eventLabel.setText(eventLogName);
            }else{
                eventLabel.setText(rb.getString("key.error27"));
            }
        }
    }
    
    /**
     * Prints Error log to provided TextArea. Reports errors.
     * @param errorBox The area where the error log will be printed.
     * @return True if ErrorLog reading was successful, false otherwise
     */
       public boolean loadErrorLog(TextArea errorBox){
        try {
            String userfolder = System.getProperty("user.home");
            String separator = File.separator;
            String logpath = userfolder + separator + "EFISCEN" + separator + "logs" + separator;
            File file = new File(logpath + errorLogName);
            errorBox.clear();
            if(file.exists()){
                FileReader fr;
                try {
                    fr = new FileReader(file);
                } catch (FileNotFoundException fnf) {
                    System.err.println("Error log not found");
                    return false;
                }
                BufferedReader input = new BufferedReader(fr);
                for(String lineRead = input.readLine(); lineRead != null; lineRead = input.readLine()){
                    errorBox.appendText(lineRead + "\n");
                }
                return true;
            }else{
                errorBox.appendText("No errors");
                return false;
            }
        } catch (IOException ex) {
            System.err.println("Error reading errorlog");
            return false;
        }
    }
    
    /**
     * Prints eventLog to provided TextArea. Reports errors.
     * @param eventBox TextArea where event log will be printed.
     * @return True if eventLog reading was successful, false otherwise.
     */
    public boolean loadEventLog(TextArea eventBox){
        try {
            String userfolder = System.getProperty("user.home");
            String separator = File.separator;
            String logpath = userfolder + separator + "EFISCEN" + separator + "logs" + separator;
            File file = new File(logpath + eventLogName);
            eventBox.clear();
            if(file.exists()){
                FileReader fr;
                try {
                    fr = new FileReader(file);
                } catch (FileNotFoundException fnf) {
                    System.err.println("Event log not found");
                    return false;
                }
                BufferedReader input = new BufferedReader(fr);
                for(String lineRead = input.readLine(); lineRead != null; lineRead = input.readLine()){
                    eventBox.appendText(lineRead + "\n");
                }
                return true;
            }else{
                eventBox.appendText("No events");
                return false;
            }
        } catch (IOException ex) {
            System.err.println("Error reading eventlog");
            return false;
        }
    }
    
    /**
     * Opens log folder
     */
    public void openFolder(){
        String userfolder = System.getProperty("user.home");
        String separator = File.separator;
        String logpath = userfolder + separator + "EFISCEN" + separator + "logs" + separator;
        File file = new File (logpath);
        file.getAbsolutePath();
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException ex) {
            System.err.println(rb.getString("key.error28"));
        }
    }
    
}
