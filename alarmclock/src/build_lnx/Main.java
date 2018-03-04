import done.*;
import todo.*;
import se.lth.cs.realtime.semaphore.*;

public class Main extends Thread {

	public static void main(String[] args) {
		int i = 0;
                if (i > 0) {
                  // Ugly, but needed to avoid deadcode removal
                  ClockInput.giveInput();
                }
		ClockOutput.setupAlarm();
		MutexSem sem = new MutexSem();// Force code generation for MutexSem
		CountingSem sem2 = new CountingSem();// Force code generation for CountingSem
		ClockInput ci = new ClockInput();
	        ClockOutput co = new ClockOutput();
	        AlarmClock control = new AlarmClock(ci, co);
		control.start();
	}
}

