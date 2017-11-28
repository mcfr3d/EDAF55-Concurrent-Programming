package threads;


import models.CameraMonitor;

public class OutputThread extends Thread {
    private CameraMonitor cameraMonitor;
    public OutputThread(CameraMonitor cameraMonitor) {
        this.cameraMonitor = cameraMonitor;
    }

    @Override
    public void run() {
        while(cameraMonitor.isAlive()){
            int currentMotionMode = cameraMonitor.getMotionMode();
        }
    }
}