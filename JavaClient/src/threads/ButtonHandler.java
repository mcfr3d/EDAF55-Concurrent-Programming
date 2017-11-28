package threads;
import actions.ConnectAction;
import actions.MotionAction;
import actions.SyncAction;
import constants.Constants;

import actions.Action;
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
        while(cameraMonitor.isAlive()){
            Action action = buttonMonitor.getAction();
            switch (action.getAction()){
                case Constants.ActionType.CHANGE_MOTION:
                    MotionAction motionAction = (MotionAction) action;
                    cameraMonitor.setForceMode(motionAction.getCode());
                    break;
                case Constants.ActionType.CHANGE_SYNC:
                    SyncAction syncAction = (SyncAction) action;
                    cameraMonitor.setForceMode(syncAction.getCode());
                    break;
                case Constants.ActionType.CONNECT:
                    ConnectAction connectAction = (ConnectAction) action;
                    cameraMonitor.connectCamera(connectAction.getAddress() , 6666);
                default:
                    continue;
            }
        }


    }
}