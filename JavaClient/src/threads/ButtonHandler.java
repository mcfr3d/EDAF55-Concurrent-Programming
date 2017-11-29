package threads;

import actions.Action;
import constants.Constants;
import models.ButtonMonitor;
import models.CameraMonitor;



public class ButtonHandler extends Thread {

    private CameraMonitor cameraMonitor;
    private ButtonMonitor buttonMonitor;
    public ButtonHandler(CameraMonitor cameraMonitor, ButtonMonitor buttonMonitor){
        this.cameraMonitor = cameraMonitor;
        this.buttonMonitor = buttonMonitor;
    }

    @Override
    public void run() {
        while(cameraMonitor.isAlive() && !isInterrupted()){
            Action action = buttonMonitor.getAction();
            if(Constants.Flags.DEBUG) System.out.println("Polled action: " + action.toString());
            action.execute(cameraMonitor);
        }
        if(isInterrupted() && Constants.Flags.DEBUG) System.out.println("ButtonHandler was terminated");
    }
}