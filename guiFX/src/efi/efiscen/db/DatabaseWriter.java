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

import efi.efiscen.database.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods to write values to the database.
 * Connections must be given to constructor, before saving values to database.
 * 
 */
public class DatabaseWriter {
    
    final Writer writer;
    
    
    /**
     * Constructor for writer
     * @param server Server name
     * @param database Database name
     * @param port Database port
     * @param usr Username for database
     * @param pass Password for database
     * @param driverType Driver type
     * @throws DBException
     * @throws efi.efiscen.database.DatabaseComponentsException 
     */
    public DatabaseWriter(String server, String database, int port,String usr,
            String pass,efi.efiscen.database.DriverType driverType) throws DBException, efi.efiscen.database.DatabaseComponentsException{
        writer = new Writer(server, database, port, usr,pass, driverType);
    }
    
    /**
     * Writes values to the database.
     * Writes values sid, m_id, reg, grStock, area, 
     * deadWood, natMort, thinHarvest, felHarvest, felAv, grStockAv, incrAv,
     * a_0_150 and v_0_150 to base-table.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id Matrix id
     * @param step current year 
     * @param grStock growing stock
     * @param area area number
     * @param deadWood dead wood 
     * @param natMort natural mortality 
     * @param thinHarvest thinning harvest
     * @param felHarvest felling harvest
     * @param felAv felling average
     * @param grStockAv growing stock average
     * @param incrAv increase average
     * @param a_0_150 distribution of area by age classes
     * @param v_0_150 distribution of growing stock by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing was unsuccessful
     * @throws SQLException
     */
    public boolean Base(int sid, int m_id, int step, float grStock, float area,
            float deadWood, float natMort, float thinHarvest, float felHarvest, float felAv,
            float grStockAv, float incrAv, ArrayList a_0_150, ArrayList v_0_150) throws DBException, SQLException{

        //Check that the series size is what it is supposed to be.
        if(a_0_150.size() != 16){
            throw new DBException("Base","Illegal size in array a_0_150");
        }
        if(v_0_150.size() != 16){
            throw new DBException("Base","Illegal size in array v_0_150");
        }
        
        //put series in a map.
        Map<String,String> variables = new HashMap<>();
        for(int i = 0; i < a_0_150.size()-1; i++){
            variables.put("a_"+i*10+"_"+(i+1)*10, ""+a_0_150.get(i));
        }
        variables.put("a_>150", ""+a_0_150.get(15));
        for(int i = 0; i < v_0_150.size()-1; i++){
            variables.put("v_"+i*10+"_"+(i+1)*10, ""+v_0_150.get(i));  
        }
        variables.put("v_>150", ""+v_0_150.get(15));
        //other variables
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("step", ""+step);
        variables.put("grstock", ""+grStock);
        variables.put("area", ""+area);
        variables.put("deadwood", ""+deadWood);
        variables.put("natmort", ""+natMort);
        variables.put("thinharvest", ""+thinHarvest);
        variables.put("felharvest", ""+felHarvest);
        variables.put("felav", ""+felAv);
        variables.put("grstockav", ""+grStockAv);
        variables.put("incrav", ""+incrAv);
        return writer.insert("base", variables);
    }
    
    /**
     * Writes values to database.
     * Writes values sid, step, c_trees, c_stem, c-foliage, c_branches, 
     * c_cRoots, c_fRoots, c_soil, cwl, fwl, nwl, sol, cel, lig, hum1, hum2, 
     * and cout to carboncountry -table
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param step current year
     * @param c_trees carbon in trees
     * @param c_stem carbon in tree stems
     * @param c_foliage carbon in foliage
     * @param c_branches carbon in branches
     * @param c_cRoots Carbon in coarse roots
     * @param c_fRoots Carbon in fine roots
     * @param c_soil Total carbon stock in soil
     * @param cwl Carbon in coarse woody litter
     * @param fwl Carbon in fine woody litter
     * @param nwl Carbon in non-woody litter
     * @param sol Carbon in soluble compounds
     * @param cel Carbon in holocellulose
     * @param lig Carbon in lignin-like compounds
     * @param hum1 Carbon in first humus compartment
     * @param hum2 Carbon in second humus compartment
     * @param cout Carbon released to atmosphere (gross)
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean CarbonCountry(int sid, int step, float c_trees, 
            float c_stem, float c_foliage, float c_branches, float c_cRoots, float c_fRoots,
            float c_soil, float cwl, float fwl, float nwl, float sol, float cel, float lig,
            float hum1, float hum2, float cout) throws DBException, SQLException{
        
        Map<String,String> variables = new HashMap<>();
        variables.put("simulation_id", ""+sid);
        variables.put("step", ""+step);
        variables.put("c_trees", ""+c_trees);
        variables.put("c_stem", ""+c_stem);
        variables.put("c_foliage", ""+c_foliage);
        variables.put("c_branches", ""+c_branches);
        variables.put("c_croots", ""+c_cRoots);
        variables.put("c_froots", ""+c_fRoots);
        variables.put("c_soil", ""+c_soil);
        variables.put("cwl", ""+cwl);
        variables.put("fwl", ""+fwl);
        variables.put("nwl", ""+nwl);
        variables.put("sol", ""+sol);
        variables.put("cel", ""+cel);
        variables.put("lig", ""+lig);
        variables.put("hum1", ""+hum1);
        variables.put("hum2", ""+hum2);
        variables.put("cout", ""+cout);
        return writer.insert("carboncountry", variables);
    }
    
    /**
     * Writes carbon soil  values into database.
     * Writes values sid, s_id, step, c_trees, cwl, fwl,
     * nwl, sol, cel, lig, hum1, hum2, c_soil, cout, litin, cwl_in, fwl_in, 
     * and nwl_int to  carbonsoil-table.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param step current year 
     * @param c_trees total Carbon in trees
     * @param cwl Carbon in coarse woody litter
     * @param fwl Carbon in fine woody litter
     * @param nwl Carbon in non-woody litter
     * @param sol Carbon in soluble compounds
     * @param cel Carbon in holocellulose
     * @param lig Carbon in lignin-like compounds
     * @param hum1 Carbon in first humus compartment
     * @param hum2 Carbon in second humus compartment
     * @param c_soil Total carbon stock in soil
     * @param cout Carbon released to atmosphere (gross)
     * @param litin litter input to soil carbon pool
     * @param cwl_in coarse woody litter input
     * @param fwl_in fine woody litter
     * @param nwl_in non woody litter
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean Carbonsoil(int sid, int soil_id, int step, float c_trees,
            float cwl, float fwl, float nwl, float sol, float cel, float lig, 
            float hum1, float hum2, float c_soil, float cout, float litin, 
            float cwl_in, float fwl_in, float nwl_in) throws DBException, SQLException{

        Map<String,String> variables = new HashMap<>();
        variables.put("simulation_id", ""+sid);
        variables.put("soil_id", ""+soil_id);
        variables.put("step", ""+step);
        variables.put("c_trees", ""+c_trees);
        variables.put("c_soil", ""+c_soil);
        variables.put("cwl", ""+cwl);
        variables.put("fwl", ""+fwl);
        variables.put("nwl", ""+nwl);
        variables.put("sol", ""+sol);
        variables.put("cel", ""+cel);
        variables.put("lig", ""+lig);
        variables.put("hum1", ""+hum1);
        variables.put("hum2", ""+hum2);
        variables.put("c_soil", ""+c_soil);
        variables.put("cout", ""+cout);
        variables.put("litin", ""+litin);
        variables.put("cwl_in", ""+cwl_in);
        variables.put("fwl_in", ""+fwl_in);
        variables.put("nwl_in", ""+nwl_in);
        return writer.insert("carbonsoil", variables);
    }
    
    /**
     * Writes deadwood values into database.
     * Writes values sid, step, c_trees, c_stem, c-foliage, c_branches, 
     * c_cRoots, c_fRoots, c_soil, cwl, fwl, nwl, sol, cel, lig, hum1, hum2, 
     * and cout to deadwood -table
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param step current year
     * @param c_trees carbon in trees
     * @param c_stem carbon in tree stems
     * @param c_foliage carbon in foliage
     * @param c_branches carbon in branches
     * @param c_cRoots Carbon in coarse roots
     * @param c_fRoots Carbon in fine roots
     * @param c_soil  Total carbon in soil
     * @param cwl Carbon in coarse woody litter
     * @param fwl Carbon in fine woody litter
     * @param nwl Carbon in non-woody litter
     * @param sol Carbon in soluble compounds
     * @param cel Carbon in holocellulose
     * @param lig Carbon in lignin-like compounds
     * @param hum1 Carbon in first humus compartment
     * @param hum2 Carbon in second humus compartment
     * @param cout Carbon released to atmosphere (gross)
     * @return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean Deadwood(int sid, int step, float c_trees,
            float c_stem, float c_foliage, float c_branches, float c_cRoots, float c_fRoots,
            float c_soil, float cwl, float fwl, float nwl, float sol, float cel, float lig,
            float hum1, float hum2, float cout) throws DBException, SQLException{
        
//        if(!lock("deadwood")){
//           throw new DBException("Deadwood","Table deadwood is in use, cannot lock. Please try again later.");
//        }
        
        Map<String,String> variables = new HashMap<>();
        variables.put("simulation_id", ""+sid);
        variables.put("step", ""+step);
        variables.put("c_trees", ""+c_trees);
        variables.put("c_soil", ""+c_soil);
        variables.put("cwl", ""+cwl);
        variables.put("fwl", ""+fwl);
        variables.put("nwl", ""+nwl);
        variables.put("sol", ""+sol);
        variables.put("cel", ""+cel);
        variables.put("lig", ""+lig);
        variables.put("hum1", ""+hum1);
        variables.put("hum2", ""+hum2);
        variables.put("c_soil", ""+c_soil);
        variables.put("cout", ""+cout);
        variables.put("c_stem", ""+c_stem);
        variables.put("c_foliage", ""+c_foliage);
        variables.put("c_branches", ""+c_branches);
        variables.put("c_croots", ""+c_cRoots);
        variables.put("c_froots", ""+c_fRoots);
        return writer.insert("deadwood", variables);
    }
    
    /**
     * Writes felling values by matrix into the database.
     * Writes values sid,m_id, step, felRem, 
     * a_0_150 and v_0_150 to fellingmatrix -table.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param felRem Volume of removals from final fellings
     * @param a_0_150 distribution of felled ares by age classes
     * @param v_0_150 distribution of removals by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean FellingMatrix(int sid, int m_id, int step,
            float felRem, ArrayList a_0_150, ArrayList v_0_150) throws DBException, SQLException{

        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < a_0_150.size()-1; i++){
            variables.put("a_"+i*10+"_"+(i+1)*10, ""+a_0_150.get(i));
        }
        variables.put("a_>150", ""+a_0_150.get(15));
        for(int i = 0; i < v_0_150.size()-1; i++){
            variables.put("v_"+i*10+"_"+(i+1)*10, ""+v_0_150.get(i));  
        }
        variables.put("v_>150", ""+v_0_150.get(15));
        //variables
        variables.put("simulation_id", ""+sid);
        variables.put("step", ""+step);
        variables.put("matrix_id", ""+m_id);
        variables.put("felrem", ""+felRem);
        
        return writer.insert("fellingmatrix", variables);
    }
    
    /**
     * Writes felling residue values into a database.
     * Writes values sid, m_id, step, c_topsRes, c_brRes,
     * c_lvRes, c_crRes, c_topsRem, c_brRem, c_lvRem, c_crRem and n_0_150 
     * to fellresidues -table
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param c_topsRes carbon in stem residues added to the soil
     * @param c_brRes carbon in residues from branches added to the soil
     * @param c_lvRes carbon in from foliages added to the soil 
     * @param c_crRes carbon in coarse roots residues
     * @param c_topsRem carbon removed in top wood removals
     * @param c_brRem carbon removed in branches removals
     * @param c_lvRem carbon removed in foliage removals
     * @param c_crRem carbon removed in coarse roots removals
     * @param n_0_150 distribution of removals by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean FellResidues(int sid, int m_id, int step, float c_topsRes,
            float c_brRes, float c_lvRes, float c_crRes, float c_topsRem, float c_brRem,
            float c_lvRem, float c_crRem, ArrayList n_0_150) throws DBException, SQLException{
        
        if(n_0_150.size() != 16){
            throw new DBException("FellResidues","Illegal size in array n_0_150");
        }
        
        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < n_0_150.size()-1; i++){
            variables.put(""+i*10+"_"+(i+1)*10, ""+n_0_150.get(i));
        }
        variables.put(">150", ""+n_0_150.get(15));
        //variables
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("step", ""+step);
        variables.put("c_topsres", ""+c_topsRes);
        variables.put("c_brres", ""+c_brRes);
        variables.put("c_flres", ""+c_lvRes);
        variables.put("c_crres", ""+c_crRes);
        variables.put("c_topsrem", ""+c_topsRem);
        variables.put("c_brrem", ""+c_brRem);
        variables.put("c_flrem", ""+c_lvRem);
        variables.put("c_crrem", ""+c_crRem);
        
        return writer.insert("fellresidues", variables);
    }
    
    /**
     * Writes natural mortality values into database.
     * Writes values sid, m_id,step, nmort, dWood, 
     * c_Wood, dw_0_150 and nm_0_150 to natmort- table
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param nmort natural mortality
     * @param dWood total volume of standing dead wood
     * @param c_dWood carbon in standing dead wood
     * @param dw_0_150 distribution of deadwood by age classes
     * @param nm_0_150 distribution of natural mortality by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean NatMort(int sid,int m_id,int step, float nmort, float dWood, float c_dWood,
            ArrayList dw_0_150, ArrayList nm_0_150) throws DBException, SQLException{
        
        if(dw_0_150.size() != 16){
           throw new DBException("NatMort","Illegal size in array dw_0_150");
        }
        
        if(nm_0_150.size() != 16){
           throw new DBException("NatMort","Illegal size in array nm_0_150");
        }
        
        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < dw_0_150.size()-1; i++){
            variables.put("dw_"+i*10+"_"+(i+1)*10, ""+dw_0_150.get(i));
        }
        variables.put("dw_>150", ""+dw_0_150.get(15));
        for(int i = 0; i < nm_0_150.size()-1; i++){
            variables.put("nm_"+i*10+"_"+(i+1)*10, ""+nm_0_150.get(i));
        }
        variables.put("nm_>150", ""+nm_0_150.get(15));
        //variables
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("step", ""+step);
        variables.put("nmort", ""+nmort);
        variables.put("dwood", ""+dWood);
        variables.put("c_dwood", ""+c_dWood);
        
        return writer.insert("natmort", variables);
    }
    
    /**
     * Writes thinning values by matrix into database.
     * Writes values sid, m_id,step, thinRem, 
     * a_0_150 and v_0_150 to thinningmatrix -table.
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param thinRem volume of removals from  thinnings
     * @param a_0_150 distribution of area thinned by age classes
     * @param v_0_150 distribution of thinned volume by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean ThinningMatrix(int sid,int m_id, int step, 
            float thinRem, ArrayList a_0_150, ArrayList v_0_150) throws DBException, SQLException{
        
        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < a_0_150.size()-1; i++){
            variables.put("a_"+i*10+"_"+(i+1)*10, ""+a_0_150.get(i));
        }
        variables.put("a_>150", ""+a_0_150.get(15));
        for(int i = 0; i < v_0_150.size()-1; i++){
            variables.put("v_"+i*10+"_"+(i+1)*10, ""+v_0_150.get(i));  
        }
        variables.put("v_>150", ""+v_0_150.get(15));
        //variables
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("step", ""+step);
        variables.put("thinrem", ""+thinRem);
        
        return writer.insert("thinningmatrix", variables);
    }
    
    /**
     * Writes thinning residue values into database.
     * Writes values sid, m_id, step, c_topsRes, 
     * c_brRes, c_lvRes, c_crRes, c_topsRem, c_brRem, c_lvRem, c_crRem,
     * and n_0_150 to thinresidues -table 
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param c_topsRes carbon in stems residues added to the soil
     * @param c_brRes carbon in residues from branches added to the soil
     * @param c_lvRes carbon in residues from foliage added to the soil
     * @param c_crRes carbon in residues from coarse roots
     * @param c_topsRem carbon removed in top wood removals
     * @param c_brRem carbon removed in branches removals
     * @param c_lvRem carbon removed in foliage removals
     * @param c_crRem carbon removed in coarse roots removals
     * @param n_0_150 distribution of removals by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean ThinResidues(int sid,int m_id, int step, float c_topsRes,
            float c_brRes, float c_lvRes, float c_crRes, float c_topsRem, float c_brRem,
            float c_lvRem, float c_crRem, ArrayList n_0_150) throws DBException, SQLException{
        
        if(n_0_150.size() != 16){
            throw new DBException("ThinResidues","Illegal size in array n_0_150");
        }
        
        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < n_0_150.size()-1; i++){
            variables.put(""+i*10+"_"+(i+1)*10, ""+n_0_150.get(i));
        }
        variables.put(">150", ""+n_0_150.get(15));
        //variables
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("step", ""+step);
        variables.put("c_topsres", ""+c_topsRes);
        variables.put("c_brres", ""+c_brRes);
        variables.put("c_flres", ""+c_lvRes);
        variables.put("c_crres", ""+c_crRes);
        variables.put("c_topsrem", ""+c_topsRem);
        variables.put("c_brrem", ""+c_brRem);
        variables.put("c_flrem", ""+c_lvRem);
        variables.put("c_crrem", ""+c_crRem);
        
        return writer.insert("thinresidues", variables);
    }
    
    /**
     * Writes tree carbon values into database.
     * Writes values sid, m_id, step, c_trees, 
     * c_st_0_150, c_br_0_150, c_lv_0_150, c_cr_0_150 and c_fr_0_150 
     * to treec -table
     * @param sid Session id that will be present in all data entries saved into 
     * a database. Can be used for example to identify data from certain run of the EFISCEN tool.
     * @param m_id matrix id
     * @param step current year
     * @param c_trees carbon in trees
     * @param c_st_0_150 distribution of Carbon in stem by age classes
     * @param c_br_0_150 distribution of Carbon in branches by age classes
     * @param c_lv_0_150 distribution of Carbon in foliage by age classes
     * @param c_cr_0_150 distribution of Carbon in coarse roots by age classes
     * @param c_fr_0_150 distribution of Carbon in fine roots by age classes
     * @return return Returns <code>true</code> if successful else <code>false</code>
     * @throws DBException when writing to database was unsuccessful
     * @throws SQLException
     */
    public boolean TreeC(int sid, int m_id, int step, float c_trees, ArrayList c_st_0_150,
            ArrayList c_br_0_150, ArrayList c_lv_0_150, ArrayList c_cr_0_150, ArrayList c_fr_0_150) throws DBException, SQLException{
        
        if(c_st_0_150.size() != 16){
            throw new DBException("TreeC","Illegal size in array a_0_150");
        }
        if(c_br_0_150.size() != 16){
            throw new DBException("TreeC","Illegal size in array v_0_150");
        }
        
        if(c_lv_0_150.size() != 16){
            throw new DBException("TreeC","Illegal size in array v_0_150");
        }
        
        if(c_cr_0_150.size() != 16){
            throw new DBException("TreeC","Illegal size in array v_0_150");
        }
        
        Map<String,String> variables = new HashMap<>();
        //put series in a map.
        for(int i = 0; i < c_st_0_150.size()-1; i++){
            variables.put("c_st_"+i*10+"_"+(i+1)*10, ""+c_st_0_150.get(i));
        }
        variables.put("c_st_>150", ""+c_st_0_150.get(15));
        for(int i = 0; i < c_br_0_150.size()-1; i++){
            variables.put("c_br_"+i*10+"_"+(i+1)*10, ""+c_br_0_150.get(i));
        }
        variables.put("c_br_>150", ""+c_br_0_150.get(15));
        for(int i = 0; i < c_lv_0_150.size()-1; i++){
            variables.put("c_fl_"+i*10+"_"+(i+1)*10, ""+c_lv_0_150.get(i));
        }
        variables.put("c_fl_>150", ""+c_lv_0_150.get(15));
        for(int i = 0; i < c_cr_0_150.size()-1; i++){
            variables.put("c_cr_"+i*10+"_"+(i+1)*10, ""+c_cr_0_150.get(i));
        }
        variables.put("c_cr_>150", ""+c_cr_0_150.get(15));
        for(int i = 0; i < c_fr_0_150.size()-1; i++){
            variables.put("c_fr_"+i*10+"_"+(i+1)*10, ""+c_fr_0_150.get(i));
        }
        variables.put("c_fr_>150", ""+c_fr_0_150.get(15));
        //variables
        variables.put("step", ""+step);
        variables.put("simulation_id", ""+sid);
        variables.put("matrix_id", ""+m_id);
        variables.put("c_trees", ""+c_trees);
        
        return writer.insert("treec", variables);
    }
    
    /**
     * Write matrix ids to matrix -table. If same primary key already exists, id 
     * will not be replaced.
     * @param country_id
     * @param region_id
     * @param owner_id
     * @param species_id
     * @param site_id
     * @return True if writing was successful, false if not.
     * @throws SQLException 
     */
    public Integer Matrix(int country_id, int region_id, int owner_id, 
            int species_id, int site_id) throws SQLException{
        Map<String,String> variables = new HashMap<>();
        variables.put("country_id", ""+country_id);
        variables.put("region_id", ""+region_id);
        variables.put("owner_id", ""+owner_id);
        variables.put("species_id", ""+species_id);
        variables.put("site_id", ""+site_id);
        return writer.insertAutoID("matrix", variables);
    }
    
    /**
     * Saves simulation information to the database table 'simulation'. 
     * @param scenarioID Scenario id for this simulation.
     * @param countryID Country id for this simulation.
     * @param projectID Project id for this simulation.
     * @param parameterFile Name of the .prs-file for this simulation.
     * @return Unique id for this simulation.
     * @throws SQLException 
     */
    public int simulation(int scenarioID, int countryID, int projectID, 
            String parameterFile) throws SQLException {
        Map<String,String> variables = new HashMap<>();
        variables.put("scenario_id", ""+scenarioID);
        variables.put("country_id", ""+countryID);
        variables.put("project_id", ""+projectID);
        variables.put("parameter_file", "'"+parameterFile+"'");
        variables.put("timestamp", ""+(System.currentTimeMillis()/1000));
        return writer.insertAutoID("simulation", variables);
    }
}
