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
package efi.efiscen.db;

import efi.efiscen.com.ComArFlt;
import efi.efiscen.database.DatabaseComponentsException;
import efi.efiscen.database.DriverType;
import efi.efiscen.database.Reader;
import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMMatrix;
import efi.efiscen.gm.GMParLocator;
import efi.efiscen.gm.GMSoil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for parsing the data and saving it into the database.
 * Uses Connections.java to build a connection and Writer.java to saving 
 * data to database. 
 * 
 */
public class DatabaseExporter {
    
    private final GMEfiscen m_pExperiment;
    private final DatabaseWriter out;
    private final Reader reader;
    private final Map<Long,Integer> matrixIDs;
    
    /**
     * Creates a database saver pointing to a specified database.
     * @param server Address to a database where outputs will be saved. 
     * Addresses to external database usually resemble mysql.url.com/db_name. 
     * Database type is chosen by prefixing the address with the type name. 
     * @param driver Driver type enumeration
     * @param username Username used to log into the database specified in the address.
     * @param password Password used to log into the database specified in the address.
     * @param m_pExperiment Experiment to save into database.
     * @throws DBException when writing to database was unsuccessful
     * @throws DatabaseComponentsException 
     */

    public DatabaseExporter(String server, String database, int port, DriverType driver,
            String username,String password,GMEfiscen m_pExperiment) throws DBException, DatabaseComponentsException {
        this.m_pExperiment = m_pExperiment;
        out = new DatabaseWriter(server,database,port,username,password,driver);
        reader = new Reader(out.writer);
        matrixIDs = new HashMap<>();
    }
    
    /**
     * Saves all the simulation outputs into a database. Saves CarbonCoyntry, 
     * CarbonSoil, FellResidues, FellingMatrix, NatMort, ThinResidues, ThinnigMatrix
     * and DeadWood to the database. Also matrix ids are stored to matrix -table. Logs exceptions.
     * @param scenarioID Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param countryISO ISO code for country that this data represents.
     * @param projectID Project id that is saved to simulation -table.
     * @param parameterFilename
     * @return returns simulation id or -1 when saving is unsuccessful.
     * @throws java.sql.SQLException
     */
    public int save(int scenarioID,int countryISO, int projectID, 
            String parameterFilename) throws SQLException {
        //check that country with ciso-code exists in EFISCENCOUNTRIES
        ResultSet set = null;
        
        //save matrix ids to matrix table
        saveMatrices(countryISO);
        int simulationID = -1;
        try {
            simulationID = saveSimulation(scenarioID, countryISO, projectID, 
                    parameterFilename);
        } catch (DBException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
        if(simulationID == -1){
            System.err.println("Creating simulation ID failed");
            return -1;
        }else{
            try {
                /*String query = "SELECT ID FROM efiscencountries WHERE CISO="+ciso;
                Statement statement = out.getStatement();
                try {
                    statement.execute(query);
                    set = statement.getResultSet();
                    if(set.next())
                        sid = (int)set.getInt("ID");
                    else 
                        System.err.println("SID not found");
                } catch (SQLException ex) {
                    System.out.println("error querying country ID");
                    ex.printStackTrace();
                    return false;
                }*/
                saveBase(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveCarbonCountry(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveCarbonSoil(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveFellResidues(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveFellingMatrix(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveNatMort(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveThinResidues(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveThinningMatrix(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveTreeC(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
            try {
                saveDeadwood(simulationID);
            } catch (DBException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return simulationID;
    }
    
    /**
     * Saves base data values into a database.
     * Loads data from m_pExperiement, parses and saves it to the database.
     * FROM EXPORTMAIN
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public void saveBase(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float grs,garea,hrvthin,hrvfel,grincr,grsav,felav,dwood,nmort;
            float grsprev;
            // To get the increment
            float hrvthinres,hrvfelres,ccont,dens,cfactor;
            int ur,uo,ust,usp;

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                try {
                    pTable = m_pExperiment.m_mTables.get(uKey);
                    ur = pTable.getRegionID();
                    uo = pTable.getOwnerID();
                    ust = pTable.getSiteID();
                    usp = pTable.getSpeciesID();
                    //float val;
                    ComArFlt<Float> pComar,pComarDW,pComarNM;
                    ComArFlt<ArrayList<Float>> pPComar,pPStComar; // ptr
                    ComArFlt<ArrayList<Float>> pPComarFelRes,pPComarThRes,pPComarFelResRem,pPComarThResRem; // ptr
                    pComar = m_pExperiment.m_mafGrStock.get(uKey);
                    pComarDW = m_pExperiment.m_mafDeadWood.get(uKey);
                    pComarNM = m_pExperiment.m_mafNatMort.get(uKey);
                    pPComar = m_pExperiment.m_mafAreas.get(uKey);
                    pPStComar = m_pExperiment.m_mafStocks.get(uKey);
                    pPComarFelRes = m_pExperiment.m_mafFelRsd.get(uKey);
                    pPComarThRes = m_pExperiment.m_mafThRsd.get(uKey);
                    pPComarFelResRem = m_pExperiment.m_mafFelRsdRem.get(uKey);
                    pPComarThResRem = m_pExperiment.m_mafThRsdRem.get(uKey);

                    ccont = 0;
                    dens = 0;
                    ccont = m_pExperiment.m_plCcont.getParameterValue(uKey,0);
                    dens  = m_pExperiment.m_plWoodDens.getParameterValue(uKey,0);
                    if (ccont==0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont = 0.5f;

                    }
                    if (dens==0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        dens = 0.45f;
                    }
                    cfactor = ccont*dens;
                    int nHowMany = pComar.getSize();
                    grsprev = 0;
                    ArrayList<Float> ptemp;
                    for (int i=0;i<nHowMany;i++) {
                        //grs = pComar.getData(i);
                        hrvthin = 0;
                        hrvfel = 0;
                        hrvthinres = 0;
                        hrvfelres = 0;
                        grincr = 0;
                        grsav = 0;
                        felav = 0;
                        dwood = 0;
                        nmort = 0;
                        //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                        grs = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafGrStock,i);
                        garea = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafArea,i);
                        dwood = (Float)pComarDW.getData(i);
                        if (i>0) {
                            nmort = (Float)pComarNM.getData(i-1);
                            hrvthin = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafThinnings,i-1);
                            hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);
                            for (int ii = 0; ii < m_pExperiment.m_mafFellings.size(); ii++)
                                m_pExperiment.m_mafFellings.get(ii);
                            ptemp = pPComarThRes.getData(i-1);
                            hrvthinres = ((ArrayList<Float>)ptemp).get(0);
                            ptemp = pPComarFelRes.getData(i-1);
                            hrvfelres = ((ArrayList<Float>)ptemp).get(0);
                            // Adding topwood removals ...
                            ptemp = pPComarThResRem.getData(i-1);
                            hrvthinres += ((ArrayList<Float>)ptemp).get(0);
                            ptemp = pPComarFelResRem.getData(i-1);
                            hrvfelres += ((ArrayList<Float>)ptemp).get(0);

                            grincr = (grs - grsprev + hrvthin + hrvfel + nmort
                                    + hrvthinres/cfactor + hrvfelres/cfactor);

                        }
                        if (garea > 0) {
                            grsav = grs/garea;
                            felav = (hrvthin+hrvfel)/(garea*m_pExperiment.m_nStep);
                            grincr/=garea*m_pExperiment.m_nStep;
                        }
                        grsprev = grs;
                        //pdist = (ArrayList) pPComar.getData(i);
                        ptemp = pPComar.getData(i);
                        ArrayList a_0_150 = null;
                        ArrayList v_0_150 = null;
                        if (ptemp instanceof ArrayList) {
                            a_0_150 = (ArrayList)ptemp;
                        }
                        ptemp = pPStComar.getData(i);
                        if (ptemp instanceof ArrayList) {
                            v_0_150 = (ArrayList)ptemp;
                        }
                        out.Base(sid,matrixIDs.get(uKey), m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,
                                grs,garea, dwood, nmort,hrvthin,hrvfel,felav, 
                                grsav, grincr,a_0_150,v_0_150);
                        //out.println(pfm);
                    }
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }
        }
    }
    
    /**
     * Saves simulation information to the database table 'simulation'. 
     * @param scenarioID Scenario id for this simulation.
     * @param countryID Country id for this simulation.
     * @param projectID Project id for this simulation.
     * @param parameterFilename Name of the .prs-file for this simulation.
     * @return Unique id for this simulation.
     * @throws DBException
     */
    public int saveSimulation(int scenarioID, int countryID, 
            int projectID, String parameterFilename) throws DBException {
        synchronized(m_pExperiment) {
            int res = -1;
            try {
                res = out.simulation(scenarioID, countryID, projectID, parameterFilename);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
            return res;
        }
    }
    
    /**
     * Parses carbon country values from the simulation and saves into database.
     * FROM EXPORTGENSOIL.
     * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveCarbonCountry(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost,csoil;
            float ctot,cstem,cbr,clvs,ccroots,cfroots;
            int nHowMany = m_pExperiment.m_afSoilCwl.getSize();
            for (int i=0;i<nHowMany;i++) {
                cwl = m_pExperiment.m_afSoilCwl.getData(i);
                fwl = m_pExperiment.m_afSoilFwl.getData(i);
                nwl = m_pExperiment.m_afSoilNwl.getData(i);
                sol = m_pExperiment.m_afSoilSol.getData(i);
                cel = m_pExperiment.m_afSoilCel.getData(i);
                lig = m_pExperiment.m_afSoilLig.getData(i);
                hum1 = m_pExperiment.m_afSoilHm1.getData(i);
                hum2 = m_pExperiment.m_afSoilHm2.getData(i);
                csoil = cwl + fwl + nwl + sol + cel + lig + hum1 + hum2;
                clost = m_pExperiment.m_afSoilClost.getData(i);
                clost = clost/m_pExperiment.m_nStep;
                ctot	= m_pExperiment.m_afCarbon.getData(i);
                cstem	= (m_pExperiment.m_afStem.getData(i)).floatValue();
                clvs	= (m_pExperiment.m_afLeaves.getData(i)).floatValue();
                cbr     = (m_pExperiment.m_afBranches.getData(i)).floatValue();
                ccroots = (m_pExperiment.m_afCroots.getData(i)).floatValue();
                cfroots = (m_pExperiment.m_afFroots.getData(i)).floatValue();
                //out.printf("%d,%f,%f,%f,%f,%f,%f,%f,",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ctot,cstem,clvs,cbr,ccroots,cfroots,csoil);
                //out.printf("%f,%f,%f,%f,%f,%f,%f,%f,%f",cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost);
                //out.print(newLine);
                out.CarbonCountry(sid,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,
                        ctot,cstem,clvs,cbr,ccroots,cfroots,
                        csoil,cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost);
            }
        }
    }
    
    /**
     * Parses carbon soil data from the simulation and saves into database.
     * FROM EXPORTMAINSOIL.
     * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public void saveCarbonSoil(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            Float cwl = 0f,fwl = 0f,nwl = 0f,sol = 0f,cel = 0f,lig = 0f,hum1 = 0f,hum2 = 0f,
                    clost = 0f,litin = 0f,ctot = 0f,ctrees = 0f;
            Float  stprev = 0.f,cwlin = 0.f,fwlin = 0.f,nwlin = 0.f,cinout = 0.f;
            //int ur,uo,ust,usp;
            long lr,lo,lst,lsp;
            //out.println("S_ID,REG,OWN,ST,SP,Step,C_Trees,CWL,FWL,NWL,SOL,CEL,
            //LIG,HUM1,HUM2,C_Soil,COUT,LITIN,CWL_IN,FWL_IN,NWL_IN,C_BAL");

            GMSoil pSoil;
            for (Long uKey : m_pExperiment.m_mSoils.keySet())
            {
                pSoil = m_pExperiment.m_mSoils.get(uKey);
                lr = lo = lst = lsp = uKey;
                lr = lr>>24;
                lo = lo&0xFF0000;
                lo = lo>>16;
                lst = lst&0xFF00;
                lst = lst>>8;
                lsp = uKey&0xFF;

                long key = (lr << 24) + (lo << 16) + (lst << 8) + lsp;


                ComArFlt<Float> pcomCwl,pcomFwl,pcomNwl,pcomCel,pcomLig,pcomSol,
                        pcomHm1,pcomHm2,pcomClost;
                ComArFlt<Double> pcomCwlIn,pcomFwlIn,pcomNwlIn,pcomInOut;

                pcomCwl = m_pExperiment.m_mafSoilCwl.get(uKey);
                pcomFwl = m_pExperiment.m_mafSoilFwl.get(uKey);
                pcomNwl = m_pExperiment.m_mafSoilNwl.get(uKey);
                pcomCel = m_pExperiment.m_mafSoilCel.get(uKey);
                pcomLig = m_pExperiment.m_mafSoilLig.get(uKey);
                pcomSol = m_pExperiment.m_mafSoilSol.get(uKey);
                pcomHm1 = m_pExperiment.m_mafSoilHm1.get(uKey);
                pcomHm2 = m_pExperiment.m_mafSoilHm2.get(uKey);
                pcomClost = m_pExperiment.m_mafSoilClost.get(uKey);
                pcomCwlIn = m_pExperiment.m_mafSoilCwlIn.get(uKey);
                pcomFwlIn = m_pExperiment.m_mafSoilFwlIn.get(uKey);
                pcomNwlIn = m_pExperiment.m_mafSoilNwlIn.get(uKey);
                pcomInOut = m_pExperiment.m_mafSoilInOut.get(uKey);
                stprev = 0.f;
                if (pcomCwl == null)
                    continue;
                int nHowMany = pcomCwl.getSize();
                for (int i=0;i<nHowMany;i++) {
                    cwl = pcomCwl.getData(i);
                    fwl = pcomFwl.getData(i);
                    nwl = pcomNwl.getData(i);
                    cel = pcomCel.getData(i);
                    lig = pcomLig.getData(i);
                    sol = pcomSol.getData(i);
                    hum1 = pcomHm1.getData(i);
                    hum2 = pcomHm2.getData(i);             
                    clost = pcomClost.getData(i);
                    cwlin = pcomCwlIn.getData(i).floatValue();
                    fwlin = pcomFwlIn.getData(i).floatValue();
                    nwlin = pcomNwlIn.getData(i).floatValue();
                    cinout = pcomInOut.getData(i).floatValue();
                    clost = clost/m_pExperiment.m_nStep;

                    ctot = cwl + fwl + nwl + cel + lig + sol + hum1 + hum2;
                    ctrees = m_pExperiment.summarize(lr,lo,lst,lsp,m_pExperiment.m_mafBiomass,i);
                    litin = 0f;
                    if (i > 0) 
                        litin = ctot - stprev + m_pExperiment.m_nStep*clost;
                    // Rownding errors
                    if ((Float)litin < 0.001)
                        litin = 0.0f;
                    stprev = ctot;
                    //out.printf("%d,%d,%d,%d,%d,%d,%f,%f,%f,%f,",uKey,lr,lo,lst,lsp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ctrees,cwl,fwl,nwl);
                    //out.printf("%f,%f,%f,%f,%f,%f,",ctot,cstem,clvs,cbr,ccroots,cfroots);
                    //out.printf("%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f",sol,cel,lig,hum1,hum2,ctot,clost,litin,cwlin,fwlin,nwlin,cinout);
                    out.Carbonsoil(sid,uKey.intValue(),
                            m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,
                            ctrees,cwl,fwl,nwl,sol,cel,lig,hum1,hum2,ctot,clost,
                            litin,cwlin,fwlin,nwlin);
                }
            }
        }
    }
    
    /**
     * Parses dead wood data from the simulation and saves into database
     * FROM exportGenSoil
     * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public void saveDeadwood(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float cwl,fwl,nwl,sol,cel,lig,hum1,hum2,clost,csoil;
            float ctot,cstem,cbr,clvs,ccroots,cfroots;
            int nHowMany = m_pExperiment.m_afSoilCwl.getSize();
            for (int i=0;i<nHowMany;i++) {
                cwl = m_pExperiment.m_afSoilCwl.getData(i);
                fwl = m_pExperiment.m_afSoilFwl.getData(i);
                nwl = m_pExperiment.m_afSoilNwl.getData(i);
                sol = m_pExperiment.m_afSoilSol.getData(i);
                cel = m_pExperiment.m_afSoilCel.getData(i);
                lig = m_pExperiment.m_afSoilLig.getData(i);
                hum1 = m_pExperiment.m_afSoilHm1.getData(i);
                hum2 = m_pExperiment.m_afSoilHm2.getData(i);
                csoil = cwl + fwl + nwl + sol + cel + lig + hum1 + hum2;
                clost = m_pExperiment.m_afSoilClost.getData(i);
                clost = clost/m_pExperiment.m_nStep;
                ctot	= m_pExperiment.m_afCarbon.getData(i);
                cstem	= ((Double)m_pExperiment.m_afStem.getData(i)).floatValue();
                clvs	= ((Double)m_pExperiment.m_afLeaves.getData(i)).floatValue();
                cbr		= ((Double)m_pExperiment.m_afBranches.getData(i)).floatValue();
                ccroots = ((Double)m_pExperiment.m_afCroots.getData(i)).floatValue();
                cfroots = ((Double)m_pExperiment.m_afFroots.getData(i)).floatValue();
                out.Deadwood(sid,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,
                        ctot,cstem,clvs,cbr,ccroots,cfroots,csoil,cwl,fwl,nwl,sol,
                        cel,lig,hum1,hum2,clost);
            }
        }
    }
    /**
     * Parses felling residue data from the simulation and saves into a database.
     * FROM exportBeFelSlash
     * * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public void saveFellResidues(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            ArrayList<Float> pdist = new ArrayList<>();
            int ur,uo,ust,usp;
            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt pComar;
                ComArFlt pPComar,pPComRes,pPComResRem; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafBeFelSlash.get(uKey);
                pPComRes = m_pExperiment.m_mafFelRsd.get(uKey);
                pPComResRem = m_pExperiment.m_mafFelRsdRem.get(uKey);
                int nHowMany = pComar.getSize();
                //grsprev = 0;
                Object ptemp;
                for (int i=0;i<nHowMany;i++) {
                    if (i>0) {
                        ArrayList<Float> ar = null;
                        ArrayList<Float> vr = null;
                        ArrayList<Float> qr = null;
                        //out.printf("%d,%d,%d,%d,%d,%d",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);

                        ptemp = pPComRes.getData(i-1);
                        if (ptemp instanceof ArrayList) {
                            ar = (ArrayList)ptemp;
                        }

                        ptemp = pPComResRem.getData(i-1);
                        if (ptemp instanceof ArrayList) {
                            vr = (ArrayList)ptemp;
                        }

                        ptemp = pPComar.getData(i);
                        if (ptemp instanceof ArrayList) {
                            qr = (ArrayList)ptemp;
                        }
                        out.FellResidues(sid,matrixIDs.get(uKey),
                                m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ar.get(0),
                                ar.get(1),ar.get(2),ar.get(3),vr.get(0),vr.get(1),vr.get(2),
                                vr.get(3),qr);
                    }
                }
            }
        }
    }
    /**
     * Parses data and saves from felling to matrix
     * FROM EXPORTMEFIGFELREMS
     * Loads data from m_pExperiement, parses and saves it to matrix.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveFellingMatrix(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float hrvfel;
            float grsprev;
            ArrayList<Float> pdist;
            int ur,uo,ust,usp;

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPArComar;
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafMfqFelRems.get(uKey);
                pPArComar = m_pExperiment.m_mafMfqFelAreas.get(uKey);
                int nHowMany = pComar.getSize();
                grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    //grs = pComar.getData(i);
                    hrvfel = 0;
                    /*
                    hrvthin = 0;
                    hrvfel = 0;
                    grincr = 0;
                    //out.printf("%d",m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                    grs = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafGrStock,i);
                    garea = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafArea,i);
                    */
                    if (i>0) {
                        hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);

                        ArrayList ar = pPArComar.getData(i);
                        ArrayList vr = pPComar.getData(i);

                        //out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel);
                        out.FellingMatrix(sid,matrixIDs.get(uKey),m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel,ar,vr);
                    }
                }
            }
        }
    }
    /**
     * Saves natural mortality data to database.  
     * FROM exportNatMort
     * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveNatMort(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float grs,grsdw;
            float ccont,dens,cfactor;
            int ur,uo,ust,usp;

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                try {
                    pTable = m_pExperiment.m_mTables.get(uKey);
                    ur = pTable.getRegionID();
                    uo = pTable.getOwnerID();
                    ust = pTable.getSiteID();
                    usp = pTable.getSpeciesID();
                    //float val;
                    ComArFlt pComar,pComarDW;
                    ComArFlt pPComar,pPComarDW; // ptr
                    pComarDW = m_pExperiment.m_mafDeadWood.get(uKey);
                    pComar = m_pExperiment.m_mafNatMort.get(uKey);
                    pPComar = m_pExperiment.m_mafNatMortDistr.get(uKey);
                    pPComarDW = m_pExperiment.m_mafDeadWoodDistr.get(uKey);
                    // To have carbon in deadwood
                    ccont = 0;
                    dens = 0;
                    ccont = m_pExperiment.m_plCcont.getParameterValue(uKey,0);
                    dens  = m_pExperiment.m_plWoodDens.getParameterValue(uKey,0);
                    if (ccont==0.0) {
                        System.out.println("Debug:Carbon content = 0 during output!\nwill use 0.5");
                        ccont = 0.5f;
                    }
                    if (dens==0.0) {
                        System.out.println("Debug:Wood density = 0 during output!\nwill use 0.45");
                        ccont = 0.45f;
                    }
                    cfactor = ccont*dens;
                    int nHowMany = pComar.getSize() + 1; // Because we have one element more in distribution!
                    //grsprev = 0;
                    Object ptemp;
                    for (int i=0;i<nHowMany;i++) {
                        grs = 0;
                        grsdw = 0;
                        ArrayList ar = null;
                        ArrayList vr = null;
                        if (i < pComar.getSize()+1)
                            if (i>0)
                                grs = (Float)pComar.getData(i-1);
                        if (i < pComarDW.getSize()+1)
                            grsdw = (Float)pComarDW.getData(i);
                        //out.printf("%d,%d,%d,%d,%d,%d,%f,%f,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs,grsdw,grsdw*cfactor);

                        ptemp = pPComarDW.getData(i);
                        if (ptemp instanceof ArrayList) {
                            ar = (ArrayList)ptemp;
                        }

                        ptemp = pPComar.getData(i);
                        if (ptemp instanceof ArrayList) {
                            vr = (ArrayList)ptemp;
                        }

                        out.NatMort(sid,matrixIDs.get(uKey),
                                m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs,
                                grsdw,grsdw*cfactor,ar,vr);
                    }
                } catch (GMParLocator.GMParLocatorException ex) {
                    System.err.println(ex);
                }
            }
        }
    }
    
    /**
     * Saves thinning to matrix
     * FROM EXPORTMEFIQTHINREMS
     * Loads data from m_pExperiement, parses and saves it to a matrix.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveThinningMatrix(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            // Header first
            //float grs,garea,hrvthin,hrvfel,grincr,grsav,felav;
            float hrvfel;
            float grsprev;
            int ur,uo,ust,usp;

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                ur = pTable.getRegionID();
                uo = pTable.getOwnerID();
                ust = pTable.getSiteID();
                usp = pTable.getSpeciesID();
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPArComar; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafMfqThRems.get(uKey);
                pPArComar = m_pExperiment.m_mafMfqThAreas.get(uKey);

                int nHowMany = pComar.getSize();
                grsprev = 0;
                Object ptemp;
                for (int i=0;i<nHowMany;i++) {
                    //grs = pComar.getData(i);
                    hrvfel = 0;
                    if (i>0) {
                        hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafThinnings,i-1);

                       // out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel);

                        ArrayList<Float> ar = pPArComar.getData(i);
                        ArrayList<Float> vr = pPComar.getData(i);
                        out.ThinningMatrix(sid,matrixIDs.get(uKey),m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,hrvfel,ar,vr);
                    }
                }
            }
        }
    }
    
    /**
     * Parses thinning residue data from the simulation and saves into a database
     * FROM exportBeThinSlash
     * Loads thinning residue from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveThinResidues(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            ArrayList<Float> pdist = new ArrayList<>();
            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPComRes,pPComResRem; // ptr
                pComar = m_pExperiment.m_mafGrStock.get(uKey);
                pPComar = m_pExperiment.m_mafBeThSlash.get(uKey);
                pPComRes = m_pExperiment.m_mafThRsd.get(uKey);
                pPComResRem = m_pExperiment.m_mafThRsdRem.get(uKey);

                int nHowMany = pComar.getSize();
                //grsprev = 0;
                Object ptemp;
                for (int i=0;i<nHowMany;i++) {
                    if (i>0) {
                        //hrvfel = m_pExperiment.summarize(ur,uo,ust,usp,m_pExperiment.m_mafFellings,i-1);
                        //out.printf("%d,%d,%d,%d,%d,%d",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep);
                        ArrayList<Float> ar = null;
                        ArrayList<Float> vr = null;
                        ArrayList<Float> qr = null;
                        ptemp = pPComRes.getData(i-1);
                        if (ptemp instanceof ArrayList) {
                            ar = (ArrayList)ptemp;
                        }
                        ptemp = pPComResRem.getData(i-1);
                        if (ptemp instanceof ArrayList) {
                            vr = (ArrayList)ptemp;
                        }

                        ptemp = pPComar.getData(i);
                        if (ptemp instanceof ArrayList) {
                            qr = (ArrayList)ptemp;
                        }
                        out.ThinResidues(sid,matrixIDs.get(uKey),
                                m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,ar.get(0),
                                ar.get(1),ar.get(2),ar.get(3),vr.get(0),vr.get(1),vr.get(2),
                                vr.get(3),qr);
                    }
                }
            }
        }
    }
    /**
     * Parses TreeC data and saves into a database 
     * FROM exportMainCarbon
     * Loads data from m_pExperiement, parses and saves it to the database.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public synchronized void saveTreeC(int sid) throws DBException, SQLException {
        synchronized(m_pExperiment) {
            float grs; //,garea,hrvthin,hrvfel,grincr,grsav,felav;
            //float grsprev;

            GMMatrix pTable;
            for (Long uKey : m_pExperiment.m_mTables.keySet())
            {
                pTable = m_pExperiment.m_mTables.get(uKey);
                //float val;
                ComArFlt<Float> pComar;
                ComArFlt<ArrayList<Float>> pPComar,pPStComar,pPLvComar,pPCrComar,pPFrComar; // ptr
                pComar = m_pExperiment.m_mafBiomass.get(uKey);
                pPComar = m_pExperiment.m_mafCStem.get(uKey);
                pPStComar = m_pExperiment.m_mafCBranches.get(uKey);
                pPLvComar = m_pExperiment.m_mafCLeaves.get(uKey);
                pPCrComar = m_pExperiment.m_mafCCRoots.get(uKey);
                pPFrComar = m_pExperiment.m_mafCFRoots.get(uKey);
                int nHowMany = pComar.getSize();
                //grsprev = 0;
                for (int i=0;i<nHowMany;i++) {
                    grs = (Float)pComar.getData(i);

                    //out.printf("%d,%d,%d,%d,%d,%d,%f",uKey,ur,uo,ust,usp,m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,grs);
                    ArrayList<Float> c_st_0_150 = pPComar.getData(i);
                    ArrayList<Float> c_br_0_150 = pPStComar.getData(i);
                    ArrayList<Float> c_lv_0_150 = pPLvComar.getData(i);
                    ArrayList<Float> c_cr_0_150 = pPCrComar.getData(i);
                    ArrayList<Float> c_fr_0_150 = pPFrComar.getData(i);

                    out.TreeC(sid,matrixIDs.get(uKey),
                            m_pExperiment.m_nBaseYear+i*m_pExperiment.m_nStep,
                            grs,c_st_0_150, c_br_0_150, c_lv_0_150, c_cr_0_150, c_fr_0_150);
                }
            }
        }
    }
    
    /**
     * Saves matrix ids to database.
     * @param ciso
     * @throws SQLException 
     */
    public synchronized void saveMatrices(int ciso) throws SQLException{
        synchronized(m_pExperiment) {
            for (long uKey : m_pExperiment.m_mTables.keySet()){
                Integer matrixID = null;
                GMMatrix pTable = m_pExperiment.m_mTables.get(uKey);
                matrixID = checkMatrix(ciso, pTable);
                if(matrixID==null) {
                    int ur = pTable.getRegionID();
                    int uo = pTable.getOwnerID();
                    int usp = pTable.getSpeciesID();
                    int ust = pTable.getSiteID();
                    try{
                        matrixID = out.Matrix(ciso, ur, uo, usp, ust);
                    }catch(SQLException e){
                        //if duplicate exists, go on
                        int i = 0;
                    }
                }
                matrixIDs.put(pTable.m_wID, matrixID);
            }
        }
    }
    
    private Integer checkMatrix(int ciso, GMMatrix matrix) {
        synchronized(m_pExperiment) {
            Integer id = null;
            Set<Map<String,String>> results = null;
            Set<String> columns = new HashSet<>();
            columns.add("id");
            Map<String,String> where = new HashMap<>();
            where.put("country_id", ciso+"");
            where.put("region_id", matrix.getRegionID()+"");
            where.put("owner_id", matrix.getOwnerID()+"");
            where.put("site_id", matrix.getSiteID()+"");
            where.put("species_id", matrix.getSpeciesID()+"");
            try {
                results = reader.getVariables("matrix", columns, where);
            } catch (SQLException ex) {
                System.err.println("error when searching for matrix");
            }
            for(Map<String,String> result : results) {
                id = Integer.parseInt(result.get("id"));
            }
            return id;
        }
    }
    
    /**
     * Saves selected outputs to the database.
     *
     * @param sid Session id that will be present in all data entries saved into
     * a database. Can be used for example to identify data from certain run of
     * the EFISCEN tool.
     * @param ciso ISO country-code is used to identify that the output data
     * concerns a certain country.
     * @param projectID the value of projectID
     * @param selectedOutputs File path to a text file containing definitions
 about which outputs to save. The Path must include the file name and the
 name must end with “.txt”. The same selections are used for saving
 outputs to files and to a database. If this file is not used then all
 outputs are saved.
     * @param paramaterFilename the value of paramaterFilename
     * @throws java.sql.SQLException
     * @return the int
     */
    public int saveSelectedDatabase(int sid, int ciso, int projectID, String selectedOutputs, String paramaterFilename) throws SQLException {
        BufferedReader reader = null;
        //FileReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(selectedOutputs));
        } catch (FileNotFoundException ex) {
            System.err.println("Error reading output selection file");
            return -1;
        }
        String line;
        try {
            line = reader.readLine();
        } catch (IOException ex) {
            System.err.println("Error reading output selection file");
            return -1;
        }
        int simulationID = -1;
        if(line!=null){
            try {
                simulationID = saveSimulation(sid, ciso, projectID, paramaterFilename);
                saveMatrices(ciso);
            } catch (DBException ex) {
                System.err.println("error when saving simulation table");
            }
        }
        if(simulationID == -1){
            System.err.println("Creating simulation ID failed");
            return -1;
        }else{
            while (line != null) {
                String[] temp = line.split(" ");
                String id = temp[0];
                int value = Integer.parseInt(temp[1]);
                switch (id) {
                    case "base":
                        if (value == 1) {
                            try {
                                saveBase(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "felling_matrix":
                        if (value == 1) {
                            try {
                                saveFellingMatrix(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "thinning_matrix":
                        if (value == 1) {
                            try {
                                saveThinningMatrix(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "natmort":
                        if (value == 1) {
                            try {
                                saveNatMort(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "carbon_soil":
                        if (value == 1) {
                            try {
                                saveCarbonSoil(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "treec_matrix":
                        if (value == 1) {
                            try {
                                saveTreeC(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "carbon_country":
                        if (value == 1) {
                            try {
                                saveCarbonCountry(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "felling_residues":
                        if (value == 1) {
                            try {
                                saveFellResidues(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "thinning_residues":
                        if (value == 1) {
                            try {
                                saveThinResidues(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                    case "deadwood":
                        if (value == 1) {
                            try {
                                saveDeadwood(simulationID);
                            } catch (DBException ex) {
                                System.err.println(ex);
                            }
                            break;
                        }
                }
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    System.err.println("Error reading output selection file");
                    return -1;
                }
            }
        }
        return simulationID;
    }
}
