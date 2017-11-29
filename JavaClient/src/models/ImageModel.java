package models;

import javafx.scene.image.Image;

public class ImageModel {
    public final Image image;
    public final long timeStamp;
    public ImageModel(Image image,long timeStamp){
        this.image = image;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Image with timestamp: " + timeStamp;
    }
}
