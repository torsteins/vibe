package div;


import gnu.io.CommPortIdentifier;
import java.util.Enumeration;

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

/**
 *
 * @author tstroemm
 */
public class FindComPort {
    
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
}
