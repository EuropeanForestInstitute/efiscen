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
package efi.efiscen.gm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Scenario realization. Keeps growth changes, soil climate, demands for
 * thinning and felling, aforestation, deforestation and tree species change.
 * 
 */
public class GMScenario implements Serializable {

    public String m_sName;
    public String climName; //climate scenario name
    public String manName; //management scenario name

    // Here we suppose to have pararray of two vars for soil - Tav and DI
    public ArrayList<GMEfiscenario> m_plSoilClim;
    // Same for increment rates - one variables now
    public ArrayList<GMEfiscenario> m_plForClim;
    // Pararrays for fellings and thinnings - both at same: fellings value is
    // first!
    public ArrayList<GMEfiscenario> m_plCuttings;
    // Pararray for aforestation
    public ArrayList<GMEfiscenario> m_plAfor;
    // Pararray for deforestation
    public ArrayList<GMEfiscenario> m_plDefor;
    // Pararray for Thin_finFellings properties: with three values for kind of
    // felllings (six in total) - stem - branches - leaves -- parts going away
    // as removals purpose to fill GMFELLINGS structure while fellings.
    public ArrayList<GMEfiscenario> m_plCutProps;
    // Species change (like Ruppert version)
    public ArrayList<GMEfiscenario> m_plSpecCh;
    // Thinning ages
    public ArrayList<GMEfiscenario> m_plThinAge;
    // Felling ages
    public ArrayList<GMEfiscenario> m_plFellAge;

    // Additional parlocators to keep ratios of fellings and thinnings
    public GMParLocator m_plCutRatios;
    public GMParLocator m_plClimAgeLims;

    // Current scenarios items keeping
    public GMEfiscenario m_pCurSoilClim;
    public GMEfiscenario m_pCurForClim;
    public GMEfiscenario m_pCurCuttings;
    public GMEfiscenario m_pCurAfor;
    public GMEfiscenario m_pCurDefor;
    public GMEfiscenario m_pCurCutProps;
    public GMEfiscenario m_pCurSpecCh;
    public GMEfiscenario m_pCurThinAge;
    public GMEfiscenario m_pCurFellAge;

    // Keeping number of steps for "circling"
    public int m_nStepsSoil;
    public int m_nStepsFor;
    public int m_nStepsCut;
    public int m_nStepsAfor;
    public int m_nStepsDefor;
    public int m_nStepsCutProps;
    public int m_nStepsSpecCh;
    public int m_nStepsThinAge;
    public int m_nStepsFellAge;
    public boolean isCircling = false;  // whether scenario has started circling
    public boolean isCirclingGUI = false;  // whether scenario has started circling (for GUI)
    public boolean allow_circling = false;  // for GUI allowing circling

    transient private Iterator m_posCuttings;
    transient private Iterator m_posForClim;
    transient private Iterator m_posSoilClim;
    transient private Iterator m_posAfor;
    transient private Iterator m_posDefor;
    transient private Iterator m_posCutProps;
    transient private Iterator m_posSpecCh;
    transient private Iterator m_posThinAge;
    transient private Iterator m_posFellAge;
    
    private boolean defaultScenario;

    /**
     * Default constructor.
     */
    public GMScenario () {
        m_nStepsSoil = 0;
        m_nStepsFor  = 0;
        m_nStepsCut  = 0;
        m_nStepsAfor = 0;
        m_nStepsDefor = 0;
        m_nStepsCutProps = 0;
        m_nStepsSpecCh = 0;
        m_nStepsThinAge = 0;
        m_nStepsFellAge = 0;

        m_pCurCuttings = null;
        m_pCurForClim  = null;
        m_pCurSoilClim = null;
        m_pCurAfor     = null;
        m_pCurDefor	   = null;
        m_pCurCutProps = null;
        m_pCurSpecCh   = null;
        m_pCurThinAge   = null;
        m_pCurFellAge  = null;

        m_posCuttings  = null;
        m_posForClim   = null;
        m_posSoilClim  = null;
        m_posAfor      = null;
        m_posDefor     = null;
        m_posCutProps  = null;
        m_posSpecCh    = null;
        m_posThinAge    = null;
        m_posFellAge    = null;

        m_plCuttings = new ArrayList<>();
        m_plForClim = new ArrayList<>();
        m_plSoilClim = new ArrayList<>();
        m_plAfor = new ArrayList<>();
        m_plDefor = new ArrayList<>();
        m_plCutProps = new ArrayList<>();
        m_plSpecCh = new ArrayList<>();
        m_plThinAge = new ArrayList<>();
        m_plFellAge = new ArrayList<>();
        m_plCutRatios = new GMParLocator();
        m_plClimAgeLims = new GMParLocator();

        m_sName = "untitled";
        defaultScenario = false;
    }

    /**
     * Parametrized constructor.
     */
    public GMScenario (GMEfiscenario pCurCuttings, GMEfiscenario pCurForClim,
            GMEfiscenario pCurSoilClim, GMEfiscenario pCurAfor, GMEfiscenario pCurDefor,
            GMEfiscenario pCurCutProps, GMEfiscenario pCurSpecCh,
            GMEfiscenario pCurThinAge, GMEfiscenario pCurFellAge) {
        m_nStepsSoil = 0;
        m_nStepsFor  = 0;
        m_nStepsCut  = 0;
        m_nStepsAfor = 0;
        m_nStepsDefor = 0;
        m_nStepsCutProps = 0;
        m_nStepsSpecCh = 0;
        m_nStepsThinAge = 0;
        m_nStepsFellAge = 0;

        m_pCurCuttings = pCurCuttings;
        m_pCurForClim  = pCurForClim;
        m_pCurSoilClim = pCurSoilClim;
        m_pCurAfor     = pCurAfor;
        m_pCurDefor	   = pCurDefor;
        m_pCurCutProps = pCurCutProps;
        m_pCurSpecCh   = pCurSpecCh;
        m_pCurThinAge   = pCurThinAge;
        m_pCurFellAge  = pCurFellAge;

        m_posCuttings  = null;
        m_posForClim   = null;
        m_posSoilClim  = null;
        m_posAfor      = null;
        m_posDefor     = null;
        m_posCutProps  = null;
        m_posSpecCh    = null;
        m_posThinAge    = null;
        m_posFellAge    = null;

        m_plCuttings = new ArrayList<>();
        m_plCuttings.add(pCurCuttings);
        m_plForClim = new ArrayList<>();
        m_plForClim.add(pCurForClim);
        m_plSoilClim = new ArrayList<>();
        m_plSoilClim.add(pCurSoilClim);
        m_plAfor = new ArrayList<>();
        m_plAfor.add(pCurAfor);
        m_plDefor = new ArrayList<>();
        m_plDefor.add(pCurDefor);
        m_plCutProps = new ArrayList<>();
        m_plCutProps.add(pCurCutProps);
        m_plSpecCh = new ArrayList<>();
        m_plSpecCh.add(pCurSpecCh); 
        m_plThinAge = new ArrayList<>();
        m_plThinAge.add(pCurThinAge);
        m_plFellAge = new ArrayList<>();
        m_plFellAge.add(pCurFellAge);
        m_plCutRatios = new GMParLocator();
        m_plClimAgeLims = new GMParLocator();
        
        m_sName = "untitled";
        defaultScenario = false;
    }

    /**
     * Checks the current scenarios and updates the steps if necessary.
     * @param nst next step
     * @return zero if successful else -1
     */
    public int checkScenario (int nst) {
        if (m_pCurCuttings == null && m_pCurForClim == null && m_pCurSoilClim == null
                && m_pCurAfor == null && m_pCurDefor == null)
            return -1;
        if (m_pCurCuttings.getEs_nStep() <= nst-m_nStepsCut) {
            if (!isCircling && !m_posCuttings.hasNext())
                if (m_pCurCuttings.getEs_nStep() == nst-m_nStepsCut)
                    isCircling = true;
            if (allow_circling) {
                //m_posCuttings = m_plCuttings.iterator();
                //m_nStepsCut+=m_pCurCuttings.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posCuttings.hasNext()) {
                m_pCurCuttings = (GMEfiscenario) m_posCuttings.next();
                if (!allow_circling && !m_posCuttings.hasNext()) isCirclingGUI = true;
            }
        }
        
        if (m_pCurForClim.getEs_nStep() <= nst-m_nStepsFor) {
            if (!isCircling && !m_posForClim.hasNext())
                if (m_pCurForClim.getEs_nStep() == nst-m_nStepsFor)
                        isCircling = true;
            
            if (allow_circling) {
                //m_posForClim = m_plCuttings.iterator();
                //m_nStepsFor+=m_pCurForClim.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posForClim.hasNext()) {
                m_pCurForClim = (GMEfiscenario) m_posForClim.next();
                if (!allow_circling && !m_posForClim.hasNext()) isCirclingGUI = true;
            }
        }

        if (m_pCurSoilClim.getEs_nStep() <= nst-m_nStepsSoil) {
            if (!isCircling && !m_posSoilClim.hasNext())
                if (m_pCurSoilClim.getEs_nStep() == nst-m_nStepsSoil)
                        isCircling = true;
            
            if (allow_circling) {
                //m_posSoilClim = m_plSoilClim.iterator();
                //m_nStepsSoil+=m_pCurSoilClim.getEs_nStep();
                isCircling = false;
                allow_circling = false;
            }
            
            if (!isCircling && m_posSoilClim.hasNext()) {
                m_pCurSoilClim = (GMEfiscenario) m_posSoilClim.next();
                if (!allow_circling && !m_posSoilClim.hasNext()) isCirclingGUI = true;
            }
        }

        if (m_pCurAfor.getEs_nStep() <= nst-m_nStepsAfor) {
            if (!isCircling && !m_posAfor.hasNext())
                if (m_pCurAfor.getEs_nStep() == nst-m_nStepsAfor)
                        isCircling = true;
            
            if (allow_circling) {
                //m_posAfor = m_plAfor.iterator();
                //m_nStepsAfor+=m_pCurAfor.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posAfor.hasNext()) {
                m_pCurAfor = (GMEfiscenario) m_posAfor.next();
                if (!allow_circling && !m_posAfor.hasNext()) isCirclingGUI = true;
            }
        }

        if (m_pCurDefor.getEs_nStep() <= nst-m_nStepsDefor) {
            if (!isCircling && !m_posDefor.hasNext())
                if (m_pCurDefor.getEs_nStep() == nst-m_nStepsDefor)
                        isCircling = true;
            
            if (allow_circling) {
                //m_posDefor = m_plDefor.iterator();
                //m_nStepsDefor+=m_pCurDefor.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posDefor.hasNext()) {
                m_pCurDefor = (GMEfiscenario) m_posDefor.next();
                if (!allow_circling && !m_posDefor.hasNext()) isCirclingGUI = true;
            }
        }

        if (m_pCurCutProps.getEs_nStep() <= nst-m_nStepsCutProps) {
            if (!isCircling && !m_posCutProps.hasNext())
                if (m_pCurCutProps.getEs_nStep() == nst-m_nStepsCutProps)
                        isCircling = true;
            
            if (allow_circling) {
               // m_posCutProps = m_plCutProps.iterator();
                //m_nStepsCutProps+=m_pCurCutProps.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posCutProps.hasNext()) {
                m_pCurCutProps = (GMEfiscenario) m_posCutProps.next();
                if (!allow_circling && !m_posCutProps.hasNext()) isCirclingGUI = true;
            }
        }
        
        if (m_pCurSpecCh.getEs_nStep() <= nst-m_nStepsSpecCh) {
            if (!isCircling && !m_posSpecCh.hasNext())
                if (m_pCurSpecCh.getEs_nStep() == nst-m_nStepsSpecCh)
                        isCircling = true;
            
            if (allow_circling) {
                //m_posSpecCh = m_plSpecCh.iterator();
                //m_nStepsSpecCh+=m_pCurSpecCh.getEs_nStep();
                isCircling = false;
                allow_circling = false;
                isCirclingGUI = false;
            }
            
            if (!isCircling && m_posSpecCh.hasNext()) {
                m_pCurSpecCh = (GMEfiscenario) m_posSpecCh.next();
                if (!allow_circling && !m_posSpecCh.hasNext()) isCirclingGUI = true;
            }
        }
        
        if (m_pCurThinAge != null)
            if (m_pCurThinAge.getEs_nStep() <= nst-m_nStepsThinAge) {
                if (allow_circling) {
                    isCircling = false;
                    allow_circling = false;
                    isCirclingGUI = false;
                }

                if (!isCircling && m_posThinAge.hasNext()) {
                    m_pCurThinAge = (GMEfiscenario) m_posThinAge.next();
                }
            }
        
        if (m_pCurFellAge != null)
            if (m_pCurFellAge.getEs_nStep() <= nst-m_nStepsFellAge) {

                if (allow_circling) {
                    isCircling = false;
                    allow_circling = false;
                    isCirclingGUI = false;
                }

                if (!isCircling && m_posFellAge.hasNext()) {
                    m_pCurFellAge = (GMEfiscenario) m_posFellAge.next();
                }
            }
        
        return 0;
    }

    /**
     * Set the head positions of all collections
     */
    public void getHeads () {
        m_posForClim = m_plForClim.iterator();
        if (m_posForClim.hasNext())
            m_pCurForClim = (GMEfiscenario)m_posForClim.next();
        m_posSoilClim = m_plSoilClim.iterator();
        if (m_posSoilClim.hasNext())
            m_pCurSoilClim = (GMEfiscenario)m_posSoilClim.next();
        m_posCuttings = m_plCuttings.iterator();
        if (m_posCuttings.hasNext())
            m_pCurCuttings = (GMEfiscenario)m_posCuttings.next();
        m_posAfor = m_plAfor.iterator();
        if (m_posAfor.hasNext())
            m_pCurAfor = (GMEfiscenario)m_posAfor.next();
        m_posDefor = m_plDefor.iterator();
        if (m_posDefor.hasNext())
            m_pCurDefor = (GMEfiscenario)m_posDefor.next();
        m_posCutProps = m_plCutProps.iterator();
        if (m_posCutProps.hasNext())
            m_pCurCutProps = (GMEfiscenario)m_posCutProps.next();
        m_posSpecCh = m_plSpecCh.iterator();
        if (m_posSpecCh.hasNext())
            m_pCurSpecCh = (GMEfiscenario)m_posSpecCh.next();
        m_posThinAge = m_plThinAge.iterator();
        if (m_posThinAge.hasNext())
            m_pCurThinAge = (GMEfiscenario)m_posThinAge.next();
        m_posFellAge = m_plFellAge.iterator();
        if (m_posFellAge.hasNext())
            m_pCurFellAge = (GMEfiscenario)m_posFellAge.next();
    }
    
    public boolean getIsCircling() {
        return isCircling;
    }
    
    public boolean getIsCirclingGUI() {
        return isCirclingGUI;
    }
    
    /**
     * For GUI. When user wants to continue after scenario has started circling
     * @param isCircling
     */
    public void setIsCircling(boolean isCircling) {
        this.isCircling = isCircling;
        allow_circling = !isCircling;
    }

    /**
     * @return the defaultScenario
     */
    public boolean isDefaultScenario() {
        return defaultScenario;
    }

    /**
     * @param defaultScenario the defaultScenario to set
     */
    public void setDefaultScenario(boolean defaultScenario) {
        this.defaultScenario = defaultScenario;
    }
    
}

