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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Stores the settings. Stores settings for default outputPath, default inputpath, default username,
 * default password, default DBaddress and default DBtype. The class is  used by 
 * the SettingsController class.
 * 
 */
public class Settings {
    private final StringProperty defaultOutputPath = new SimpleStringProperty();
    private final StringProperty defaultInputPath = new SimpleStringProperty();
    private final StringProperty defaultUsername = new SimpleStringProperty();
    private final StringProperty defaultPassword = new SimpleStringProperty();
    private final StringProperty defaultDBAddress = new SimpleStringProperty();
    private final StringProperty defaultDBType = new SimpleStringProperty();
    private final StringProperty defaultPort = new SimpleStringProperty();
    
    /**
     * Returns default output path.
     * @return defaultOutputPath
     */
    public StringProperty getDefaultOutputPath() {
        return defaultOutputPath;
    }
    /**
     * Returns default input path
     * @return defaultInputPath
     */
    public StringProperty getDefaultInputPath() {
        return defaultInputPath;
    }

    public StringProperty getDefaultPort() {
        return defaultPort;
    }
    
    /**
     * Returns default username
     * @return defaultUsername
     */
    public StringProperty getDefaultUsername() {
        return defaultUsername;
    }
    
    /**
     * Returns default password
     * @return defaultPassword
     */
    public StringProperty getDefaultPassword() {
        return defaultPassword;
    }

    /**
     * Returns default database address
     * @return defaultDBAddress
     */
    public StringProperty getDefaultDBAddress() {
        return defaultDBAddress;
    }
    
    /**
     * Returns default database type
     * @return defaultDBType
     */
    public StringProperty getDefaultDBType() {
        return defaultDBType;
    }
}
