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

import java.io.*;
import java.util.logging.Level;

/**
 * Helper class for reading the input from files. Logs errors to a log file
 * with a Logger object. Can be used to read every line in a file or all the lines
 * that are given via constructor that don't begin with ^, #, . or * symbol. 
 * Has a method too for reading all the given lines via parameter that don't
 * begin with ^, #, . or * symbol.
 * 
 */
public class LineReader {

    private File file;
    private BufferedReader input;
    private FileReader fr;
    private int line = 0;
    private Logger logger;
    private final String lineReaderError = "Line reader error";
    private final String fileNotFound = "File not found";
    private final String prematureEOF = "End of file reached";

    /**
     * Default constructor.
     */
    public LineReader () {
    }
    
    /**
     * Parameterized constructor.
     * @param file the name of the file to be read
     * @param logger Logger object that writes error messages to a log file
     * @throws EFISCENFileNotFoundException Exception if the file was not found when creating 
     * a new file reader
     */
    public LineReader (File file,Logger logger) throws EFISCENFileNotFoundException{
        this();
        this.logger = logger;
        line = 0;
        if (file != null) {
            this.file = file;
            try {
                fr = new FileReader(file);
            } catch (FileNotFoundException fnf) {
                //fnf.printStackTrace();
                logger.logEntry(fileNotFound,"error",file.getName(),-1);
                throw new EFISCENFileNotFoundException(file.getName());
            }
            input = new BufferedReader(fr);
        }
    }

    /**
     * Simple line reader which returns null when file ends.
     * @return read line or null if the file doesn't have any more lines to be 
     * read
     */
    public String readLineSimple() {
        try {
            line+=1;
            String rVal = input.readLine();
            if(rVal!=null) rVal=rVal.trim();
            return rVal;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Reads a line from text file which is given via constructor. 
     * Ignores lines that begin with ^, #, . or * symbol.
     * in case of premature end of file reports error with PrematureEOF method. 
     * @return read line or null if there aren't anymore lines to be read
     */
    public String readLine () {
        String lineRead = null;
        try {
            lineRead = input.readLine();
            line+=1;
            if(lineRead == null) {
               reportPrematureEOF();
                return null;
            }
            while (lineRead.matches("^#.*")) {
                lineRead = input.readLine();
                line+=1;
                if(lineRead == null) {
                    reportPrematureEOF();
                    return null;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            reportLineReadeError();
            return null;
        }
        if(lineRead == null)
            reportPrematureEOF();
        if(lineRead!=null) lineRead=lineRead.trim();
        return lineRead.trim();
    }
    
    /**
     * Returns the number of lines this reader has read.
     * @return number of lines read by the reader
     */ 
    public Integer getLineNumber() {
        return line;
    }
    
    /**
     * Report premature end of file. Logs the error with Logger object.
     */
    private void reportPrematureEOF() {
        logger.logEntry(prematureEOF,"error in file " + file.getName() + 
                    ", line " + line );
    }
    
    /**
     * Report line reader error. Logs the error with Logger object.
     */
    private void reportLineReadeError() {
        logger.logEntry(lineReaderError,"error in file " + file.getName() + 
                    ", line " + line );
    }
    
    /**
     * Returns the name of the file.
     * @return the name of the file
     */
    public String getFileName() {
        return this.file.getName();
    }

    /**
     * Read a line from a text file and ignore lines that begin with ^, #, . or * symbol.
     * The file is given in a parameter. Sets the given file to the attribute of the class. 
     * @param file the file to be read
     * @return read line or null if unsuccessful cause of FileNotFoundException,
     * premature end of file or IOException.
     */
    public String readLine (File file) {
        String lineRead;
        this.file = file;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
            return null;
        }
        input =  new BufferedReader(fr);
        try {
            lineRead = input.readLine();
            if(lineRead == null)
                    reportPrematureEOF();
            line+=1;
            while ( lineRead.matches("^#.*")) {
                lineRead = input.readLine();
                if(lineRead == null) {
                    reportPrematureEOF();
                    return null;
                }
                line+=1;
        }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            reportLineReadeError();
            return null;
        }
        return lineRead;
    }
    
    /**
     * Closes the file reader. In case of IOException logs the error with
     * Logger object to a log file.
     */
    public void close() {
        try {
            fr.close();
        } catch (IOException ex) {
            logger.logEntry(lineReaderError,"error in file " + file.getName() + 
                ", line " + line );
        }
    }
}

