package actions;

import models.CameraMonitor;

public class SyncAction extends Action {

    public final int code;

    public SyncAction(int code){
        this.code = code;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.setForceMode(code);
    }
}