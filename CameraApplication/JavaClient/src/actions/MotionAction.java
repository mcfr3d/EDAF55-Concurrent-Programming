package actions;

import models.CameraMonitor;

public class MotionAction extends Action {

    public final int code;

    public MotionAction(int code){
        this.code = code;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.setForceMode(code);
    }

    @Override
    public String toString() {
        return "Motion action with code: " + code + ".";
    }
}