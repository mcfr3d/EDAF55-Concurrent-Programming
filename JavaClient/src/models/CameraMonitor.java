package models;

import javafx.scene.image.Image;
import threads.InputThread;

import java.io.IOException;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

enum MotionMode{
    MOTION,IDLE,AUTO;
}
public class CameraMonitor {
    HashMap<Integer , CameraModel> cameraMap;
    boolean sync = false;
    MotionMode motionMode = MotionMode.AUTO;
    boolean alive = true;
    public CameraMonitor(){
        cameraMap = new HashMap<>();
    }
    synchronized public void addImage(int camera,ImageModel imageModel){
            cameraMap.get(camera).putImage(imageModel);
            notifyAll();
    }
    synchronized public boolean isAlive(){
        return alive;
    }

    synchronized public void connectCamera(String ip , int port){
        try {
            Socket socket = new Socket(ip, port);
            InputThread inputThread = new InputThread(socket, this);
            cameraMap.put(inputThread.hashCode(),new CameraModel(ip,port));
            inputThread.start();
        }catch(IOException e){
            //Couldnt connect
        }
    }
    /*synchronized public HashMap<Integer,ImageModel> getImages(){
        HashMap<Integer , ImageModel> imageMap = new HashMap<>();

        cameraMap.forEach((key, value) -> {
            imageMap.put(key , value.getImage());
        });
        return imageMap;
    }*/



    synchronized public boolean isSync() {
        return sync;
    }

    synchronized public void setSync(boolean sync) {
        this.sync = sync;
    }

    synchronized public Map.Entry<Integer , ImageModel> getImage(){
        long minTime = Long.MAX_VALUE;
        int oldestEntry = Integer.MAX_VALUE;
        while(cameraMap.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(Map.Entry<Integer, CameraModel> pair : cameraMap.entrySet()){
            while(!pair.getValue().hasImage()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(pair.getValue().getPreviousImageTime() == Long.MIN_VALUE){
                System.out.println("ZERRRRO");
                return new AbstractMap.SimpleEntry<>(pair.getKey(),pair.getValue().getImage());
            }
            long timeStamp = pair.getValue().peekImage().getTimeStamp();

            if(timeStamp < minTime){
                oldestEntry = pair.getKey();
                minTime = timeStamp;
                System.out.println("FOUND");
            }
        }
        System.out.println("id: "+oldestEntry);
        ImageModel oldestImage = cameraMap.get(oldestEntry).peekImage();
        try {
            System.out.println("Sleep: " + (oldestImage.getTimeStamp()-cameraMap.get(oldestEntry).getPreviousImageTime()));
            Thread.sleep(oldestImage.getTimeStamp()-cameraMap.get(oldestEntry).getPreviousImageTime());
            cameraMap.get(oldestEntry).getImage();
            //wait(oldestImage.getTimeStamp()-minTime);
            return  new AbstractMap.SimpleEntry<Integer, ImageModel>(oldestEntry,oldestImage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    synchronized public void kill() {
        alive = false;
    }
}
