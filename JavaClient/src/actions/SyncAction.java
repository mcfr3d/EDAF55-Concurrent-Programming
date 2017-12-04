package actions;

import models.CameraMonitor;

public class SyncAction extends Action {

    public boolean sync;

    public SyncAction(boolean sync){
        this.sync = sync;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.setForceSync(sync);
    }

    @Override
    public String toString() {
        return "SyncAction with Sync value: " + sync + ".";
    }
}