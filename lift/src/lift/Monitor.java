package lift;

public class Monitor {
	private int here; // If here !=next , here (floor number) tells from which
						// floor
	// the lift is moving and next to which floor it is moving.
	private int next; // If here ==next , the lift is standing still on the
						// floor
	// given by here.
	private int[] waitEntry;// The number of persons waiting to enter the lift
							// at the
	// various floors.
	private int[] waitExit; // The number of persons (inside the lift) waiting
							// to leave
	// the lift at the various floors.
	private int load; // The number of people currently occupying the lift.

	private int maxLoad;
	private LiftView lv;
	private int totalWaitingPeople;

	public Monitor(LiftView lv) {
		maxLoad = 4;
		waitEntry = new int[7];
		waitExit = new int[7];
		here = 0;
		next = 0;
		load = 0;
		totalWaitingPeople = 0;
		this.lv = lv;
	}

	public synchronized void addPerson(Person p) {
		int personEntryFloor = p.entryFloor();
		int personExitFloor = p.exitFloor();
		addPersonToFloor(personEntryFloor);
		enterLift(personEntryFloor, personExitFloor);
		exitLift(personExitFloor);
	}

	private synchronized void addPersonToFloor(int entryFloor) {
		waitEntry[entryFloor]++;
		totalWaitingPeople++;
		drawLevel(entryFloor, waitEntry[entryFloor]);
		notifyAll();
	}

	private synchronized void enterLift(int entryFloor, int exitFloor) {
		while (!entryTo(entryFloor)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitEntry[entryFloor]--;
		waitExit[exitFloor]++;
		load++;
		totalWaitingPeople--;
		drawLevel(entryFloor, waitEntry[entryFloor]);
		lv.drawLift(entryFloor, load);
		notifyAll();

	}

	private synchronized void exitLift(int exitFloor) {
		while (!exitOn(exitFloor)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitExit[exitFloor]--;
		load--;
		lv.drawLift(exitFloor, load);
		notifyAll();

	}

	private synchronized boolean exitOn(int exitFloor) {
		return (exitFloor == here && here == next);
	}

	private synchronized boolean entryTo(int entryFloor) {
		return (entryFloor == here && (here == next && load != maxLoad));
	}

	public synchronized void drawLevel(int floor, int persons) {
		lv.drawLevel(floor, persons);
	}

	public synchronized void moveLift(int next) {
		here = this.next;
		notifyAll();
		waitForPassengers();
		this.next = next;

	}

	private synchronized void waitForPassengers() {
		if (waitExit[here] != 0 || (waitEntry[here] > 0 && load < 4) || (totalWaitingPeople == 0)) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*public synchronized boolean direction(boolean goingUp){
		int personsAbove = 0;
		int currentFloor;
		for(currentFloor = here; currentFloor <7;currentFloor++){
			personsAbove = personsAbove + waitEntry[currentFloor] + waitExit[currentFloor];
		}
		if(goingUp && personsAbove == 0){
			return false;
		}
		return goingUp;
		
	}*/
}
