package todo;

import done.ClockOutput;
import se.lth.cs.realtime.semaphore.MutexSem;

public class SharedData {
	private ClockOutput output;
	private int time;
	private int alarm;
	private boolean alarmOn;
	private MutexSem mutxSem;
	private int count;
	private boolean alarmSounding;
	private int timeInSeconds;

	SharedData(ClockOutput out) {
		output = out;
		mutxSem = new MutexSem();
		count = 20;
		this.timeInSeconds = 0;
	}

	public void showTime(int time) {
		mutxSem.take();
		formatTime(time);
		output.showTime(this.time);
		if (this.time == alarm && alarmOn) {
			alarmSounding = true;
		}
		if (alarmSounding) {
			output.doAlarm();
			count--;
			if (count == 0) {
				alarmOffPriv();
			}
		}
		mutxSem.give();
	}

	private void formatTime(int time) {
		int dif = time - this.timeInSeconds;
		this.timeInSeconds = time;
		int hour = this.time / 10000;
		int minute = (this.time % 10000) / 100;
		int second = this.time % 100;

		second = second + dif;
		if (second > 59) {
			minute++;
			second = second % 60;
		}

		if (minute > 59) {
			hour++;
			minute = minute % 60;
		}

		if (hour > 23) {
			hour = 0;
		}
		this.time = hour * 10000 + minute * 100 + second;
	}

	private void alarmOffPriv() {
		alarmSounding = false;
		count = 20;

	}

	public void alarmOff() {
		mutxSem.take();
		alarmOffPriv();
		mutxSem.give();
	}

	public void setAlarm(int value) {
		mutxSem.take();
		alarm = value;
		mutxSem.give();
	}

	public void alarmOn(boolean b) {
		mutxSem.take();
		alarmOn = b;
		mutxSem.give();
	}

	public void setTime(int value) {
		mutxSem.take();
		this.time = value;
		mutxSem.give();
	}

	public boolean isAlarmSounding() {
		mutxSem.take();
		boolean sound = alarmSounding;
		mutxSem.give();
		return sound;
	}
}
