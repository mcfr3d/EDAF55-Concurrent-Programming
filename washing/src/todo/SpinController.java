package todo;

import se.lth.cs.realtime.*;
import done.AbstractWashingMachine;

public class SpinController extends PeriodicThread {
	private SpinEvent event;
	private AbstractWashingMachine mach;
	private int ticker;

	public SpinController(AbstractWashingMachine mach, double speed) {
		super((long) (1000 / speed)); // TODO: replace with suitable period
		this.mach = mach;
	}

	public void perform() {
		SpinEvent newEvent = (SpinEvent) this.mailbox.tryFetch();
		if (newEvent != null)
			event = newEvent;
		if (event == null)
			return;
		switch (event.getMode()) {
		case SpinEvent.SPIN_SLOW:
			slowSpin();
			break;
		case SpinEvent.SPIN_OFF:
			offSpin();
			break;
		case SpinEvent.SPIN_FAST:
			fastSpin();
			break;
		default:
			break;
		}
	}

	private void fastSpin() {
		if (mach.getWaterLevel() != 0) {
			mach.setSpin(AbstractWashingMachine.SPIN_OFF);
			return;
		}
		mach.setSpin(AbstractWashingMachine.SPIN_FAST);
	}

	private void offSpin() {
		mach.setSpin(AbstractWashingMachine.SPIN_OFF);
	}

	private void slowSpin() {
		if (ticker < 60) {
			mach.setSpin(AbstractWashingMachine.SPIN_LEFT);
			ticker++;
		} else {
			mach.setSpin(AbstractWashingMachine.SPIN_RIGHT);
			ticker = ((ticker + 1) % 120);
		}
	}
	


}
