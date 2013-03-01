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
package testVibe;

import div.FindComPort;
import gnu.io.*;
import java.io.IOException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import vibe.StreamVibe;

/**
 *
 * @author tstroemm
 */
public class StreamVibeTest {
    private static final String SERIALPORTNAME = "/dev/tty.usbserial-AD01UBSI";
    SerialPort port = null;
    StreamVibe vibe = null;
    
    
    @Before
    public void setUp() throws PortInUseException,
            UnsupportedCommOperationException, IOException {
        
        CommPortIdentifier portID = FindComPort.getSerialCommPortIDbyName(SERIALPORTNAME);
        assertNotNull(portID);
        assertEquals(portID.getPortType(), CommPortIdentifier.PORT_SERIAL);
        
        CommPort commPort = portID.open(this.getClass().getName(), 2000);
        this.port = (SerialPort) commPort;  
        this.port.enableReceiveTimeout(10000);

        
        this.vibe = new StreamVibe(this.port.getOutputStream(),
                                   this.port.getInputStream());
    }
    
    @After
    public void tearDown() {
        this.port.close();
    }
    
    @Test
    public void sanityTest() throws InterruptedException {
        // Do a simple read
        assertTrue(this.vibe.configure("DH"));
        
        assertTrue(this.vibe.setXBeeRemote(0x55, 0x22));
        assertTrue(this.vibe.configure("DH", "DL"));
        assertTrue(this.vibe.setXBeeRemote(0,0xFACE));
        Thread.sleep(1000);
        assertTrue(this.vibe.configure("DH", "DL"));
    }
    
}
