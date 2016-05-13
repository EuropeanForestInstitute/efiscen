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
package efi.efiscen.cli;

import efi.efiscen.io.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.tools.JavaFileManager.Location;

/**
 * Directs System.out and System.err into a logfile. CLIVerboseLogger directs
 * messages coming to System.out to event log and messages coming to System.err
 * to error log. Both of these are located in folder "logs". \n
 * Debug logger can also be created by printing "Debug:" to either of these streams. 
 * To give more information about
 * the session that the debug messages are related to, id can be given to messages by printing
 * in a single line "Debug:id:", followed by the id. Debug logs are printed to
 * folder "debug".\n
 * Logger thread can be ended by stopLogging method
 * 
 */

public class CLIVerboseLogger extends Thread {
    private Logger debug;
    private Logger errorLogger = null;
    private Logger eventLogger = null;
    private String id = "";
    private String logpath;
    private boolean run;
    
    /**
     * Creates new CLIVerboseLogger. 
     */
    public CLIVerboseLogger() {
        String userfolder = System.getProperty("user.home");
        String separator = File.separator;
        logpath = userfolder + separator + "EFISCEN" + separator;
        File dir = new File(logpath);
        if(!dir.exists()){
            dir.mkdir();
        }
        debug = new Logger("./"+System.currentTimeMillis() + "Debug.log", logpath+"debug");
    }
    
    /**
     * Starts logger. Starts directing System.out and System.err to Logger.
     * Logger thread can be ended by printing "END" to System.out or System.err.
     */
    @Override
    public void run() {
        run = true;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream reader = new PrintStream(byteArrayOutputStream);
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        PrintStream reader2 = new PrintStream(byteArrayOutputStream1);
        //to save standard out end err
        PrintStream stdout_save = System.out;
        PrintStream stderr_save = System.err;
        System.setErr(reader);
        System.setOut(reader2);
        String toString = "";
        String toString1 = "";
        
        try {
            do {
                toString = byteArrayOutputStream.toString();
                toString1 = byteArrayOutputStream1.toString();
                byteArrayOutputStream1.reset();
                byteArrayOutputStream.reset();
                 if(toString!=null && toString.length()>0) {
                    final String timeStamp = new SimpleDateFormat("(HH:mm) ").format(Calendar.getInstance().getTime());
                    String a = toString;
                    //direct debug messages to debuglog
                            while(a.contains("Debug:")){
                                int index1 = a.indexOf("Debug:")+6;
                                int index2 = a.indexOf("\n", index1 );
                                if(a.substring(index1,index1+3).equals("id:")){
                                    id = a.substring(index1+3, index2);
                                    String eventLogName = id + "events.txt";
                                    String errorLogName = id + "errors.txt";
                                    errorLogger = new Logger(errorLogName,logpath+"logs");
                                    eventLogger = new Logger(eventLogName,logpath+"logs");
                                }else
                                debug.logEntry(id, a.substring(index1, index2));
                                a = a.substring(0, index1-6) + a.substring(index2+1);
                            }
                    if(a.length()>7)
                            if(errorLogger!=null)
                                errorLogger.logEntry(a);
                    byteArrayOutputStream.reset();
                }
                if(toString1!=null && toString1.length()>0) {
                    String a = toString1;
                    //direct debug messages to debuglog
                            while(a.contains("Debug:")){
                                int index1 = a.indexOf("Debug:")+6;
                                int index2 = a.indexOf("\n", index1 );
                                if(a.substring(index1,index1+3).equals("id:")){
                                    id = a.substring(index1+3, index2-1);
                                    String eventLogName = id + "events.txt";
                                    String errorLogName = id + "errors.txt";
                                    errorLogger = new Logger(errorLogName,logpath+"logs");
                                    eventLogger = new Logger(eventLogName,logpath+"logs");
                                    
                                }else
                                debug.logEntry(id, a.substring(index1, index2));
                                a = a.substring(0, index1-6) + a.substring(index2+1);
                            }
                    if(a.length()>7)
                            if(eventLogger!=null)
                                eventLogger.logEntry(a);
                    byteArrayOutputStream1.reset();
                }
                Thread.sleep(50);
            } while(toString1!=null && run);
        } catch (InterruptedException ex) {
            System.setErr(stderr_save);
            System.setOut(stdout_save);
            return;
        }
//        System.setErr(stderr_save);
//        System.setOut(stdout_save);
//        System.out.println("end logging");
        debug.close();
        
    }
    
    public void stopLogging(){
        run = false;
    }
}
