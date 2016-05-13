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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * This class contains methods to write values to the database.
 * 
 */
public class Writer {
    
    private Connector connection;
    private Statement statement;
    private DriverType driverType;
    private boolean connected;
    private String fieldQuote;
    /**
     * Forms a connection to a database. Username and password can be null
     * if the database and the driver do not need them.
     * @param server Address of the database or ODBC data source name. For ODBC drivers name of the configured data source.
     * For jdbc drivers database address and database name (for example //localhost/my_database ).
     * @param database Database name
     * @param port Port number
     * @param usr username to use when connecting to database.
     * @param pass password to use when connecting to database.
     * @param driverType Type of the driver to use when connecting to database.
     * @throws DatabaseComponentsException
     */
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public Writer(String server, String database, int port, String usr,
            String pass,DriverType driverType) throws DatabaseComponentsException {
        connected = false;
        statement = null;
        connection = new Connector(usr,pass,server,database,port,driverType);
        statement = connection.getStatement();
        this.driverType = driverType;
        this.fieldQuote = this.driverType.quote;
        if(driverType==DriverType.Odbc) {
            String databaseName = "";
            try {
                DatabaseMetaData meta = connection.getConnection().getMetaData();
                databaseName = meta.getDatabaseProductName();
                System.out.println("DATABASE = "+databaseName);
                if(databaseName.equals("MySQL")) {
                    this.driverType = DriverType.MySql;
                    this.fieldQuote = DriverType.MySql.quote;
                }
                if(databaseName.equals("PostgreSQL")) {
                    this.driverType = DriverType.PostgreSQL;
                    this.fieldQuote = DriverType.PostgreSQL.quote;
                }
            } catch (SQLException ex) {
                throw new DatabaseComponentsException("Writer", "Error when detecting driver");
            }
        }
        connected = true;
    }
    /**
     * Forms a connection to a database.
     * @param addr Database URL
     * @param usr User name
     * @param pass Password
     * @param driverType Driver type
     * @throws DatabaseComponentsException 
     */
    public Writer(String addr,String usr,
            String pass,DriverType driverType) throws DatabaseComponentsException {
        connected = false;
        statement = null;
        connection = new Connector(usr,pass,addr,driverType);
        statement = connection.getStatement();
        this.driverType = driverType;
        this.fieldQuote = this.driverType.quote;
        if(driverType==DriverType.Odbc) {
            String databaseName = "";
            try {
                DatabaseMetaData meta = connection.getConnection().getMetaData();
                databaseName = meta.getDatabaseProductName();
                System.out.println("DATABASE = "+databaseName);
                if(databaseName.equals("MySQL")) {
                    this.driverType = DriverType.MySql;
                    this.fieldQuote = DriverType.MySql.quote;
                }
                if(databaseName.equals("PostgreSQL")) {
                    this.driverType = DriverType.PostgreSQL;
                    this.fieldQuote = DriverType.PostgreSQL.quote;
                }
            } catch (SQLException ex) {
                throw new DatabaseComponentsException("Writer", "Error when detecting driver");
            }
        }
        connected = true;
    }

    /**
     * Writes given variables to database table. Variables to write are given
     * in a Map.
     * @param table Name of the table to which values are written.
     * @param variables Map containing variables to write (Column, value). Key 
     * indicates the column name and value gives the value which is written to the column.<br> The statement
     * is following form "INSERT INTO "table" (`key1`, `key2`, ..., `keyN`) VALUES 
     * (value1, value2, ..., valueN)"
     * @return True if write was successful, false if not.
     * @throws SQLException 
     */
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public boolean insert(String table, Map<String,String> variables) throws SQLException {
        StringBuilder query = new StringBuilder();
        //Insert following columns
        query.append("INSERT INTO ").append(table).append(" (");

        int i = 0;
        for(String var : variables.keySet()) {
            var = this.fieldQuote+var+this.fieldQuote;
            query.append(var).append(",");
            i++;
        }
        //following values are inserted
        int lastpos = query.length()-1;
        query.replace(lastpos,lastpos+1,")");
        query.append(" VALUES (");
        int p = 0;
        for(String val : variables.values()) {
            query.append(val).append(",");
            p++;
        }
        
        //replace last comma with closing bracket
        lastpos = query.length()-1;
        query.replace(lastpos,lastpos+1,")");
        //Execute query
        String qr = query.toString();
        //System.out.println("query = "+qr);
        int result = 0;
        result = statement.executeUpdate(qr);                               
        return result != 0;
    }
    
    public boolean update(String table,Map<String,String> variables, 
            Map<String,String> where) throws SQLException {
        StringBuilder query = new StringBuilder();
        //Insert following columns
        query.append("UPDATE \"").append(table).append("\" SET ");
        int i = 0;
        for(String col : variables.keySet()) {
            String val = variables.get(col);
            if(driverType == DriverType.MySql) val = "'"+val+"'";
            if(i!=0) query.append(" ,");
            if(driverType == DriverType.MySql) col = "`"+col+"`";
            query.append("\"").append(col).append("\"").append("=\"").append(val).append("\" ");
            i++;
        }
        //following values are inserted
        query.append(" WHERE ");
        int p = 0;
        for(String col : where.keySet()) {
            String val = where.get(col);
            if(p!=0) query.append(", ");
            if(driverType == DriverType.MySql) val = ""+val+"";
            if(driverType == DriverType.MySql) col = "`"+col+"`";
            query.append("\"").append(col).append("\"=\"").append(val).append("\"");
            p++;
        }
        //query.append(")");
        //Execute query
        String qr = query.toString();
        int result = 0;
            if(driverType == DriverType.Odbc || driverType == DriverType.PostgreSQL){
                result = statement.executeUpdate(qr);                               
            } else if(driverType == DriverType.MySql){
                qr = qr.replace("\"", "");
                result = statement.executeUpdate(qr);   
            }
        return result != 0;
    }
    
    /**
     * Writes variables to tables with automatic ID assignment. The automatically
     * created ID is returned. <br><br>Query to get the generated id is for <br>MS Access 
     * "SELECT @@IDENTITY" <br>MySQL "SELECT LAST_INSERT_ID()"
     * <br>PostgreSQL "SELECT CURRVAL (pg_get_serial_sequence('[table_name]','id'));"
     * @param table Name of the table to which values are written.
     * @param variables Map containing variables to write (Column, value). Key 
     * indicates the column name and value gives the value which is written to the column.<br> The statement
     * is following form "INSERT INTO "table" (`key1`, `key2`, ..., `keyN`) VALUES 
     * (value1, value2, ..., valueN)"
     * @return Automatically created ID or null if not successful.
     * @throws SQLException 
     */
    @Support(database = "Odbc")
    @Support(database = "PostgreSQL")
    @Support(database = "MySql")
    public int insertAutoID(String table, Map<String,String> variables) throws SQLException {
        StringBuilder query = new StringBuilder();
        //Insert following columns
        query.append("INSERT INTO ").append(this.fieldQuote).append(table).append(this.fieldQuote).append(" (");
        int i = 0;
        for(String var : variables.keySet()) {
            var = this.fieldQuote+var+this.fieldQuote;
            query.append(var).append(",");
            i++;
        }
        int lastpos = query.length()-1;
        query.replace(lastpos,lastpos+1,")");
        //following values are inserted
        query.append(" VALUES (");
        int p = 0;
        for(String val : variables.values()) {
            query.append(val).append(",");
            p++;
        }
        lastpos = query.length()-1;
        query.replace(lastpos,lastpos+1,")");
        //Execute query
        String qr = query.toString();
        
        System.out.println("query = "+qr);

        ResultSet result = null;                       

        statement.executeUpdate(qr);
        String id_qr = ("SELECT @@IDENTITY");
        if(driverType == DriverType.MySql){
            id_qr = ("SELECT LAST_INSERT_ID()");
        }
        if(driverType == DriverType.PostgreSQL){
            id_qr = ("SELECT CURRVAL (pg_get_serial_sequence('"+table+"','id'));");
        }
//        }else if(driverType == DriverType.PostgreSQL){
//            id_qr = ("SELECT LAST_INSERT_ID()");
//        }
        result = statement.executeQuery(id_qr);
        int resultID = -1;
        if(result.next())
            resultID = result.getInt(1);
        return resultID;
    }
    
    @Support(database = "Odbc")
    @Support(database = "PostgreSQL")
    @Support(database = "MySql")
    Connector getConnector() {
        return connection;
    }
    
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")        
    DriverType getDriverType() {
        return driverType;
    }
    
    @Support(database = "Odbc")
    @Support(database = "MySql")
    @Support(database = "PostgreSQL")
    public boolean isConnected() {
        return connected;
    }
}
