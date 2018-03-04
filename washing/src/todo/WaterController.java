package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;

public class WaterController extends PeriodicThread {
	private WaterEvent event;
	private AbstractWashingMachine mach;
	private boolean ackSent;

	public WaterController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed));
		this.mach = mach;
	}

	public void perform() {
		WaterEvent newEvent = (WaterEvent) this.mailbox.tryFetch();
		if (newEvent != null) {
			event = newEvent;
			ackSent = false;
		}
		if (event == null)
			return;
		switch (event.getMode()) {
		case WaterEvent.WATER_IDLE:
			waterIdle();
			break;
		case WaterEvent.WATER_DRAIN:
			waterDrain();
			break;
		case WaterEvent.WATER_FILL:
			waterFill();
			break;
		default:
			break;
		}
	}

	private void waterFill() {
		double targetLvl = event.getLevel();
		double machineLvl = mach.getWaterLevel();
		if (machineLvl - targetLvl < 0)
			mach.setFill(true);
		else
			mach.setFill(false);
		if ((machineLvl - targetLvl) >= 0 && ackSent == false) {
			mach.setFill(false);
			((WashingProgram) event.getSource()).putEvent(new AckEvent(this));
			ackSent = true;
		}
	}

	private void waterDrain() {
		mach.setFill(false);
		double machineLvl = mach.getWaterLevel();
		if (machineLvl > 0)
			mach.setDrain(true);

		if (machineLvl == 0 && ackSent == false) {
			((WashingProgram) event.getSource()).putEvent(new AckEvent(this));
			mach.setDrain(false);
			ackSent = true;
		}
	}

	private void waterIdle() {
		mach.setFill(false);
		mach.setDrain(false);

	}
}
