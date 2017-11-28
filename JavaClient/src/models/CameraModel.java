package models;

import javafx.scene.image.Image;

import java.util.*;

public class CameraModel {
    private boolean sync;
    private String ip;
    private int port;
    private long previousImageTime = Long.MIN_VALUE;
    private LinkedList<ImageModel> imageBuffer;

    public CameraModel(String ip, int port) {
        imageBuffer = new LinkedList<>();
        this.ip = ip;
        this.port = port;

    }


    public void putImage(ImageModel image) {
        ListIterator itr = imageBuffer.listIterator();
        while(itr.hasNext()){
            if(image.getTimeStamp() < ((ImageModel)itr.next()).getTimeStamp()){
                if(itr.hasPrevious()){
                    itr.previous();
                    itr.add(image);
                }else{
                    imageBuffer.addFirst(image);
                }
                return;
            }
        }
        imageBuffer.addLast(image);
    }

    public boolean hasImage() {
        return !imageBuffer.isEmpty();
    }

    public ImageModel getImage() {
        ImageModel imageModel = imageBuffer.poll();
        return imageModel;

    }
    public ImageModel peekImage() {
        return imageBuffer.peek();

    }


    public long getPreviousImageTime() {
        return previousImageTime;
    }
}
