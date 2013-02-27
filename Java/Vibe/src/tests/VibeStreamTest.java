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
package tests;

import gnu.io.*;
import java.io.*;
import java.util.Enumeration;
import vibe.StreamVibe;
import vibe.Vibe;

/**
 *
 * @author tstroemm
 */
public class VibeStreamTest {
    private static final String SERIALPORTNAME = "/dev/tty.usbserial-AD01UBSI";

    public static void main(String[] args) throws PortInUseException, IOException, InterruptedException, UnsupportedCommOperationException {
        new VibeStreamTest().testVibe();
    }
    
    
    public void testVibe()
            throws PortInUseException, IllegalArgumentException, IOException {
        
        System.out.println("Starting test of Vibe");
        CommPortIdentifier portID = getSerialCommPortIDbyName(SERIALPORTNAME);
        if (portID == null) {
            System.err.println("Found no available port "+ SERIALPORTNAME);
            return;
        }
        
        Vibe vibe = new Vibe(portID);
        
        // Test 1: Attempt to read (should return null)
        String res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 2: Set the state
        vibe.setState(1, 2, 3);
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 3: Set another state
        vibe.setState(5, 6, 7);
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 4: Update a state
        vibe.setState(1, 2, 9);
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 5: Set a single state and update
        vibe.setAndSendSingle(1, 2, 10);
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 6: Send regular update
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 7: Test clear
        vibe.clearState();
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 8: Test resetState()
        vibe.resetState();
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
        // Test 9: Set a new state again
        vibe.setState(1, 2, 9);
        vibe.forceUpdate();
        res = vibe.readMessage();
        System.out.println("Read a line: "+res);
        
    }
    
    
    public void testStreamVibe() throws PortInUseException, IOException, InterruptedException, UnsupportedCommOperationException {
        System.out.println("Starting test of StreamVibe...");
        
        
        CommPortIdentifier portID = getSerialCommPortIDbyName(SERIALPORTNAME);
        if (portID == null) return;
        
        
        System.out.println("Found commPortID: "+portID.getName());
        CommPort commPort = portID.open(this.getClass().getName(), 2000);
        SerialPort port = (SerialPort) commPort;  
        port.enableReceiveTimeout(10000);

        System.out.println("Opened commPort:  "+port.getName());


        
        InputStream inStream = port.getInputStream();
        OutputStream outStream = port.getOutputStream();
        
        StreamVibe vibe = new StreamVibe(outStream, inStream);
        
        System.out.println("Created Vibe element");

        try {
            String boss = vibe.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        System.out.print("Writing... ");
        vibe.writeLine("ON\n");
        System.out.println("ON  written successfully");
        System.out.print("Reading... ");
        String res = vibe.readLine();
        System.out.println("Success: "+res);
        Thread.sleep(5000);
        
        System.out.print("Writing... ");
        vibe.writeLine("OFF\n");
        System.out.println("OFF written successfully");
        System.out.print("Reading... ");
        res = vibe.readLine();
        System.out.println("Success: "+res);
        Thread.sleep(5000);
        
        System.out.print("Writing... ");
        vibe.writeLine("ON\n");
        System.out.println("ON  written successfully");
        System.out.print("Reading... ");
        res = vibe.readLine();
        System.out.println("Success: "+res);
        Thread.sleep(5000);
        
        System.out.print("Writing... ");
        vibe.writeLine("OFF\n");
        System.out.println("OFF written successfully");
        System.out.print("Reading... ");
        res = vibe.readLine();
        System.out.println("Success: "+res);
        Thread.sleep(5000);
        
        System.out.print("Writing... ");
        vibe.writeLine("ON\n");
        System.out.println("ON  written successfully");
        System.out.print("Reading... ");
        res = vibe.readLine();
        System.out.println("Success: "+res);
        Thread.sleep(5000);
        
        
        commPort.close();
        
        System.out.println("\n\nClosed successfully!");
        
        
       
    }
    
    
    public static CommPortIdentifier getSerialCommPortIDbyName(String name) {
        // Get list of all ports
        Enumeration<CommPortIdentifier> portList;
        portList = CommPortIdentifier.getPortIdentifiers();
        
        // Check each port in list and find matching port
        CommPortIdentifier result = null;
        while (portList.hasMoreElements()) {
            CommPortIdentifier id = portList.nextElement();
            if (id.getName().equals(name)) {
                result = id;
            }
        }
        
        // Return null if result is invalid
        if ((result == null)
                || (result.isCurrentlyOwned()) 
                || (result.getPortType() != CommPortIdentifier.PORT_SERIAL)) {
            return null;
        }
        
        // Else
        return result;
    }
    
    
    
    /* Thanks to http://rxtx.qbang.org/wiki/index.php/Discovering_comm_ports */
    static String getPortTypeName ( int portType ) {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    
}
