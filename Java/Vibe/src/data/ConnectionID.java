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
public final class ConnectionID {
    private final int adrHigh;
    private final int adrLow;
    
    private ConnectionID(int adrHigh, int adrLow) {
        if (adrHigh == 0) {
            throw new IllegalArgumentException("High adr must be non-zero!");
        }
        this.adrHigh = adrHigh;
        this.adrLow  = adrLow;
    }
    
    /**
     * Will create a new Connection ID object with the given address. If the
     * address is invalid, e.g. the adrHigh parameter is 0, the call will
     * return null
     * 
     * @param adrHigh High 16 bits of XBee address
     * @param adrLow Low 16 bits of XBee address
     * @return ConnectionID object
     */
    public static ConnectionID newConnectionID(int adrHigh, int adrLow) {
        if (adrHigh == 0) {
            return null;
        }
        return new ConnectionID(adrHigh, adrLow);
    }
    
    public final int getAdrHigh() {
        return this.adrHigh;
    }
    
    public final int getAdrLow() {
        return this.adrLow;
    }
    
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ConnectionID)) return false;
        ConnectionID that = (ConnectionID) o;
        return ((this.adrHigh == that.adrHigh) && (this.adrLow == that.adrLow));
    }

    @Override
    public final int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.adrHigh;
        hash = 41 * hash + this.adrLow;
        return hash;
    }   
}
