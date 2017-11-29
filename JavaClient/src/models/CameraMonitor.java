package models;

import constants.Constants;
import javafx.util.Pair;
import threads.InputThread;
import threads.MotionListener;
import threads.OutputThread;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;


public class CameraMonitor {
    private final static int SYNC_THRESHOLD = 200;
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
                //wait exception RIP
            }
        }
        return motionMode;
    }
    synchronized public int getMotionModeOutput() {
        while(!motionModeChanged){
            try {
                wait();
            } catch (InterruptedException e) {
                //wait exception
            }
        }
        return motionMode;
    }
    private void forceIdle(Socket socket){
        try {
            OutputStream os = socket.getOutputStream();
            os.write(0x00);
            os.write(Constants.MotionCode.IDLE);

        } catch (IOException e) {
            //Error writing
        }
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
       if(forceMode != Constants.MotionMode.AUTO){
           setMotionMode(forceMode);
       }
       notifyAll();
    }

    synchronized public void addImage(int camera, ImageModel imageModel){
            cameraMap.get(camera).putImage(imageModel);
            notifyAll();
    }
    synchronized public boolean isAlive(){
        return alive;
    }

    synchronized public void connectCamera(String address , int port, int key){
        try {
            Socket socket = new Socket(address, port);
            activeSockets.add(socket);
            forceIdle(socket);
            InputThread inputThread = new InputThread(socket, this); //ERROR
            MotionListener motionListener = new MotionListener(this,address); //ERROR
            cameraMap.put(key,new CameraModel(address,port));
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
                        long diff = cameraModels.get(n).peekImage().timeStamp - cameraModels.get(m).peekImage().timeStamp;
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
    synchronized public ArrayList<Pair<Integer,ImageModel>> getImage(){

        ArrayList<Pair<Integer,ImageModel>> imageList  = new ArrayList<>();
        while(!anyHasImages() ){
            try {
                wait();

            } catch (InterruptedException e) {
                //Error
            }
        }
        if(sync && forceSync){
            getImagesSync(imageList);

        }else{
            getImageAsync(imageList);
        }

        imageList.sort(new Comparator<Pair<Integer, ImageModel>>() {
            @Override
            public int compare(Pair<Integer, ImageModel> o1, Pair<Integer, ImageModel> o2) {
                return (int) (o1.getValue().getTimeStamp() - o2.getValue().getTimeStamp());
            }
        });
        return imageList;

    }
    private void getImageAsync(ArrayList<Pair<Integer,ImageModel>> imageList){
        if(longestDiff() <= SYNC_THRESHOLD){
            sync = true;
        }
        for(Map.Entry<Integer,CameraModel> entryToArray :cameraMap.entrySet()){
            if(entryToArray.getValue().hasImage()) {
                imageList.add(new Pair<>(entryToArray.getKey(), entryToArray.getValue().getImage()));
            }
        }
    }
    private void getImagesSync(ArrayList<Pair<Integer,ImageModel>> imageList) {
        for (Map.Entry<Integer, CameraModel> entry : cameraMap.entrySet()) {
            if (entry.getValue().hasImage()) {
                while (!allHasImage()) {
                    try {
                        long currentTime = System.currentTimeMillis();

                        wait(SYNC_THRESHOLD);

                        if (System.currentTimeMillis() >= currentTime + SYNC_THRESHOLD) {
                            sync = false; //TODO Fault tolerance
                            break;
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
                if (longestDiff() > SYNC_THRESHOLD) {
                    sync = false;
                }
                for (Map.Entry<Integer, CameraModel> entryToArray : cameraMap.entrySet()) {
                    if (entryToArray.getValue().hasImage()) {
                        imageList.add(new Pair<>(entryToArray.getKey(), entryToArray.getValue().getImage()));
                    }
                }
                break;
            }
        }
    }
    synchronized public void kill() {
        alive = false;
    }
}
