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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Shows features and which database types support them.
 * 
 */
public class Features {
    
    private final DriverType driverType;
    private final Map<String,Boolean> supportedMethods;
    private final String className;
    
    public Features(DriverType driverType, Class element) {
        supportedMethods = new HashMap<>();
        className = element.getName();
        this.driverType = driverType;
        Method[] methods = element.getMethods();
        for(Method method : methods) {
            boolean support = false;
            Annotation[] annotations = method.getAnnotations();
            for(Annotation ann : annotations) {
                if(ann.annotationType().equals(Supports.class)) {
                    Support[] supports = ((Supports)ann).value();
                    for(Support sp : supports) {
                        if(sp.database().equals(driverType.toString())) {
                            support = true;
                            break;
                        }
                    }
                }
            }
            supportedMethods.put(method.getName(), support);
        }
    }
    
    public boolean supports(String method)  {
        return supportedMethods.get(method);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Class : ");
        builder.append(className);
        builder.append(System.lineSeparator());
        for(String key : supportedMethods.keySet()) {
            builder.append(key);
            builder.append(" : ");
            builder.append(supportedMethods.get(key));
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
