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

import efi.efiscen.com.ComArFlt;
import efi.efiscen.com.ComFltPipe;
import efi.efiscen.com.ComFltPipeElement;
import efi.efiscen.gm.GMCell;
import efi.efiscen.gm.GMCollection;
import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMEfiscenario;
import efi.efiscen.gm.GMGrFunDefault;
import efi.efiscen.gm.GMMatrix;
import efi.efiscen.gm.GMMatrixInit;
import efi.efiscen.gm.GMParArray;
import efi.efiscen.gm.GMParLocator;
import efi.efiscen.gm.GMScenario;
import efi.efiscen.gm.GMSimulation;
import efi.efiscen.gm.GMSoil;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Loads the input for the program from files. First, experiment is loaded by calling
 * loadExperiment(). This initializes an GMEfiscen collection for current experiment.
 * Loading a scenario with LoadScenario() is required for some methods.
 * 
 */
public class InputLoader {

    private GMScenario m_Scenario; // scenario collection
    private GMEfiscen m_pExperiment; //experiment collection
    private GMGrFunDefault m_pGrFun;
    private GMMatrix m_pCurMatrix;
    //private LineReader reader;
    private int m_nMatrNum;
    private float m_scaleAreas;
    //private boolean m_bIsInteractive;
    private Logger errorLogger;
    private Logger eventLogger;
    private TreeSet<Long> matrixIDs = new TreeSet<>();
    private final String errorFileParsing = "File parsing error";
    private final String fileError = "File error";
    private final String eventOptionalFileMissing = "Optional file is missing";
    private final String eventFileLoadStart = "File reading started";
    private final String eventFileLoadEnd = "File reading finished";
    private final String eventIncorrectData = "Incorrect data";
    private String errorLogName;
    private String eventLogName;
    private String path;
    private String volClassFile = "";
    private String bioparametersFile = "";

    /**
     * Default constructor.
     */
    public InputLoader() {
        m_Scenario = new GMScenario();
        m_pExperiment = new GMEfiscen(); //collection for experiment data 
        m_pGrFun = new GMGrFunDefault();
        m_pCurMatrix = new GMMatrix();
    }

    /**
     * Loads an experiment from given file and initializes experiment collection. 
     * Parses information about experiment from experiment file and loads parameters,
     * bioparameters, matrixes, soils from individual files and adds 
     * data to a new GMEfiscen collection. Keeps track of number of errors
     * occured during the reading and adds them to counter given in parameters.
     * @param sFileIn File path to an experiment file. File name must be included.
     * @param numErrors Counter for number of errors. Adds number of errors to counter.
     * @param filenames
     * @return If reading was successful, returns a new GMEfiscen object with data 
     * read from sFileIn, otherwise null.
     * @throws EFISCENFileNotFoundException if input file was not found
     * @throws EFISCENException  EFISCENFileParsingException if file reading was unsuccessful
     */
    public GMEfiscen loadExperiment(String sFileIn, AtomicInteger numErrors, 
            Map<String,String> filenames) throws EFISCENFileNotFoundException, EFISCENException {
        String temp = sFileIn.substring(0, sFileIn.length() - 3);
        File f = new File(sFileIn);
        path = f.getAbsolutePath();
        int index  = temp.lastIndexOf(File.separator);
        String name = temp.substring(index+1);
        path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator;
        String id = name + System.currentTimeMillis();
        errorLogName = id + "errors.txt";
        eventLogName = id + "events.txt";
        System.out.println("Debug:id:"+id);
        String userfolder = System.getProperty("user.home");
        String separator = File.separator;
        String logpath = userfolder + separator + "EFISCEN" + separator;
        File dir = new File(logpath);
        if(!dir.exists()){
            dir.mkdir();
        }
        errorLogger = new Logger(errorLogName,logpath+"logs");
        eventLogger = new Logger(eventLogName,logpath+"logs");
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        if(filenames!=null) filenames.put("experiment", sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        try {
            String strLine = reader.readLine();
            if (!strLine.substring(0, 3).equals("EFI")) {
                String error = "Wrong experiment file";
                errorLogger.logEntry(fileError, error);
                System.err.println(error);
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            // Name of Experiment reading
            strLine = reader.readLine();
            m_pExperiment = new GMEfiscen(strLine, 0);

            // Now Base year
            strLine = reader.readLine();
            int nbe = 0;
            try {
                nbe = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error",
                        reader.getFileName(), reader.getLineNumber());
            }
            m_pExperiment.m_nBaseYear = nbe;

            // Now Regions
            strLine = reader.readLine();
            int nHm = 0;
            int nid = 0;
            long lid = 0;
            String sname;
            String[] split;
            try {
                //num regions
                nHm = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());
            }
            for (int i = 0; i < nHm; i++) {
                strLine = reader.readLine();
                split = strLine.split("\\s+");
                if (split == null || split.length < 2) {
                    break;
                }
                TreeSet<Integer> processedNumbers = new TreeSet<>();
                try {
                    // num
                    nid = NumberParser.convertInt(split[0], reader, errorLogger);
                    if (!processedNumbers.contains(nid)) {
                        processedNumbers.add(nid);
                    } else {
                        errorLogger.logEntry(this.errorFileParsing, "region number"
                                + " encountered twice.");
                    }
                    // id
                    if(split.length==3)
                        lid = NumberParser.convertInt(split[1], reader, errorLogger);
                    else lid = 0;
                } catch (NumberFormatException nfe) {
                    errorLogger.logEntry(this.errorFileParsing, "number of region"
                            + " definitions is lower than defined in the file.");

                    break;
                }
                if(split.length==3) sname = split[2];
                else sname = split[1];
                GMCollection pReg = new GMCollection(sname, nid); // region
                pReg.m_lISOID = lid;
                m_pExperiment.addCollection(m_pExperiment.getRegions(), pReg);
            }

            // Now Owners
            if (!addParsedCollection(reader, m_pExperiment.getOwners())) {
                errorLogger.logEntry(errorFileParsing, "Could not parse owners!");
            }

            // Now Sites
            if (!addParsedCollection(reader, m_pExperiment.getSites())) {
                errorLogger.logEntry(errorFileParsing, "Could not parse sites!");
            }

            // Now Species
            if (!addParsedCollection(reader, m_pExperiment.getSpecies())) {
                errorLogger.logEntry(errorFileParsing, "Could not parse species!");
            }
            for (long region = 0; region < m_pExperiment.m_mRegions.size(); region++) {
                for (long owner = 0; owner < m_pExperiment.m_mOwners.size(); owner++) {
                    for (long site = 0; site < m_pExperiment.m_mSites.size(); site++) {
                        for (long species = 0; species < m_pExperiment.m_mSpecies.size(); species++) {
                            long key = (region << 24) + (owner << 16) + (site << 8) + species;
                            matrixIDs.add(key);
                        }
                    }
                }
            }
            int nRegions = (int) m_pExperiment.m_mRegions.size();
            int nOwners = (int) m_pExperiment.m_mOwners.size();
            int nSites = (int) m_pExperiment.m_mSites.size();
            int nSpecies = (int) m_pExperiment.m_mSpecies.size();
            int numMatricesExpected = nRegions*nOwners*nSites*nSpecies;
            
            String sParName = reader.readLine();
            if (!loadParameters(path + sParName)) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            if(filenames!=null) filenames.put("parameters", sParName);
            String sBioName = reader.readLine();
            bioparametersFile = sBioName;
            if (!loadBioParameters(path + sBioName)) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            if(filenames!=null) filenames.put("bioparameters", sBioName);
            String sDataName = reader.readLine();
            if (loadData(path, path + sDataName, numMatricesExpected) == null) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            if(filenames!=null) filenames.put("matrixdata", sDataName);
            String sSoilName = reader.readLine();
            if (!loadSoils(path + sSoilName)) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
/*
            String sExtraName = reader.readLine();
            if (!sExtraName.equals("nofile") && sExtraName != null)
                if (!loadExtraInformation(path + sExtraName)) {
                    throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
                }*/
            eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
            //m_pExperiment.initSoilStocks();
            //fillGridLegend(m_pCurMatrix);
            //fillGrid(m_pCurMatrix);
            m_pExperiment.updateHistory();
            // TODO: after this enable scenario and save buttons
            //m_btnScen.EnableWindow(TRUE);
            //m_btnSave.EnableWindow(TRUE);
            //m_Scenario.getHeads();
            MatrixParameterReporter mParamReporter =
                    new MatrixParameterReporter();
            mParamReporter.setLogger(errorLogger);
            if(!mParamReporter.checkParameters(m_pExperiment))
                System.err.println("Parameter missing (see errorlog)");
            if(!mParamReporter.checkMatrices(m_pExperiment))
                System.err.println("Matrix contains undefined identifiers (see errorlog)");
            reader.close();
            numErrors.set(errorLogger.getNumErrorsLogged());
            eventLogger.close();
            errorLogger.close();
            return m_pExperiment;
        } catch (GMParLocator.GMParLocatorException ex) {
            System.err.println(ex);
        }
        return null;
    }

    /**
     * Parses a collection from file and adds it to the map of collections. Sends
     * errors to eventLogger.
     * @param reader linereader which points to the experiment file where data
     * is read.
     * @param colMap collection map in which the parsed collection is added
     * @return true if successful else false
     */
    private boolean addParsedCollection(LineReader reader, java.util.HashMap colMap) {
        String strLine, sname;
        int nHm = 0, nid;
        String[] split;
        strLine = reader.readLine();
        try {
            nHm = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {

            return false;
        }
        int numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 2) {
                return false;
            }
            try {
                // id
                nid = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {

                return false;
            }
            // data
            sname = split[1];
            GMCollection pOwn = new GMCollection(sname, nid); // owners
            m_pExperiment.addCollection(colMap, pOwn);
            numExtracted++;
        }
        if (numExtracted != nHm) {
            eventLogger.logEntry(eventIncorrectData, "number of extracted"
                    + " entries differs from expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        return true;
    }

    /**
     * Loads a scenario from given file. Reads scenario file paths from given file,
     * reads forest grow, climate, cuttings, removals, afforestation, deforestation
     * scenarios and species change from individual files and adds data to a new GMScenario 
     * collection. Keeps track of number of errors
     * occured during the reading and adds them to counter given in parameters.
     * @param sFileIn File path to a scenario file.
     * @param numErrors Counter for number of errors. Adds number of errors to counter.
     * @return If reading was successful, returns a new GMScenario object with data 
     * read from sFileIn, otherwise null.
     * @throws EFISCENFileNotFoundException If some scenario file is not found.
     */
    public GMScenario loadScenario(String sFileIn,AtomicInteger numErrors) throws EFISCENFileNotFoundException{
        m_Scenario = new GMScenario();
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        String strLine;
        ArrayList<Float> vals = null;

        File f = new File(sFileIn);
        String path = f.getAbsolutePath();
        path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator;

        // Name of Scenario reading
        strLine = reader.readLine();
        m_Scenario.m_sName = strLine;

        String sInFileName;
        // Forest grow scenario
        sInFileName = reader.readLine();
        if (!loadForClimV4(path + sInFileName)) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(1,"m_plForClim");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(1.0f);
            //vals.add(vals);
            pSc.getEs_paData().addParameter(pPar);

            m_Scenario.m_plForClim.add(pSc);
        }

        // Soil Climate scenario
        sInFileName = reader.readLine();
        if (!loadSoilClim(path + sInFileName)) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(2,"m_plSoilClim");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(4.0f);
            vals.add(-50.0f);
           // vals.add(vals);
            pSc.getEs_paData().addParameter(pPar);

            m_Scenario.m_plSoilClim.add(pSc);
        }

        // Fellings, thinnings
        sInFileName = reader.readLine();
        if (!loadBusiness(path + sInFileName)) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(2,"m_plCuttings");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(0, -1.0f);
            vals.add(1, -1.0f);
           // vals.add(vals);
            pSc.getEs_paData().addParameter(pPar);
            m_Scenario.m_plCuttings.add(pSc);
            // Adding ratio values
            pPar = new GMParArray(2,"m_plCutRatios");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(1.0f);
            vals.add(1.0f);
          //  vals.add(vals);
            m_Scenario.m_plCutRatios.addParameter(pPar);

        }

        // Fellings, thinnings properties version Hans
        sInFileName = reader.readLine();
        if (!loadFelPropsEx(path + sInFileName)) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(12,"m_plCutProps");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(0.95f); //fellings - stem removals
            vals.add(0.0f);	//fellings - tops
            vals.add(0.0f);	//fellings - branches removals
            vals.add(0.0f);	//fellings - leaves removals
            vals.add(0.0f);	//fellings - deadwood
            vals.add(0.9f);	//thinnings - stem removals
            vals.add(0.0f);	//thinnings - tops
            vals.add(0.0f);	//thinnings - branches removals
            vals.add(0.0f);	//thinnings - leaves removals
            vals.add(0.0f);	//thinnings - deadwood
            vals.add(0.0f);	//fellings - coarse roots removals (Uppsala)
            vals.add(0.0f);	//thinnings - coarse roots ()
            pSc.getEs_paData().addParameter(pPar);

            m_Scenario.m_plCutProps.add(pSc);
        }

        // Aforestation
        sInFileName = reader.readLine();
            if (!loadAforestation(path + sInFileName)) {
                GMEfiscenario pSc = new GMEfiscenario();
                pSc.setEs_nStep(1001);
                GMParArray pPar = new GMParArray(1,"m_plAfor");
                pPar.setM_uRegion(0);
                pPar.setM_uOwner(0);
                pPar.setM_uSite(0);
                pPar.setM_uSpecies(0);
                vals = pPar.getM_Vals();
                vals.add(0.0f);
                vals.add(0.0f);
                pSc.getEs_paData().addParameter(pPar);
                
                m_Scenario.m_plAfor.add(pSc);
                
            }
        // Deforestation
        sInFileName = reader.readLine();
        if (!loadDeforestation(path + sInFileName)) {
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(1,"m_plDefor");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(0.0f);
            pSc.getEs_paData().addParameter(pPar);

            m_Scenario.m_plDefor.add(pSc);

        }
        // Species change
        sInFileName = reader.readLine();
        if (sInFileName != null && !sInFileName.equals("nofile")) {
            loadSpecChange(path + sInFileName);
        } else {
            eventLogger.logEntry(eventOptionalFileMissing, "species change");
            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(1001);
            GMParArray pPar = new GMParArray(1,"m_plSpecCh");
            pPar.setM_uRegion(0);
            pPar.setM_uOwner(0);
            pPar.setM_uSite(0);
            pPar.setM_uSpecies(0);
            vals = pPar.getM_Vals();
            vals.add(0.0f);
            pSc.getEs_paData().addParameter(pPar);

            m_Scenario.m_plSpecCh.add(pSc);
        }
        // individual matrix scaling 
        sInFileName = reader.readLine();
        if (sInFileName!=null && !sInFileName.trim().equals("nofile")) {
            loadScalingFactors(path + sInFileName);
        } else {
            eventLogger.logEntry(eventOptionalFileMissing, "matrix scaling");
        }
        sInFileName = reader.readLine();
        if (sInFileName!=null && !sInFileName.trim().equals("nofile")) {
            loadThinningChange(path + sInFileName);
        } else {
            eventLogger.logEntry(eventOptionalFileMissing, "thinning changes");
        }
        sInFileName = reader.readLine();
        if (sInFileName!=null && !sInFileName.trim().equals("nofile")) {
            loadFellingChange(path + sInFileName);
        } else {
            eventLogger.logEntry(eventOptionalFileMissing, "felling changes");
        }
        m_Scenario.getHeads();
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        eventLogger.close();
        numErrors.set(errorLogger.getNumErrorsLogged());
        errorLogger.close();
        return m_Scenario;
    }

    /**
     * Loads scaling factor for selected matrices. Experiment collection must be initialized.
     * @param filename input filepath
     * @throws EFISCENFileNotFoundException if file not found
     */
    private void loadScalingFactors(String filename) throws EFISCENFileNotFoundException {
        try {
            LineReader reader = new LineReader(new File(filename), errorLogger);
            eventLogger.logEntry(eventFileLoadStart, "filename " + filename);
            long cr, co, cst, csp, nHm;
            String strLine, split[];

            //header two lines, ignored
            strLine = reader.readLine();
            strLine = reader.readLine();
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            //number of scaling definitions in the file
            try {
                nHm = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "filename "
                        + reader.getFileName() + " line " + reader.getLineNumber());
                return;
            }
            for (int i = 0; i < nHm; i++) {
                strLine = reader.readLine();
                //split = strLine.split("\\s+");
                ArrayList<Integer> pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4, reader,
                        errorLogger);
                cr = pIDs.get(0);
                co = pIDs.get(1);
                cst = pIDs.get(2);
                csp = pIDs.get(3);
                strLine = reader.readLine();
                float scaling = 1f;
                try {
                    scaling = NumberParser.convertFloat(strLine, reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    errorLogger.logEntry(errorFileParsing, "error on line "
                            + reader.getLineNumber() + " in file " + reader.getFileName());
                }
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                for (long r : m_pExperiment.m_mRegions.keySet()) {
                    for (long o : m_pExperiment.m_mOwners.keySet()) {
                        for (long st : m_pExperiment.m_mSites.keySet()) {
                            for (long sp : m_pExperiment.m_mSpecies.keySet()) {
                                long cKey = (r << 24) + (o << 16) + (st << 8) + sp;
                                long uKey = cKey;
                                if (cr == 0)  cKey = cKey & ~0xFF000000;
                                if (co == 0)  cKey = cKey & ~0xFF0000;
                                if (cst == 0) cKey = cKey & ~0xFF00;
                                if (csp == 0) cKey = cKey & ~0xFF;
                                if(key == cKey)
                                    scaleMatrix(uKey, scaling);
                            }
                        }
                    }
                }
            }
    //        float grStock = 0f;
    //        for(long key : m_pExperiment.m_mTables.keySet()) {
    //            GMMatrix pTable = m_pExperiment.m_mTables.get(key);
    //            grStock += pTable.getValue();
    //        }
    //        m_pExperiment.m_afStock.setData(0, grStock);
            m_pExperiment.resetHistory();
            reader.close();
            eventLogger.logEntry(eventFileLoadEnd, "filename " + filename);
        } catch (GMParLocator.GMParLocatorException ex) {
            System.err.println(ex);
        }
    }
    
    /**
     * @deprecated 
     * Reads new GMSimulation object from given path.
     * @param filename Path to file where GMSimulation is read from
     * @return new GMSimulation from file
     */
    public GMSimulation deserializeSession(String filename) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        GMSimulation sim = null;
        try
        {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            sim = (GMSimulation)in.readObject();
            in.close();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return sim;
    }

    /**
     * Scales a specified matrix from current experiment.
     * @param key Key to the matrix.
     * @param scaling 
     */
    private void scaleMatrix(long key, float scaling) {
        GMMatrix pTable = m_pExperiment.m_mTables.get(key);
        if (pTable != null) {
            try {
                float ageh, agel;
                // A little bit more complicated
                GMParArray parAr;
                // management 
                float minageNetti, maxageNetti;
                float shiftNetti;
                shiftNetti = 0;
                parAr = m_pExperiment.m_plHarvestAge.getParameter(pTable.m_wID);
                if (parAr != null) {
                    if (parAr.m_nSize == 6) {
                        // 
                        minageNetti = parAr.m_Vals.get(0);
                        maxageNetti = parAr.m_Vals.get(1);
                        //shiftNetti = 0.15*minageNetti;
                        //if (shiftNetti<5)
                        //    shiftNetti = 5;
                        minageNetti -= shiftNetti;
                        maxageNetti -= shiftNetti;
                        float abage = (float) ((1.0 - parAr.m_Vals.get(5)) * parAr.m_Vals.get(0));
                        pTable.setFellingsRegimes(minageNetti, maxageNetti, parAr.m_Vals.get(2),
                                parAr.m_Vals.get(3), parAr.m_Vals.get(4), abage);
                        //
                        // parAr.m_Vals.get(3),parAr.m_Vals.get(4),abage);
                        // parAr.m_Vals.get(3),parAr.m_Vals.get(4),abage);

                    } else {
                        ageh = parAr.m_Vals.get(0);
                        pTable.setFellingsSimple(ageh);
                    }
                } else {
                    pTable.setFellingsSimple(50.0f);
                }
                // And of simple approach
                // Now setting Thinnings ranges simple approach first!
                agel = m_pExperiment.m_plThinRange.getParameterValue(pTable.m_wID, 0);
                ageh = m_pExperiment.m_plThinRange.getParameterValue(pTable.m_wID, 1);
                // Just for Netti!
                agel -= shiftNetti;
                if (agel < 5.) {
                    agel = 5.0f;
                }
                ageh -= shiftNetti;
                //  
                pTable.setThinningsSimple(agel, ageh);
                pTable.scaleArea(scaling);
                //now we have to reinit history for step 0!
                ageh = m_pExperiment.m_plThHistory.getParameterValue(pTable.m_wID, 0);
                pTable.resetThinHistory(ageh,scaling);
    //            //Area
    //            ComArFlt data = m_pExperiment.m_mafArea.get(key);
    //            if (data != null)
    //                data.setData(0, pTable.getArea());
    //            //Growing stock
    //            data = m_pExperiment.m_mafGrStock.get(key);
    //            if (data != null)
    //               data.setData(0, pTable.getValue());
    //            //Biomass
    //            GMCarbonAlloc pCAl = new GMCarbonAlloc();
    //            pCAl.setCa_cstem(0.0);
    //            pCAl.setCa_cbranch(0.0);
    //            pCAl.setCa_ccroots(0.0);
    //            pCAl.setCa_cfroots(0.0);
    //            pCAl.setCa_cleaves(0.0);
    //            pCAl.setCa_ccont(m_pExperiment.m_plCcont.getParameterValue(key,0));
    //            pCAl.setCa_dns(m_pExperiment.m_plWoodDens.getParameterValue(key,0));
    //            pCAl.setCa_nsize(m_pExperiment.m_plCompXvals.getParameter(key).m_nSize);
    //            pCAl.setCa_pxvals(m_pExperiment.m_plCompXvals.getParameter(key).m_Vals);
    //            pCAl.setCa_pstem(m_pExperiment.m_plStemShare.getParameter(key).m_Vals);
    //            pCAl.setCa_pbranch(m_pExperiment.m_plBranchShare.getParameter(key).m_Vals);
    //            pCAl.setCa_pcroots(m_pExperiment.m_plCrootsShare.getParameter(key).m_Vals);
    //            pCAl.setCa_pfroots(m_pExperiment.m_plFrootsShare.getParameter(key).m_Vals);
    //            pCAl.setCa_pleaves(m_pExperiment.m_plLeavesShare.getParameter(key).m_Vals);
    //            // Compartments
    //            ArrayList<Float> pvalSt = new ArrayList<>(16);
    //            for (int i=0;i<16;i++) 
    //                pvalSt.add(0.0f);
    //            ArrayList<Float> pvalLv = new ArrayList<>(16);
    //            for (int i=0;i<16;i++)
    //                pvalLv.add(0.0f);
    //            ArrayList<Float> pvalBr = new ArrayList<>(16);
    //            for (int i=0;i<16;i++)
    //                pvalBr.add(0.0f);
    //            ArrayList<Float> pvalCr = new ArrayList<>(16);
    //            for (int i=0;i<16;i++)
    //                pvalCr.add(0.0f);
    //            ArrayList<Float> pvalFr = new ArrayList<>(16);
    //            for (int i=0;i<16;i++)
    //                pvalFr.add(0.0f);
    //            float val = pTable.getBiomassDistr(pCAl,m_pExperiment.m_pDistrLims,pvalSt,pvalBr,pvalLv,pvalCr,pvalFr,16);
    //            //total biomass
    //            data = m_pExperiment.m_mafBiomass.get(key);
    //            if (data != null) {
    //                data.setData(0,val);
    //            }
    //            //Compartments
    //
    //            data = m_pExperiment.m_mafCStem.get(key);
    //            if (data != null) {
    //                data.setData(0,pvalSt);
    //                
    //            }
    //            data = m_pExperiment.m_mafCLeaves.get(key);
    //            if (data != null) {
    //                data.setData(0,pvalLv);
    //                
    //            }
    //            data = m_pExperiment.m_mafCBranches.get(key);
    //            if (data != null) {
    //                data.setData(0,pvalBr);
    //                
    //            }
    //            data = m_pExperiment.m_mafCCRoots.get(key);
    //            if (data != null) {
    //                data.setData(0,pvalCr);
    //                
    //            }
    //            data = m_pExperiment.m_mafCFRoots.get(key);
    //            if (data != null) {
    //                data.setData(0,pvalFr);
    //                
    //            }
            } catch (GMParLocator.GMParLocatorException ex) {
                System.err.println(ex);
            }
        }
    }

    /**
     * Log bioallocation error. Sends bioallocation errormessage to errorlogger.
     * @param reader Reader that was used to read bioallocation data.
     */
    private void reportBiolAllocError(LineReader reader) {
        errorLogger.logEntry(this.errorFileParsing, "All bioallocaton "
                + "rows must have same length", reader.getFileName(),
                reader.getLineNumber());
    }

    /**
     * Loads bio parameters such as allocations by compartments, etc from
     * bioparameters file to experiment collection. GMEfiscen must be initialized.
     * @param sFileIn input file for bioparameters
     * @return true if reading was successful, false otherwise
     * @throws EFISCENFileNotFoundException if file was not found
     */
    public boolean loadBioParameters(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        String strLine;
        int nst = 0;
        int nHm = 0;
        int cr = 0, co = 0, cst = 0, csp = 0;
        String sname;
        String[] split;

        // Carbon content
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "filename "
                    + reader.getFileName() + " line " + reader.getLineNumber());

            return false;
        }
        TreeSet<Long> idSet = new TreeSet<>();
        int numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix ID defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plCcont");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+",
                    reader, errorLogger);
            m_pExperiment.m_plCcont.addParameter(pPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        // Wood density
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        idSet = new TreeSet<>();
        numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plWoodDens");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+",
                    reader, errorLogger);
            m_pExperiment.m_plWoodDens.addParameter(pPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }

        // Age limits for compartments
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        idSet = new TreeSet<>();
        numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plCompXvals");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plCompXvals.addParameter(pPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }

        // Now shares of compartments itself.
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getFileName() + " line " + reader.getLineNumber());
            return false;
        }
        idSet = new TreeSet<>();
        numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getFileName() + " line " + reader.getLineNumber());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            // Stem is here
            GMParArray pPar = new GMParArray(nst,"m_plStemShare");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            float valsSize = pPar.m_Vals.size();
            m_pExperiment.m_plStemShare.addParameter(pPar);

            // Branches
            strLine = reader.readLine();
            /*
             * try { nst = Integer.parseInt(strLine); } catch
             * (NumberFormatException nfe) {
             *
             * }
             */
            GMParArray pComPar = new GMParArray(nst,"m_plBranchShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            if (valsSize != pComPar.m_Vals.size()) {
                reportBiolAllocError(reader);
            }

            // Some trick - recalculation from original data
            for (int jj = 0; jj < nst; jj++) {
                if (pPar.m_Vals.get(jj) > 0) {
                    pComPar.m_Vals.set(jj, pComPar.m_Vals.get(jj) / pPar.m_Vals.get(jj));
                }
            }
            m_pExperiment.m_plBranchShare.addParameter(pComPar);
            // Coarce roots
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plCrootsShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            if (valsSize != pComPar.m_Vals.size()) {
                reportBiolAllocError(reader);
            }

            // Some trick - recalculation from original data
            for (int jj = 0; jj < nst; jj++) {
                if (pPar.m_Vals.get(jj) > 0) {
                    pComPar.m_Vals.set(jj, pComPar.m_Vals.get(jj) / pPar.m_Vals.get(jj));
                }
            }
            m_pExperiment.m_plCrootsShare.addParameter(pComPar);
            // Fine roots
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plFrootsShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            if (valsSize != pComPar.m_Vals.size()) {
                reportBiolAllocError(reader);
            }
            // Some trick - recalculation from original data
            for (int jj = 0; jj < nst; jj++) {
                if (pPar.m_Vals.get(jj) > 0) {
                    pComPar.m_Vals.set(jj, pComPar.m_Vals.get(jj) / pPar.m_Vals.get(jj));
                }
            }
            m_pExperiment.m_plFrootsShare.addParameter(pComPar);

            // Leaves/needles
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plLeavesShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            if (valsSize != pComPar.m_Vals.size()) {
                reportBiolAllocError(reader);
            }

            // Some trik - recalculation from original data
            for (int jj = 0; jj < nst; jj++) {
                if (pPar.m_Vals.get(jj) > 0) {
                    pComPar.m_Vals.set(jj, pComPar.m_Vals.get(jj) / pPar.m_Vals.get(jj));
                }
            }
            m_pExperiment.m_plLeavesShare.addParameter(pComPar);
            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        // Litter production parameters
        // Age limits for compartments
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        idSet = new TreeSet<>();
        numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plLtrCompXvals");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrCompXvals.addParameter(pPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        // Now shares of compartments itself...
        loadShares(reader);
        // Added  January 2010 
        // Coarse roots share going to coarse woody liiter
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        idSet = new TreeSet<>();
        numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plCroots2CWL");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plCroots2CWL.addParameter(pPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }
    
    /**
     * Loads shares from LineReader, tries to parse data and saves shares to
     * current experiment collection.
     * @param reader Points to file where shares are read.
     * @return false if reading was unsuccessful, true otherwise
     */
    boolean loadShares(LineReader reader) {
        Integer cr = 0, co = 0, cst = 0, csp = 0;
        String strLine = reader.readLine();
        String[] split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        String sname = split[0];
        int nHm = 0;
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        TreeSet<Long> idSet = new TreeSet<>();
        int numExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            int nst = 0;
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            // Stem is here
            GMParArray pPar = new GMParArray(nst,"m_plLtrStemShare");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrStemShare.addParameter(pPar);
            // Branches
            strLine = reader.readLine();
            GMParArray pComPar = new GMParArray(nst,"m_plLtrBranchShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrBranchShare.addParameter(pComPar);

            // Coarce roots
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plLtrCrootsShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrCrootsShare.addParameter(pComPar);

            // Fine roots
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plLtrFrootsShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrFrootsShare.addParameter(pComPar);

            // Leaves/needles
            strLine = reader.readLine();
            pComPar = new GMParArray(nst,"m_plLtrLeavesShare");
            pComPar.m_uRegion = cr;
            pComPar.m_uOwner = co;
            pComPar.m_uSite = cst;
            pComPar.m_uSpecies = csp;
            pComPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            m_pExperiment.m_plLtrLeavesShare.addParameter(pComPar);

            numExtracted++;
        }
        if (numExtracted != nHm) {
            errorLogger.logEntry(eventIncorrectData, numExtracted
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        return true;
    }

    /**
     * Loads soils from file to experiment collection. GMEfiscen must be initialized.
     * @param sFileIn inputfile for soils.
     * @return false if reading was unsuccessful, true otherwise
     * @throws EFISCENFileNotFoundException
     */
    public boolean loadSoils(String sFileIn) throws EFISCENFileNotFoundException {
        String strLine;
        int nHm;
        long cr = 0, co = 0, cst = 0, csp = 0;
        String sname;
        String[] split;
        // Ok to start
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        int extractedEntries = 0;
        TreeSet<Long> idSet = new TreeSet<>();
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            // Region_owner_site_species reading and make ID for Soil
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            long ulID, ulRes;
            ulID = (long) cr;
            ulRes = ulID << 24;
            ulID = (long) co;
            ulRes = ulRes + (ulID << 16);
            ulID = (long) cst;
            ulRes = ulRes + (ulID << 8);
            ulID = (long) csp;
            ulRes = ulRes + ulID;
            // Soil object construction

            // Now fill the structure
            strLine = reader.readLine();
            double fcwl, ffwl, fnwl, fsol, fcel, flig, fhm1, fhm2;
            split = strLine.split("\\s+");
            if (split == null || split.length < 8) {
                break;
            }
            try {
                fcwl = NumberParser.convertDouble(split[0], reader, errorLogger);
                ffwl = NumberParser.convertDouble(split[1], reader, errorLogger);
                fnwl = NumberParser.convertDouble(split[2], reader, errorLogger);
                fsol = NumberParser.convertDouble(split[3], reader, errorLogger);
                fcel = NumberParser.convertDouble(split[4], reader, errorLogger);
                flig = NumberParser.convertDouble(split[5], reader, errorLogger);
                fhm1 = NumberParser.convertDouble(split[6], reader, errorLogger);
                fhm2 = NumberParser.convertDouble(split[7], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                return false;
            }
            // Scaling compartments if they are predefined 11.12.2007 - MJ request
            double totsoil = fcwl + ffwl + fnwl + fsol + fcel + flig + fhm1
                    + fhm2;
            double si_cwl, si_fwl, si_nwl, si_sol, si_cel, si_lig, si_hm1, si_hm2;
            si_cwl = fcwl;
            si_fwl = ffwl;
            si_nwl = fnwl;
            si_sol = fsol;
            si_cel = fcel;
            si_lig = flig;
            si_hm1 = fhm1;
            si_hm2 = fhm2;
            if (m_scaleAreas != 1.0 && totsoil > 0) {
                si_cwl *= m_scaleAreas;
                si_fwl *= m_scaleAreas;
                si_nwl *= m_scaleAreas;
                si_sol *= m_scaleAreas;
                si_cel *= m_scaleAreas;
                si_lig *= m_scaleAreas;
                si_hm1 *= m_scaleAreas;
                si_hm2 *= m_scaleAreas;
            }
            // End scaling
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 8) {
                break;
            }
            try {
                fcwl = NumberParser.convertDouble(split[0], reader, errorLogger);
                ffwl = NumberParser.convertDouble(split[1], reader, errorLogger);
                fnwl = NumberParser.convertDouble(split[2], reader, errorLogger);
                fsol = NumberParser.convertDouble(split[3], reader, errorLogger);
                fcel = NumberParser.convertDouble(split[4], reader, errorLogger);
                flig = NumberParser.convertDouble(split[5], reader, errorLogger);
                fhm1 = NumberParser.convertDouble(split[6], reader, errorLogger);
                fhm2 = NumberParser.convertDouble(split[7], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                return false;
            }
            double si_acwl, si_afwl, si_anwl, si_ksol, si_kcel, si_klig, si_khm1, si_khm2;
            si_acwl = fcwl;
            si_afwl = ffwl;
            si_anwl = fnwl;
            si_ksol = fsol;
            si_kcel = fcel;
            si_klig = flig;
            si_khm1 = fhm1;
            si_khm2 = fhm2;

            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                fsol = NumberParser.convertDouble(split[0], reader, errorLogger);
                fcel = NumberParser.convertDouble(split[1], reader, errorLogger);
                flig = NumberParser.convertDouble(split[2], reader, errorLogger);
                fhm1 = NumberParser.convertDouble(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                return false;
            }
            double si_psol, si_pcel, si_plig, si_phum;
            si_psol = fsol;
            si_pcel = fcel;
            si_plig = flig;
            si_phum = fhm1;
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 6) {
                break;
            }
            try {
                fcwl = NumberParser.convertDouble(split[0], reader, errorLogger);
                ffwl = NumberParser.convertDouble(split[1], reader, errorLogger);
                fnwl = NumberParser.convertDouble(split[2], reader, errorLogger);
                fsol = NumberParser.convertDouble(split[3], reader, errorLogger);
                fcel = NumberParser.convertDouble(split[4], reader, errorLogger);
                flig = NumberParser.convertDouble(split[5], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                return false;
            }
            double si_cw2cel, si_cw2sol, si_fw2cel, si_fw2sol, si_nw2cel, si_nw2sol;
            si_cw2cel = fcwl;
            si_cw2sol = ffwl;
            si_fw2cel = fnwl;
            si_fw2sol = fsol;
            si_nw2cel = fcel;
            si_nw2sol = flig;
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 2) {
                break;
            }
            try {
                fhm1 = NumberParser.convertDouble(split[0], reader, errorLogger);
                fhm2 = NumberParser.convertDouble(split[1], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                return false;
            }
            double si_clhm1, si_clhm2;
            si_clhm1 = fhm1;
            si_clhm2 = fhm2;

            /**
             * TODO parameters for the constructor are named differently
             * than values passed there.
             */
            GMSoil pSol = new GMSoil(si_cwl, si_fwl, si_nwl, si_sol, si_cel, si_lig, si_hm1, si_hm2,
                    si_acwl, si_afwl, si_anwl, si_ksol, si_kcel, si_klig, si_khm1, si_khm2,
                    si_psol, si_pcel, si_plig, si_phum, si_cw2cel, si_cw2sol, si_fw2cel, si_fw2sol, si_nw2cel, si_nw2sol,
                    si_clhm1, si_clhm2);
            pSol.m_wID = ulRes;
            m_pExperiment.addSoil(pSol);
            extractedEntries++;
        }
        if (extractedEntries != nHm) {
            errorLogger.logEntry(eventIncorrectData, extractedEntries
                    + " entries were extracted, " + nHm + " were expected file "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Loads parameters of a simulation. Data includes number and size of age and
     * volume classes, coefficients for growing function, young forest coefficient, 
     * regrow after thinnings coefficients, age of harvest and thinnings, beta 
     * coefficient, age limits, volume series, natural mortality, mortality and 
     * decay rates and thinnings history. GMEfiscen must be initialized.
     *
     * @param sFileIn inputfile from which parameters are read.
     * @return false if unsuccessful, true otherwise
     * @throws EFISCENFileNotFoundException if file not found
     * @throws EFISCENFileParsingException if file reading was unsuccessful
     */
    public boolean loadParameters(String sFileIn) throws EFISCENFileNotFoundException, EFISCENFileParsingException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        String strLine;
        String[] split;
        int nst;

        strLine = reader.readLine();
        try {
            nst = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        m_pExperiment.m_nStep = nst;

        int nHm;
        int cr = 0, co = 0, cst = 0, csp = 0;
        String sname;
        GMParLocator addPar;

        // Number of age classes
        addPar = addParsedParameter(reader, m_pExperiment.m_plAgeNum);
        if (addPar != null) {
            m_pExperiment.m_plAgeNum = addPar;
        } else {
            return false;
        }
        // Size of age classes
        addPar = addParsedParameter(reader, m_pExperiment.m_plAgeClasses);
        if (addPar != null) {
            m_pExperiment.m_plAgeClasses = addPar;
        } else {
            return false;
        }
        // Number of volume classes
        addPar = addParsedParameter(reader, m_pExperiment.m_plVolNum);
        if (addPar != null) {
            m_pExperiment.m_plVolNum = addPar;
        } else {
            return false;
        }
        // Size of volume classes
        addPar = addParsedParameter(reader, m_pExperiment.m_plVolClasses);
        if (addPar != null) {
            m_pExperiment.m_plVolClasses = addPar;
        } else {
            return false;
        }
        // Coefficients for Growing Function
        addPar = addParsedParameter(reader, m_pExperiment.m_plGrCoeff);
        if (addPar != null) {
            m_pExperiment.m_plGrCoeff = addPar;
        } else {
            return false;
        }
        // Young forest coeffs
        addPar = addParsedParameter(reader, m_pExperiment.m_plYoungCoeff);
        if (addPar != null) {
            m_pExperiment.m_plYoungCoeff = addPar;
        } else {
            return false;
        }
        // Regrow after thinnings coeff
        addPar = addParsedParameter(reader, m_pExperiment.m_plRegrowCoeff);
        if (addPar != null) {
            m_pExperiment.m_plRegrowCoeff = addPar;
        } else {
            return false;
        }
        // Age of Harvest
        addPar = addParsedParameter(reader, m_pExperiment.m_plHarvestAge);
        if (addPar != null) {
            m_pExperiment.m_plHarvestAge = addPar;
        } else {
            return false;
        }
        // Ages of Thinnings
        addPar = addParsedParameter(reader, m_pExperiment.m_plThinRange);
        if (addPar != null) {
            m_pExperiment.m_plThinRange = addPar;
        } else {
            return false;
        }
        // Beta coeff
        addPar = addParsedParameter(reader, m_pExperiment.m_plBeta);
        if (addPar != null) {
            m_pExperiment.m_plBeta = addPar;
        } else {
            return false;
        }
        // Age Limits
        addPar = addParsedParameter(reader, m_pExperiment.m_plAgeLims);
        if (addPar != null) {
            m_pExperiment.m_plAgeLims = addPar;
        } else {
            return false;
        }
        // Volume Series
        addPar = addParsedParameter(reader, m_pExperiment.m_plVolSers);
        if (addPar != null) {
            m_pExperiment.m_plVolSers = addPar;
        } else {
            return false;
        }
        // Natural mortality!
        // Age limits
        addPar = addParsedParameter(reader, m_pExperiment.m_plMortRateXvals);
        if (addPar != null) {
            m_pExperiment.m_plMortRateXvals = addPar;
        } else {
            return false;
        }
        // Rates itself
        addPar = addParsedParameter(reader, m_pExperiment.m_plMortRate);
        if (addPar != null) {
            m_pExperiment.m_plMortRate = addPar;
        } else {
            return false;
        }
        // Decay rates
        addPar = addParsedParameter(reader, m_pExperiment.m_plDeadWoodDrate);
        if (addPar != null) {
            m_pExperiment.m_plDeadWoodDrate = addPar;
        } else {
            return false;
        }

        m_pGrFun = new GMGrFunDefault();

        // !!!!!!!!!!!!!!!!!
        // Thinnings history
        strLine = reader.readLine();
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            return false;
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        TreeSet<Long> idSet = new TreeSet<>();
        int numDataExtracted = 0;
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

            }
            GMParArray pPar = new GMParArray(nst,"m_plThHistory");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            for (int irr = 0; irr < nst; irr++) {
                if (pPar.m_Vals.get(irr) < 0.0 || pPar.m_Vals.get(irr) > 1.0) {
                    errorLogger.logEntry(errorFileParsing, "Wrong thin history "
                            + "value\n Parameter number" + irr + 1 + ": in set " + cr + " "
                            + co + " " + cst + " " + csp + " \nassign default = 0.5..."
                            + " on line"
                            + reader.getLineNumber() + " in file " + reader.getFileName());
                    System.err.println("Wrong thin history value\n Parameter number "
                            + irr + 1 + ": in set " + cr + " " + co + " " + cst + " " + csp + " \nassign default = 0.5...");
                    pPar.m_Vals.add(irr, 0.5f);
                }
            }
            numDataExtracted++;
            m_pExperiment.m_plThHistory.addParameter(pPar);
        }
        if (numDataExtracted != nHm) {
            errorLogger.logEntry(errorFileParsing, numDataExtracted + " entries "
                    + "were extracted, " + nst + " were expected "
                    + reader.getFileName() + " line " + reader.getLineNumber());
        }

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }
    
    /**
     * Log parsing error. Sends parsing error report to errorLogger.
     * @param reader Reader used during parsing.
     */
    private void reportParsingError(LineReader reader) {
        errorLogger.logEntry(this.errorFileParsing, " on line "
                + reader.getLineNumber() + " in file " + reader.getFileName());
    }

    /**
     * Parses the parameters from file and adds them to GMParLocator given in
     * parameters.
     * @param reader linereader which reads lines from the file
     * @param par parlocator in which the parameters are added
     * @return GMParLocator containing the new parameters or null if unsuccesful
     * @throws EFISCENFileParsingException if file reading was unsuccesfull
     */
    private GMParLocator addParsedParameter(LineReader reader, GMParLocator par) throws EFISCENFileParsingException {
        String split[];
        String sname, strLine;
        int cr = 0, co = 0, cst = 0, csp = 0;
        int nHm, nst;

        strLine = reader.readLine();
        if (strLine == null) {
            throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
        }
        split = strLine.split("\\s+");
        if (split == null || split.length < 2) {
            reportParsingError(reader);
            throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
        }
        sname = split[0];
        try {
            nHm = NumberParser.convertInt(split[1], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
        }
        TreeSet<Long> idSet = new TreeSet<>();
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null || split.length < 4) {
                reportParsingError(reader);
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                if (!idSet.contains(key)) {
                    idSet.add(key);
                } else {
                    errorLogger.logEntry(errorFileParsing, "matrix id defined "
                            + "twice in file " + reader.getFileName() + " line "
                            + reader.getLineNumber());
                }
            }
            strLine = reader.readLine();
            split = strLine.split("\\s+");
            if (split == null) // || split.length < 1
            {
                break;
            }
            try {
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            GMParArray pPar = new GMParArray(nst);
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, "\\s+", reader, errorLogger);
            par.addParameter(pPar);
        }
        return par;
    }

    /**
     * Load volume classes limits from file to a new GMParLocator. Loads data, 
     * tries to parse it and loads it to new GMParlocator. To compability with EFISCEN 2.
     * @param sFileIn intputfile path
     * @return GMParLocator object if loading was succesful otherwise null
     * @throws EFISCENFileNotFoundException if file not found
     * @throws EFISCENFileParsingException if file reading was unsuccessful
     */
    public GMParLocator loadYLimits(String sFileIn) throws EFISCENFileNotFoundException, EFISCENFileParsingException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        try {
            if (m_pExperiment == null) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            GMParLocator pplRet = new GMParLocator("y_limits");
            String strLine;
            String[] split;
            int nHowMany = 0;
            strLine = reader.readLine();
            try {
                NumberParser.convertInt(strLine, reader, errorLogger);
                nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

            }
            TreeSet<Long> idSet = new TreeSet<>();
            for (int i = 0; i < nHowMany; i++) {
                strLine = reader.readLine();
                int cr = 0, co = 0, cst = 0, csp = 0;
                split = strLine.trim().split("\\s+");
                if (split == null || split.length < 4) {
                    break;
                }
                try {
                    cr = NumberParser.convertInt(split[0], reader, errorLogger);
                    co = NumberParser.convertInt(split[1], reader, errorLogger);
                    cst = NumberParser.convertInt(split[2], reader, errorLogger);
                    csp = NumberParser.convertInt(split[3], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                }
                if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                    long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                    if (!idSet.contains(key)) {
                        idSet.add(key);
                    } else {
                        errorLogger.logEntry(errorFileParsing, "matrix id defined "
                                + "twice in file " + reader.getFileName() + " line "
                                + reader.getLineNumber());
                    }
                }
                long ulID, ulRes;
                ulID = (long) cr;
                ulRes = ulID << 24;
                ulID = (long) co;
                ulRes = ulRes + (ulID << 16);
                ulID = (long) cst;
                ulRes = ulRes + (ulID << 8);
                ulID = (long) csp;
                ulRes = ulRes + ulID;
                int nv;
                nv = (int) m_pExperiment.m_plVolNum.getParameterValue(ulID, 0);
                GMParArray pPar = new GMParArray(nv,"load y limits");
                pPar.setM_uRegion(cr);
                pPar.setM_uOwner(co);
                pPar.setM_uSite(cst);
                pPar.setM_uSpecies(csp);
                float lims = 0;
                for (int j = 0; j < nv; j++) {
                    strLine = reader.readLine();
                    try {
                        lims = NumberParser.convertFloat(strLine, reader, errorLogger);
                    } catch (NumberFormatException nfe) {
                        reportParsingError(reader);

                    }
                    // Perhaps we should just change m_vals to public
                    java.util.ArrayList vals = pPar.getM_Vals();
                    vals.add(j, lims);
                    /**
                     * is adding vals-array into itself supposed to happen?
                     */
                    //vals.add(vals);
                }
                pplRet.addParameter(pPar);
            }
            eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
            reader.close();
            return pplRet;
        }  catch (GMParLocator.GMParLocatorException ex) {
            System.err.println(ex);
        }
        return null;
    }

    /**
     * Load climate for forest grow to experiment collection. GMEfiscen must be initialized.
     * @param sFileIn Full input file path
     * @return true if loading was successful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadForClim(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany = 0;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        ListIterator<InputLoaderListener> iter = listeners.listIterator();
        while (iter.hasNext()) {
            iter.next().onLoadClimateHeader(strLine);
        }
        // TODO: set window text
        //if (m_bIsInteractive)
        //    m_cstClimName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(nHowMany + 1);

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", nHowMany + 1,
                    reader, errorLogger);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(1,"m_plForClim");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(i + 1));
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plForClim.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }
    /**
     * Load climate for forest grow with age-depended rations - V4 version (for
     * Ari).
     *
     * @param sFileIn
     * @return true if loading was succesful else false
     */
    private LinkedList<InputLoaderListener> listeners = new LinkedList<>();

    /**
     * Adds listener to stack of listeners.
     * @param listener listener to register
     */
    public void registerListener(InputLoaderListener listener) {
        this.listeners.push(listener);
    }

    /**
     * Load climate for forest grow to experiment collection. GMEfiscen must be initialized.
     * @param sFileIn input file path
     * @return true if loading was successful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadForClimV4(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nst = 0;
        int nHowMany;
        int nHm;
        int cr, co, cst, csp;
        String sname;
        String[] split;

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        if(strLine!=null)
            m_Scenario.climName = strLine;
        else
            m_Scenario.climName = "";
        String name = strLine;
        ListIterator<InputLoaderListener> iter = listeners.listIterator();
        while (iter.hasNext()) {
            iter.next().onLoadClimateHeader(name);
        }
        // TODO: set window text
        //if (m_bIsInteractive)
        //    m_cstClimName.setWindowText(strLine);
        // Age Limits first
        strLine = reader.readLine();
        try {
            nHm = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        strLine = reader.readLine();
        for (int i = 0; i < nHm; i++) {
            strLine = reader.readLine();
            split = strLine.split(",");
            if (split == null || split.length < 4) {
                break;
            }
            try {
                cr = NumberParser.convertInt(split[0], reader, errorLogger);
                co = NumberParser.convertInt(split[1], reader, errorLogger);
                cst = NumberParser.convertInt(split[2], reader, errorLogger);
                csp = NumberParser.convertInt(split[3], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                return false;
            }
            strLine = reader.readLine();
            try {
                split = strLine.split(",");
                nst = NumberParser.convertInt(split[0], reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

            }
            GMParArray pPar = new GMParArray(nst,"m_plClimAgeLims");
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            pPar.m_Vals = StringParser.getFlArFromString(strLine, ",", reader,
                    errorLogger);
            m_Scenario.m_plClimAgeLims.addParameter(pPar);
        }
        strLine = reader.readLineSimple();

        // Now ratios
        strLine = reader.readLineSimple();
        while (strLine != null) {
            try {
                nHm = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                return false;
            }
            strLine = reader.readLineSimple();
            try {
                nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                return false;
            }

            GMEfiscenario pSc = new GMEfiscenario();
            pSc.setEs_nStep(nHm);

            for (int i = 0; i < nHowMany; i++) {
                strLine = reader.readLine();
                split = strLine.split(","); // split.("\\s+")
                if (split == null || split.length < 4) {
                    break;
                }
                try {
                    cr = NumberParser.convertInt(split[0], reader, errorLogger);
                    co = NumberParser.convertInt(split[1], reader, errorLogger);
                    cst = NumberParser.convertInt(split[2], reader, errorLogger);
                    csp = NumberParser.convertInt(split[3], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                    return false;
                }
                strLine = reader.readLine();
                try {
                    split = strLine.split(",");
                    nst = NumberParser.convertInt(split[0], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                }
                GMParArray pPar = new GMParArray(nst,"m_plForClim");
                pPar.m_uRegion = cr;
                pPar.m_uOwner = co;
                pPar.m_uSite = cst;
                pPar.m_uSpecies = csp;
                pPar.m_Vals = StringParser.getFlArFromString(strLine, ",", reader,
                        errorLogger);
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plForClim.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Load climate for soil development from input file to m_pExperiment.
     * Experiment collection must be initialized.
     * @param sFileIn Full input file path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadSoilClim(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        String[] split;
        // Two lines header reading
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        //strLine = reader.readLineSimple();
        float beta, gamma, tvar, pvar;
        split = strLine.split(",");
        if (split == null || split.length < 4) {
            return false;
        }
        try {
            beta = NumberParser.convertFloat(split[0], reader, errorLogger);
            gamma = NumberParser.convertFloat(split[1], reader, errorLogger);
            tvar = NumberParser.convertFloat(split[2], reader, errorLogger);
            pvar = NumberParser.convertFloat(split[3], reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        m_pExperiment.setSoilClimateVars(beta, gamma, tvar, pvar);
        // Scenario starts here
        strLine = reader.readLineSimple(); // Explanation string
        strLine = reader.readLineSimple();
        int nHowMany;
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(2 * nHowMany + 1);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", 2 * nHowMany + 1,
                    reader, errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(2,"m_plSoilClim");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(2 * i + 1));
                pPar.m_Vals.add(pData.get(2 * i + 2));
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plSoilClim.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Load fellings and thinnings properties i.e. parts of stem-branches-lives
     * goes away as removals we suppose that first column (in corresponding set
     * of columns) is felling, and second one is thinnings.
     * @param sFileIn inputfile path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadFelProps(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            errorLogger.logEntry(errorFileParsing, "error on line "
                    + reader.getLineNumber() + " in file " + reader.getFileName());

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(6 * nHowMany + 1);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", 6 * nHowMany + 1,
                    reader, errorLogger);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(6,"m_plCutProps");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(6 * i + 1));  //fellings - stem removals
                pPar.m_Vals.add(pData.get(6 * i + 2));	//fellings - branches removals
                pPar.m_Vals.add(pData.get(6 * i + 3));	//fellings - leaves removals
                pPar.m_Vals.add(pData.get(6 * i + 4));	//thinnnings - stem
                pPar.m_Vals.add(pData.get(6 * i + 5));	//thinnings - branches
                pPar.m_Vals.add(pData.get(6 * i + 6));	//thinnings - leaves

                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plCutProps.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Load fellings and thinnings properties i.e. parts of
     * stem-tops-branches-lives goes away as removals we suppose that first
     * column (in corresponding set of columns) is felling, and second one is
     * thinnings release of Loadfelprops developed for Hans. September 2009
     * (Uppsala) - added coarse roots part to removals.
     * Experiment collection must be initialized.
     * @param sFileIn inputfile path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadFelPropsEx(String sFileIn) throws EFISCENFileNotFoundException {
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(12 * nHowMany + 1);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", 12 * nHowMany + 1,
                    reader, errorLogger);
            //Uppsala from 10 to 12 ad all '12' below
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(12,"m_plCutProps");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                /**
                 * Puts the number of steps into m_Vals - array. Is this
                 * intended?
                 */
                /*
                 * pPar.m_Vals.add(pData.get(12*i));	//fellings - stem
                 * removals pPar.m_Vals.add(pData.get(12*i+1));	//fellings -
                 * tops removals pPar.m_Vals.add(pData.get(12*i+2));
                 * //fellings - branches removals
                 * pPar.m_Vals.add(pData.get(12*i+3));	//fellings - leaves
                 * pPar.m_Vals.add(pData.get(12*i+5));	//fellings - deadwood
                 * pPar.m_Vals.add(pData.get(12*i+6));	//thinnings - stem
                 * pPar.m_Vals.add(pData.get(12*i+7));	//thinnings - tops
                 * pPar.m_Vals.add(pData.get(12*i+8));	//thinnings -
                 * branches pPar.m_Vals.add(pData.get(12*i+9)); //thinnings
                 * - leaves pPar.m_Vals.add(pData.get(12*i+11)); //thinnings
                 * - deadwood pPar.m_Vals.add(pData.get(12*i+4));	//fellings
                 * - coarse roots (Uppsala)
                 * pPar.m_Vals.add(pData.get(12*i+10)); //thinnings - coarse
                 * roots (Uppsala)
                 */
                /*
                 * pPar->m_Vals[0] = pData[12*i+1]; pPar->m_Vals[1] =
                 * pData[12*i+2]; pPar->m_Vals[2] = pData[12*i+3];
                 * pPar->m_Vals[3] = pData[12*i+4]; pPar->m_Vals[4] =
                 * pData[12*i+6]; pPar->m_Vals[5] = pData[12*i+7];
                 * pPar->m_Vals[6] = pData[12*i+8]; pPar->m_Vals[7] =
                 * pData[12*i+9]; pPar->m_Vals[8] = pData[12*i+10];
                 * pPar->m_Vals[9] = pData[12*i+12]; pPar->m_Vals[10] =
                 * pData[12*i+5]; pPar->m_Vals[11] = pData[12*i+11];
                 */
                pPar.m_Vals.add(pData.get(12 * i + 1));	 //fellings - stem removals
                pPar.m_Vals.add(pData.get(12 * i + 2));	 //fellings - tops removals
                pPar.m_Vals.add(pData.get(12 * i + 3));	 //fellings - branches removals
                pPar.m_Vals.add(pData.get(12 * i + 4));	 //fellings - leaves
                pPar.m_Vals.add(pData.get(12 * i + 6));	 //fellings - deadwood
                pPar.m_Vals.add(pData.get(12 * i + 7));	 //thinnings - stem
                pPar.m_Vals.add(pData.get(12 * i + 8));	 //thinnings - tops
                pPar.m_Vals.add(pData.get(12 * i + 9));	 //thinnings - branches
                pPar.m_Vals.add(pData.get(12 * i + 10)); //thinnings - leaves
                pPar.m_Vals.add(pData.get(12 * i + 12)); //thinnings - deadwood
                pPar.m_Vals.add(pData.get(12 * i + 5));	 //fellings - coarse roots (Uppsala)
                pPar.m_Vals.add(pData.get(12 * i + 11)); //thinnings - coarse roots (Uppsala)

                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plCutProps.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Load fellings and thinnings regimes we suppose that first column (in
     * corresponding set of columns) is felling, and second one is thinnings.
     * Experiment collection must be initialized.
     * @param sFileIn input file path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException when file not found
     */
    public boolean loadBusiness(String sFileIn) throws EFISCENFileNotFoundException {
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader;
        try {
             reader = new LineReader(new File(sFileIn), errorLogger);
        } catch(EFISCENFileNotFoundException ex) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        String name = strLine.substring(0, strLine.length() - 3);
        if(strLine!=null)
            m_Scenario.manName = strLine;
        else
            m_Scenario.manName = "";
        ListIterator<InputLoaderListener> iter = listeners.listIterator();
        while (iter.hasNext()) {
            iter.next().onLoadCuttingHeader(name);
        }
        // TODO: set window text
        //if (m_bIsInteractive)
        //    m_cstManName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(2 * nHowMany + 1);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", 2 * nHowMany + 1,
                    reader, errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(2,"m_plCuttings");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                // First value is fellings, second one is thínnings!
                pPar.m_Vals.add(pData.get(2 * i + 1)); // (2*i+1)
                pPar.m_Vals.add(pData.get(2 * i + 2)); // (2*i+2)
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plCuttings.add(pSc);
            strLine = reader.readLineSimple();
        }
        // Filling ratios parlocator
        for (int i = 0; i < nHowMany; i++) {
            GMParArray pRPar = new GMParArray(2,"m_plCutRatios");
            int ind = 4 * i;
            pRPar.m_uRegion = (int) pIDs.get(ind);
            pRPar.m_uOwner = (int) pIDs.get(ind + 1);
            pRPar.m_uSite = (int) pIDs.get(ind + 2);
            pRPar.m_uSpecies = (int) pIDs.get(ind + 3);
            // First value is fellings, second one is thínnings!
            pRPar.m_Vals.add(1.0f);
            pRPar.m_Vals.add(1.0f);
            m_Scenario.m_plCutRatios.addParameter(pRPar);
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Aforestation scenario Version to keep soil input due to afforestation.
     * Experiment collection must be initialized.
     * Updated  August 2010.
     * @param sFileIn inputfile path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if file was not found
     */
    public boolean loadAforestation(String sFileIn) throws EFISCENFileNotFoundException{
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        //m_cstClimName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(2 * nHowMany + 1);

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", 2 * nHowMany + 1,
                    reader, errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(2,"m_plAfor");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(2 * i + 1)); // (2*i+1) for both
                try{
                    pPar.m_Vals.add(pData.get(2 * i + 2)); // Here we get the share of input soil carbon: August 2010
                }catch(Exception e){
                    errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());
                    return false;
                }
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plAfor.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Loads deforestation scenario.
     * Experiment collection must be initialized.
     * @param sFileIn inputfile
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadDeforestation(String sFileIn) throws EFISCENFileNotFoundException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        //m_cstClimName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(nHowMany + 1);

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", nHowMany + 1, reader,
                    errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(1,"m_plDefor");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(i + 1)); // (i+1)
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plDefor.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Loads matrixes from inputfile to experiment collection. GMEfiscen must be initialized.
     * @param path Path to folder where matrixes-file is.
     * @param sFileIn inputfile for matrices.
     * @param numMatricesExpected How many matrices should be in the inputfile.
     * creates an entry to error log if this the numbers do not match.
     * @return the current matrix, null if reading was unsuccessful
     * @throws EFISCENException if file reading was unsuccessful
     */
    public GMMatrix loadData(String path, String sFileIn, int numMatricesExpected)
            throws EFISCENException {
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        try {
            if (m_pExperiment == null) {
                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            String strLine;
            String[] splitStr;
            strLine = reader.readLine();
            volClassFile = strLine;
            GMParLocator pplVolLims = loadYLimits(path + strLine);
            int nHowMany;
            strLine = reader.readLine();
            try {
                nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                errorLogger.logEntry(errorFileParsing, "error on line "
                        + reader.getLineNumber() + " in file " + reader.getFileName());

                throw new EFISCENFileParsingException(errorLogger, reader.getFileName());
            }
            if (numMatricesExpected != nHowMany) {
                errorLogger.logEntry(errorFileParsing, "number of matrices "+nHowMany+ " in file "
                        + reader.getFileName() + " doesnt match number of matrices "+numMatricesExpected+" in "
                        + ".efs file.");
            }
            TreeSet<Long> idSet = new TreeSet<>();
            for (int i = 0; i < nHowMany; i++) {
                strLine = reader.readLine();
                long cr = 0, co = 0, cst = 0, csp = 0;
                splitStr = strLine.trim().split("\\s+");
                if (splitStr == null || splitStr.length < 4) {
                    break;
                }

                try {
                    cr = NumberParser.convertInt(splitStr[0], reader, errorLogger);
                    co = NumberParser.convertInt(splitStr[1], reader, errorLogger);
                    cst = NumberParser.convertInt(splitStr[2], reader, errorLogger);
                    csp = NumberParser.convertInt(splitStr[3], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                }

                if (cr != 0 && co != 0 && cst != 0 && csp != 0) {
                    long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                    if (!idSet.contains(key)) {
                        idSet.add(key);
                    } else {
                        errorLogger.logEntry(errorFileParsing, "matrix id defined "
                                + "twice in file " + reader.getFileName() + " line "
                                + reader.getLineNumber());
                    }
                }

                //strLine = reader.readLine();
                int na, nv;
                float agew, volw;
                ArrayList<Float> pagew; // Modification for non regular by ages!

                float zclass = 0.0f;
                strLine = reader.readLine();
                splitStr = strLine.trim().split("\\s+");
                if (splitStr == null) {
                    break;
                }
                try {
                    zclass = NumberParser.convertFloat(splitStr[0], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                }
                /*
                 * if (agew<=0) { System.out.println("Wrong age class size\n
                 * Matrix: %d %d %d %d skipped...",cr,co,cst,csp); for (int
                 * ierr=0;ierr<nv;ierr++) strLine = reader.readLine(); continue;
                }
                 */
                GMMatrixInit Mi = new GMMatrixInit();
                long ulID;
                ulID = (long) cr;
                Mi.setMi_id(ulID << 24);
                ulID = (long) co;
                Mi.setMi_id(Mi.getMi_id() + (ulID << 16));
                ulID = (long) cst;
                Mi.setMi_id(Mi.getMi_id() + (ulID << 8));
                ulID = (long) csp;
                Mi.setMi_id(Mi.getMi_id() + ulID);
                ulID = Mi.getMi_id();
                na = (int) m_pExperiment.m_plAgeNum.getParameterValue(ulID, 0);
                nv = (int) m_pExperiment.m_plVolNum.getParameterValue(ulID, 0);
                agew = m_pExperiment.m_plAgeClasses.getParameterValue(ulID, 0);
                volw = m_pExperiment.m_plVolClasses.getParameterValue(ulID, 0);

                if (agew <= 0) {
                    errorLogger.logEntry(errorFileParsing, "Wrong age class size\n"
                            + " Matrix: " + cr + " " + co + " " + cst + " " + csp + " skipped..."
                            + " on line" + reader.getLineNumber() + " in file "
                            + reader.getFileName());
                    System.err.println("Wrong age class size\n Matrix: " + cr + " " + co + " " + cst + " " + csp + " skipped...");
                    for (int ierr = 0; ierr < nv; ierr++) {
                        strLine = reader.readLine();
                    }
                    continue;
                }

                // Non regular ages!
                GMParArray parAgew;
                parAgew = m_pExperiment.m_plAgeLims.getParameter(ulID);
                pagew = parAgew.m_Vals;
                int nAgewSize = parAgew.m_nSize;
                if (na != nAgewSize) {
                    errorLogger.logEntry(errorFileParsing, "Wrong number of age "
                            + "limits\n Matrix: " + cr + " " + co + " " + cst + " " + csp
                            + " skipped...\ncheck *.prs file" + " on line"
                            + reader.getLineNumber() + " in file " + reader.getFileName());
                    System.err.println("Wrong number of age classes\n Matrix: " + cr + " " + co + " " + cst + " " + csp + " skipped...\ncheck *.prs file");
                    for (int ierr = 0; ierr < nv; ierr++) {
                        strLine = reader.readLine();
                    }
                    continue;
                }
                // Counting maximal age
                float upperAge;

                upperAge = pagew.get(na - 1);
                // Number of cells by X-axis calculation
                int numcellsX = 0;
                int[] pnumc = new int[na];
                numcellsX = (int) (pagew.get(0) / m_pExperiment.m_nStep);
                pnumc[0] = numcellsX;
                for (int ja = 1; ja < na; ja++) {
                    pnumc[ja] = (int) ((pagew.get(ja) - pagew.get(ja - 1)) / m_pExperiment.m_nStep);
                    numcellsX += pnumc[ja];
                }

                Mi.setMi_xb(0.0f);
                Mi.setMi_xt(upperAge); //na*agew;
                Mi.setMi_xs(m_pExperiment.m_nStep);
                Mi.setMi_yb(0.0f);
                Mi.setMi_yt(nv * volw);
                Mi.setMi_ys(volw);

                int npart;
                npart = 1;
                if (m_pExperiment.m_nStep > 0) {
                    npart = (int) agew / m_pExperiment.m_nStep;
                }
                GMMatrix pMatr = new GMMatrix(numcellsX, nv);
                //GMMatrix pMatr = new GMMatrix(na*npart,nv);
                pMatr.initRegular(Mi);
                GMParArray pplClasses = null;
                if (pplVolLims != null) {
                    pplClasses = pplVolLims.getParameter(ulID);
                }
                if (pplClasses != null) {
                    pMatr.fillRegularByX(0.0f, pplClasses.m_Vals);
                } else {
                    pMatr.fillRegular(0.0f);
                }

                pMatr.addToBare(zclass);

                for (int ii = 0; ii < nv; ii++) {
                    strLine = reader.readLine();
                    String sDataString;
                    // TODO: make sure this works
                    sDataString = String.format("%d %s", na, strLine);
                    ArrayList<Float> fdata;
                    fdata = StringParser.getFlArFromString(sDataString, "\\s+", reader,
                            errorLogger);
                    //check the fdata size to avoid OutOfBounds exception
                    if (fdata.size() < na) {
                            errorLogger.logEntry(errorFileParsing,
                                    " Wrong number of entries for area " + fdata.size() + "\n in .aer file!");
                            errorLogger.logEntry(errorFileParsing, "Check data!"
                                    + "\nMatrix: \nReg " + cr + "\nOwner " + co + "\nSite "
                                    + cst + "\nSpec " + csp + "\n");
                            return null;
                        }
                    int ncurind = 0;
                    for (int j = 0; j < na; j++) {
                        float ar = fdata.get(j);
                        if (ar < 0) {
                            errorLogger.logEntry(errorFileParsing,
                                    " Negative Area! " + ar + "\nWill set to zero!");
                            errorLogger.logEntry(errorFileParsing, "Check data!"
                                    + "\nMatrix\nReg " + cr + "\nOwner " + co + "\nSite "
                                    + cst + "\nSpec " + csp + "\n");
                            ar = 0.0f;
                        }
                        npart = pnumc[j];
                        float part = ar / npart;
                        GMCell pCell;
                        for (int jj = 1; jj < npart; jj++) {
                            pCell = pMatr.getAt(ncurind + jj, ii + 1);
                            pCell.setM_Area(part);
                            ar -= part;
                        }
                        pCell = pMatr.getAt(ncurind + npart, ii + 1);
                        ncurind += npart;
                        if (ar < 0) {
                            errorLogger.logEntry(errorFileParsing, "Negative Area! " + ar + "\nWill set to zero!");
                            errorLogger.logEntry(errorFileParsing, "Check data!"
                                    + "\nMatrix\nReg " + cr + "\nOwner " + co + "\nSite " + cst + "\nSpec " + csp + "\n");
                            ar = 0.0f;
                        }
                        pCell.setM_Area(ar);
                    }
                }
                pMatr.setGrFunction(m_pGrFun);
                ArrayList<Float> volser;
                ArrayList<Float> agelims;
                int netti;
                GMParArray pVols = null;
                pVols = m_pExperiment.m_plVolSers.getParameter(pMatr.m_wID);
                volser = pVols.m_Vals;
                GMParArray pLages = null;
                pLages = m_pExperiment.m_plAgeLims.getParameter(pMatr.m_wID);
                agelims = pLages.m_Vals;
                netti = pLages.m_nSize;
                GMParArray pparCoef;
                ArrayList<Float> grc;
                pparCoef = m_pExperiment.m_plGrCoeff.getParameter(pMatr.m_wID);
                grc = pparCoef.m_Vals;
                if (pparCoef.m_nSize == 5) {
                    m_pGrFun.setCoeffEx(grc.get(0), grc.get(1), grc.get(2), grc.get(3), grc.get(4));
                } else {
                    m_pGrFun.setCoeff(grc.get(0), grc.get(1), grc.get(2));
                }
                ArrayList<Float> beta;
                beta = m_pExperiment.m_plBeta.getParameter(pMatr.m_wID).m_Vals;
                pMatr.calcTransitions(volser, agelims, netti, beta.get(0));
                ArrayList<Float> ygr;
                ygr = m_pExperiment.m_plYoungCoeff.getParameter(pMatr.m_wID).m_Vals;
                pMatr.m_FromBare = ygr.get(0);
                // Now setting fellings ranges simple approach first!
                float ageh, agel;
                // A little bit more complicated
                GMParArray parAr;
                // Just for Netti!
                float minageNetti, maxageNetti;
                float shiftNetti;
                shiftNetti = 0;
                parAr = m_pExperiment.m_plHarvestAge.getParameter(pMatr.m_wID);
                if (parAr != null) {
                    if (parAr.m_nSize == 6) {
                        // Just for!
                        minageNetti = parAr.m_Vals.get(0);
                        maxageNetti = parAr.m_Vals.get(1);
                        //shiftNetti = 0.15*minageNetti;
                        //if (shiftNetti<5)
                        //    shiftNetti = 5;
                        minageNetti -= shiftNetti;
                        maxageNetti -= shiftNetti;
                        float abage = (float) ((1.0 - parAr.m_Vals.get(5)) * parAr.m_Vals.get(0));
                        pMatr.setFellingsRegimes(minageNetti, maxageNetti, parAr.m_Vals.get(2),
                                parAr.m_Vals.get(3), parAr.m_Vals.get(4), abage);
                        //
                        //pMatr.setFellingsRegimes(parAr.m_Vals.get(0),parAr.m_Vals.get(1),parAr.m_Vals.get(2),
                        // parAr.m_Vals.get(3),parAr.m_Vals.get(4),abage);

                    } else {
                        ageh = parAr.m_Vals.get(0);
                        pMatr.setFellingsSimple(ageh);
                    }
                } else {
                    errorLogger.logEntry(errorFileParsing, "Could not locate "
                            + "fellings regime :-(\nMatrixID: " + cr + " " + co + " " + cst
                            + " " + csp + "\nUse age 50");
                    pMatr.setFellingsSimple(50.0f);
                }
                // And of simple approach
                // Now setting Thinnings ranges simple approach first!
                agel = m_pExperiment.m_plThinRange.getParameterValue(pMatr.m_wID, 0);
                ageh = m_pExperiment.m_plThinRange.getParameterValue(pMatr.m_wID, 1);
                // Just for !
                agel -= shiftNetti;
                if (agel < 5.) {
                    agel = 5.0f;
                }
                ageh -= shiftNetti;
                // End for !
                pMatr.setThinningsSimple(agel, ageh);
                ageh = m_pExperiment.m_plRegrowCoeff.getParameterValue(pMatr.m_wID, 0);
                pMatr.m_RegrGamma = ageh;
                ageh = m_pExperiment.m_plThHistory.getParameterValue(pMatr.m_wID, 0);
                // Scaling if needed! GertJan
                if (m_scaleAreas != 1.0) {
                    pMatr.scaleArea(m_scaleAreas);
                }
                pMatr.setThinHistory(ageh);
                // And of simple approach
                m_pExperiment.addTable(pMatr);
                m_pCurMatrix = pMatr;
                m_nMatrNum += 1;
            }
            //m_TotalArea = m_pExperiment.getArea(0,0,0,0);
            //m_TotalVilume = m_pExperiment.getValue(0,0,0,0);
            eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
            reader.close();
            return m_pCurMatrix;
        } catch (GMParLocator.GMParLocatorException ex) {
            System.err.println(ex);
        }
        return null;
    }
    
    /**
     * @deprecated 
     * Loads extra information from file.
     * @param fileName file name with or without file extension (.eif)
     * @param path path to file
     * @return step or -1 if unsuccessful
     */
    public int loadExtraInformation(String fileName, String path) {
        if (!fileName.endsWith(".eif")) fileName = fileName.concat(".eif");
        try {
            int step;
            try (DataInputStream dis = new DataInputStream(new FileInputStream(path.concat(fileName)))) {
                int length=dis.readInt();
                byte[] data=new byte[length];
                dis.readFully(data);
                String name = new String(data,"UTF-8");
                int id = dis.readInt();
                m_pExperiment = new GMEfiscen(name, id);
                m_pExperiment.m_nBaseYear = dis.readInt();
                step = dis.readInt();
                m_pExperiment.m_nStep = dis.readInt();
                m_pExperiment.m_bIsStart = dis.readBoolean();
                m_pExperiment.m_FelInt = dis.readFloat();
                m_pExperiment.m_ThinInt = dis.readFloat();
                m_scaleAreas = dis.readFloat();
                m_pExperiment.historyUpdateCounter = dis.readInt();
                int size = dis.readInt();
                for (int j = 0; j < size; j++) {
                    Long lid = dis.readLong();
                    int nid = dis.readInt();
                    length=dis.readInt();
                    data=new byte[length];
                    dis.readFully(data);
                    name = new String(data,"UTF-8");
                    GMCollection pReg = new GMCollection(name, nid); // region
                    pReg.m_lISOID = lid;
                    m_pExperiment.addCollection(m_pExperiment.getRegions(), pReg);
                }
                size = dis.readInt();
                for (int j = 0; j < size; j++) {
                    Long lid = dis.readLong();
                    int nid = dis.readInt();
                    length=dis.readInt();
                    data=new byte[length];
                    dis.readFully(data);
                    name = new String(data,"UTF-8");
                    GMCollection pOwn = new GMCollection(name, nid); // owner
                    pOwn.m_lISOID = lid;
                    m_pExperiment.addCollection(m_pExperiment.getOwners(), pOwn);
                }
                size = dis.readInt();
                for (int j = 0; j < size; j++) {
                    Long lid = dis.readLong();
                    int nid = dis.readInt();
                    length=dis.readInt();
                    data=new byte[length];
                    dis.readFully(data);
                    name = new String(data,"UTF-8");
                    GMCollection pSit = new GMCollection(name, nid); // site
                    pSit.m_lISOID = lid;
                    m_pExperiment.addCollection(m_pExperiment.getSites(), pSit);
                }
                size = dis.readInt();
                for (int j = 0; j < size; j++) {
                    Long lid = dis.readLong();
                    int nid = dis.readInt();
                    length=dis.readInt();
                    data=new byte[length];
                    dis.readFully(data);
                    name = new String(data,"UTF-8");
                    GMCollection pSpe = new GMCollection(name, nid); // species
                    pSpe.m_lISOID = lid;
                    m_pExperiment.addCollection(m_pExperiment.getSpecies(), pSpe);
                }
                for (long region = 0; region < m_pExperiment.m_mRegions.size(); region++) {
                    for (long owner = 0; owner < m_pExperiment.m_mOwners.size(); owner++) {
                        for (long site = 0; site < m_pExperiment.m_mSites.size(); site++) {
                            for (long species = 0; species < m_pExperiment.m_mSpecies.size(); species++) {
                                long key = (region << 24) + (owner << 16) + (site << 8) + species;
                                matrixIDs.add(key);
                            }
                        }
                    }
                }
                int nHowMany = dis.readInt();
                ComArFlt<Float> comAr;
                ComArFlt<ArrayList<Float>> comArAr;
                for (int i = 0; i < nHowMany; i++) {
                    int cr = dis.readInt();
                    int co = dis.readInt();
                    int cst = dis.readInt();
                    int csp = dis.readInt();
                    long key = (cr << 24) + (co << 16) + (cst << 8) + csp;
                    if (cr != 0 && co != 0 && cst != 0 && csp != 0)
                        if (!matrixIDs.contains(key))
                            matrixIDs.add(key);
                    
                    GMMatrixInit Mi = new GMMatrixInit();
                    Mi.setMi_id(key);
                    Mi.setMi_xb(dis.readFloat());
                    Mi.setMi_xt(dis.readFloat());
                    Mi.setMi_xs(dis.readFloat());
                    Mi.setMi_yb(dis.readFloat());
                    Mi.setMi_yt(dis.readFloat());
                    Mi.setMi_ys(dis.readFloat());
                    
                    int numcellsX = dis.readInt();
                    int nv = dis.readInt();
                    
                    GMMatrix pTable = new GMMatrix(numcellsX, nv);
                    pTable.initRegular(Mi);
                   
                    int nHowManyCell = dis.readInt();
                    
                    for(int j=0;j<nHowManyCell;j++) {
                        GMCell pCell = new GMCell();
                        
                        pCell.m_wX = dis.readInt();
                        pCell.m_wY = dis.readInt();
                        pCell.m_wID = dis.readLong();
                        pCell.setM_Xmin(dis.readFloat());
                        pCell.setM_Xmax(dis.readFloat());
                        pCell.setM_Xval(dis.readFloat());
                        pCell.setM_Ymin(dis.readFloat());
                        pCell.setM_Ymax(dis.readFloat());
                        pCell.setM_Yval(dis.readFloat());
                        // Mefique stuff - for a while!
                        pCell.setM_ThArea(dis.readFloat());
                        pCell.setM_ThRem(dis.readFloat());
                        pCell.setM_FelArea(dis.readFloat());
                        pCell.setM_FelRem(dis.readFloat());
                        // Bioenergy purposes - slash keeping
                        pCell.setM_ThSlash(dis.readFloat());
                        pCell.setM_FelSlash(dis.readFloat());
                        // Natural mortality stuff
                        pCell.setM_NatMrt(dis.readFloat());
                        pCell.setM_DWood(dis.readFloat());
                        // Version 3.2! MJ - natural disturbances!
                        pCell.setM_FireReplSus(dis.readFloat());
                        pCell.setM_FireNonReplSus(dis.readFloat());
                        pCell.setM_WindReplSus(dis.readFloat());
                        pCell.setM_WindNonReplSus(dis.readFloat());
                        pCell.setM_InsReplSus(dis.readFloat());
                        pCell.setM_InsNonReplSus(dis.readFloat());
                        
                        pCell.setM_Area(dis.readFloat());
                        pCell.setM_ThinArea(dis.readFloat());
                        pCell.setM_MoveAsThin(dis.readFloat());
                        
                        pCell.setM_MoveByX(dis.readFloat());
                        pCell.setM_MoveByY(dis.readFloat());
                        pCell.setM_MoveByXY(dis.readFloat());
                        pCell.setM_MoveByXOrg(dis.readFloat());
                        pCell.setM_MoveByYOrg(dis.readFloat());
                        pCell.setM_MoveByXYOrg(dis.readFloat());
                        pCell.setM_Move(dis.readFloat());
                        pCell.setM_MoveAway(dis.readFloat());
                        
                        pCell.setM_FellingsShare(dis.readFloat());
                        pCell.setM_ThinShare(dis.readFloat());
                        pCell.setM_Income(dis.readFloat());
                        pCell.setM_bThinned(dis.readBoolean());
                        
                        pTable.m_Cells.add(pCell);
                    }
                  
                    pTable.m_DeadWood = dis.readFloat();
                    pTable.m_BareArea = dis.readFloat();
                    pTable.m_FromBare = dis.readFloat();
                    pTable.m_RegrGamma = dis.readFloat();
                    m_pExperiment.m_BareFund.addArea(key, dis.readDouble());
                    m_pExperiment.grsprev.put(key, dis.readFloat());
                    
                    ComFltPipe m_fpDwPipe = pTable.getM_fpDwPipe();
                    for (int ii = 0; ii < m_fpDwPipe.m_nSize; ii++) {
                        ComFltPipeElement m_pData = m_fpDwPipe.getElement(ii);
                        m_pData.setCfp_nind(dis.readInt());
                        m_pData.setCfp_value(dis.readFloat());
                        m_pData.setCfp_threm(dis.readFloat());
                        m_pData.setCfp_felrem(dis.readFloat());
                        m_pData.setCfp_uplim(dis.readFloat());
                    }
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafGrStock.put(key, comAr);
                   
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafArea.put(key, comAr);
                   
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafAfforFund.put(key, comAr);
                   
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafBareArea.put(key, comAr);
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafBiomass.put(key, comAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafCStem.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafCLeaves.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafCBranches.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafCCRoots.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafCFRoots.put(key, comArAr);
                   
                    // Areas and stocks distribution
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafAreas.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafStocks.put(key, comArAr);
                   
                    // Mefique stuff!
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafMfqThAreas.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafMfqThRems.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafMfqFelAreas.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafMfqFelRems.put(key, comArAr);
                    // End Mefique!
                    
                    // Bioenergy stuff
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafBeThSlash.put(key, comArAr);
                   
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafBeFelSlash.put(key, comArAr);
                     
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafNatMortDistr.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafDeadWoodDistr.put(key, comArAr);
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafIncrement.put(key, comAr);
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafAvrIncrement.put(key, comAr);
                   
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafDeadWood.put(key, comAr);
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafNatMort.put(key, comAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafThRsd.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafFelRsd.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafThRsdRem.put(key, comArAr);
                    
                    comArAr = readFlValue(dis);
                    m_pExperiment.m_mafFelRsdRem.put(key, comArAr);
                   
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafThinnings.put(key, comAr);
                    
                    comAr = readFlValue(dis);
                    m_pExperiment.m_mafFellings.put(key, comAr);
                    
                    comAr = readDblValue(dis);
                    m_pExperiment.m_mafPotentialFellingsArea.put(key, comAr);
                    
                    comAr = readDblValue(dis);
                    m_pExperiment.m_mafPotentialFellingsVolume.put(key, comAr);
                    
                    m_pExperiment.m_mTables.put(key, pTable);
                }
                loadSoilsSession(dis);
                loadBioParametersSession(dis);
                loadParametersSession(dis);
                m_pExperiment.m_afStock = readFlValue(dis);
                m_pExperiment.m_afCarbon = readFlValue(dis);
                m_pExperiment.m_afStem = readDblValue(dis);
                m_pExperiment.m_afBranches = readDblValue(dis);
                m_pExperiment.m_afLeaves = readDblValue(dis);
                m_pExperiment.m_afCroots = readDblValue(dis);
                m_pExperiment.m_afFroots = readDblValue(dis);
                m_pExperiment.m_afThinVolume = readFlValue(dis);
                m_pExperiment.m_afFellVolume = readFlValue(dis);
            }
            return step;
        } catch (IOException ex) {
            return -1;
        }
    }
    
    /**
     * @deprecated 
     * Creates GMParlocators for parameters from DataInputStream and stores them to current 
     * experiment collection.
     * @param dis datainputstream for parameters
     * @return true
     */
    private boolean loadParametersSession(DataInputStream dis) {
            m_pExperiment.m_plAgeNum = loadLocatorParameters(dis);
            m_pExperiment.m_plAgeClasses = loadLocatorParameters(dis);
            m_pExperiment.m_plVolNum = loadLocatorParameters(dis);
            m_pExperiment.m_plVolClasses = loadLocatorParameters(dis);
            m_pExperiment.m_plGrCoeff = loadLocatorParameters(dis);
            m_pExperiment.m_plYoungCoeff = loadLocatorParameters(dis);
            m_pExperiment.m_plRegrowCoeff = loadLocatorParameters(dis);
            m_pExperiment.m_plHarvestAge = loadLocatorParameters(dis);
            m_pExperiment.m_plThinRange = loadLocatorParameters(dis);
            m_pExperiment.m_plBeta = loadLocatorParameters(dis);
            m_pExperiment.m_plVolSers = loadLocatorParameters(dis);
            m_pExperiment.m_plMortRateXvals = loadLocatorParameters(dis);
            m_pExperiment.m_plMortRate = loadLocatorParameters(dis);
            m_pExperiment.m_plDeadWoodDrate = loadLocatorParameters(dis);
            m_pExperiment.m_plThHistory = loadLocatorParameters(dis);

            return true;
    }
    
    /**
     * Creates GMParlocator from datainputstream.
     * @param dis 
     * @return new GMParlocator
     */
    private GMParLocator loadLocatorParameters(DataInputStream dis) {
        try {
            GMParLocator locator = new GMParLocator();
            int l_size = dis.readInt();
            for (int ii = 0; ii < l_size; ii++) {
                GMParArray pPar = new GMParArray();
                pPar.m_uRegion = dis.readInt();
                pPar.m_uOwner = dis.readInt();
                pPar.m_uSite = dis.readInt();
                pPar.m_uSpecies = dis.readInt();
                int size = dis.readInt();
                if (size != -1) {
                    for (int j = 0; j < size; j++) {
                        float ff = dis.readFloat();
                        pPar.m_Vals.add(ff);
                    }
                    pPar.m_nSize = size;
                }
                locator.addParameter(pPar);
            }
            return locator;
        } catch (IOException ex) {
            return null;
        }
    }
    
    /**
     * @deprecated 
     * Creates GMParlocators for parameters from DataInputStream and stores them to current 
     * experiment collection.
     * @param dis datainputstream for bioparameters
     * @return true
     */
    private boolean loadBioParametersSession(DataInputStream dis) {
            m_pExperiment.m_plCcont = loadLocatorParameters(dis);
            m_pExperiment.m_plWoodDens = loadLocatorParameters(dis);
            m_pExperiment.m_plCompXvals = loadLocatorParameters(dis);
            m_pExperiment.m_plStemShare = loadLocatorParameters(dis);
            m_pExperiment.m_plBranchShare = loadLocatorParameters(dis);
            m_pExperiment.m_plCrootsShare = loadLocatorParameters(dis);
            m_pExperiment.m_plFrootsShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLeavesShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLtrCompXvals = loadLocatorParameters(dis);
            m_pExperiment.m_plCroots2CWL = loadLocatorParameters(dis);
            
            m_pExperiment.m_plLtrStemShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLtrBranchShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLtrCrootsShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLtrFrootsShare = loadLocatorParameters(dis);
            m_pExperiment.m_plLtrLeavesShare = loadLocatorParameters(dis);
  
            return true;
    }
    
    /**
     * Loads shares from DataInputStream, parses them and stores them to current
     * experiment collection.
     * @param cr region to store shares
     * @param co owner to store shares
     * @param cst site to store shares
     * @param csp species to store shares
     * @param dis DataInputStream for shares.
     * @return true if successful, false in case of error
     */
    private boolean loadShares(int cr, int co, int cst, int csp, DataInputStream dis) {
        try {
            GMParArray pPar = new GMParArray();
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            //pPar.m_sName = "stem share";
            int size = dis.readInt();
            for (int j = 0; j < size; j++)
                pPar.m_Vals.add(dis.readFloat());
            pPar.m_nSize = size;
            m_pExperiment.m_plLtrStemShare.addParameter(pPar);
            
            pPar = new GMParArray();
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            size = dis.readInt();
            for (int j = 0; j < size; j++)
                pPar.m_Vals.add(dis.readFloat());
            pPar.m_nSize = size;
            //pPar.m_sName = "branch share";
            m_pExperiment.m_plLtrBranchShare.addParameter(pPar);
            
            pPar = new GMParArray();
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            size = dis.readInt();
            for (int j = 0; j < size; j++)
                pPar.m_Vals.add(dis.readFloat());
            pPar.m_nSize = size;
            //pPar.m_sName = "croot share";
            m_pExperiment.m_plLtrCrootsShare.addParameter(pPar);
            
            pPar = new GMParArray();
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            size = dis.readInt();
            for (int j = 0; j < size; j++)
                pPar.m_Vals.add(dis.readFloat());
            pPar.m_nSize = size;
            //pPar.m_sName = "froot share";
            m_pExperiment.m_plLtrFrootsShare.addParameter(pPar);
            
            pPar = new GMParArray();
            pPar.m_uRegion = cr;
            pPar.m_uOwner = co;
            pPar.m_uSite = cst;
            pPar.m_uSpecies = csp;
            //pPar.m_sName = "leaves share";
            size = dis.readInt();
            for (int j = 0; j < size; j++)
                pPar.m_Vals.add(dis.readFloat());
            pPar.m_nSize = size;
            m_pExperiment.m_plLtrLeavesShare.addParameter(pPar);
            
            return true;
        } catch (IOException ex) {
            System.err.println("Error in loading share ioparameters");
            return false;
        }
    }
    
    /**
     * @deprecated 
     * Loads soils from DataInputStream, parses them and stores them to current
     * experiment collection.
     * @param dis DataInputStream for soils
     * @return false if reading was unsuccessful, true otherwise
     */
    private boolean loadSoilsSession(DataInputStream dis) {
        try {
            
            int size = dis.readInt();
            
            for (int i = 0; i < size; i++) {
                long key = dis.readLong();

                double si_cwl, si_fwl, si_nwl, si_sol, si_cel, si_lig, si_hm1, si_hm2,
                            si_acwl, si_afwl, si_anwl, si_ksol, si_kcel, si_klig, si_khm1, si_khm2,
                            si_psol, si_pcel, si_plig, si_phum, si_cw2cel, si_cw2sol, si_fw2cel, si_fw2sol, si_nw2cel, si_nw2sol,
                            si_clhm1, si_clhm2;
                si_cwl = dis.readDouble();
                si_fwl = dis.readDouble();
                si_nwl = dis.readDouble();
                si_sol = dis.readDouble();
                si_cel = dis.readDouble();
                si_lig = dis.readDouble();
                si_hm1 = dis.readDouble();
                si_hm2 = dis.readDouble();
                si_acwl = dis.readDouble();
                si_afwl = dis.readDouble();
                si_anwl = dis.readDouble();
                si_ksol = dis.readDouble();
                si_kcel = dis.readDouble();
                si_klig = dis.readDouble();
                si_khm1 = dis.readDouble();
                si_khm2 = dis.readDouble();
                si_psol = dis.readDouble();
                si_pcel = dis.readDouble();
                si_plig = dis.readDouble();
                si_phum = dis.readDouble();
                si_cw2cel = dis.readDouble();
                si_cw2sol = dis.readDouble();
                si_fw2cel = dis.readDouble();
                si_fw2sol = dis.readDouble();
                si_nw2cel = dis.readDouble();
                si_nw2sol = dis.readDouble();
                si_clhm1 = dis.readDouble();
                si_clhm2 = dis.readDouble();

                GMSoil pSol = new GMSoil(si_cwl, si_fwl, si_nwl, si_sol, si_cel, si_lig, si_hm1, si_hm2,
                            si_acwl, si_afwl, si_anwl, si_ksol, si_kcel, si_klig, si_khm1, si_khm2,
                            si_psol, si_pcel, si_plig, si_phum, si_cw2cel, si_cw2sol, si_fw2cel, si_fw2sol, si_nw2cel, si_nw2sol,
                            si_clhm1, si_clhm2);
                pSol.m_wID = key;
                
                pSol.m_CwBasket = dis.readDouble();
                pSol.m_FwBasket = dis.readDouble();
                pSol.m_NwBasket = dis.readDouble();
                pSol.setInOut(dis.readDouble());
                m_pExperiment.addSoil(pSol);
            }
            
            ComArFlt comAr;
            
            for (long key : m_pExperiment.m_mSoils.keySet()) {
                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilCwl.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilFwl.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilNwl.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilCel.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilSol.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilLig.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilHm1.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilHm2.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafSoilClost.put(key, comAr);

                comAr = readFlValue(dis);
                m_pExperiment.m_mafCSoil.put(key, comAr);

                comAr = readDblValue(dis);
                m_pExperiment.m_mafSoilCwlIn.put(key, comAr);

                comAr = readDblValue(dis);
                m_pExperiment.m_mafSoilFwlIn.put(key, comAr);

                comAr = readDblValue(dis);
                m_pExperiment.m_mafSoilNwlIn.put(key, comAr);

                comAr = readDblValue(dis);
                m_pExperiment.m_mafSoilInOut.put(key, comAr);             
                
            }
            
            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilCwl = comAr;
 
            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilFwl = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilNwl = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilCel = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilSol = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilLig = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilHm1 = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilHm2 = comAr;

            comAr = readFlValue(dis);
            m_pExperiment.m_afSoilClost = comAr;
            return true;
        } catch (IOException ex) {
            System.err.println("Error in loading soils");
            return false;
        }
    }
        
    /**
     * Reads float values from DataInputStream to container.
     * @param dis DataInputStream for floats
     * @return Object containing the read floats
     */
    private ComArFlt readFlValue(DataInputStream dis) {
        ComArFlt comAr = new ComArFlt();
        try {
            // size of comAr
            int sizeComAr = dis.readInt();
            for (int p = 0; p < sizeComAr; p++) {
                // size of array list or float (size = -1)
                int size = dis.readInt();
                if (size > -1) {
                    ArrayList<Float> list = new ArrayList<>();
                    for (int i = 0; i < size; i++)
                        list.add(dis.readFloat());
                    comAr.addData(list);
                } else
                    comAr.addData(dis.readFloat());
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(InputLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return comAr;
    }
    
    /**
     * Reads float values from DataInputStream to container, also prints floats.
     * @param dis DataInputStream for floats
     * @return container of floats
     */
    private ComArFlt readFlValuee(DataInputStream dis) {
        ComArFlt comAr = new ComArFlt();
        try {
            // size of comAr
            int sizeComAr = dis.readInt();
            for (int p = 0; p < sizeComAr; p++) {
                // size of array list or float (size = -1)
                int size = dis.readInt();
                if (size > -1) {
                    ArrayList<Float> list = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        float ff = dis.readFloat();
                        list.add(ff);
                        
                    System.out.println(ff);
                    }
                    comAr.addData(list);
                } else {
                        float ff = dis.readFloat();
                    comAr.addData(ff);
                    System.out.println(ff);
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(InputLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return comAr;
    }
    
    /**
     * Reads double values from DataInputStream to container.
     * @param dis DataInputSream for doubles
     * @return Object containing read doubles
     */
    private ComArFlt readDblValue(DataInputStream dis) {
        ComArFlt comAr = new ComArFlt();
        try {
            // size of comAr
            int sizeComAr = dis.readInt();
            for (int p = 0; p < sizeComAr; p++) {
                // size of array list or float (size = -1)
                int size = dis.readInt();
                if (size > -1) {
                    ArrayList<Double> list = new ArrayList<>();
                    for (int i = 0; i < size; i++)
                        list.add(dis.readDouble());
                    comAr.addData(list);
                } else
                    comAr.addData(dis.readDouble());
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(InputLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return comAr;
    }
    
    /**
     * loads file containing thinning range changes over the simulation.
     * Experiment collection must be initialized.
     * @param sFileIn
     * @return false if reading was unsuccessful, true otherwise
     * @throws EFISCENFileNotFoundException 
     */
    public boolean loadThinningChange(String sFileIn) 
            throws EFISCENFileNotFoundException {
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        //m_cstClimName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(nHowMany*2 + 1);

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", nHowMany*2 + 1, reader,
                    errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(1,"m_plThinAge");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(i*2+1)); // (i+1)
                pPar.m_Vals.add(pData.get(i*2+2));
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plThinAge.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }
    
    /**
     * loads file containing felling changes over the simulation. 
     * Experiment collection must be initialized.
     * @param sFileIn input file path
     * @return false if unscuccessful, otherwise true 
     * @throws EFISCENFileNotFoundException if file not found
     */
    public boolean loadFellingChange(String sFileIn) 
            throws EFISCENFileNotFoundException {
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        //GMParLocator pplRet = new GMParLocator();
        String strLine;
        int nHowMany;
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        //m_cstClimName.setWindowText(strLine);
        strLine = reader.readLineSimple();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        ArrayList<Integer> pIDs = new ArrayList<>(4 * nHowMany);
        ArrayList<Float> pData = new ArrayList<>(nHowMany + 1);

        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        pIDs = StringParser.getIntArFromStringEx(strLine, ",", 4 * nHowMany, reader,
                errorLogger);
        strLine = reader.readLineSimple();
        strLine = reader.readLineSimple();
        while (strLine != null) {
            GMEfiscenario pSc = new GMEfiscenario();
            pData = StringParser.getFlArFromStringEx(strLine, ",", nHowMany + 1, reader,
                    errorLogger);
            //sscanf(strLine,"%d,%f,%f",&(pSc->es_nStep),pData);
            pSc.setEs_nStep(pData.get(0).intValue());
            for (int i = 0; i < nHowMany; i++) {
                GMParArray pPar = new GMParArray(1,"m_plFellAge");
                int ind = 4 * i;
                pPar.m_uRegion = (int) pIDs.get(ind);
                pPar.m_uOwner = (int) pIDs.get(ind + 1);
                pPar.m_uSite = (int) pIDs.get(ind + 2);
                pPar.m_uSpecies = (int) pIDs.get(ind + 3);
                pPar.m_Vals.add(pData.get(i + 1)); // (i+1)
                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plFellAge.add(pSc);
            strLine = reader.readLineSimple();
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Load species change scenario to current scenario collection 
     * (Rupport's version) NOTE: one source matrix
     * - few destinations! Data in pairs: destination::ratio - will keep in one
     * pararray! GMEfiscen must be initialized.
     * @param sFileIn inputfile path
     * @return true if loading was succesful else false
     * @throws EFISCENFileNotFoundException if inputfile not found
     */
    public boolean loadSpecChange(String sFileIn) throws EFISCENFileNotFoundException {
        if (m_pExperiment == null) {
            return false;
        }
        eventLogger.logEntry(eventFileLoadStart, "filename " + sFileIn);
        LineReader reader = new LineReader(new File(sFileIn), errorLogger);
        String strLine;
        String split[];
        int nHowMany, nSteps;
        strLine = reader.readLine();
        try {
            nSteps = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        strLine = reader.readLine();
        try {
            nHowMany = NumberParser.convertInt(strLine, reader, errorLogger);
        } catch (NumberFormatException nfe) {
            reportParsingError(reader);

            return false;
        }
        for (int js = 0; js < nSteps; js++) {
            GMEfiscenario pSc = new GMEfiscenario();
            int nst;
            strLine = reader.readLine();
            try {
                nst = NumberParser.convertInt(strLine, reader, errorLogger);
            } catch (NumberFormatException nfe) {
                reportParsingError(reader);

                return false;
            }
            pSc.setEs_nStep(nst);

            //pData = new ArrayList<Float>(2*nv*na);
            //pData = StringParser.getFlArFromStringEx(strLine,",",6*nHowMany+1);
            pSc.setEs_nStep(nst);
            for (int i = 0; i < nHowMany; i++) {
                strLine = reader.readLine();
                int cr, co, cst, csp;
                int ndest;
                split = strLine.split("\\s+");
                if (split == null || split.length < 5) {
                    break;
                }
                try {
                    cr = NumberParser.convertInt(split[0], reader, errorLogger);
                    co = NumberParser.convertInt(split[1], reader, errorLogger);
                    cst = NumberParser.convertInt(split[2], reader, errorLogger);
                    csp = NumberParser.convertInt(split[3], reader, errorLogger);
                    ndest = NumberParser.convertInt(split[4], reader, errorLogger);
                } catch (NumberFormatException nfe) {
                    reportParsingError(reader);

                    return false;
                }

                //int na,nv;

                long ulmID, ulID;
                ulID = (long) cr;
                ulmID = ulID << 24;
                ulID = (long) co;
                ulmID = ulmID + (ulID << 16);
                ulID = (long) cst;
                ulmID = ulmID + (ulID << 8);
                ulID = (long) csp;
                ulmID = ulmID + ulID;
                ulID = ulmID;

                //GMMatrix pTable = m_pExperiment.getTable(ulID);

                //na = (int) m_pExperiment.m_plAgeNum.getParameterValue(ulID,0);
                //nv = (int) m_pExperiment.m_plVolNum.getParameterValue(ulID,0);
                int nMsize = 2 * ndest;
                ArrayList<Float> pData = new ArrayList<>(nMsize);
                GMParArray pPar = new GMParArray(nMsize,"m_plSpecCh");
                pPar.m_uRegion = cr;
                pPar.m_uOwner = co;
                pPar.m_uSite = cst;
                pPar.m_uSpecies = csp;

                // Reading source species and ratios
                strLine = reader.readLine();
                pData = StringParser.getFlArFromStringEx(strLine, "\\s+", nMsize,
                        reader, errorLogger);
                pPar.m_Vals = pData;

                pSc.getEs_paData().addParameter(pPar);
            }
            m_Scenario.m_plSpecCh.add(pSc);
        }
        eventLogger.logEntry(eventFileLoadEnd, "filename " + sFileIn);
        reader.close();
        return true;
    }

    /**
     * Get the number of matrixes.
     * @return number of matrixes
     */
    public int getM_nMatrNum() {
        return m_nMatrNum;
    }

    /**
     * Get the scale of areas.
     * @return scale of areas
     */
    public float getM_scaleAreas() {
        return m_scaleAreas;
    }

    /**
     * Get the scenario.
     * @return current scenario
     */
    public GMScenario getM_Scenario() {
        return m_Scenario;
    }

    /**
     * Get the experiment.
     * @return current experiment
     */
    public GMEfiscen getM_pExperiment() {
        return m_pExperiment;
    }

    /**
     * Get the matrix.
     * @return current matrix
     */
    public GMMatrix getM_pCurMatrix() {
        return m_pCurMatrix;
    }

    /**
     * Get the GrFunction.
     * @return current grfunction
     */
    public GMGrFunDefault getM_pGrFun() {
        return m_pGrFun;
    }

    /**
     * Set the scale area.
     * @param m_scaleAreas
     */
    public void setM_scaleAreas(float m_scaleAreas) {
        this.m_scaleAreas = m_scaleAreas;
    }

    /**
     * Get experiment file path
     * @return experiment file path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Get volclassfile name
     * @return volclassfile name
     */
    public String getVolClassFile() {
        return volClassFile;
    }

    /**
     * Getter for bioparameters filename
     * @return  Bioparameters filename
     */
    public String getBioparametersFile() {
        return bioparametersFile;
    }
    
    /**
     * Getter for errorlog name
     * @return name of errorlog 
     */
    public String getErrorLogName(){
        return errorLogName;
    }
        
    /**
     * Getter for eventlog name
     * @return name of eventlog
     */
    public String getEventLogName(){
        return eventLogName;
    }
}
