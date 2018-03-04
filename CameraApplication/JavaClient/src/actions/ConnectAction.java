package actions;

import models.CameraMonitor;

public class ConnectAction extends Action {

    public final String address;
    public final int port;
    public final int key;


    public ConnectAction(String address, int key){
        this(address, 6666,key);
    }

    public ConnectAction(String address, int port, int key) {
        this.address = address;
        this.port = port;
        this.key = key;
    }

    @Override
    public void op(CameraMonitor monitor) {
        monitor.connectCamera(address , port , key);
    }

    @Override
    public String toString() {
        return "ConnectAction with address: " + address + ", port: " + port + " and key: " + key + ".";
    }
}