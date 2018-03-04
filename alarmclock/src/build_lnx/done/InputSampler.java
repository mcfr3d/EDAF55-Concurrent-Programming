package done;

class InputSampler extends Thread {
	public static final native boolean haveInput();

	public void run() {
		try {
			while (!isInterrupted()) {
				if (haveInput())
					ClockInput.giveInput();
				sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

}
