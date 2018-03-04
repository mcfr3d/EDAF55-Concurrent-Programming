package threads;

import constants.Constants;
import models.CameraMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class OutputThread extends Thread {

    private CameraMonitor cameraMonitor;
    public OutputThread(CameraMonitor cameraMonitor) {
        this.cameraMonitor = cameraMonitor;
    }

    @Override
    public void run() {
        while(cameraMonitor.isAlive() && !isInterrupted()){
            int currentMotionMode = cameraMonitor.getMotionModeOutput();
            byte code;
            switch (currentMotionMode){
                case Constants.MotionMode.IDLE:
                    code = Constants.MotionCode.IDLE;
                    break;
                case Constants.MotionMode.MOVIE:
                    code = Constants.MotionCode.MOVIE;
                    break;
                default:
                    continue;
            }

            // Broadcast the newly set mode to all cameras
            for(Socket socket : cameraMonitor.getActiveSockets()){
                OutputStream os = null;
                try {
                    os = socket.getOutputStream();
                    // Might do something in the future with the first byte
                    os.write(0x00);
                    os.write(code);
                } catch (IOException e) {
                    if(Constants.Flags.DEBUG) System.out.println("OutputStream in OutputThread caused IOException.");
                }

            }
            cameraMonitor.setMotionModeChanged(false);
        }
    }
}