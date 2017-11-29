package models;

import actions.Action;

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

            }
        }
        return actionList.poll();
    }

    synchronized public void addAction(Action action){
        actionList.addLast(action);
        notifyAll();
    }



}