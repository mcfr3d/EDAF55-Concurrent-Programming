package models;

import constants.Constants;
import javafx.util.Pair;
import threads.InputThread;
import threads.MotionListener;
import threads.OutputThread;

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

    /*
    - Main operations
     */

    synchronized public void kill() {
        alive = false;
    }

    synchronized public boolean isAlive(){
        return alive;
    }

    /*
    - MotionListener operations
     */

    synchronized public int getMotionModeMotion() {
        while(forceMode != Constants.MotionMode.AUTO){
            try {
                wait();
            } catch (InterruptedException e) {
                if(Constants.Flags.DEBUG) System.out.println("Thread interrupted in getMotionModeMotion(), interrupting thread.");
                Thread.currentThread().interrupt();
                return -1;
            }
        }
        return motionMode;
    }

    synchronized public void setMotionMode(int mode){
        motionMode = mode;
        motionModeChanged = true;
        if(Constants.Flags.DEBUG) System.out.println("Setting new motion in setMotionMode to " + mode + ".");
        notifyAll();
    }

    /*
    - OutputThread operations
     */
    synchronized public ArrayList<Socket> getActiveSockets() {
        return (ArrayList<Socket>)activeSockets.clone();
    }

    synchronized public int getMotionModeOutput() {
        while(!motionModeChanged){
            try {
                wait();
            } catch (InterruptedException e) {
                if(Constants.Flags.DEBUG) System.out.println("Thread interrupted in getMotionModeOutput(), interrupting thread.");
                Thread.currentThread().interrupt();
                return -1;
            }
        }
        return motionMode;
    }

    synchronized public void setMotionModeChanged(boolean motionModeChanged) {
        if(Constants.Flags.DEBUG) System.out.println("Setting motionModeChange to " + motionModeChanged + ".");
        this.motionModeChanged = motionModeChanged;
    }

    /*
    - ButtonHandler actions
     */

    // Used in MotionAction
    synchronized public void setForceMode(int mode){
       forceMode = mode;
       if(forceMode != Constants.MotionMode.AUTO){
           if(Constants.Flags.DEBUG) System.out.println("Setting forceMode in setForceMode to " + mode + ".");
           setMotionMode(forceMode);
       }
       notifyAll();
    }

    // Used in SyncAction
    synchronized public void setSync(boolean sync) {
        // TODO
        if(Constants.Flags.DEBUG) System.out.println("Setting sync in setSync to " + sync + ".");
        this.sync = sync;
    }

    // Used in ConnectAction
    synchronized public void connectCamera(String address , int port, int key){
        Socket socket = null;
        //Korrekt addres och port
        try {
            socket = new Socket(address, port);
        } catch(IOException e) {
            // TODO: Tell user
            if(Constants.Flags.DEBUG) System.out.println("Unable to create socket with address: " + address +
                    " and port: " + port + ".");
            return;
        }
        if(Constants.Flags.DEBUG) System.out.println("Successfully created a socket with address: " + address +
                " and port: " + port + ".");

        activeSockets.add(socket);
        forceIdle(socket);

        // Init camera threads
        InputThread inputThread = new InputThread(socket, this , key);
        MotionListener motionListener = new MotionListener(this,address);
        cameraMap.put(key, new CameraModel());

        // Start camera threads
        motionListener.start();
        inputThread.start();
    }
    private void forceIdle(Socket socket){
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            // Might do something in the future with the first byte
            os.write(0x00);
            os.write(Constants.MotionCode.IDLE);
        } catch (IOException e) {
            if(Constants.Flags.DEBUG) System.out.println("OutputStream in CameraMonitor caused IOException.");
        }
    }

    /*
    - InputThread operations
     */

    synchronized public void addImage(int camera, ImageModel imageModel){
        cameraMap.get(camera).putImage(imageModel);
        if(Constants.Flags.DEBUG) System.out.println("Added image model: " + imageModel + " from camera: " + camera + ".");
        notifyAll();
    }

    /*
    - SyncThread operations
     */

    // TODO: Refactor

    synchronized public boolean isSync() {
        return sync;
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
                return (int) (o1.getValue().timeStamp - o2.getValue().timeStamp);
            }
        });
        return imageList;
    }

    private boolean anyHasImages(){
        return cameraMap.values()
                .stream()
                .map(CameraModel::hasImage)
                .reduce(false,(res,hasImage) -> res || hasImage);
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

        } else{
            return Long.MAX_VALUE;
        }
    }

    private boolean allHasImage(){
        return cameraMap.values()
                .stream()
                .map((camera) -> camera.hasImage())
                .reduce(true,(res,hasImage) -> res && hasImage);
    }
}
