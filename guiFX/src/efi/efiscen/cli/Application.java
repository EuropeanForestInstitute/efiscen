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
package efi.efiscen.cli;

import efi.efiscen.database.DriverType;
import efi.efiscen.db.DBException;
import efi.efiscen.db.DatabaseExporter;
import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMScenario;
import efi.efiscen.gm.GMSimulation;
import efi.efiscen.io.EFISCENException;
import efi.efiscen.io.EFISCENFileNotFoundException;
import efi.efiscen.io.FileSaver;
import efi.efiscen.io.InputLoader;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Command line interface for efiscen. Parameters:
 * <p>1. steps - Number of steps to run the simulation.
 * <br>2. thinning - Intensity of the thinning applied during the simulation.
 * <br>3. felling - Intensity of the felling applied during the simulation.
 * <br>4. experiment - File path to an experiment file. File name must be
 * included.
 * <br>5. scaling - Scaling factor that is applied globally to areas of all the
 * matrices.
 * <br>6. scenario - File path to a scenario file. The file name must be
 * included and the name must end with “.scn”.
 * <br>7. outputfile - File path to where the output files will be saved. Path must
 * include a file name.
 * <br>8. databaseaddress - Address to a database where outputs will be saved.
 * <br>9. username - User name used to log into the database.
 * <br>10. password - Password used to log into the database.
 * <br>11. sid - Session id that will be present in all data entries saved into
 * a database.
 * <br>12. ciso - ISO country-code is used to identify that the output data
 * concerns a certain country.
 * <br>13. selected - File path to a text file containing definitions about
 * which outputs to save.
 * <br>The Path must include the file name and the name must end with “.txt”.
 * <br>14. pid - Project id.
 * <p>
 * When running EFISCEN and using database, parameters 6, 7, 13 are optional.
 * <br>When running EFISCEN and saving outputs as files, parameters 1-5 and 7
 * <br>are required and 6, 13 are optional.
 *
 *
 */
public class Application {

    private Map<String, String> arguments;
    private static String outputFilePath = null;
    private static String databaseAddress = null;
    private static String databaseName = null;
    private static int port = 3306;
    private static String experimentFilePath = null;
    private static String databaseUsername = null;
    private static String databasePassword = null;
    private static int numberOfSteps = -1 ; //Number of steps to run the simulation.
    private static int scenarioID=-1;
    private static int projectID = 0; //project ID default value
    private static int countryISO;
    private static double felling = -1;
    private static double thinning = -1;
    private static double scaling = -1;
    private static String selectedFilePath = null;
    private static String scenarioFilePath = null;
    private boolean argumentsAreOkay;
    private String errorMessage;
    
    /**
     * Creates new Application. Reads arguments.
     * @param cli_args 
     */
    public Application(String[] cli_args) {
        // PARSE ARGUMENTS
        if (!parseArguments(cli_args)) {
            System.out.println(errorMessage);
            printInfoMessage();
            return;
        }
        argumentsAreOkay = checkArguments();
    }

    /**
     * Runs efiscen. Runs if arguments given in constructor are okay, otherwise prints
     * error message and usage message.
     */
    public void run() {
        
        if(argumentsAreOkay){
            runEfiscen();
            System.out.println("Done!");
            return;
        }else{
            printInfoMessage();
            System.out.println(errorMessage);
            return;
        }
    } // run()

    /**
     * Runs EFISCEN. Outputs will be
     * saved into the file system or database depending on the arguments. Project ID is
     * set to 0 if is not provided.
     */
    public static void runEfiscen() {
        //load experiment and scenario
        System.out.println("simulation started, details are in log files!");
        PrintStream stdout_save = System.out;
        PrintStream stderr_save = System.err;
        CLIVerboseLogger cc = new CLIVerboseLogger();
        cc.start(); //start logger
        GMEfiscen m_pExperiment = null;
        GMScenario m_pScenario = null;
        AtomicInteger numErrors = new AtomicInteger(0);
        GMSimulation sim;
        InputLoader loader = new InputLoader();
        loader.setM_scaleAreas((float) scaling);
        DatabaseExporter saver = null;
        int simulationID = -1;
        Map<String,String> filenames = new HashMap<>();
        try {
            numErrors.set(0);
            m_pExperiment = loader.loadExperiment(experimentFilePath, numErrors, filenames);
            if (numErrors.get() > 0) {
                System.out.println(numErrors.get() + " errors were detected"
                        + " when loading experiment");
            }
        } catch (EFISCENFileNotFoundException ex) {
            System.err.println(ex.toString());
            return;
        } catch (EFISCENException ex) {
            System.err.println(ex.toString());
        }
        if (m_pExperiment == null) {
            return;
        }
        if (scenarioFilePath != null) {
            try {
                numErrors.set(0);
                m_pScenario = loader.loadScenario(scenarioFilePath, numErrors);
                if (numErrors.get() > 0) {
                    System.out.println(numErrors.get() + " errors were detected"
                            + " when loading scenario");
                }
            } catch (EFISCENFileNotFoundException ex) {
                System.err.println(ex.toString());
                return;
            } catch (Exception ex) {
                System.err.println("Scenario load failed");
            }
        } else {
            m_pScenario = new GMScenario();
        }
        String path = loader.getPath();
        m_pExperiment.m_FelInt = (float)felling;
        m_pExperiment.m_ThinInt = (float)thinning;
        sim = new GMSimulation(m_pExperiment, m_pScenario, 0);
        for (int i = 0; i < numberOfSteps; i++) {
            sim.onGo();
        }
        System.out.println("simulation done!");
        m_pExperiment = sim.getM_pExperiment();
        
        //save to database
        if(databaseAddress!=null){
            System.out.println("Saving to the DB....");

            DriverType driver = DriverType.Odbc;
            if (databaseAddress.contains("mysql:")) {
                databaseAddress = databaseAddress.replace("mysql:", "");
                driver = DriverType.MySql;
            } else if (databaseAddress.contains("postgresql:")) {
                databaseAddress = databaseAddress.replace("postgresql:", "");
                driver = DriverType.PostgreSQL;
            }

            try {
                saver = new DatabaseExporter(databaseAddress, databaseName, port,
                        driver, databaseUsername, databasePassword, m_pExperiment);
            } catch (efi.efiscen.database.DatabaseComponentsException ex) {
                System.err.println("Error when creating instance of DatabaseSaver.");
                System.err.println(ex);
            } catch (DBException ex) {
                System.err.println("Error when creating instance of DatabaseSaver.");
                System.err.println(ex);
            }
            if(saver!=null){
                if (selectedFilePath != null) {
                    try {
                        simulationID = saver.saveSelectedDatabase(scenarioID, countryISO, projectID, selectedFilePath,filenames.get("parameters"));
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                } else {
                    try {
                        simulationID = saver.save(scenarioID, countryISO, projectID, filenames.get("parameters"));
                    } catch (SQLException ex) {
                        System.err.println(ex);
                    }
                }
                if (simulationID == -1) {
                    System.err.println("Saving was unsuccesful!");
                }
            }
        }
        //save to files
        if (outputFilePath != null) {
            FileSaver fsaver = new FileSaver(m_pExperiment);
            int i = outputFilePath.indexOf(".");
            String ext = "";
            if (i != -1) {
                ext = outputFilePath.substring(i, outputFilePath.length());
                outputFilePath = outputFilePath.substring(0, i);
            }
            if (selectedFilePath == null) {
                if (!fsaver.saveAll(outputFilePath, ext)) {
                    System.err.println("Saving was unsuccesful!");
                }
            } else {
                fsaver.saveSelected(outputFilePath, selectedFilePath);
            }
        }
        
        System.out.println("END");  //signal logger to end listening
        cc.stopLogging();
        System.setErr(stderr_save);
        System.setOut(stdout_save);
        System.out.println("end logging");
        if(simulationID != -1){
            System.out.println("Simulation ID " + simulationID);
        }
        //cc.stopLogging();
        
        return;
    }
    
    /**
     * Parses arguments from provided String[].
     * Changes everything to lower case, removes spaces. Returns whether parsing
     * was successful or not.
     * @param cli_args Arguments for Application
     * @return False if provided arguments contained errors. True otherwise.
     */
    public boolean parseArguments(String[] cli_args) {
        arguments = new HashMap<>();

        for (String cli_arg : cli_args) {
            String[] tmp = cli_arg.split("=");
            if (tmp.length != 2) {
                errorMessage = "Check variable " + tmp[0];
                return false;
            } else if (tmp.length == 2) {
                tmp[0] = tmp[0].toLowerCase();
                arguments.put(tmp[0].trim(), tmp[1].trim());
            }
        }

        try {
            for (String command : arguments.keySet()) {
                switch (command) {
                    case "steps":
                        String sSteps = arguments.get(command);
                        int iSteps = Integer.parseInt(sSteps);
                        numberOfSteps = iSteps;
                        break;
                    case "thinning":
                        String sThin = arguments.get(command);
                        double dThin = Double.parseDouble(sThin);
                        thinning = dThin;
                        break;
                    case "felling":
                        String sFell = arguments.get(command);
                        double dFell = Double.parseDouble(sFell);
                        felling = dFell;
                        break;
                    case "experiment":
                        String sExpe = arguments.get(command).replace("\"", "");
                        experimentFilePath = sExpe;
                        break;
                    case "scaling":
                        String sScal = arguments.get(command);
                        double dScal = Double.parseDouble(sScal);
                        scaling = dScal;
                        break;
                    case "scenario":
                        String sScen = arguments.get(command).replace("\"", "");
                        scenarioFilePath = sScen;
                        break;
                    case "outputfile":
                        String sOutp = arguments.get(command).replace("\"", "");
                        outputFilePath = sOutp;
                        break;
                    case "server":
                        String sDat = arguments.get(command).replace("\"", "").toLowerCase();
                        databaseAddress = sDat;
                        break;
                    case "database":
                        databaseName = arguments.get(command).replace("\"", "").toLowerCase();
                        break;
                    case "port":
                        port = Integer.parseInt(arguments.get(command));
                        break;
                    case "username":
                        databaseUsername = arguments.get(command);
                        break;
                    case "password":
                        databasePassword = arguments.get(command);
                        break;
                    case "sid":
                        String sSid = arguments.get(command);
                        scenarioID = Integer.parseInt(sSid);
                        break;
                    case "ciso":
                        String sCis = arguments.get(command);
                        int iCis = Integer.parseInt(sCis);
                        countryISO = iCis;
                        break;
                    case "selected":
                        String sSel = arguments.get(command).replace("\"", "");
                        selectedFilePath = sSel;
                        break;
                    case "pid":
                        String sPid = arguments.get(command);
                        int iPid = Integer.parseInt(sPid);
                        projectID = iPid;
                }
            }
        } catch (NumberFormatException nfe) {
            errorMessage = "Arguments are incorrect";
            return false;
        }
        return true;
    }
    
    /**
     * Does some checks on whether arguments are valid.
     * @return True if arguments are okay, false if not.
     */
    public boolean checkArguments(){
        if (arguments.size() < 6) {
            errorMessage=("Incorrect number of arguments");
            return false;
        }
        if(outputFilePath == null && databaseAddress == null){
            errorMessage=("Output destination not specified");
            return false;
        }
        if(numberOfSteps<0){
            errorMessage="Number of steps not specified";
            return false;
        }
        if(thinning<0){
            errorMessage="Thinning factory not specified";
            return false;
        }
        if(felling<0){
            errorMessage="Felling factory not specified";
            return false;
        }
        if(scaling<0){
            errorMessage="Scaling factory not specified";
            return false;
        }
        if(experimentFilePath==null){
            errorMessage="Experiment file path not specified";
            return false;
        }
        //DATABASE USAGE
        if(databaseAddress != null){
            if(scenarioID<0){
                errorMessage="Scenario ID not specified";
                return false;
            }
            if(countryISO<0){
                errorMessage="Country ISO not specified";
                return false;
            }
        }
        return true;
    }
    
    /**
     * Prints tool information and usage instructions on the command line.
     */
    private static void printInfoMessage() {
        System.out.println("EFISCEN modelling tool v4.0");
        System.out.println("Usage: java EfiscenCLI steps=<steps> thinning=<thinning int.> "
                + "felling=<felling int.> experiment=<experiment file> [scenario=<scenario file>] "
                + "outputfile=<output file> scaling=<scale area> [selected=<selected outputs>] [pid=<project id>]");
        System.out.println("Usage when saving outputs into database:"
                + "java EfiscenCLI steps=<steps> thinning=<thinning int.> "
                + "felling=<felling int.> experiment=<experiment file> [scenario=<scenario file>] "
                + "output=<output file> scaling=<scale area>"
                + "databaseaddress=<database address> username=<username> "
                + "password=<password> sid=<SID> ciso=<CISO> "
                + "[selected=<selected outputs>] [pid=<project id>]");
        System.out.println("legend: <> - replace with a value");
        System.out.println("        [] - optional parameter that can be omitted");
    }
}
