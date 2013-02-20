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

import data.Vibrator;
import gnu.io.*;
import java.io.*;
import java.util.HashSet;

/**
 *
 * @author tstroemm
 */
public class Vibe {
    // Compile time constants

    private static final int READ_TIMEOUT = 3000; // Timeout in milliseconds
    private static final int OPEN_TIMEOUT = 2000; // Timeout in milliseconds
    // Communication variables
    private CommPortIdentifier commID;
    private SerialPort port;
    private InputStream inStream;
    private OutputStream outStream;
    private StreamVibe vibe;
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
        PrintStream ps = new PrintStream(this.outStream);
        InputStreamReader isr = new InputStreamReader(this.inStream);
        BufferedReader br = new BufferedReader(isr);
        this.vibe = new StreamVibe(ps, br);

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

    /**
     * Will return a message as a string from the remote device. This method may
     * be changed later to return a Message object, or we may move the entire
     * functionality out of the Vibe interface and handle communication
     * internally. But strings will do for now. The call blocks for
     * {@link #OPEN_TIMEOUT} milliseconds if nothing is received.
     *
     * @return The message, or null if the request timed out.
     */
    public String getMessage() {
        try {
            return this.vibe.readLine();
        } catch (IOException e) {
            // A timeout occured - return null
            return null;
        }
    }

    /**
     * Based on the local state, this will send a message to the remote system
     * containing the values for all known vibrators.
     */
    public void sendUpate() {
        String msg = "UPDATE";

        for (Vibrator vib : this.vibs) {
            msg = msg + ":" + vib.protocolString();
        }

        this.vibe.write(msg);
    }

    /**
     * Will remove all known Vibrators from the local state.
     */
    public void resetState() {
        this.vibs.clear();
    }

    /**
     * Will set the {@link Vibrator#value} of all known {@link Vibrator}s to 0.
     * No change is seen at the on-body system before a call to
     * {@link #sendUpate()} is successfully made.
     */
    public void clearState() {
        for (Vibrator vib : this.vibs) {
            vib.setValue(0);
        }
    }

    /**
     * Will update the state for the given vibrator with the given value. If
     * no vibrator on this address is know, a new vibrator will be added to
     * the local state
     * 
     * @param module Module on which the vibrator is located
     * @param vibrator Vibrator number on the module
     * @param value Vibration value to which the vibrator is set
     * @return The constructed Vibrator object containing the state
     */
    public Vibrator setState(int module, int vibrator, int value) {
        Vibrator vib = new Vibrator(module, vibrator, value);

        // Will remove the old version, and put in new version
        this.vibs.remove(vib);
        this.vibs.add(vib);
        return vib;
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
        this.vibe.write("UPDATE:" + vib.protocolString());
    }
}
