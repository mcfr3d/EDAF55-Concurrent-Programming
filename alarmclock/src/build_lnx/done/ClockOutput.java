package done;

import se.lth.cs.realtime.semaphore.*;

public class ClockOutput {

	public static final native void setupWindow();
	/**
	 * Enable alarm.
	 */
	public static final void setupAlarm() {
	  setupWindow();
	  new InputSampler().start();
	}

	/**
	 * Wake-up clock user.
	 */
	public static final native void doAlarm();

	/**
	 * Change displayed clock time.
	 */
	public static final native void showTime(int hhmmss);
	public static final native void diagnostics();
}
