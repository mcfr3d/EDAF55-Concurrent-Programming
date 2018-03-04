package lift;

public class PersonThread extends Thread {
	private Monitor monitor;
	private Person person;

	public PersonThread(Monitor monitor, Person p) {
		this.monitor = monitor;
		this.person = p;
	}

	public void run() {
		while (!this.isInterrupted()) {
			int delay = 1000 * ((int) (Math.random() * 46.0));
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
			monitor.addPerson(person);
			person = PersonFactory.randomPerson();
		}
	}

}
