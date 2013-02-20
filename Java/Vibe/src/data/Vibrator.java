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
package data;

/**
 *
 * @author tstroemm
 */
public class Vibrator {
    private static final int MAX_VALUE = 255;
    private final int module;
    private final int vibrator;
    private int value;
    
    public Vibrator(int module, int vibrator) {
        this.module = module;
        this.vibrator = vibrator;
        this.value = 0;
    }
    
    public Vibrator(int module, int vibrator, int value) {
        this(module, vibrator);
        this.setValue(value);
    }
    
    
    // Equality is only concerned with the address of the vibrator, i. e. the
    // module and vibrator variables. The value is irrelevant. This is done so
    // that using the Vibrator with a HashSet will never include two values
    // for the same vibrator. The same concept is applied to hashCode()
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vibrator)) return false;
        Vibrator that = (Vibrator) obj;
        return ((this.getModule() == that.getModule()) &&
                (this.getVibrator() == that.getVibrator()));
    }

    // The hashCode() is only concerned with the address of the vibrator, i. e.
    // the module and vibrator variables. The value is irrelevant. This is done
    // so that using the Vibrator with a HashSet will never include two
    // values for the same vibrators. The same concept is applied to equals()
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.module;
        hash = 29 * hash + this.vibrator;
        return hash;
    }
    
    @Override
    public String toString() {
        return "Vibrator("+this.module+", "+this.vibrator+", "+this.value+")";
    }
    
    public int getModule() {
        return this.module;
    }
    
    public int getVibrator() {
        return this.vibrator;
    }
    
    public int getValue() {
        return this.value;
    }
    
    /**
     * Sets the value of the Vibrator to the given value or the closest 
     * boundary value if the given value is below 0 or above {@link #MAX_VALUE}.
     * The method is final so it may be safely used by the constructor.
     * 
     * @param value Value the vibrator should be set to
     */
    public final void setValue(int value) {
        this.value = Math.max(0, Math.min(Vibrator.MAX_VALUE, value));
    }

    /**
     * Produces a string for use with our XBee protocol for the Vibe.
     * @return a string containing all information formatted to XBee protocol
     */
    public String protocolString() {
        return ""+this.module+","+this.vibrator+","+this.value;
    }
}
