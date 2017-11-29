package actions;

import models.CameraMonitor;

public class ConnectAction extends Action {

    public final String address;
    public final int port;

    public ConnectAction(String address){
        this(address, 6666);
    }

    public ConnectAction(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.connectCamera(address , port);
    }
}