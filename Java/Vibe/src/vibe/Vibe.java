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
 * Copyright (c) 2013 Carnegie Mellon University and/or its affiliates.
 * All rights reserved.
 */
package vibe;

import data.ConnectionID;
import data.Vibrator;
import gnu.io.*;
import interfaces.VibeInterface;
import java.io.*;
import java.util.HashSet;

/**
 *
 * @author tstroemm
 */
public class Vibe implements VibeInterface {
    // Compile time constants

    private static final int READ_TIMEOUT = 3000; // Timeout in milliseconds
    private static final int OPEN_TIMEOUT = 2000; // Timeout in milliseconds
    // Communication variables
    private CommPortIdentifier commID;
    private SerialPort port;
    private InputStream inStream;
    private OutputStream outStream;
    private StreamVibe vibe;
    private ConnectionID remote;
    private int timeout = -1;
    // State variables
    HashSet<Vibrator> vibs;

    /**
     * Will construct a Vibe object and connect it to the given port. Throws a
     * lot of exceptions in case something goes wrong during the connection
     * phase.
     *
     * @param commID Identifier of the SERIAL {@link CommPort} to be used
     * @throws PortInUseException Thrown if the port is already in use
     * @throws IllegalArgumentException Thrown if the port is not a SERIAL port
     * @throws IOException Thrown if it was impossible to establish Input- and
     * output streams
     */
    public Vibe(CommPortIdentifier commID)
            throws PortInUseException, IllegalArgumentException, IOException {

        // Control that the port is valid
        if (commID.getPortType() != CommPortIdentifier.PORT_SERIAL) {
            throw new IllegalArgumentException("The CommPortIdentifier must "
                    + "refer to a SERIAL port");
        } else if (commID.isCurrentlyOwned()) {
            throw new PortInUseException();
        }

        // Open communication
        this.port = (SerialPort) commID.open(this.getClass().getName(),
                Vibe.OPEN_TIMEOUT);
        this.commID = commID;
        this.inStream = this.port.getInputStream();
        this.outStream = this.port.getOutputStream();

        // Create StreamVibe
        this.vibe = new StreamVibe(this.outStream, this.inStream);

        // Initial configuration
        try {
            this.port.enableReceiveTimeout(Vibe.READ_TIMEOUT);
        } catch (UnsupportedCommOperationException ex) {
            System.err.println("Unable to set timeout. Unexpected behaviour "
                    + "may occur.");
        }

        // Initial setup of data structure
        this.vibs = new HashSet<Vibrator>();
    }


    /////////////////////////////////////////////////////////////////////////
    ////////////////////  VIBRATION STATE METHODS  //////////////////////////
    /////////////////////////////////////////////////////////////////////////
    
    @Override
    public void resetState() {
        this.vibs.clear();
    }


    @Override
    public void clearState() {
        for (Vibrator vib : this.vibs) {
            vib.setAmplitude(0);
        }
    }
    
    @Override
    public Vibrator setState(int mod, int vib, int amp) {
        Vibrator v = new Vibrator(mod, vib, amp);
        return this.setState(v);
    }
    
    @Override
    public Vibrator setState(int mod, int vib, int amp, int dur, int ivl,
                            char typ) {
        Vibrator v = new Vibrator(mod, vib, amp, dur, ivl, typ);
        return this.setState(v);
    }
    
    private Vibrator setState(Vibrator v) {
        if (this.getConnection() == null) return null;
        this.vibs.remove(v);
        this.vibs.add(v);
        return v;
    }

    /**
     * Update the local state of a single vibrator, and send an update message
     * to the on-body system with information about this vibrator only.
     * 
     * @param module Module on which the vibrator is located
     * @param vibrator Vibrator number on the module
     * @param value Vibration value to which the vibrator is set
     */
    public void setAndSendSingle(int module, int vibrator, int value) {
        Vibrator vib = setState(module, vibrator, value);
        this.vibe.writeLine("UPDATE:" + vib.protocolString());
    }

    
    
    /////////////////////////////////////////////////////////////////////////
    ////////////////////////  CONNECTION METHODS  ///////////////////////////
    /////////////////////////////////////////////////////////////////////////
    
    @Override
    public boolean connectPassively(int timeout) {
        // Ignore call if connection is already established
        if (this.remote != null) return false;
        
        // Configure XBee to send messages to FACES
        if (!this.vibe.setXBeeRemote(StreamVibe.FACES)) return false;
        
        
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean connectActively() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean connectTo(ConnectionID con, int timeout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    @Override
    public String readMessage() {
        try {
            return this.vibe.readLine();
        } catch (IOException e) {
            // A timeout occured - return null
            return null;
        }
    }

    @Override
    public void forceUpdate() {
        String msg = "UPDATE";

        for (Vibrator vib : this.vibs) {
            msg = msg + ":" + vib.protocolString();
        }
        this.vibe.writeLine(msg);
    }

    @Override
    public int available() {
        try {
            return this.inStream.available();
        } catch (IOException e) {
            System.err.println("Caught exception! " + e.getLocalizedMessage());
            e.printStackTrace(System.err);
            return -1;
        }
    }

    @Override
    public void closeConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void setTimeout(int millis) {
        this.timeout = millis;
    }

    @Override
    public void setAutoUpdate(int millis) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ConnectionID getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
