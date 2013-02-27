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
    public static final int INTERVAL_NOREPEAT = 0;
    public static final char TYPE_SQUARE = 'S';
    public static final char TYPE_TRIANGLE = 'T';
    public static final char TYPE_GAUSS = 'G';
    public static final char TYPE_DEFAULT = TYPE_SQUARE;
    
    private static final int MIN_DUR = 1;    
    private static final int MIN_IVL = 1;
    private static final int MIN_AMP = 0;
    private static final int MAX_AMP = 255;
    private final int module, vibrator;
    private int amplitude, duration, interval;
    private char type;
    
    public Vibrator(int module, int vibrator, int amplitude, int duration,
                    int interval, char type) {
        this.module = module;
        this.vibrator = vibrator;
        this.setAmplitude(amplitude);
        this.setDurationInterval(duration, interval);
        this.setType(type);
    }
    
    public Vibrator(int module, int vibrator, int amplitude) {
        this(module, vibrator, amplitude, MIN_DUR, MIN_IVL, TYPE_SQUARE);
    }
    
    public Vibrator(int module, int vibrator) {
        this(module, vibrator, MIN_AMP, MIN_DUR, MIN_IVL, TYPE_SQUARE);
    }
    
    public static Vibrator newVibrator(String s)
            throws IllegalArgumentException {
        try {
            String[] args = s.split(",");
            int mod = Integer.parseInt(args[0]);
            int vib = Integer.parseInt(args[1]);
            int amp = Integer.parseInt(args[2]);
            int dur = Integer.parseInt(args[3]);
            int ivl = Integer.parseInt(args[4]);
            char typ = args[5].charAt(0);
            return new Vibrator(mod, vib, amp, dur, ivl, typ);
        } catch (Exception e) {
            throw new IllegalArgumentException("String argument does "+
                    " not describe a valid Vibrator");
        }
    }
    
    /**
     * Returns true if the object is exactly equal to this object, (i.e.
     * contains the exact same values) and false otherwise
     * @param obj Object to be compared with this object
     * @return True if exactly equals, false otherwise
     */
    public boolean exactlyEquals(Object obj) {
        if (!this.equals(obj)) return false;
        Vibrator that = (Vibrator) obj;
        if (this.amplitude != that.amplitude) return false;
        if (this.duration != that.duration) return false;
        if (this.interval != that.interval) return false;
        if (this.type != that.type) return false;
        return true;
    }
    
    
    // Equality is only concerned with the address of the vibrator, i. e. the
    // module and vibrator variables. The amplitude is irrelevant. This is done so
    // that using the Vibrator with a HashSet will never include two amplitudes
    // for the same vibrator. The same concept is applied to hashCode()
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vibrator)) return false;
        Vibrator that = (Vibrator) obj;
        return ((this.getModule() == that.getModule()) &&
                (this.getVibrator() == that.getVibrator()));
    }

    // The hashCode() is only concerned with the address of the vibrator, i. e.
    // the module and vibrator variables. The amplitude is irrelevant. This is done
    // so that using the Vibrator with a HashSet will never include two
    // amplitudes for the same vibrators. The same concept is applied to equals()
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.module;
        hash = 29 * hash + this.vibrator;
        return hash;
    }
    
    @Override
    public String toString() {
        return "Vibrator("+this.module+", "+this.vibrator+", "+this.amplitude+
                ", " +this.duration+", "+this.interval+", "+this.type+")";
    }
    
    
    /*
     * Getters
     */
    
    public int getModule() {
        return this.module;
    } 
    public int getVibrator() {
        return this.vibrator;
    }
    public int getAmplitude() {
        return this.amplitude;
    }
    public int getDuration() {
        return this.duration;
    }
    public int getInterval() {
        return this.interval;
    }
    public char getType() {
        return this.type;
    }
    
    /**
     * Sets the amplitude of the Vibrator to the given amplitude or the closest 
     * boundary amplitude if the given amplitude is below 0 or above {@link #MAX_VALUE}.
     * The method is final so it may be safely used by the constructor.
     * 
     * @param amplitude Value the vibrator should be set to
     */
    public final void setAmplitude(int amp) {
        this.amplitude = Math.max(MIN_AMP, Math.min(Vibrator.MAX_AMP, amp));
    }

    /**
     * Produces a string for use with our XBee protocol for the Vibe.
     * @return a string containing all information formatted to XBee protocol
     */
    public String protocolString() {
        return ""+this.module+","+this.vibrator+","+this.amplitude+","+
                this.duration+","+this.interval+","+this.type;
    }

    private void setDurationInterval(int duration, int interval) {
        if (interval != Vibrator.INTERVAL_NOREPEAT) {
            interval = Math.max(MIN_IVL, interval);
            duration = Math.max(MIN_DUR, Math.min(duration, interval));
        }
        this.interval = interval;
        this.duration = duration;
        
    }

    private void setType(char type) {
        switch (type) {
            case Vibrator.TYPE_GAUSS:
            case Vibrator.TYPE_TRIANGLE:
            case Vibrator.TYPE_SQUARE:
                this.type = type;
                return;
            default:
                this.type = Vibrator.TYPE_DEFAULT;
        }
    }
}
