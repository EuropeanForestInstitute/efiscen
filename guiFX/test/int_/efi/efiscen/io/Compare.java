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
package int_.efi.efiscen.io;

import efi.efiscen.io.Logger;
import java.io.*;

class CSVDifferenceWriter {
    
    PrintWriter writer;
    int line=1;
    
    public CSVDifferenceWriter(PrintWriter writer) {
        this.writer = writer;
    }
    
    public void writeLine(String val1, String val2) {
        writer.append(val1 + "," + val2 + ",=ABS(A" + line 
                            + "-B" + line + ")\n");
        line++;
    }
    
    public void writeHeader(String header) {
        writer.append(header + "\n");
        line++;
    }
    
    public void writeMinMaxAvg() {
        int line = this.line - 2;
        writer.append("=min(C1:C" + line + "),=max(C1:C" + line 
                + "),=sum(C1:C" + line + ")/count(C1:C" + line + ")\n");
        line++;
    }
    
    public void close() {
        writer.close();
    }
}

/**
 *
 * EFI
 */
class CompareFiles {
        
    BufferedReader reader1;
    BufferedReader reader2;
    CSVDifferenceWriter writer;
    Logger logger;
    String filename1;
    String filename2;
    int numLineMismatches = 0;
    int numValueMismatches = 0;

    CompareFiles(String filename1, String filename2, Logger logger, CSVDifferenceWriter writer) {
        this.logger = logger;
        this.writer = writer;
        this.filename1 = filename1;
        this.filename2 = filename2;
        String[] temps = filename1.split("\\\\");
        writer.writeHeader(temps[temps.length-1]);
        try {
            reader1 = new BufferedReader(new FileReader(filename1));   
        } catch (FileNotFoundException ex) {
            logger.logEntry("file not found", filename1);
        }
        try {
            reader2 = new BufferedReader(new FileReader(filename2));
        } catch (FileNotFoundException ex) {
            logger.logEntry("file not found", filename2);
        }
    }
    
    public int getNumLineErrors() {
        return numLineMismatches;
    }
    
    public int getNumValueErrors() {
        return numValueMismatches;
    }

    public boolean compare() {
        boolean rValue = true;
        String line1 = "",  line2 = "";
        int line = 0;
        String[] temps = filename1.split("\\\\");
                String file1 = temps[temps.length-1];
                temps = filename2.split("\\\\");
                String file2 = temps[temps.length-1];
        logger.logEntry("started comparing files", "file1 " + file1 + 
                ", file2 " + file2);
        while(line1 != null) {
            if(!line1.equals(line2)) {
                logger.logEntry("line mismatch", "line " + line);
                rValue = false;
                numLineMismatches++;
                compareLines(line1,line2,logger,line);
            }
            try {
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                line++;
            } catch (IOException ex) {
                return false;
            }
        }
        logger.logEntry("num Line mismatches",""+numLineMismatches);
        logger.logEntry("num value mismatches",""+numValueMismatches);
        logger.logEntry("finished comparing files", "file1 " + file1 + 
                ", file2 " + file2);
        return rValue;
    }
    
    private void compareLines(String line1,String line2,Logger log,int line) {
        String[] vals1 = line1.split("\\,");
        String[] vals2 = line2.split("\\,");
        for(int i = 0; i < vals1.length;i++) {
            try {
                if(!vals1[i].equals(vals2[i])) {
                    log.logEntry("Value mismatch", "value1 " + vals1[i] + ", value2 "
                            + vals2[i] + " line " + line);
                    writer.writeLine(vals1[i], vals2[i]);
                }
            } catch(ArrayIndexOutOfBoundsException e) {}
            numValueMismatches++;
        }
        writer.writeLine("","");
    }

    public void close() {
        try {
            reader1.close();
        } catch (IOException ex) {

        }
        try {
            reader2.close();
        } catch (IOException ex) {}
    }
}