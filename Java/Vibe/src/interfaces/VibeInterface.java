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
package interfaces;

import data.ConnectionID;
import data.Vibrator;

/**
 *
 * @author tstroemm
 */
public interface VibeInterface {

    /**
     * Will instruct the Vibe to automatically accept any incoming request to
     * connect. There is no time penalty, and the method may be called again
     * immediately after it returns. If the timeout value is set to a negative
     * number, the call will not return before a connection is made.
     *
     * The call does nothing and will return false if there is already a
     * connection established.
     *
     * @return true if connection is made, false otherwise.
     */
    public boolean connectPassively(int timeout);

    /**
     * Will broadcast a single HI announcement, and accept the first respondent.
     * If nobody responds, the call times out and returns false. After the call
     * returns, there is a time penalty before the function may be called again.
     * If it is called before this, the call will block until the penalty time
     * is out before it attempts again.
     *
     * The call does nothing and will return false if there is already a
     * connection established.
     *
     * @return true if connection was made, false otherwise
     */
    public boolean connectActively();

    /**
     * Will attempt to connect to a on-body system specified by the given XBee
     * address/serial number. The parameter adr_High cannot be 0. The timeout
     * specifies for how long the attempt will occur; a negative value will wait
     * forever.
     *
     * The call does nothing and will return false if there is already a
     * connection established.
     *
     * @param adr_High The high 16 bits of the target XBee serial number
     * @param adr_Low The low 16 bits of the target XBee serial number
     * @param timeout Timeout value
     */
    public boolean connectTo(ConnectionID con, int timeout);

    /**
     * Will set the timeout parameters for the read calls. A negative value
     * indicates that there is no timeout.
     *
     * @param millis Timeout specified in milliseconds
     */
    public void setTimeout(int millis);

    /**
     * Will return whether there are any available messages ready to be
     * received.
     *
     * @return a positive value if there are messages available, 0 if nothing,
     * and -1 on error
     */
    public int available();

    /**
     * Will return a message as a string from the remote device. This method may
     * be changed later to return a Message object, or we may move the entire
     * functionality out of the Vibe interface and handle communication
     * internally. But strings will do for now. The call blocks for
     * {@link #OPEN_TIMEOUT} milliseconds if nothing is received.
     *
     * @return The message, or null if the request timed out.
     */
    public String readMessage();

    /**
     * Will close the current connection. The Vibe object will be able to
     * connect to a new on-body system by a call to {@link #connectActively()},
     * {@link #connectPassively(int)} or {@link #connectTo(int, int, int)}.
     *
     * After this call, a subsequent call to {@link #getConnection()} will
     * return null.
     */
    public void closeConnection();

    /**
     * Will update the state for the given vibrator with the given value. If no
     * vibrator on this address is know, a new vibrator will be added to the
     * local state. This call will set the vibrator to a constant amplitude.
     *
     * The amplitude must always be a non-negative number below
     * {@link Vibrator#MAX_AMP}
     *
     * @param module Module on which the vibrator is located
     * @param vibrator Vibrator number on the module
     * @param value Vibration value to which the vibrator is set
     * @return The constructed Vibrator object containing the state, or null if
     * no connection is available
     */
    public Vibrator setState(int module, int vibrator, int value);

    /**
     * Will update the state for the given vibrator with the given value. If no
     * vibrator on this address is know, a new vibrator will be added to the
     * local state.
     *
     * This call will take custom parameters for the duration, interval and
     * pulse type, in order that the remote on-body system to give periodic
     * pluses rather than being detail-controlled by the local system.
     *
     * Note that the duration must be shorter than the interval; if this does
     * not hold, the duration will be shortened to that of the interval.
     *
     * If the interval is 0 (i.e. {@link Vibrator#INTERVAL_NOREPEAT}), the pulse
     * will only be sent once (it will not repeat). In this case, the duration
     * can be any positive number.
     *
     * The duration must always be a positive number, and the interval and must
     * always be a non-negative number.
     *
     * The amplitude must always be a non-negative number below
     * {@link Vibrator#MAX_AMP}
     *
     * @param module Module on which the vibrator is located
     * @param vibrator Vibrator number on the module
     * @param value Vibration value to which the vibrator is set
     * @param duration Duration of the pulse
     * @param interval Time between pulses (must be longer than duration)
     * @param type Type of pulse
     * @return The constructed Vibrator object containing the state, or null if
     * no connection is available
     */
    public Vibrator setState(int module, int vibrator, int value,
            int duration, int interval, char type);

    /**
     * Will remove all Vibrator states from the system.
     */
    public void resetState();

    /**
     * Will set the {@link Vibrator#amplitude} of all known {@link Vibrator}s to
     * 0. Will update the remote system with the new state.
     */
    public void clearState();

    /**
     * Will send a forced Update to the remote system with information about the
     * new state.
     */
    public void forceUpdate();

    /**
     * Will configure the system to automatically update the remote state at
     * least this often. A non-positive value indicates that automatic updates
     * will not be sent; in this case, the remote system will not update unless
     * a call is made to {@link #forceUpdate()}.
     *
     * @param millis
     */
    public void setAutoUpdate(int millis);

    /**
     * Will return the {@link ConnectionID} of this Vibe, if present. If there is
     * no connection, the call will return null
     *
     * @return The connection associated with this Vibe, or null if there is
     * none
     */
    public ConnectionID getConnection();
}
