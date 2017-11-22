package models;

import javafx.scene.image.Image;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class CameraModel {
    private boolean sync;
    private String ip;
    private int port;
    private long previousImageTime = Long.MIN_VALUE;
    private Queue<ImageModel> imageBuffer;

    public CameraModel(String ip, int port) {
        imageBuffer = new LinkedList<>();
        this.ip = ip;
        this.port = port;

    }


    public void putImage(ImageModel image) {
        imageBuffer.add(image);
    }

    public boolean hasImage() {
        return !imageBuffer.isEmpty();
    }

    public ImageModel getImage() {
        ImageModel imageModel = imageBuffer.poll();
        previousImageTime = imageModel.getTimeStamp();
        return imageModel;

    }
    public ImageModel peekImage() {
        return imageBuffer.peek();

    }


    public long getPreviousImageTime() {
        return previousImageTime;
    }
}
