package todo;

import done.ClockInput;
import se.lth.cs.realtime.semaphore.Semaphore;

public class ButtonHandler extends Thread {
	Semaphore signal;
	ClockInput input;
	SharedData data;
	int clockMode;
	private int lastMode;
	private int time;

	public ButtonHandler(Semaphore signal, ClockInput input, SharedData data) {
		this.signal = signal;
		this.input = input;
		this.data = data;

	}

	public void run() {
		while (!isInterrupted()) {
			signal.take();
			clockMode = input.getChoice();
			data.alarmOn(input.getAlarmFlag());
			if (data.isAlarmSounding()) {
				data.alarmOff();
			}
			switch (clockMode) {
			case ClockInput.SHOW_TIME:
				if (lastMode == ClockInput.SET_TIME) {
					data.setTime(time);
					lastMode = input.getChoice();
				}
				break;
			case ClockInput.SET_ALARM:
				if (lastMode == ClockInput.SET_TIME) {
					data.setTime(time);
					lastMode = input.getChoice();
				}
				data.setAlarm(input.getValue());
				break;
			case ClockInput.SET_TIME:
				lastMode = ClockInput.SET_TIME;
				time = input.getValue();
				break;
			}
		}

	}

}
