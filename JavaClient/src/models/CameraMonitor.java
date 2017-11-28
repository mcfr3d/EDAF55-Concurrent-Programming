package models;

import constants.Constants;
import javafx.scene.image.Image;
import threads.InputThread;
import threads.MotionListener;
import threads.OutputThread;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Stream;


public class CameraMonitor {

    private HashMap<Integer , CameraModel> cameraMap;
    private boolean sync = true;
    private boolean forceSync = true;
    private ArrayList<Socket> activeSockets;
    private boolean motionModeChanged = false;
    private int motionMode = Constants.MotionMode.IDLE;
    private int forceMode  = Constants.MotionMode.AUTO;

    private boolean alive = true;
    public CameraMonitor(){
        cameraMap = new HashMap<>();
        activeSockets = new ArrayList<>();
        OutputThread outputThread = new OutputThread(this);
        outputThread.start();
    }

    synchronized public ArrayList<Socket> getActiveSockets() {
        return (ArrayList<Socket>)activeSockets.clone();
    }
    synchronized public int getMotionModeMotion() {
        while(forceMode != Constants.MotionMode.AUTO){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        return motionMode;
    }
    synchronized public int getMotionModeOutput() {
        while(!motionModeChanged){
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        return motionMode;
    }
    synchronized public void setMotionMode(int mode){
        motionMode = mode;
        motionModeChanged = true;
        notifyAll();
    }

    synchronized public boolean isMotionModeChanged() {
        return motionModeChanged;
    }

    synchronized public void setMotionModeChanged(boolean motionModeChanged) {
        this.motionModeChanged = motionModeChanged;
    }

    synchronized public void setForceMode(int mode){
       forceMode = mode;
       notifyAll();
    }




    synchronized public void addImage(int camera, ImageModel imageModel){
            cameraMap.get(camera).putImage(imageModel);
            notifyAll();
    }
    synchronized public boolean isAlive(){
        return alive;
    }

    synchronized public void connectCamera(String address , int port){
        try {
            Socket socket = new Socket(address, port);
            activeSockets.add(socket);
            InputThread inputThread = new InputThread(socket, this); //ERROR
            MotionListener motionListener = new MotionListener(this,address); //ERROR
            cameraMap.put(inputThread.hashCode(),new CameraModel(address,port));
            motionListener.start(); //ERRROR
            inputThread.start(); //ERROR
        }catch(IOException e){
            //Couldnt connect
        }
    }

    synchronized public boolean isSync() {
        return sync;
    }

    synchronized public void setSync(boolean sync) {
        this.sync = sync;
    }
    private boolean anyHasImages(){
        return cameraMap.values()
                .stream()
                .map((camera) -> camera.hasImage())
                .reduce(false,(res,hasImage) -> res || hasImage);
    }
    private boolean allHasImage(){

        return cameraMap.values()
                .stream()
                .map((camera) -> camera.hasImage())
                .reduce(true,(res,hasImage) -> res && hasImage);

    }
    private long longestDiff(){
        long longestDiff = 0;
        ArrayList<CameraModel> cameraModels =  new ArrayList<>(cameraMap.values());
        if(allHasImage()){
            for(int n = 0 ; n < cameraMap.values().size() ; n++){
                for(int m = n+1 ; m < cameraMap.values().size() ; m++){
                    if(cameraModels.get(n).hasImage() && cameraModels.get(m).hasImage()) {
                        long diff = cameraModels.get(n).peekImage().getTimeStamp() - cameraModels.get(m).peekImage().getTimeStamp();
                        diff = Math.abs(diff)/1000000;
                        if (diff > longestDiff) {
                            longestDiff = diff;
                        }
                    }
                }
            }
            return longestDiff;

        }else{
            return Long.MAX_VALUE;
        }


    }
    synchronized public ArrayList<Map.Entry<Integer,ImageModel>> getImage(){

        ArrayList<Map.Entry<Integer,ImageModel>> imageList  = new ArrayList<>();
        while(!anyHasImages() ){
            try {
                wait();

            } catch (InterruptedException e) {
                //Error
            }
        }
        if(sync){
            for(Map.Entry<Integer,CameraModel> entry : cameraMap.entrySet()){
                if(entry.getValue().hasImage()){
                    //En har bild.
                    while(!allHasImage()){
                        try {
                            long currentTime = System.currentTimeMillis();

                            wait(200);

                            if(System.currentTimeMillis() >= currentTime + 200){
                                sync = false;
                                break;
                            }




                        } catch (InterruptedException e) {

                        }
                    }
                    if(longestDiff() > 200 ){
                        sync = false;
                    }

                    for(Map.Entry<Integer,CameraModel> entryToArray :cameraMap.entrySet()){
                        if(entryToArray.getValue().hasImage()) {
                            imageList.add(new AbstractMap.SimpleEntry<Integer, ImageModel>(entryToArray.getKey(), entryToArray.getValue().getImage()));
                        }
                    }
                    break;
                }
            }
        }else{ //ASYNC

            if(longestDiff() <= 200){
                sync = true;
            }
            for(Map.Entry<Integer,CameraModel> entryToArray :cameraMap.entrySet()){
                if(entryToArray.getValue().hasImage()) {
                    imageList.add(new AbstractMap.SimpleEntry<Integer, ImageModel>(entryToArray.getKey(), entryToArray.getValue().getImage()));
                }
            }

        }


        imageList.sort(new Comparator<Map.Entry<Integer, ImageModel>>() {
            @Override
            public int compare(Map.Entry<Integer, ImageModel> o1, Map.Entry<Integer, ImageModel> o2) {
                return (int) (o1.getValue().getTimeStamp() - o2.getValue().getTimeStamp());
            }
        });
        return imageList;

    }

    synchronized public void kill() {
        alive = false;
    }
}
