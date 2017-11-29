package models;

import constants.Constants;
import javafx.scene.image.Image;

import java.util.*;

public class CameraModel {

    private LinkedList<ImageModel> imageBuffer;

    CameraModel() {
        imageBuffer = new LinkedList<>();
    }

    void putImage(ImageModel image) {
        ListIterator itr = imageBuffer.listIterator();
        while(itr.hasNext()){
            if(image.timeStamp < ((ImageModel)itr.next()).timeStamp){
                if(itr.hasPrevious()){
                    itr.previous();
                    itr.add(image);
                    if(Constants.Flags.DEBUG) System.out.println("Adding: " + image + " on index: " + imageBuffer.indexOf(image) + ".");
                }else{
                    if(Constants.Flags.DEBUG) System.out.println("Adding: " + image + " first.");
                    imageBuffer.addFirst(image);
                }
                return;
            }
        }
        if(Constants.Flags.DEBUG) System.out.println("Adding: " + image + " last.");
        imageBuffer.addLast(image);
    }

    boolean hasImage() {
        return !imageBuffer.isEmpty();
    }

    public ImageModel getImage() {
        ImageModel imageModel = imageBuffer.poll();
        return imageModel;
    }

    ImageModel peekImage() {
        return imageBuffer.peek();
    }
}
