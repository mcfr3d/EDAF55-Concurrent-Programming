package threads;


import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import constants.Constants;
import models.CameraMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class MotionListener extends Thread {
    private CameraMonitor cameraMonitor;
    private String address;
    private static final int port = 9091;
    private long latestMotion;

    public MotionListener(CameraMonitor cameraMonitor,String address) {
        this.cameraMonitor = cameraMonitor;
        this.address = address;

    }

    @Override
    public void run() {
        //TODO Kill on disconnect
        while (cameraMonitor.isAlive()) {
            int currentMode = cameraMonitor.getMotionModeMotion();
            String urlStr = "http://" + this.address + ":" + this.port;
            try {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = in.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                in.close();

                String response = stringBuilder.toString();
                Long[] responseTime = Arrays.stream(response.split(":"))
                        .map((time) -> Long.valueOf(time))
                        .toArray(Long[]::new);
                System.out.println(response);
                long timeSinceMotion = responseTime[2]*1000; //responsetime is in seconds orginally
                System.out.println("DIFF: " + (System.currentTimeMillis() - timeSinceMotion) );
                if(System.currentTimeMillis() - timeSinceMotion > 60000 && currentMode != Constants.MotionMode.IDLE){
                    cameraMonitor.setMotionMode(Constants.MotionMode.IDLE);
                }else if(System.currentTimeMillis() - timeSinceMotion < 1500 && currentMode != Constants.MotionMode.MOVIE){
                    cameraMonitor.setMotionMode(Constants.MotionMode.MOVIE);
                }

            } catch (MalformedURLException e) {
                //TODO MALLFORM URL
            } catch (IOException e) {

            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }
    }
}