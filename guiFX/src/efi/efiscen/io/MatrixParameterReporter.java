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

import efi.efiscen.gm.GMEfiscen;
import efi.efiscen.gm.GMMatrix;
import efi.efiscen.gm.GMParArray;
import efi.efiscen.gm.GMParLocator;
import java.util.logging.Level;

/**
 * Responsible for checking that all the matrices have all parameters.
 * If any are missing they will be reported in the log file.
 * 
 */
public class MatrixParameterReporter {
    
    private Logger logger;
    
    /**
     * Default constructor
     */
    MatrixParameterReporter() {
        logger = null;
    }
    
    /**
     * Sets error logger Logger object for the class that logs errors to a log file
     * @param logger Logger object that logs errors to a log file
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Checks if the experiment has missing matrices
     * @param efiscen observed experiment
     * @return false if experiment has missing matrices, true if there are no
     * missing matrices
     */
    public boolean checkMatrices(GMEfiscen efiscen) {
        boolean complete = true;
        for(Long r : efiscen.m_mRegions.keySet()) {
            for(Long o : efiscen.m_mOwners.keySet()) {
                for(Long st : efiscen.m_mSites.keySet()) {
                    for(Long sp : efiscen.m_mSpecies.keySet()) {
                        long id = (r << 24) + (o << 16) + (st << 8) + sp;
                        GMMatrix get = efiscen.m_mTables.get(id);
                        if(get==null) {
                            complete = false;
                            reportMissingMatrix(r,o,st,sp);
                        }
                    }
                } 
            }
        }
        return complete;
    }
    
    /**
     * Checks GMEfiscen object's parameters for errors. Checks for undefined matrices
     * and missing parameters and reports them.
     * @param efiscen observed experiment
     * @return false value if matrices have missing or undefined parameters,
     * true if parameters are correct
     */
    public boolean checkParameters(GMEfiscen efiscen) {
        GMParArray testVal = null;
        boolean complete = true;
        for(Long key : efiscen.m_mTables.keySet()) {
            try {
                GMMatrix matrix = efiscen.m_mTables.get(key);
                Integer regionID = matrix.getRegionID();
                if(!efiscen.m_mRegions.containsKey(regionID.longValue())) {
                    reportUndefinedMatrix(matrix);
                    complete = false;
                }
                Integer ownerID = matrix.getOwnerID();
                if(!efiscen.m_mOwners.containsKey(ownerID.longValue())) {
                    reportUndefinedMatrix(matrix);
                    complete = false;
                }
                Integer siteID = matrix.getSiteID();
                if(!efiscen.m_mSites.containsKey(siteID.longValue())) {
                    reportUndefinedMatrix(matrix);
                    complete = false;
                }
                Integer speciesID = matrix.getSpeciesID();
                if(!efiscen.m_mSpecies.containsKey(speciesID.longValue())) {
                    reportUndefinedMatrix(matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeClasses.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age class", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plVolClasses.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("volume class", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeLims.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age limit", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeNum.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age number", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plYoungCoeff.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("growth function coeff. yong", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plWoodDens.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("wood density", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plVolSers.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("volume series", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plThinRange.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("thinning range", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plThHistory.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("thinning history", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plStemShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("stem share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plRegrowCoeff.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("regrowth coeff.", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plMortRateXvals.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("mortality rate x values", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plMortRate.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("mortality rate", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrStemShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr stem share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrLeavesShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr leaves share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrFrootsShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr F roots share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrCrootsShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr C roots share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrCompXvals.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr Comp x values", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLtrBranchShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Ltr branch share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plBeta.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("plBeta", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeNum.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age number", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeLims.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age lims", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plAgeClasses.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("age classes", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plHarvestAge.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("harvest age", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plCcont.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("C cont", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plCroots2CWL.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("C roots 2 CWL", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plCrootsShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("C roots share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plBranchShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Branch share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plGrCoeff.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Growth coeff.", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plFrootsShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("F roots share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plLeavesShare.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Leaves share", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plDeadWoodDrate.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Dead wood rate", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plCompXvals.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Comp x vals", matrix);
                    complete = false;
                }
                
                testVal = efiscen.m_plVolNum.getParameter(key);
                if(testVal == null) {
                    reportMissingParameter("Volume number", matrix);
                    complete = false;
                }
            } catch (GMParLocator.GMParLocatorException ex) {
                System.err.println(ex);
            }
        }
        return complete;
    }
    
    /**
     * Report printing for a missing parameter. Sends error reports to System.err
     * and Logger.
     * @param parameterName the name of the observed parameter
     * @param matrix the name of the observed GMMatrix object
     */
    private void reportMissingParameter(String parameterName, GMMatrix matrix) {
        String desc = "region "+matrix.getRegionID()+
                    ", owner "+matrix.getOwnerID()+", site "+matrix.getSiteID()+
                    ", species "+matrix.getSpeciesID();
        System.err.println(desc);
        if(logger != null) {
            logger.logEntry("Parameter"+parameterName+"missing", desc);
        }
    }
    
    /**
     * Report printing for an undefined matrix. Sends error reports to System.err
     * and logger.
     * @param matrix the name of the observed GMMatrix object
     */
    private void reportUndefinedMatrix(GMMatrix matrix) {
        String desc = "region "+matrix.getRegionID()+
                    ", owner "+matrix.getOwnerID()+", site "+matrix.getSiteID()+
                    ", species "+matrix.getSpeciesID();
        //System.err.println(desc);
        if(logger != null) {
            logger.logEntry("Matrix contains undefined identifiers", desc);
        }
    }
    
    /**
     * Report printing for a missing matrix. Sends error reports to logger.
     * @param r the number of the region
     * @param o the number of the owner
     * @param st the number if the site
     * @param sp the number of the species
     */
    private void reportMissingMatrix(long r,long o,long st,long sp) {
        String desc = "region "+r+
                    ", owner "+o+", site "+st+
                    ", species "+sp;
        //System.err.println(desc);
        if(logger != null) {
            logger.logEntry("Matrix definition is missing", desc);
        }
    }
}
