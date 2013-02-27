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
package testData;

import data.Vibrator;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author tstroemm
 */
public class VibratorTest {
    
    public VibratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void sanityTest() {
        Vibrator v1 = new Vibrator(0, 1);
        assertEquals(0, v1.getAmplitude());
        
        Vibrator v2 = new Vibrator(0, 1, 50);
        
        assertTrue(v1.equals(v2));
        assertFalse(v1.exactlyEquals(v2));
        
        Vibrator v3 = new Vibrator(0, 1, -50);
        
        assertFalse(v1 == v3);
        assertTrue(v1.equals(v3));
        assertTrue(v1.exactlyEquals(v3));
        
        Vibrator v4 = new Vibrator(0, 1, 2, 3, 4, 'S');
        assertEquals("0,1,2,3,4,S", v4.protocolString());
        
        Vibrator v5 = new Vibrator(0, 1, 2, 3, 4, 'W');
        assertEquals("0,1,2,3,4,S", v5.protocolString());
        
        Vibrator v6 = Vibrator.newVibrator(v4.protocolString());
        assertTrue(v6.exactlyEquals(v4));
    }
}
