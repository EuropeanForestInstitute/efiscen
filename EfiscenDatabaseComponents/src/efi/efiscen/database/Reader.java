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
package efi.efiscen.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reads data from database.
 * 
 */
public class Reader {
    
    private Connector connection;
    private Statement statement;
    private DriverType driverType;
    private boolean connected;
    
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Reader(Writer writer) {
        connection = writer.getConnector();
        statement = connection.getStatement();
        driverType = writer.getDriverType();
        connected = writer.isConnected();
    }
    
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Reader(Connector connector) {
        this.connection = connector;
        driverType = connector.getDriverType();
        connected = false;
        statement = connection.getStatement();
    }
    
    /**
     * Constructs a database connection with given driver type. Username and password
     * can be null if the database and driver do not need them.
     * @param server Address of the database or ODBC data source name. For ODBC drivers name of the configured data source.
     * For jdbc drivers database address and database name (for example //localhost/my_database ).
     * @param database Database name
     * @param port Port number
     * @param usr Username of the database.
     * @param pass Password to to database.
     * @param driverType Type of the driver to use when connecting to database.
     * @throws efi.efiscen.database.DatabaseComponentsException
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Reader(String server, String database, int port, String usr,
            String pass, DriverType driverType) throws DatabaseComponentsException {
        connected = false;
        statement = null;
            connection = new Connector(usr,pass,server,database,port,driverType);
            statement = connection.getStatement();
            this.driverType = driverType;
            if(driverType==DriverType.Odbc) {
                String databaseName = "";
                try {
                    DatabaseMetaData meta = connection.getConnection().getMetaData();
                    databaseName = meta.getDatabaseProductName();
                    System.out.println("DATABASE = "+databaseName);
                    if(databaseName.equals("MySQL")) this.driverType = DriverType.MySql;
                    if(databaseName.equals("PostgreSQL")) this.driverType = DriverType.PostgreSQL;
                } catch (SQLException ex) {
                    throw new DatabaseComponentsException("Writer", "Error when detecting driver");
                }
            }
        connected = true;
    }
    /**
     * Constructs a database connection with given driver type.
     * @param addr Database URL
     * @param usr Username
     * @param pass Password
     * @param driverType Driver type
     * @throws DatabaseComponentsException 
     */
    public Reader(String addr,String usr,
            String pass,DriverType driverType) throws DatabaseComponentsException {
        connected = false;
        statement = null;
            connection = new Connector(usr,pass,addr,driverType);
            statement = connection.getStatement();
            this.driverType = driverType;
            if(driverType==DriverType.Odbc) {
                String databaseName = "";
                try {
                    DatabaseMetaData meta = connection.getConnection().getMetaData();
                    databaseName = meta.getDatabaseProductName();
                    System.out.println("DATABASE = "+databaseName);
                    if(databaseName.equals("MySQL")) this.driverType = DriverType.MySql;
                    if(databaseName.equals("PostgreSQL")) this.driverType = DriverType.PostgreSQL;
                } catch (SQLException ex) {
                    throw new DatabaseComponentsException("Writer", "Error when detecting driver");
                }
            }
        connected = true;
    }
    
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Get data from multiple columns. Returns all variables from selected columns.
     * @param table Table from which data is fetched.
     * @param columns Set of column names from which data is fetched.
     * @param where Variables for WHERE clause (Column_name, value).
     * @return Returns a set containing a Map for each returned row. Maps
     * store values by column name.
     * @throws java.sql.SQLException
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Set<Map<String,String>> getVariables(String table, Set<String> columns, 
            Map<String,String> where) throws SQLException {
        Set<Map<String,String>> ret = new HashSet<>();
        ResultSet result = null;
        String query = "SELECT ";
        int p = 0;
        if(columns!=null) {
            if(where!=null) {
                for(String s : where.keySet()) {
                    if(p!=0) query+= ", ";
                    query += " \""+s+"\" ";
                    p++;
                }
            }
            for(String col : columns) {
                if(p!=0) query+= ", ";
                query += " \""+col+"\" ";
                p++;
            }
            
        }
        if(columns==null) query += " * ";
        query += " FROM \""+table+"\" ";
        int i = 0;
        if(where!=null) {
            query += "WHERE";
            for(String s : where.keySet()) {
                if(i!=0) query += " AND ";
                String val = where.get(s);
                if(driverType == DriverType.MySql) query += " \""+s+"\"='"+val+"'";
                else query += " \""+s+"\"="+val;
                i++;
            }
        }
        if(driverType == DriverType.Odbc || driverType == DriverType.PostgreSQL){
            result = statement.executeQuery(query);                               
        } else if(driverType == DriverType.MySql){
            query = query.replace("\"", "");
            result = statement.executeQuery(query);   
        }
        if(result==null) {
            System.err.println("error when executing query");
            return null;
        }
        
        if(columns==null) {
            ResultSetMetaData metaData = result.getMetaData();
            columns = new HashSet<>();
            int count = metaData.getColumnCount();
            for (int x = 1; x <= count; x++)
            {
                columns.add(metaData.getColumnName(x));
            }
        }
        while(result.next()) {
            Map<String,String> data = new TreeMap<>();
            ret.add(data);
            for(String col : columns) {
                try {
                    String str = result.getString(col);
                    if(str!=null) data.put(col, str);
                } catch (SQLException ex) {
                    Object obj = result.getObject(col);
                    if(obj!=null) data.put(col, obj.toString());
                }
            }
            if(where!=null) {
                for(String col : where.keySet()) {
                    Object obj = result.getObject(col);
                    if(obj!=null) data.put(col, obj.toString());
                }
            }
        }
        return ret;
    }
    
    /**
     * Get column names from table.
     * @param table Table from where column names are read.
     * @return List of column names.
     * @throws SQLException 
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public List<String> getColumnNames(String table) throws SQLException{
        ResultSet result = null;
        List<String> names = new LinkedList();
        String query = "SELECT `COLUMN_NAME` FROM "
                + "`INFORMATION_SCHEMA`.`COLUMNS`WHERE "
                + "`TABLE_NAME`='"+table+"'";
        result = statement.executeQuery(query); 
        while(result.next()) {
            String vol = result.getString("COLUMN_NAME");
            names.add(vol);
        }
        return names;
    }
    
    /**
     * Get a float variable from the specified column. If multiple rows are returned
     * the column values will be added together.
     * @param table Table from which data is fetched.
     * @param column Column from which data is fetched.
     * @param where Variables for WHERE clause (Column_name, value).
     * @return Variable value as float.
     * @throws java.sql.SQLException
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Float getVariableFloat(String table, String column, Map<String,String> where) throws SQLException {
        float total = 0.f;
        ResultSet result = null;
        String query = "SELECT ";
        //what to select
        for(String s : where.keySet()) {
            query += " \""+s+"\", ";
        }
        query += " \""+column+"\" ";
        query += " FROM \""+table+"\" WHERE";
        int i = 0;
        //Where
        for(String s : where.keySet()) {
            if(i!=0) query += " AND ";
            String val = where.get(s);
            query += " \""+s+"\"="+val;
            i++;
        }
        if(driverType == DriverType.Odbc || driverType == DriverType.PostgreSQL){
            result = statement.executeQuery(query);   
            //Mysql database by default doesnt like double quotes
        } else if(driverType == DriverType.MySql){
            query = query.replace("\"", "");
            result = statement.executeQuery(query);   
        }
        if(result==null) {
            System.err.println("Query return is null");
            return null;
        }
        while(result.next()) {
            Float vol = result.getFloat(column);
            total += vol;
        }
        return total;
    }
    
    /**
     * Close the statement.
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public void close() {
        try {
            statement.close();
        } catch (SQLException ex) {}
    }
}
