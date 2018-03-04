package lift;

public class Lift extends Thread {
	private Monitor monitor;
	private int maxFloor;
	private boolean movingUp;
	private LiftView lv;
	private int nextFloor;

	public Lift(Monitor monitor, LiftView lv) {
		this.monitor = monitor;
		this.maxFloor = 6;
		this.nextFloor = 0;
		this.movingUp = true;
		this.lv = lv;
	}

	public void run() {
		while (!isInterrupted()) {
			/*try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			int currentFloor = nextFloor;
			uppdateNextFloor();
			monitor.moveLift(nextFloor);
			lv.moveLift(currentFloor, nextFloor);	

		}
	}

	private void uppdateNextFloor() {
		if (movingUp) {
			nextFloor++;
			if (nextFloor == maxFloor) {
				movingUp = false;
			}

		} else {
			nextFloor--;
			if (nextFloor == 0)
				movingUp = true;
		}
	}
	
	/*private void uppdateNextFloor2() {
		movingUp = monitor.direction(movingUp);
		if (movingUp) {
			nextFloor++;
			if (nextFloor == maxFloor) {
				movingUp = false;
			}

		} else {
			nextFloor--;
			if (nextFloor == 0)
				movingUp = true;
		}
	}
	*/
}