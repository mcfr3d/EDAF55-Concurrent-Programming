package todo;

import done.*;

public class WashingController implements ButtonListener {
	private AbstractWashingMachine theMachine;
	private double theSpeed;
	private WaterController waterCtrl;
	private TemperatureController tempCtrl;
	private SpinController spinCtrl;
	private WashingProgram currentWashProgram;

	public WashingController(AbstractWashingMachine theMachine, double theSpeed) {

		this.theMachine = theMachine;
		this.theSpeed = theSpeed;
		waterCtrl = new WaterController(theMachine, theSpeed);
		tempCtrl = new TemperatureController(theMachine, theSpeed);
		spinCtrl = new SpinController(theMachine, theSpeed);
		waterCtrl.start();
		tempCtrl.start();
		spinCtrl.start();
	}

	public void processButton(int theButton) {
		switch (theButton) {
		case 0:
			button0();
			break;
		case 1:
			button1();
			break;
		case 2:
			button2();
			break;
		case 3:
			button3();
			break;
		default:
			break;
		}
	}

	private void button0() {
		currentWashProgram.interrupt();
		theMachine.setDrain(false);
		theMachine.setFill(false);
		theMachine.setHeating(false);
		theMachine.setSpin(AbstractWashingMachine.SPIN_OFF);
	}

	private void button1() {
		if (currentWashProgram == null || !currentWashProgram.isAlive()) {
			currentWashProgram = new WashingProgram1(theMachine, theSpeed, tempCtrl, waterCtrl, spinCtrl);
			currentWashProgram.start();
		}
	}

	private void button2() {
		if (currentWashProgram == null || !currentWashProgram.isAlive()) {
			currentWashProgram = new WashingProgram2(theMachine, theSpeed, tempCtrl, waterCtrl, spinCtrl);
			currentWashProgram.start();

		}
	}

	private void button3() {
		if (currentWashProgram == null || !currentWashProgram.isAlive()) {
			currentWashProgram = new WashingProgram3(theMachine, theSpeed, tempCtrl, waterCtrl, spinCtrl);
			currentWashProgram.start();
		}
	}

}