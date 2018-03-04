package lift;


public class Main {

	public static void main(String[] args) {
		LiftView lv = new LiftView ();
		Monitor mv = new Monitor (lv);
		Lift l = new Lift(mv, lv);
		for(int i = 0; i<20; i++){
			PersonThread p = new PersonThread(mv,PersonFactory.randomPerson());
			p.start();
		}
		l.start();

	}

}
