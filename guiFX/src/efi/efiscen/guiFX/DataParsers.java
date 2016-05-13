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
package efi.efiscen.guiFX;

import efi.efiscen.com.ComArFlt;
import efi.efiscen.gm.GMEfiscen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.chart.XYChart;

/**
 * Interface for extracting data from the simulation backend. This class is meant
 * to decouple the user interface from the simulation backend, as well as making
 * it easier to add new extractors and modify existing ones if the simulation
 * backend changes.
 */
public class DataParsers {
    
    private Map<String,DataParser> parsers;
    private final EfiscenModel efiscenModel;
        
    /**
     * Load GMEfiscen object and initialize fetcher.
     * @param efiscenModel
     */
    public DataParsers(EfiscenModel efiscenModel){
        
        this.efiscenModel = efiscenModel;
        init();
        
    }
    
    /**
     * Load fetcher classes.
     */
    private void init(){
        parsers = new HashMap<>();
        parsers.put(SOLDataParser.id, new SOLDataParser());
        parsers.put(CELDataParser.id, new CELDataParser());
        parsers.put(LIGDataParser.id, new LIGDataParser());
        parsers.put(NWLDataParser.id, new NWLDataParser());
        parsers.put(CWLDataParser.id, new CWLDataParser());
        parsers.put(FWLDataParser.id, new FWLDataParser());
        parsers.put(HUM1DataParser.id, new HUM1DataParser());
        parsers.put(HUM2DataParser.id, new HUM2DataParser());
        parsers.put(COUTDataParser.id, new COUTDataParser());
        parsers.put(StemDataParser.id, new StemDataParser());
        parsers.put(FRootsDataParser.id, new FRootsDataParser());
        parsers.put(CRootsDataParser.id, new CRootsDataParser());
        parsers.put(BranchesDataParser.id, new BranchesDataParser());
        parsers.put(LeavesDataParser.id, new LeavesDataParser());
        parsers.put(GrStockDataParser.id, new GrStockDataParser());
        parsers.put(TotalCarbonDataParser.id, new TotalCarbonDataParser());
        parsers.put(AverageVolumeDataParser.id,new AverageVolumeDataParser());
        parsers.put(AreaDataParser.id, new AreaDataParser());
        parsers.put(AfforFundDataParser.id, new AfforFundDataParser());
        parsers.put(BareAreaDataParser.id,new BareAreaDataParser());
        parsers.put(PotentialFellAreaDataParser.id, new PotentialFellAreaDataParser());
        parsers.put(PotentialFellVolumeDataParser.id, new PotentialFellVolumeDataParser());
        parsers.put(DeadwoodDataParser.id, new DeadwoodDataParser());
        parsers.put(AverageIncrementDataParser.id, new AverageIncrementDataParser());
        parsers.put(NaturalMortalityDataParser.id, new NaturalMortalityDataParser());
        parsers.put(ThinningAreaDataParser.id, new ThinningAreaDataParser());
        parsers.put(FinalFellingAreaDataParser.id, new FinalFellingAreaDataParser());
        parsers.put(ThinningVolumeDataParser.id, new ThinningVolumeDataParser());
        parsers.put(FinalFellingVolumeDataParser.id, new FinalFellingVolumeDataParser());
        parsers.put(CarbonSoilDataParser.id, new CarbonSoilDataParser());
    }
    
    public Set<String> getParserIDs() {
        return parsers.keySet();
    }
    
    /**
     * Return a chart representing data for selected regions, owners, sites and species.
     * @param regions Regions to fetch data for.
     * @param owners Owners to fetch data for.
     * @param sites Sites to fetch data for.
     * @param species Species to fetch data for.
     * @param id ID for dataset.
     * @return Chart representing data for selections.
     */
    public XYChart.Series<Float,Float> getSeries(Set<Long> regions, 
            Set<Long> owners, Set<Long> sites, Set<Long> species, String id){
        return parsers.get(id).getSeries(efiscenModel.getEfiscen(), regions, owners, sites, species);
    }
    
    /**
     * Return latest data from selected dataset for selected regions, owners, sites and species.
     * @param regions Regions to fetch data for.
     * @param owners Owners to fetch data for.
     * @param sites Sites to fetch data for.
     * @param species Species to fetch data for.
     * @param id Dataset ID
     * @return Chart representing data for selections.
     */
    public float getLatest(Set<Long> regions, 
            Set<Long> owners, Set<Long> sites, Set<Long> species, String id){
        return parsers.get(id).getLatest(efiscenModel.getEfiscen(), regions, owners,
                sites, species);
    }
    
    /**
     * Returns appropriate tool tip for GUI button.
     * @param id Dataset ID.
     * @return Button tooltip.
     */
    public String getTipKey(String id){
        return id + "Tip";
    }
    
    /**
     * Returns the key with which to fetch unit from localization file for dataset id.
     * The key is formed with
     * @param id Dataset ID.
     * @return Unit for data.
     */
    public String getUnitKey(String id){
        return id + "Unit";
    }
    
    /**
     * Returns a Set of all the dataset IDs.
     * @return Set containing all dataset IDs. 
     */
    public Set<String> getIds(){
        if(parsers == null){
            init();
        }
        return parsers.keySet();
    }
    
    /**
     * Returns a class that can be used to fetch data for a single dataset.
     * @param id Dataset to fetch data from.
     * @return Class for fetching data from dataset.
     */
    public DataParser getParser(String id){
        if(parsers == null){
            init();
        }
        return parsers.get(id);
    }

    private class DeadwoodDataParser extends DataParser{

        public static final String id = "DeadWood";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < gmefiscen.historyUpdateCount()) {
                float point = 0f;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : gmefiscen.m_mafDeadWood.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt<Float> data = gmefiscen.m_mafDeadWood.get(uKey);
                                        if( data != null ) {
                                            point += data.getData(i);
        }
                                    }
                                }
                            }
                        }
                    }
                }
                graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),point));
                i++;
            }
            return graph;
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafDeadWood);
        }
    }

    private class AverageIncrementDataParser extends DataParser{

        public static final String id = "IncrAv";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < gmefiscen.historyUpdateCount()) {
                Float incr = 0f;
                float ar = 0f;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : gmefiscen.m_mafAvrIncrement.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt data = gmefiscen.m_mafAvrIncrement.get(uKey);
                                        ComArFlt data2 = gmefiscen.m_mafArea.get(uKey);
                                        if( data != null ) {
                                            float area = (Float)data2.getData(i);
                                            ar += area;    
                                            incr += (Float)data.getData(i)*area;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if( ar != 0f ) {
                    graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),incr/ar));
                }
                i++;
            }
            return graph;
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            float incr = 0f;
            float area = 0f;
            for( Long region : regions) {
                for( Long owner : owners) {
                    for( Long site : sites) {
                        for( Long specie : species) {
                            long key = makeKey(region, owner, site, specie);
                            for( long uKey : gmefiscen.m_mafAvrIncrement.keySet()) {
                                 long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                 if( comparableKey == key ) {
                                    ComArFlt data = gmefiscen.m_mafAvrIncrement.get(uKey);
                                    ComArFlt data2 = gmefiscen.m_mafArea.get(uKey);
                                    if( data != null && data2 != null) {
    //                                    System.out.println(data.getData(data.getSize()-1).toString());
                                        incr += (Float)data.getData(data.getSize()-1)*(Float)data2.getData(data2.getSize()-1);
                                    //                                    System.out.println(data.getData(data.getSize()-1).toString());
                                        area += (Float)data2.getData(data2.getSize()-1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(area!=0f) return incr/area;
            else return 0f;
        }
    }

    private class NaturalMortalityDataParser extends DataParser{

        public static final String id = "NatMort";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites,
                    species, gmefiscen.m_mafNatMort, gmefiscen.historyUpdateCounter-1);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, 
                    gmefiscen.m_mafNatMort, gmefiscen.historyUpdateCounter-1);
        }
    }

    private class ThinningAreaDataParser extends DataParser{

        public static final String id = "ThinArea";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafMfqThAreas);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafMfqThAreas);
        }
    }

    private class FinalFellingAreaDataParser extends DataParser{

        public static final String id = "FFarea";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafMfqFelAreas);
        }


        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafMfqFelAreas);
        }
    }

    private class ThinningVolumeDataParser extends DataParser{

        public static final String id = "ThinRems";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, 
                    species, gmefiscen.m_mafMfqThRems);//, gmefiscen.historyUpdateCounter);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, 
                    gmefiscen.m_mafMfqThRems);//, gmefiscen.historyUpdateCounter);
        }
    }
    //Bug fixing. Showing chart for felling and thinnings from wrong position
    //TODO: not elegant enough ;)
    //EFI 2016 April
    private class FinalFellingVolumeDataParser extends DataParser{

        public static final String id = "FelRems";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, 
                    species, gmefiscen.m_mafMfqFelRems);
//            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, 
//                    species, gmefiscen.m_mafFellings,gmefiscen.historyUpdateCounter-1);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species,
                    gmefiscen.m_mafMfqFelRems);
                    //gmefiscen.m_mafFellings, gmefiscen.historyUpdateCounter-1);
        }
    }

    private class PotentialFellVolumeDataParser extends DataParser{

        public static final String id = "PotentFellVol";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafPotentialFellingsVolume);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafPotentialFellingsVolume);
        }
    }

    private class PotentialFellAreaDataParser extends DataParser{

        public static final String id = "PotentFellArea";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafPotentialFellingsArea);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafPotentialFellingsArea);
        }
    }

    private class AfforFundDataParser extends DataParser{

        public static final String id = "AfforFund";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafAfforFund);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafAfforFund);
        }
    }

    private class BareAreaDataParser extends DataParser{

        public static final String id = "BareArea";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafBareArea);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafBareArea);
        }
    }

    private class AverageVolumeDataParser extends DataParser{

        public static final String id = "avgVolume";
      

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < gmefiscen.historyUpdateCount()) {
                float growth = 0f;
                float area = 0f;
                int p = 0;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : gmefiscen.m_mafGrStock.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt data = gmefiscen.m_mafGrStock.get(uKey);
                                        ComArFlt data2 = gmefiscen.m_mafArea.get(uKey);
                                        if( data!=null && data2!=null) {
                                            growth += (float)data.getData(i);
                                            area += (float)data2.getData(i);
                                            p++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if( area != 0f ) graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),growth/area));
                i++;
            }
            return graph;
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            float growth = 0f;
            float area = 0f;
            for( Long region : regions) {
                for( Long owner : owners) {
                    for( Long site : sites) {
                        for( Long specie : species) {
                            long key = makeKey(region, owner, site, specie);
                            for( long uKey : gmefiscen.m_mafGrStock.keySet()) {
                                 long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                 if( comparableKey == key ) {
                                    ComArFlt<Float> data = gmefiscen.m_mafGrStock.get(uKey);
                                    if( data != null && data.getSize() > 0) {
                                        growth += data.getData(data.getSize()-1);
                                    }
                                    ComArFlt<Float> data2 = gmefiscen.m_mafArea.get(uKey);
                                    if( data2 != null && data2.getSize() > 0) {
                                        area += data2.getData(data2.getSize()-1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(area!=0f) return growth/area;
            else return 0f;
        }
    }

    private class AreaDataParser extends DataParser{

        public static final String id = "Area";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafArea);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafArea);
        }
    }

    private class GrStockDataParser extends DataParser{

        public static final String id = "Vol";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafGrStock);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafGrStock);
        }
    }

    private class NWLDataParser extends DataParser{

        public static final String id = "NWL";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilNwl);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
           return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilNwl);
        }
    }

    private class FWLDataParser extends DataParser{

        public static final String id = "FWL";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilFwl);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilFwl);
        }
    }

    private class CWLDataParser extends DataParser{

        public static final String id = "CWL";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilCwl);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilCwl);
        }
    }

    private class SOLDataParser extends DataParser{

        public static final String id = "SOL";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilSol);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilSol);
        }
    }

    private class CELDataParser extends DataParser{

        public static final String id = "CEL";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilCel);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilCel);
        }
    }

    private class LIGDataParser extends DataParser{

        public static final String id = "LIG";
        
        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilLig);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilLig);
        }
    }

    private class HUM1DataParser extends DataParser{

        public static final String id = "HUM1";
        

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilHm1);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilHm1);
        }
    }

    private class HUM2DataParser extends DataParser{

        public static final String id = "HUM2";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilHm2);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilHm2);
        }
    }

    private class COUTDataParser extends DataParser{

        public static final String id = "COUT";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafSoilClost);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafSoilClost);
        }
    }

    private class CarbonSoilDataParser extends DataParser{

        public static final String id = "CSoil";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCSoil);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrix(regions, owners, sites, species, gmefiscen.m_mafCSoil);
        }
    }

    private class StemDataParser extends DataParser{

        public static final String id = "Stem";


        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCStem);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafCStem);
        }
    }



    private class CRootsDataParser extends DataParser{

        public static final String id = "CRoots";


        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCCRoots);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafCCRoots);
        }
    }


    private class FRootsDataParser extends DataParser{

        public static final String id = "FRoots";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCFRoots);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafCFRoots);
        }
    }



    private class BranchesDataParser extends DataParser{

        public static final String id = "Branches";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCBranches);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafCBranches);
        }
    }


    private class LeavesDataParser extends DataParser{

        public static final String id = "Leaves";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrixAr(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafCLeaves);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractLatestMatrixAr(regions, owners, sites, species, gmefiscen.m_mafCLeaves);
        }
    }

    private class TotalCarbonDataParser extends DataParser{

        public static final String id = "tCarbon";

        @Override
        public XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return this.extractSeriesMatrix(gmefiscen, regions, owners, sites, species, gmefiscen.m_mafBiomass);
        }

        @Override
        public float getLatest(GMEfiscen gmefiscen, Set<Long> regions, 
                Set<Long> owners, Set<Long> sites, Set<Long> species) {
            return extractLatestMatrix(regions, owners, sites, species,gmefiscen.m_mafBiomass);
        }
    }

    /**
     * Base class for classes that fetch data from the simulation backend.
     */
    public abstract class DataParser {
        
        public String id;
           
        /**
         * Return a chart representing data for selected regions, owners, sites and species.
         * Implementation recommendation: Call one of the provided extractSeries-methods with
         * the proper data storage.
         * @param gmefiscen Gmefiscen object from which data is fetched.
         * @param regions Regions to fetch data for.
         * @param owners Owners to fetch data for.
         * @param sites Sites to fetch data for.
         * @param species Species to fetch data for.
         * @return Chart representing data for selections.
         */
        public abstract XYChart.Series<Float,Float> getSeries(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species);

        /**
         * Return latest data from dataset for selected regions, owners, sites and species. 
         * Implementation recommendation: Call one of the provided extractLatest-methods with
         * the proper data storage.
         * @param gmefiscen GMEfiscen object from which data is fetched.
         * @param regions Regions to fetch data for.
         * @param owners Owners to fetch data for.
         * @param sites Sites to fetch data for.
         * @param species Species to fetch data for.
         * @return Chart representing data for selections.
         */
        public abstract float getLatest(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species);

        /**
         * Extracts latest data point from the matrices specified with regions,
         * owners, sites and species. Similar to @extractLatestMatrix except 
         * that data storage is a sequence of arraylists.
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected float extractLatestMatrixAr(Set<Long> regions, Set<Long> owners, 
                Set<Long> sites, Set<Long> species, HashMap<Long,ComArFlt<ArrayList<Float>>> storage) {
            float point = 0f;
            for( Long region : regions) {
                for( Long owner : owners) {
                    for( Long site : sites) {
                        for( Long specie : species) {
                            long key = makeKey(region, owner, site, specie);
                            for( long uKey : storage.keySet()) {
                                 long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                 if( comparableKey == key ) {
                                    ComArFlt<ArrayList<Float>> data = storage.get(uKey);
                                    if( data != null && data.getSize()>0) {
                                        for(float f : data.getData(data.getSize()-1)) {
                                            point += f;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return point;
        }
        /**
         * Extracts latest data point from the matrices specified with regions,
         * owners, sites and species.
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected float extractLatestMatrix(Set<Long> regions, Set<Long> owners, 
                Set<Long> sites, Set<Long> species, HashMap<Long,? extends ComArFlt<? extends Number>> storage) {
            float point = 0f;
            for( Long region : regions) {
                for( Long owner : owners) {
                    for( Long site : sites) {
                        for( Long specie : species) {
                            long key = makeKey(region, owner, site, specie);
                            for( long uKey : storage.keySet()) {
                                 long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                 if( comparableKey == key ) {
                                    ComArFlt<? extends Number> data = storage.get(uKey);
                                    if( data != null && data.getSize()>0) {
                                        point += data.getData(data.getSize()-1).floatValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return point;
        }
        
        /**
         * Extracts latest data point from the matrices specified with regions,
         * owners, sites and species. The data encompasses the steps defined
         * by the steps parameter and starts from the first step.
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected float extractLatestMatrix(Set<Long> regions, Set<Long> owners, 
                Set<Long> sites, Set<Long> species, 
                HashMap<Long,? extends ComArFlt<? extends Number>> storage,
                int steps) {
            float point = 0f;
            for( Long region : regions) {
                for( Long owner : owners) {
                    for( Long site : sites) {
                        for( Long specie : species) {
                            long key = makeKey(region, owner, site, specie);
                            for( long uKey : storage.keySet()) {
                                 long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                 if( comparableKey == key ) {
                                    ComArFlt<? extends Number> data = storage.get(uKey);
                                    if( data != null && data.getSize()>0) {
                                        point += data.getData(data.getSize()-1).floatValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return point;
        }

        /**
         * Extracts a series of values for selected matrices.
         * @param gmefiscen
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected XYChart.Series<Float,Float> extractSeriesMatrix(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species, 
                HashMap<Long,? extends ComArFlt<? extends Number>> storage) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < gmefiscen.historyUpdateCount()) {
                float point = 0.f;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : storage.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt<? extends Number> data = storage.get(uKey);
                                        if( data != null && data.getSize()>i) {
                                            Number fl = data.getData(i);
                                            if(fl!=null)
                                                point += fl.floatValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),point));
                i++;
            }
            return graph;
        }
        
        /**
         * Extracts a series of values for selected matrices. Data provided
         * starts from the first step and encompasses the number of steps
         * provided. 
         * @param gmefiscen
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected XYChart.Series<Float,Float> extractSeriesMatrix(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species, 
                HashMap<Long,? extends ComArFlt<? extends Number>> storage, int steps) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < steps) {
                float point = 0.f;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : storage.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt<? extends Number> data = storage.get(uKey);
                                        if( data != null && data.getSize()>i) {
                                            Number fl = data.getData(i);
                                            if(fl!=null)
                                                point += fl.floatValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),point));
                i++;
            }
            return graph;
        }
        
        /**
         * Extracts a series of totals for selected matrices.
         * @param gmefiscen
         * @param regions
         * @param owners
         * @param sites
         * @param species
         * @param storage
         * @return 
         */
        protected XYChart.Series<Float,Float> extractSeriesMatrixAr(GMEfiscen gmefiscen, 
                Set<Long> regions, Set<Long> owners, Set<Long> sites, Set<Long> species, 
                HashMap<Long,ComArFlt<ArrayList<Float>>> storage) {
            XYChart.Series<Float,Float> graph = new XYChart.Series<>();
            Integer i = 0;
            while(  i < gmefiscen.historyUpdateCount()) {
                float point = 0.f;
                for( Long region : regions) {
                    for( Long owner : owners) {
                        for( Long site : sites) {
                            for( Long specie : species) {
                                long key = makeKey(region, owner, site, specie);
                                for( long uKey : storage.keySet()) {
                                     long comparableKey = makeComparableKey(uKey, region, owner, site, specie);
                                     if( comparableKey == key ) {
                                        ComArFlt<ArrayList<Float>> data = storage.get(uKey);
                                        if( data != null ) {
                                            for(float f : data.getData(i)) {
                                                point += f;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                graph.getData().add(new XYChart.Data<Float,Float>(i.floatValue(),point));
                i++;
            }
            return graph;
        }

        /**
         * Packs the region, owner, site and species into one key value of
         * long-type.
         * @param region Region number.
         * @param owner Owner number.
         * @param site Site number
         * @param species Species number.
         * @return key
         */
        protected long makeKey( long region, long owner, long site, long species) {

            long key = (region<<24) + (owner<<16) + (site<<8) + species;
            return key;
        }

        /**
         * Packs region, owner, site and species into a key of type long. If any
         * value is 0 it means any. For example if region is 0 then it means
         * the key represents any region.
         * @param key Key to convert.
         * @param region Region number for original key.
         * @param owner Owner number for original key.
         * @param site Site number for original key.
         * @param species Species number for original key.
         * @return key
         */
        protected long makeComparableKey(long key, long region, long owner, long site, long species) {
            if (region == 0)  key = key & ~0xFF000000;
            if (owner == 0)  key = key & ~0xFF0000;
            if (site == 0) key = key & ~0xFF00;
            if (species == 0) key = key & ~0xFF;
            return key;
        }
    }
}