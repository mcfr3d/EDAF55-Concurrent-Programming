package models;

import actions.Action;
import constants.Constants;

import java.util.LinkedList;

public class ButtonMonitor{

    private LinkedList<Action> actionList;

    public ButtonMonitor(){
        actionList = new LinkedList<>();
    }

    synchronized public Action getAction(){
        while(actionList.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if(Constants.Flags.DEBUG) System.out.println("ButtonHandler interrupted, terminating handler.");
                return null;
            }
        }
        return actionList.poll();
    }

    synchronized public void addAction(Action action){
        actionList.addLast(action);
        if(Constants.Flags.DEBUG) System.out.println("Added action: " + action.toString());
        notifyAll();
    }



}