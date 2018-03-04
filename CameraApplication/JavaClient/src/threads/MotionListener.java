package threads;

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
    private static final int PORT = 9091;
    private static final int IDLE_THRESHOLD = 60000; // 60 seconds
    private static final int MOVIE_THRESHOLD = 1500; // 1.5 seconds
    private static final int DELAY = 500; // 0.5 seconds

    public MotionListener(CameraMonitor cameraMonitor, String address) {
        this.cameraMonitor = cameraMonitor;
        this.address = address;
    }

    @Override
    public void run() {
        //TODO Kill on disconnect
        while (cameraMonitor.isAlive() && !isInterrupted()) {
            int currentMode = cameraMonitor.getMotionModeMotion();
            String urlStr = "http://" + this.address + ":" + MotionListener.PORT;

            // Fetching URL
            URL url = null;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                // TODO: Inform user that motion server disconnected
                if(Constants.Flags.DEBUG) System.out.println("URL: " + urlStr + " does not exist.\n" +
                        "Terminating MotionListener");
                return;
            }

            // Reading input
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader in = null;
            try {
                URLConnection conn = url.openConnection();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    stringBuilder.append(inputLine);

                }
            } catch (IOException e) {
                if(Constants.Flags.DEBUG) System.out.println("BufferedReader in MotionListener caused IOException.\n" +
                        "Terminating MotionListener.");
                return;
            }

            String response = stringBuilder.toString();
            Long[] responseTime = Arrays.stream(response.split(":"))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toArray(Long[]::new);

            // Setting motion mode for a given response
            long timeSinceMotion = responseTime[2]*1000; // Response time is in seconds originally
            long diff = System.currentTimeMillis() - timeSinceMotion;

            switch(currentMode) {
                case Constants.MotionMode.IDLE:
                    if(diff < MotionListener.MOVIE_THRESHOLD) cameraMonitor.setMotionMode(Constants.MotionMode.MOVIE);
                    break;
                case Constants.MotionMode.MOVIE:
                    if(diff > MotionListener.IDLE_THRESHOLD) cameraMonitor.setMotionMode(Constants.MotionMode.IDLE);
                    break;
                default:
                    continue;
            }

            // Delay 500ms
            try {
                Thread.sleep(MotionListener.DELAY);
            } catch (InterruptedException e) {
                if(Constants.Flags.DEBUG) System.out.println("MotionListener was interrupted during sleep.\n " +
                        "Terminating MotionListener.");
                return;
            }
        }
    }
}