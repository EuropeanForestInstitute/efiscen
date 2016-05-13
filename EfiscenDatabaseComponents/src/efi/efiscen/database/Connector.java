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

import java.sql.*;
import java.util.*;
/**
 * This class includes methods to make a connection to a database.
 * 
 */
public class Connector {
    
    private Connection connection = null;
    private Statement statement = null;
    private final DriverType driverType;
    private String databaseName;
    /**
     * Makes a connection to a database. Username and password can be null
     * if the database and the driver do not need them.
     * @param username Username for database.
     * @param password Password for database.
     * @param databaseName Name of database (URL)
     * @param driverType Type of the driver to use when connecting to database.
     * MySql, PostgreSQL and Odbc currently supported.
     * @throws DatabaseComponentsException If connection was unsuccessful.
     */
    public Connector(String username, String password, String databaseName, 
            DriverType driverType) throws DatabaseComponentsException{
        this.databaseName = databaseName;
        this.driverType = driverType;
        try {
           // Class.forName("com.mysql.jdbc.Driver");
            StringBuilder sb = null;
            if(driverType==DriverType.MySql)  {
                sb = new StringBuilder("jdbc:mysql:");
                sb.append(databaseName);
            }
            else if(driverType==DriverType.Odbc) {
                sb = new StringBuilder("jdbc:odbc:");
                sb.append(databaseName);
                //sb = new StringBuilder("jdbc:odbc:Driver={Microsoft Access Driver (*.accdb)};DBQ="+dbName);               
            }else if(driverType==DriverType.PostgreSQL) {
                sb = new StringBuilder("jdbc:postgresql:");
                sb.append(databaseName);
                //sb = new StringBuilder("jdbc:odbc:Driver={Microsoft Access Driver (*.accdb)};DBQ="+dbName);               
            }
            if(sb==null) {
                System.err.println("Driver not found");
                return;
            }
            //temp for debugging:
            List drivers = Collections.list(DriverManager.getDrivers());
            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = (Driver) drivers.get(i);
                String name = driver.getClass().getName();
                System.out.println(name);

                boolean isJdbcCompliant = driver.jdbcCompliant();
                System.out.println(isJdbcCompliant);
            }
            Properties props = new Properties();
            props.setProperty("user",username);
            props.setProperty("password",password);
            props.setProperty("useSSL","true");
            props.setProperty("verifyServerCertificate", "false");
            props.setProperty("requireSSL", "false");
            connection = DriverManager.getConnection(sb.toString(), props);
            //connection = DriverManager.getConnection(sb.toString(), username, password);
            if(connection != null){
                statement = connection.createStatement();
                System.out.println("Connected to: "+databaseName);
            }else{
                throw new DatabaseComponentsException("Connections constructor","failed to connect to "+sb);
            }
        } catch (SQLException ex) {
            throw new DatabaseComponentsException("Connections constructor",ex.getMessage());
        }
    }
    /**
     * Makes a connection to a database. Username and password can be null
     * if the database and the driver do not need them.
     * @param username Username for database.
     * @param password Password for database.
     * @param server Server URL
     * @param port Port to connect
     * @param database Database name 
     * @param driverType Type of the driver to use when connecting to database.
     * MySql, PostgreSQL and Odbc currently supported.
     * @throws DatabaseComponentsException If connection was unsuccessful.
     */
    public Connector(String username, String password, String server, String database, 
            int port, DriverType driverType) throws DatabaseComponentsException{
        this.databaseName = server;
        this.driverType = driverType;
        try {
           // Class.forName("com.mysql.jdbc.Driver");
            StringBuilder sb = null;
            if(driverType==DriverType.MySql)  {
                sb = new StringBuilder("jdbc:mysql:");
                this.databaseName = "//"+server+":"+port+"/"+database;
                sb.append(databaseName);
            }
            else if(driverType==DriverType.Odbc) {
                sb = new StringBuilder("jdbc:odbc:");
                this.databaseName = database;
                sb.append(databaseName);
                //sb = new StringBuilder("jdbc:odbc:Driver={Microsoft Access Driver (*.accdb)};DBQ="+dbName);               
            }else if(driverType==DriverType.PostgreSQL) {
                sb = new StringBuilder("jdbc:postgresql:");
                this.databaseName = "//"+server+":"+port+"/"+database;
                sb.append(databaseName);  
            }
            if(sb==null) {
                System.err.println("Driver not found");
                return;
            }
            //temp for debugging:
            List drivers = Collections.list(DriverManager.getDrivers());
            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = (Driver) drivers.get(i);
                String name = driver.getClass().getName();
                System.out.println(name);
//      int majorVersion = driver.getMajorVersion();
//      System.out.println(majorVersion);
//      int minorVersion = driver.getMinorVersion();
//      System.out.println(minorVersion);
                boolean isJdbcCompliant = driver.jdbcCompliant();
                System.out.println(isJdbcCompliant);
            }
            Properties props = new Properties();
            props.setProperty("user",username);
            props.setProperty("password",password);
            props.setProperty("useSSL","true");
            props.setProperty("verifyServerCertificate", "false");
            props.setProperty("requireSSL", "false");
            connection = DriverManager.getConnection(sb.toString(), props);
            if(connection != null){
                statement = connection.createStatement();
                System.out.println("Connected to: "+server);
            }else{
                throw new DatabaseComponentsException("Connections constructor","failed to connect to "+sb);
            }
        } catch (SQLException ex) {
            throw new DatabaseComponentsException("Connections constructor",ex.getMessage());
        }
    }
    
    public DriverType getDriverType() {
        return driverType;
    }

    /**
     * Returns <code>Statement</code> from <code>Connection</code>
     * @return <code>Statement</code>
     */
    public Statement getStatement() {
        return statement;
    }
    
    /**
     * Get connection Java SQL connection.
     * @return Connection from this Connector instance.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns name of chosen database.
     * @return Name of database,
     */
    String getDatabaseName() {
        return databaseName;
    }
}
