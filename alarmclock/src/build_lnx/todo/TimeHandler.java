package todo;

import se.lth.cs.realtime.RTInterrupted;

public class TimeHandler extends Thread {
	private long t, diff;
	private int t0;
	private SharedData data;

	TimeHandler(SharedData data) {
		this.data = data;
	}

	public void run() {
		t = System.currentTimeMillis();
		t0 = 0;
		while (!isInterrupted()) {
			t += 1000;
			diff = t - System.currentTimeMillis();
			data.showTime(t0);
			if (diff > 0)
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					throw new RTInterrupted(e.toString());
				}
			t0 += 1;
		}
	}

}
