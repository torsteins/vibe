/*
 * The Vibe
 * 
 * 18-549 Embedded Systems Design
 * Spring 2013
 * Group 6
 * 
 * Debjani Biswas <dbiswas@cmu.edu>
 * Xiao Bo Zhao <xiaoboz@andrew.cmu.edu>
 * Jonathan Carreon <jcarreon@andrew.cmu.edu>
 * Torstein Stromme <tstroemm@andrew.cmu.edu>
 * 
 * 
 * Copyright (c) 2013 Carnegie Mellon University. All rights reserved.
 * 
 */
package vibe;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tests.Debug;


/**
 * The StreamVibe class is responsible for the lower levels of message parsing.
 * In case we want to use the API mode of the XBee later, we should only need
 * to change this file.
 * 
 * @author tstroemm
 */
public class StreamVibe {
    public static final int FACES = 0xFACE;
    public static final int BASES = 0xBA5E;
    public static final int PRIVATE = 0xFFFF;
    
    
    private PrintStream out;
    private OutputStream outputStream;
    private BufferedReader in;
    

    public StreamVibe(OutputStream outStream, InputStream inStream) {
        this.out = new PrintStream(outStream);
        this.outputStream = outStream;
        
        InputStreamReader isr = new InputStreamReader(inStream);
        this.in = new BufferedReader(isr);
    }
    
    public void writeLine(String s) {
        // Makes sure to add a newline to indicate that the package is finished
        this.out.print(s+"\n");
        this.out.flush();
        try {
            this.outputStream.flush();
        } catch (IOException ex) {
            System.err.println("Flushing the outputStream failed");
            ex.printStackTrace(System.err);
        }
    }
    
    public String readLine() throws IOException {
        // Takes care of removing the newline that is received
        return in.readLine();
    }

    /**
     * Will configure the XBee to broadcast its messages to the specified
     * group.
     * 
     * @param broadcastDestination The group to which messages should be sent
     * @return True if configuration was successful, false otherwise
     */
    public boolean setXBeeRemote(int broadcastDestination) {
        String cmd2;
        switch (broadcastDestination) {
            case FACES:
                cmd2 = "DL"+"FACE";
                break;
            case BASES:
                cmd2 = "DL"+"BASE";
                break;
            default:
            case PRIVATE:
                return false;
        }
        
        String cmd1 = "DH"+"0";
        
        return configure(cmd1, cmd2);
    }

    /**
     * Will configure the XBee to broadcast its messages to the specified
     * group.
     * 
     * @param broadcastDestination The group to which messages should be sent
     * @return True if configuration was successful, false otherwise
     */
    public boolean setXBeeLocalGroup(int broadcastDestination) {
        String cmd;
        switch (broadcastDestination) {
            case FACES:
                cmd = "MY"+"FACE";
                break;
            case BASES:
                cmd = "MY"+"BASE";
                break;
            default:
                cmd = "MY"+"FFFF";
        }
        
        return configure(cmd);
    }

    /**
     * Will configure the XBee to send its messages to a single recipient only,
     * specified by the adrHigh and adrLow. If adrHigh is set to 0, it will go
     * to broadcast mode
     * 
     * @param adrHigh
     * @param adrLow
     * @return 
     */
    public boolean setXBeeRemote(int adrHigh, int adrLow) {
        String cmd1 = "DH"+Integer.toHexString(adrHigh).toUpperCase();
        String cmd2 = "DL"+Integer.toHexString(adrLow).toUpperCase();
        
        return configure(cmd1, cmd2);
    }
    
    /**
     * Will configure the XBee according to the configuration string. Will
     * return true upon success, and false otherwise. 
     *
     * 
     * @param commands The commands that should be sent (do not include AT)
     * @return True if the configuration was successful, false otherwise
     */
    // Note: should be private, but is public for testing purposes
    public boolean configure(String... commands) {
        String configuration = "";
        for (String c : commands) {
            configuration = configuration+c+",";
        }
        
        try {
            Debug.info("Before sleep");
            Thread.sleep(1200);
            Debug.info("After sleep");
            this.out.print("+++");
            this.out.flush();
            this.outputStream.flush();
            Debug.info("Before read");
            String result = this.in.readLine();
            Debug.info("Sent +++, received rpsv: "+result);
            
            if (result.equals("OK")) {
                boolean success = true;
                this.out.print("AT"+configuration+"CN\n");
                this.out.flush();
                this.outputStream.flush();
                Debug.info("Sent line: AT"+configuration+"CN");
                for (int i = 0; i < commands.length; i++) {
                    result = this.in.readLine();
                    Debug.info("Got rsvp: "+result);
                    if (result.contains("ERROR")) {
                        success = false;
                    }
                }
                
                return success;
            }
            
        } catch (IOException ex) {
            System.err.println("Error: IOException during configure");
            ex.printStackTrace(System.err);
        } catch (InterruptedException ex) {
            System.err.println("Error: InterruptedExcetpion during configure");
            ex.printStackTrace(System.err);
        }
        return false;
    }
    
}
