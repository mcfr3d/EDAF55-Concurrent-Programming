package components;


public class CameraModel {
    private String name;
    private String ip;
    private boolean connected;
    public CameraModel(String name, String ip, boolean connected){
        this.connected = connected;
        this.ip = ip;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }


    public boolean isConnected() {
        return connected;
    }

    public CameraModel setConnected(boolean connected) {
        return new CameraModel(name,ip,connected);
    }
}
