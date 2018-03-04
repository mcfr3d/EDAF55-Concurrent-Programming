package todo;

import done.AbstractWashingMachine;

public class WashingProgram1 extends WashingProgram {

	protected WashingProgram1(AbstractWashingMachine mach, double speed, TemperatureController tempController,
			WaterController waterController, SpinController spinController) {
		super(mach, speed, tempController, waterController, spinController);
	}

	@Override
	protected void wash() throws InterruptedException {

		// lock the machine
		myMachine.setLock(true);

		// fill halfway
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
		mailbox.doFetch(); // Wait for Ack

		// Set water regulation to idle => drain pump stops
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.5));

		// set temp to 60c
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_SET, 60));
		mailbox.doFetch(); // Wait for Ack

		// start spinning
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));

		// wait 30 min
		sleep(minToMilis(30 / mySpeed));

		// Switch of temp regulation
		myTempController.putEvent(new TemperatureEvent(this, TemperatureEvent.TEMP_IDLE, 0.0));

		// Switch off spin
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

		// Drain
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0.0));
		mailbox.doFetch(); // Wait for Ack

		// Set water regulation to idle => drain pump stops
		myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.0));

		// wash cycle
		for (int i = 0; i < 5; i++) {

			// fill halfway
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_FILL, 0.5));
			mailbox.doFetch(); // Wait for Ack

			// Set water regulation to idle => drain pump stops
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.5));

			// start spinning
			mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_SLOW));

			// wait 2 min
			sleep(minToMilis(2 / mySpeed));

			// Switch off spin
			mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

			// Drain
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_DRAIN, 0.0));
			mailbox.doFetch(); // Wait for Ack

			// Set water regulation to idle => drain pump stops
			myWaterController.putEvent(new WaterEvent(this, WaterEvent.WATER_IDLE, 0.0));

		}

		// start spinning
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_FAST));

		// wait 5 min
		sleep(minToMilis(5 / mySpeed));

		// Switch off spin
		mySpinController.putEvent(new SpinEvent(this, SpinEvent.SPIN_OFF));

		// Unlock
		myMachine.setLock(false);
		// stops thread
		interrupt();

	}

}
