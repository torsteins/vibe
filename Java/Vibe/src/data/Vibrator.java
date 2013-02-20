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
    private static final int MIN_VALUE = 0;
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
    
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vibrator)) return false;
        Vibrator that = (Vibrator) obj;
        return ((this.getModule() == that.getModule()) &&
                (this.getVibrator() == that.getVibrator()));
    }

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
    
    public final boolean setValue(int value) {
        if (value > Vibrator.MAX_VALUE) return false;
        if (value < Vibrator.MIN_VALUE) return false;
        this.value = value;
        return true;
    }

    public String protocolString() {
        return ""+this.module+","+this.vibrator+","+this.value;
    }
}
