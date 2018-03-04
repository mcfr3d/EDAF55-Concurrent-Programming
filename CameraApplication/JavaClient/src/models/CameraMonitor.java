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
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CameraMonitor {

    private PriorityQueue<Pair<Integer, ImageModel>> buffer;
    private boolean sync = true;
    private boolean forceSync = true;
    private HashMap<Integer, Pair<Socket,MotionListener>> connectionMap; //Change to map
    private boolean motionModeChanged = false;
    private int motionMode = Constants.MotionMode.IDLE;
    private int forceMode  = Constants.MotionMode.AUTO;
    private boolean alive = true;
    private HashMap<Integer,Integer> bufferCounter ;

    public CameraMonitor(){
        buffer = new PriorityQueue<>(new Comparator<Pair<Integer, ImageModel>>() {
            @Override
            public int compare(Pair<Integer, ImageModel> o1, Pair<Integer, ImageModel> o2) {
                return (int) (o1.getValue().timeStamp - o2.getValue().timeStamp);
            }
        });
        bufferCounter = new HashMap<>();
        connectionMap = new HashMap<>();
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

    private synchronized void incrementBufferSize(int key){
        bufferCounter.put(key, bufferCounter.get(key)+1);
    }
    private synchronized void decrementBufferSize(int key){
        bufferCounter.put(key, bufferCounter.get(key)-1);
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
        return connectionMap.values()
                .stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toCollection(ArrayList::new));

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
    public synchronized void setForceSync(boolean sync) {
        forceSync = sync;
    }


    // Used in ConnectAction
    synchronized public void disconnectCamera(int key){
        if(connectionMap.containsKey(key)) {
            Pair<Socket, MotionListener> connection = connectionMap.get(key);
            connectionMap.remove(key);
            try {
                Socket socket = connection.getKey();
                if (socket != null) socket.close();

            } catch (IOException e) {
                if (Constants.Flags.DEBUG) System.out.println("Socket is already closed");
            }
            connection.getValue().interrupt();
            bufferCounter.remove(key);

            //Remove images from this camera
            Iterator<Pair<Integer, ImageModel>> bufferIterator = buffer.iterator();
            while (bufferIterator.hasNext()) {
                Pair<Integer, ImageModel> currentItem = bufferIterator.next();
                if (currentItem.getKey() == key) {
                    bufferIterator.remove();
                }

            }
        }

    }
    synchronized public void connectCamera(String address , int port, int key){
        Socket socket = null;
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

        forceMode(socket);

        // Init camera threads
        InputThread inputThread = new InputThread(socket, this , key);
        MotionListener motionListener = new MotionListener(this,address);

        connectionMap.put(key  , new Pair<>(socket,motionListener));
        bufferCounter.put(key,0);
        
        // Start camera threads
        motionListener.start();
        inputThread.start();

    }
    private synchronized void forceMode(Socket socket){
        OutputStream os = null;
        try {
            byte code;
            os = socket.getOutputStream();
            // Might do something in the future with the first byte
            os.write(0x00);
            switch (motionMode){
                case Constants.MotionMode.IDLE:
                    code = Constants.MotionCode.IDLE;
                    break;
                case Constants.MotionMode.MOVIE:
                    code = Constants.MotionCode.MOVIE;
                    break;
                default:
                    code = Constants.MotionCode.IDLE;
                    break;
            }
            os.write(code);
        } catch (IOException e) {
            if(Constants.Flags.DEBUG) System.out.println("OutputStream in CameraMonitor caused IOException.");
        }
    }

    /*
    - InputThread operations
     */

    synchronized public void addImage(int key, ImageModel imageModel){
        buffer.add(new Pair<>(key,imageModel));
        if(Constants.Flags.DEBUG) System.out.println("Added image model: " + imageModel + " from camera: " + key + ".");
        incrementBufferSize(key);
        notifyAll();
    }

    /*
    - SyncThread operations
     */

    private synchronized boolean shouldBeAsync(){
        return !bufferCounter.values()
                .stream()
                .map(size -> size > 0)
                .reduce(true,(res,hasImage) -> res && hasImage);
    }
    private synchronized boolean shouldBeSync(long oldestTimeStamp){
        return  oldestTimeStamp + 400 >= System.currentTimeMillis() &&  bufferCounter.values()
                .stream()
                .map((size) -> size > 5)
                .reduce(true , (res, hasImage) -> res && hasImage);
    }

    synchronized public Pair<Integer,ImageModel> getImage(){
        while(buffer.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                //Error
            }
        }
        if(sync && forceSync){
            return getImageSync();
        }else{
            System.out.println("ASYNC");
            return getImageAsync();
        }

    }

    private synchronized Pair<Integer, ImageModel> getImageSync(){
        while(!buffer.isEmpty() && buffer.peek().getValue().timeStamp + 400 >= System.currentTimeMillis()){
            try {
                long dt = Math.max(buffer.peek().getValue().timeStamp + 400 - System.currentTimeMillis(),0);
                wait(dt);
            } catch (InterruptedException e) {
                //RIP
            }
        }
        if(buffer.isEmpty()){
            return null;
        }

        Pair<Integer,ImageModel> image = buffer.poll();
        sync = !shouldBeAsync();
        decrementBufferSize(image.getKey());
        return image;

    }

    private synchronized Pair<Integer,ImageModel> getImageAsync(){
        while(!buffer.isEmpty() && buffer.peek().getValue().timeStamp + 200 >= System.currentTimeMillis()){
            try {
                long dt = Math.max(buffer.peek().getValue().timeStamp + 200 - System.currentTimeMillis(),0);
                wait(dt);
            } catch (InterruptedException e) {

            }
        }
        if(buffer.isEmpty()){
            return null;
        }
        Pair<Integer,ImageModel> image = buffer.poll();
        if(!buffer.isEmpty()) {
            sync = shouldBeSync(buffer.peek().getValue().timeStamp);
        }
        decrementBufferSize(image.getKey());
        return image;

    }
}
