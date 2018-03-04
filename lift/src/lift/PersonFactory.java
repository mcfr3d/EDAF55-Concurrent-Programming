package lift;

public class PersonFactory {

	static public Person randomPerson() {
		int entryFloor = (int) (Math.random() * 7);
		int exitFloor = (int) (Math.random() * 7);
		while (entryFloor == exitFloor)					
			exitFloor = (int) (Math.random() * 7);
		return new Person(entryFloor, exitFloor);
	}

}
