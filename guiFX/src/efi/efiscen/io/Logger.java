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
package efi.efiscen.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Writes error messages to log file. 
 * 
 */
public class Logger {
    
    private PrintWriter writer;
    private int errorsLogged = 0;
    private String filename;
    private String logFileName;
    private boolean init;
    
    /**
     * Parameterized constructor
     * @param logFileName the name of the log file
     */
    public Logger(String logFileName) {
        this.logFileName = logFileName;
        init = false;
    }
    
    /**
     * Parameterized constructor
     * @param logFileName the name of the log file
     * @param directory the directory of the file
     */
    public Logger(String logFileName, String directory) {
        File dir = new File(directory);
        if(!dir.exists()){
            dir.mkdir();
        }
        this.logFileName = directory + File.separator + logFileName;
        init = false;
    }
    
    /**
     * Creates the file for the logger.
     */
    private void createFile(){
        File file = new File(logFileName);
        int i=0;
        while(file.exists() && !file.canWrite()) {
            file = new File(logFileName.substring(0, logFileName.length()-4) + i + logFileName.substring(logFileName.length()-4, logFileName.length()));
            i++;
        }
        filename = file.getAbsolutePath();
        try {
            writer = new PrintWriter(new FileOutputStream(file, true));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR - log file not initialised");
        }
        init = true;
    }
    
    /**
     * Creates a new log entry and raises logged errors count to errorsLogged
     * attribute of the class.
     * @param type type of the error
     * @param description description of the error
     */
    public void logEntry(String type, String description) {
        if(!init){
            createFile();
        }
        String ln = System.getProperty("line.separator");
        if(writer!=null){
            writer.write(type + ": " + description + ln);
            writer.flush();
        }
        errorsLogged++;
    }
    
    /**
     * Creates a new log entry and raises logged errors count to errorsLogged
     * attribute of the class.
     * @param description description of the error
     */
    public void logEntry(String description) {
        if(!init){
            createFile();
        }
        String ln = System.getProperty("line.separator");
        if(writer!=null){
            writer.write(description + ln);
            writer.flush();
        }
        errorsLogged++;
    }
    
    /**
     * Creates a new log entry. Writes error type, description, filename and
     * line number of the error to a log file. Raises logged errors count to 
     * errorsLogged attribute of the class.
     * @param type type of the error
     * @param description description of the error
     * @param file the name of the input file
     * @param line line where the error is located in the file
     */
    public void logEntry(String type, String description, String file, int line) {
        if(!init){
            createFile();
        }
        String ln = System.getProperty("line.separator");
        if(line >= 0) {
            writer.write(type + ":_" + description + ": File " + file 
                + " line " + line);
        }
        else writer.write(type + ":_" + description + ": File " + file );
        writer.flush();
        errorsLogged++;
    }
    
    /**
     * Returns the number of errors logged in the log file
     * @return number of errors logged in the log file
     */
    public int getNumErrorsLogged() {
        return errorsLogged;
    }
    
    /**
     * Closes the log file
     */
    public void close() {
        if(init){
            writer.close();
        }
        init = false;
        errorsLogged = 0;
    }
    
    /**
     * Returns the name of the log file
     * @return the name of the log file 
     */
    public String getLogFileName(){
        return logFileName;
    }
}
