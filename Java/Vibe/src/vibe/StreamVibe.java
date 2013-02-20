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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;


/**
 * The StreamVibe class is responsible for the lower levels of message parsing.
 * In case we want to use the API mode of the XBee later, we should only need
 * to change this file.
 * 
 * @author tstroemm
 */
public class StreamVibe {
    private PrintStream out;
    private BufferedReader in;
    

    public StreamVibe(PrintStream out, BufferedReader in) {
        this.in = in;
        this.out = out;
    }
    
    public void write(String s) {
        // Makes sure to add a newline to indicate that the package is finished
        this.out.print(s+"\n");
    }
    
    public String readLine() throws IOException {
        // Takes care of removing the newline that is received
        return in.readLine();
    }
    
}
