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

import efi.efiscen.io.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import static java.lang.Thread.MIN_PRIORITY;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Directs System.out and System.err into a provided TextArea. Also directs messages
 * starting with "Debug:" to a debug file located in debug.
 * 
 */

public class ConsolePrinter extends Thread {
    
    private TextArea console;
    private AtomicBoolean isCancelled = new AtomicBoolean(false);
    private Logger debug;
    String s = null;
    String id = "";
    
    /**
     * Constructor for ConsolePrinter
     * @param console text area
     */
    public ConsolePrinter(TextArea console) {
        this.console = console;
        this.setDaemon(true);
        //create debuglog
        String userfolder = System.getProperty("user.home");
        String separator = File.separator;
        String logpath = userfolder + separator + "EFISCEN" + separator;
        File dir = new File(logpath);
        if(!dir.exists()){
            dir.mkdir();
        }
        debug = new Logger(System.currentTimeMillis() + "Debug.log", logpath + "debug");
    }
    
    /**
     * Starts reading of System.out and System.err messages from the stream.
     * Prints System.out and System.err messages with timestamp into the provided
     * text area.
     */
    @Override
    public void run() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream reader = new PrintStream(byteArrayOutputStream);
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        PrintStream reader2 = new PrintStream(byteArrayOutputStream1);
        System.setErr(reader);
        System.setOut(reader2);
        while(!isCancelled.get()) {
            try {
                final String toString = byteArrayOutputStream.toString();
                final String toString1 = byteArrayOutputStream1.toString();
                byteArrayOutputStream1.reset();
                byteArrayOutputStream.reset();
                 if(toString.length()>0) {
                    final String timeStamp = new SimpleDateFormat("(HH:mm) ").format(Calendar.getInstance().getTime());
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            String a = toString;
                            while(a.contains("Debug:")){
                                int index1 = a.indexOf("Debug:")+6;
                                int index2 = a.indexOf("\n", index1 );
                                if(a.substring(index1,index1+3).equals("id:")){
                                    id = a.substring(index1+3, index2);
                                }else
                                debug.logEntry(id, a.substring(index1, index2));
                                a = a.substring(0, index1-6) + a.substring(index2+1);
                            }
                            a = a.replace("\n", ("\n"+timeStamp));
                            console.appendText(timeStamp + a.substring(0, a.length()-timeStamp.length()));
                        } 
                    });
                }
                if(toString1.length()>0) {
                    final String timeStamp = new SimpleDateFormat("(HH:mm) ").format(Calendar.getInstance().getTime());
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            String a = toString1;
                            //direct debug messages to debuglog
                            while(a.contains("Debug:")){
                                int index1 = a.indexOf("Debug:")+6;
                                int index2 = a.indexOf("\n", index1 );
                                if(a.substring(index1,index1+3).equals("id:")){
                                    id = a.substring(index1+3, index2);
                                }else
                                debug.logEntry(id, a.substring(index1, index2));
                                a = a.substring(0, index1-6) + a.substring(index2+1);
                            }
                            a = a.replace("\n", ("\n"+timeStamp));
                            if(a.length()>7)
                            console.appendText(timeStamp + a.substring(0, a.length()-timeStamp.length()));
                          
                        } 
                    });
                    byteArrayOutputStream1.reset();
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
