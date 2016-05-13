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

import efi.efiscen.cli.EfiscenCLI;
import java.io.File;
import junit.framework.TestCase;

/**
 * Runs EFISCEN with all data in the dataset and outputs to file.
 * EFI
 */
public class FileComparerTest extends TestCase {
    
    String[] asCNames = {"AUT_Austria", "BEL_Belgium", "BUL_Bulgaria",
    "CZE_Czech", "DEN_Denmark", "EST_Estonia",
    "FIN_Finland", "FRA_France", "GER_Germany",
    "HUN_Hungary", "IRE_Ireland", "ITA_Italy", 
    "LAT_Latvia", "LIT_Lithuania", "LUX_Luxembourg", 
    "NLA_Netherlands", "NOR_Norway", "POR_Portugal", 
    "POL_Poland", "ROM_Romania", 
    "SLO_Slovenia", "SLR_Slovakia", "SPA_Spain", 
    "SWE_Sweden", "SWI_Switzerland", "UKA_UKingdom", 
    "ALB_Albania", "CRO_Croatia", "BLR_Belarus", 
    "MOL_Moldova", "TUR_Turkey", "UKR_Ukraine", 
    "BIH_Bosnia", "CYP_Cyprus", "GRE_Greece", 
    "MKD_Macedonia", "MNE_Montenegro", "SRB_Serbia" 
    };
    
    public void testOne() {
        
        File f = new File("C:\\EFISCENdataset");
        assertTrue(f.isDirectory());
        for(File c : f.listFiles()) {
            File[] listFiles = c.listFiles();
            for(File expFile : listFiles) {
                if(expFile.getName().contains(".efs")) {
                    for(File scn : listFiles) {
                        if(scn.getName().contains(".scn")) {
                            String[] tmp = expFile.getName().split("\\.");
                            String out = c.getName();
                            String scnName = scn.getName().split("\\.")[0];
                            out += scnName.substring(scnName.indexOf("_"));
                            if(expFile.getName().contains("DW")) out+= "_DW";
                          /*  if(tmp.length>0) out += tmp[0];
                            tmp = scn.getName().split("\\.");
                            if(tmp.length>0) out += tmp[0];*/
                            String[] args = {"5","1.0","1.0",expFile.getAbsolutePath(),
                                "1.0",scn.getAbsolutePath(),"C:\\TestJavaNew\\"+out};
                            /*String cArgs = "C:\\Efiscen313j.exe 5 1 1 "+expFile.getAbsolutePath()+
                                " "+scn.getAbsolutePath()+" C:\\test\\cpp"+tmp.getName() + " 1";*/
                            try{
                                EfiscenCLI.main(args);
                               /* try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {}
                                runtime.exec(cArgs);*/
                            } catch(Exception ex) {
                                System.out.println(expFile.getName());
                                System.out.println(scn.getName());
                            }
                        }
                    }
                }
            }
        }/*
        File i1 = new File("C:\\test");
        File i2 = new File("C:\\test2");
        Logger log = new Logger("diff.log");
        for(File d : i1.listFiles()) {
            for(File d2 : i2.listFiles()) {
                if(d.getName().equals(d2.getName())) {
                    String[] args = { d.getAbsolutePath(), d2.getAbsolutePath() };
                    break;
                }
            }  
        }
                */
    }
}
