package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;

public class TemperatureController extends PeriodicThread {
	private AbstractWashingMachine mach;
	private TemperatureEvent event;
	private double tempMargin = 2;
	private boolean ackSent;

	public TemperatureController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed));
		this.mach = mach;
	}

	public void perform() {

		TemperatureEvent newEvent = (TemperatureEvent) this.mailbox.tryFetch();
		if (newEvent != null) {
			event = newEvent;
			ackSent = false;
		}
		if (event == null)
			return;
		switch (event.getMode()) {
		case TemperatureEvent.TEMP_IDLE:
			tempIdle();
			break;
		case TemperatureEvent.TEMP_SET:
			tempSet();
			break;
		default:
			break;
		}
	}

	private void tempSet() {
		if (mach.getWaterLevel() == 0) {
			mach.setHeating(false);
			return;
		}
		double targetTemp = event.getTemperature();
		double machineTemp = mach.getTemperature();
		if (machineTemp <= (targetTemp - tempMargin)) {
			mach.setHeating(true);
		} else if (machineTemp >= targetTemp) {
			mach.setHeating(false);
		}

		if ((targetTemp - machineTemp) >= 0 && (targetTemp - machineTemp) <= 2 && ackSent == false) {
			((WashingProgram) event.getSource()).putEvent(new AckEvent(this));
			ackSent = true;
		}

	}

	private void tempIdle() {
		mach.setHeating(false);

	}
}