package actions;


public abstract class Action{

    private int action;

    public Action(int action){
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}