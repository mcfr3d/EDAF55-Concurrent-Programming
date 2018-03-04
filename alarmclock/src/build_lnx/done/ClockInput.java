package done;

import se.lth.cs.realtime.semaphore.*;

public class ClockInput {

    /**
     * Semaphore signaling when the user have changed any setting.
     * Actually, according to normal rules for data abstraction, 
     * data (such as the flags and the semaphore in this class) 
     * should be accessed via methods, say awaitAnyButton() to 
     * take the semaphore. However, here we expose variables
     * through a get-method for semaphore-teaching reasons.
     */
    public static CountingSem anyButtonChanged = new CountingSem();

    /**
     * Get-method to access the semaphore instance directly.
     */
    public static final CountingSem getSemaphoreInstance() {
        return anyButtonChanged;
    }

    /**
     * Called by ClockDevice native code.
     */
    public static final void giveInput() {
	    anyButtonChanged.give();
    }
    
    /**
     * Get check-box state.
     */
    public static final native boolean getAlarmFlag();
    
    /**
     * Get radio-buttons choice.
     */
    public static final native int getChoice();
    
    /**
     * Return values for getChoice.
     */
    public static final int SHOW_TIME   = 0;
    public static final int SET_ALARM   = 1;
    public static final int SET_TIME    = 2;
    
    /**
     * When getChoice returns a new choice, and the previous choice
     * was either SET_ALARM or SET_TIME, the set-value of the display
     * is returned in the format hhmmss where h, m, and s denotes 
     * hours, minutes, and seconds digits respectively. This means,
     * for example, that the hour value is obtained by dividing the
     * return value by 10000.
     */
    public static final native int getValue();
}
