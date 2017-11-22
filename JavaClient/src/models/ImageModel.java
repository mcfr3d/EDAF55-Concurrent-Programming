package models;

import javafx.scene.image.Image;

public class ImageModel {
    private Image image;
    private long timeStamp;
    public ImageModel(Image image,long timeStamp){
        this.image = image;
        this.timeStamp = timeStamp;
    }

    public Image getImage() {
        return image;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
