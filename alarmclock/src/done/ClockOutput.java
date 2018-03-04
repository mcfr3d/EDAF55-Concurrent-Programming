package done;

import java.awt.Toolkit;

public class ClockOutput {

    private ClockTimeDisplay theDisplay;
    
    ClockOutput(ClockTimeDisplay display) {
        theDisplay = display;
    }
    
    /** 
     * Wake-up clock user.
     */
    public void doAlarm() {
    	System.out.print("Beep! \u0007  "); // BELL char, or ctrl-G.
    	Toolkit.getDefaultToolkit().beep(); // Dito modern/AWT style.
        theDisplay.setAlarmPulse(true);
        try {Thread.sleep(300);} catch (InterruptedException e) {}
        theDisplay.setAlarmPulse(false);
    }
    
    /**
     * If the display is currently used to display the time, update it.
     * If user is using display for setting clock or alarm time, do
     * nothing when with actual hardware or show info when simulating.
     */
    public void showTime(int hhmmss) {
        theDisplay.setTime(hhmmss);
    }
    
    /** 
     * Display debug/logging message on hardware-dependent serial out.
     * @param msg The string to display.
     */
    public void console(String msg) {
    	System.out.println(msg);
    }
    
    /**
     * Format and display ClockInput data.
     * @param time The value as hh:mm:ss
     * @param flag The state of the check box, T if checked.
     * @param mode The ClockInput state.
     */
    public void console(long curr, int time, boolean flag, int mode) {
    	String choice = null;
    	switch (mode) {
    	case ClockInput.SHOW_TIME: choice = "SHOW_TIME"; break;
    	case ClockInput.SET_ALARM: choice = "SET_ALARM"; break;
    	case ClockInput.SET_TIME:  choice = "SET_TIME "; break;
    	}
    	System.out.printf("At ms=%05d: ", curr%10000); // ms within a minute
    	System.out.printf("Value=%02d:%02d:%02d  AlarmFlag=%s  ", 
    			time/100/100, time/100,time, flag?"T":"F");
		System.out.printf("Choice=%s%n", choice);
    }
}
