package threads;


import models.CameraMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
            String urlStr = "http://" + this.address + ":" + this.port;
            try {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    System.out.println(this.address + " : " + inputLine);
                in.close();

            } catch (MalformedURLException e) {
                //TODO MALLFORM URL
            } catch (IOException e) {


            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }
}