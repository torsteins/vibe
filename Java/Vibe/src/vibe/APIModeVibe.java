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

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author tstroemm
 */
public class APIModeVibe {
    private static final String SERIALPORTNAME = "/dev/tty.usbserial-AD01UBSI";
    private static final int BAUDRATE = 9600;

    public static void main(String[] args) throws XBeeException {
        PropertyConfigurator.configure("nbproject/log4j.properties");
        XBee xbee = new XBee();
        xbee.open(SERIALPORTNAME, BAUDRATE);
        xbee.close();
    }
}
