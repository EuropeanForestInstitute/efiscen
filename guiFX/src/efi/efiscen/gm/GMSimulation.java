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

/**
 * Takes care of the actual simulation.
 * 
 */
public class GMSimulation implements Serializable {

    private GMEfiscen m_pExperiment;
    private final GMScenario m_Scenario;
    private int m_nStep;
    private int m_nStepsByClick = 1;
    private float m_TotalVolume;
    private int timesteps = 1;
    // For checking whether the msg that scenario has started circling has been
    // written to the logger
    private boolean allow_circling = false;
    //do not serialize!

    /**
     * Default constructor.
     * @param m_pExperiment
     * @param m_Scenario
     * @param m_nStep
     */
    public GMSimulation (GMEfiscen m_pExperiment, GMScenario m_Scenario, int m_nStep) {
        this.m_pExperiment = m_pExperiment;
        this.m_Scenario = m_Scenario;
        this.m_nStep = m_nStep;
    }
    
    public void setTimeSteps( int timesteps ) {
        this.timesteps = timesteps;
    }
    
    public int getTimeSteps() {
        return timesteps;
    }
    
    public void setM_nStep(int step){
        m_nStep = step;
    }

    /**
     * Run one step on the scenario on the given experiment.
     * @return the experiment including ran scenarios
     */
    public GMEfiscen stepScenario () {
        try {
            m_Scenario.checkScenario(m_nStep);
            
            // Changing management parameter (fell age) depending on step
            if (m_Scenario.m_pCurFellAge != null)
                m_pExperiment.setHarvestAge(m_Scenario.m_pCurFellAge.getEs_paData());
            
            // Changing management parameter (thin age) depending on step
            if (m_Scenario.m_pCurThinAge != null)
                m_pExperiment.setThinRange(m_Scenario.m_pCurThinAge.getEs_paData());
            
            if (m_Scenario.m_pCurSoilClim != null)
                m_pExperiment.setSoilClimate(m_Scenario.m_pCurSoilClim.getEs_paData());

            if (m_Scenario.m_pCurForClim != null) {
                m_pExperiment.setForestClimateV4(m_Scenario.m_plClimAgeLims,m_Scenario.m_pCurForClim.getEs_paData()); //V4
                //m_pExperiment.setForestClimate(m_Scenario.m_pCurForClim.getEs_paData());
            }
            // We are checking for management
            if (m_Scenario.m_pCurCuttings != null) {
                GMFellings pFl = new GMFellings();
                float volr;
                GMParArray pEl;
                for (Long uKey : m_Scenario.m_plCutRatios.m_mElements.keySet())
                {
                    pEl = m_Scenario.m_plCutRatios.m_mElements.get(uKey);
                    //float remshare = m_Scenario.m_pCurCutProps.getEs_paData().getParameterValue(uKey,0);
                    pFl.setF_volume(0.0);
                    pFl.setF_ratio(1.0);
                    //pFl.setF_stem(remshare);
                    float parm = m_Scenario.m_pCurCuttings.getEs_paData().getParameterValue(uKey,0);
                    if (parm>=0.0) {
                        pFl = m_pExperiment.reportHarvestLevel(pEl.m_uRegion,pEl.m_uOwner,
                            pEl.m_uSite,pEl.m_uSpecies,pFl,m_Scenario.m_pCurCutProps.getEs_paData()); 
                        volr = 1.0f;
                        if (pFl.getF_volume() > 0)
                            volr = (float)(parm/pFl.getF_volume());
                        if (volr>1.0) 
                            volr = 1.0f;
                        pEl.m_Vals.set(0,volr);
                    }
                    else
                        pEl.m_Vals.set(0,1.0f);

                    //remshare = m_Scenario.m_pCurCutProps.getEs_paData().getParameterValue(uKey,3);
                    pFl.setF_volume(0.0);
                    pFl.setF_ratio(1.0);
                    //pFl.setF_stem(remshare);
                    parm = m_Scenario.m_pCurCuttings.getEs_paData().getParameterValue(uKey,1);
                    if (parm>=0.0) {
                        pFl = m_pExperiment.reportThinningsLevel(pEl.m_uRegion,pEl.m_uOwner,
                            pEl.m_uSite,pEl.m_uSpecies,pFl,m_Scenario.m_pCurCutProps.getEs_paData()); 
                        volr = 1.0f;
                        if (pFl.getF_volume() > 0)
                            volr = (float)(parm/pFl.getF_volume());
                        if (volr>1.0) 
                            volr = 1.0f;
                        pEl.m_Vals.set(1,volr);
                    }
                    else
                        pEl.m_Vals.set(1,1.0f);
                    m_Scenario.m_plCutRatios.m_mElements.put(uKey, pEl);
                }
            }
            return m_pExperiment;
        } catch (GMParLocator.GMParLocatorException ex) {
            System.err.println(ex);
        }
        return null;
    }

    /**
     * Runs the simulation.
     * @return 
     */
    public int onGo() {
        float volume;
        //float area;
        if (m_pExperiment == null)
            return 0;
        synchronized(m_pExperiment) {
            //m_pCurMatrix.addToBare(10);
            //m_pCurMatrix.grow();
            int cur_timestep = 0;   // added for checking in what step scenario starts circling
            for (int i=0;i<m_nStepsByClick * timesteps;i++) {
                boolean isCircling = m_Scenario.getIsCircling();    // whether scenario is circling
                if (isCircling) {
                // Scenario has started circling -> write to the logger
                    String temp = m_pExperiment.m_sName;
                    System.err.println("Scenario has started circling " + "Scenario: "+m_Scenario.m_sName);
                    if (allow_circling) return cur_timestep;
                }

                stepScenario();
                isCircling = m_Scenario.getIsCircling();
                if (!allow_circling || !isCircling && allow_circling) {
                    try {
                        if (m_Scenario.m_pCurCuttings != null)
                            //volume = m_pExperiment.goAheadEx(m_Scenario.m_plCutRatios);
                            //volume = m_pExperiment.goAheadEx(m_Scenario.m_plCutRatios,m_Scenario.m_pCurAfor.getEs_paData(),
                            //			m_Scenario.m_pCurDefor.getEs_paData());
                            //volume = m_pExperiment.goAheadEx(m_Scenario.m_plCutRatios,m_Scenario.m_pCurAfor.getEs_paData(),
                            //			m_Scenario.m_pCurDefor.getEs_paData(),m_Scenario.m_pCurCutProps.getEs_paData());
                            volume = m_pExperiment.goAheadExRup(m_Scenario.m_plCutRatios,m_Scenario.m_pCurAfor.getEs_paData(),
                                        m_Scenario.m_pCurDefor.getEs_paData(),m_Scenario.m_pCurCutProps.getEs_paData(),m_Scenario.m_pCurSpecCh.getEs_paData());
                        else
                            volume = m_pExperiment.goAhead();
                        m_TotalVolume = volume;
                        m_nStep+=1;
                        //area = m_pCurMatrix.getArea();
                        //if(area > 0)
                        //	m_VolAv = 1000*volume/area;
                        //m_VolTot = 1000*volume;
                        m_pExperiment.updateHistory();
                        /* added by Janne so all the UI elements are properly updated
                        * each step
                        */
                        cur_timestep = i*m_nStepsByClick+1;
                    } catch (GMParLocator.GMParLocatorException ex) {
                        System.err.println(ex);
                        System.err.println("simulation could not be completed");
                        return cur_timestep;
                    }
                }
            }
            //return m_pExperiment;
            return cur_timestep;
        }
    }

    public GMScenario getM_Scenario() {
        return m_Scenario;
    }

    public float getM_TotalVolume() {
        return m_TotalVolume;
    }

    public int getM_nStep() {
        return m_nStep;
    }

    public int getM_nStepsByClick() {
        return m_nStepsByClick;
    }

    public GMEfiscen getM_pExperiment() {
        return m_pExperiment;
    }
    
    public void setM_pExperiment(GMEfiscen m_pExperiment) {
        this.m_pExperiment = m_pExperiment;
    }
    
    public void setAllow_circling(boolean allow_circling) {
        this.allow_circling = allow_circling;
    }

    /*
    public void onLoad ()
    {
        // TODO: Add your control notification handler code here
        // We are checking if it's not "first" load!
        if (m_scaleAreas!=1.0 && m_pExperiment) {
            CEfisAskScaleDlg esAsk;
            esAsk.m_nStatus = -13;
            if (esAsk.DoModal()==IDOK) {
                int nret;
                nret = esAsk.m_nStatus;
                switch (nret) {
                case 1087:
                    m_scaleAreas = esAsk.m_scf;
                    break;
                case 1089:
                    m_scaleAreas = 1.0f;
                    break;
                }
                String sEdText;
                //sEdText.Format("Radio= %d",nret);
                //AfxMessageBox(sEdText);
                //sEdText.Format("%4.3f",esAsk.m_scf);
                //AfxMessageBox(sEdText);
                sEdText.format("%4.3f",m_scaleAreas);
            }
        }
        static char BASED_CODE szFilter[] = "EFIscen Files (*.efs)|*.efs|Text Files (*.txt)|*.txt|All Files (*.*)|*.*||";

        CFileDialog cfdLoad(1,NULL,NULL,OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT,szFilter);
        CString sFileName;
        if (cfdLoad.DoModal()==IDOK) {
            sFileName = cfdLoad.GetPathName();
            int nretLoad;
            nretLoad = loadExperiment(sFileName);
            if(nretLoad) {
                m_TotalArea = m_pExperiment.getArea(0,0,0,0);
                m_TotalVolume = m_pExperiment.getValue(0,0,0,0);
                fillTreeCtrls();
                fillEditCtrls();
                ChartSelReinit(FALSE);
                String strWname;
                strWname.Format("Efiscen3::%s",m_pExperiment->m_sName);
                SetWindowText(strWname);
                m_cstClimName.SetWindowText("Undefined");
                m_cstManName.SetWindowText("Undefined");

            }
        }
    }
    */

}

