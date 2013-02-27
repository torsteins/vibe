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

/**
 *
 * @author tstroemm
 */
public class Debug {
    public static final boolean DEBUG = true;
    
    public static void info(Object o) {
        if (Debug.DEBUG) {
            System.out.println("Debug info: "+o);
            System.out.flush();
        }
    }
    
    public static void println(Object o) {
        if (Debug.DEBUG) {
            System.out.println(o);
            System.out.flush();
        }
    }
    
    public static void print(Object o) {
        if (Debug.DEBUG) {
            System.out.print(o);
            System.out.flush();
        }
    }
}
