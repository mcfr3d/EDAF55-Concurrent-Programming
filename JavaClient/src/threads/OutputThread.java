package threads;


import constants.Constants;
import models.CameraMonitor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class OutputThread extends Thread {
    private static final byte MOVIE = (byte) 0xFF;
    private static final byte IDLE = 0x00;

    private CameraMonitor cameraMonitor;
    public OutputThread(CameraMonitor cameraMonitor) {
        this.cameraMonitor = cameraMonitor;
    }

    @Override
    public void run() {
        while(cameraMonitor.isAlive()){
            int currentMotionMode = cameraMonitor.getMotionModeOutput();
            byte code;
            switch (currentMotionMode){
                case Constants.MotionMode.IDLE:
                    code = IDLE;
                    break;
                case Constants.MotionMode.MOVIE:
                    code = MOVIE;
                    break;
                default:
                    continue;
            }
            for(Socket socket : cameraMonitor.getActiveSockets()){
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write(0x00);
                    os.write(code);

                } catch (IOException e) {

                }

            }
            cameraMonitor.setMotionModeChanged(false);
        }
    }
}