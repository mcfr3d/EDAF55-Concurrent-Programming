package actions;

import models.CameraMonitor;

public class DisconnectAction extends Action {

    public final int key;



    public DisconnectAction(int key) {

        this.key = key;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.disconnectCamera(key);
    }

    @Override
    public String toString() {
        return "DisconnectAction with key: " + key + ".";
    }
}