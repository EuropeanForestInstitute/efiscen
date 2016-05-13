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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * EFI
 */
public class FileComparer {
    
    public static void main(String[] args) {
        if(args.length!=2) System.out.println("to run the program:\n"
                +"java MainComparer <folder1> <folder2>");
        else {
            String filename1 = args[0];
            String sFMainC1 = filename1 + "_treeC_matr.csv";
            String sFGen1 = filename1 + "_gdat"+".csv";
            String sFGenSpec1 = filename1 + "_gspec"+".csv";
            String sFslGen1 = filename1 + "_carbon_country" + ".csv";
            String sFslMain1 = filename1 + "_carbon_soil" + ".csv";

            String filename2 = args[1];
            String sFMainC2 = filename2 + "_treeC_matr.csv";
            String sFGen2 = filename2 + "_gdat"+".csv";
            String sFGenSpec2 = filename2 + "_gspec"+".csv";
            String sFslGen2 = filename2 + "_carbon_country" + ".csv";
            String sFslMain2 = filename2 + "_carbon_soil" + ".csv";

            int totalLineMisses = 0;
            int totalValueMisses = 0;
            Logger log = new Logger("log.txt");
            CSVDifferenceWriter writer = null;
            try {
                writer = new CSVDifferenceWriter(new PrintWriter(new File("log.csv")));
            } catch (FileNotFoundException ex) {
                
            }
            CompareFiles comp = new CompareFiles(sFMainC1,sFMainC2,log,writer);
            comp.compare();
            totalLineMisses = comp.getNumLineErrors();
            totalValueMisses = comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(sFGen1,sFGen2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(sFGenSpec1,sFGenSpec2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(sFslGen1,sFslGen2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(sFslMain1,sFslMain2,log,writer);
            comp.compare();
            comp.close();
            
            String fell1 = filename1 + "_fell_matr"+".csv";       
            String fellRes1 = filename1 + "_fell_residues"+".csv";
            String thinRes1 = filename1 + "_thin_residues"+".csv";       
            String natMort1 = filename1 + "_natmort"+".csv";
            
            String fell2 = filename2 + "_fell_matr"+".csv";       
            String fellRes2 = filename2 + "_fell_residues"+".csv";
            String thinRes2 = filename2 + "_thin_residues"+".csv";       
            String natMort2 = filename2 + "_natmort"+".csv";
            
            comp = new CompareFiles(fell1,fell2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(fellRes1,fellRes2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(thinRes1,thinRes2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            comp = new CompareFiles(natMort1,natMort2,log,writer);
            comp.compare();
            totalLineMisses += comp.getNumLineErrors();
            totalValueMisses += comp.getNumValueErrors();
            comp.close();
            writer.writeHeader("min,max,average");
            writer.writeMinMaxAvg();
            log.logEntry("total line mismatches", ""+totalLineMisses);
            log.logEntry("total value mismatches", ""+totalValueMisses);
            log.close();
            writer.close();
        }
    }
}
