package components;

import javafx.application.Platform;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Camera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import models.ImageModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ImageGridView extends AnchorPane {
    LinkedHashMap<Integer, CameraView> cameraViewMap;

    public ImageGridView(double width, double height) {

        cameraViewMap = new LinkedHashMap<>();
        updateSize(width, height);

    }


    public void updateSize(double width, double height) {
        setPrefSize(width, height);
        //updateImages(width, height);

    }
    public void connectCamera(int key,String address) {
        CameraView cameraView = new CameraView(key, address);
        getChildren().add(cameraView);
        cameraViewMap.put(key, cameraView);
        updateImages(getPrefWidth(), getPrefHeight());
    }

    private void updateImages(double width, double height) {
        int counter = 0;
        if (cameraViewMap.size() > 1) {

            for (CameraView cameraView: cameraViewMap.values()) {
                double viewWidth  = width / 2 - 30;
                double viewHeight = height / (cameraViewMap.size()/2) - 30;
                cameraView.updateSize(viewWidth,viewHeight);
                double offsetLeft = 15*(1+counter%2) + viewWidth * counter%2;
                double offsetTop  = (counter > 1 ? 30 : 15) +  viewHeight * counter/2;
                setLeftAnchor(cameraView, offsetLeft);
                setTopAnchor(cameraView, offsetTop);
                counter++;

            }

        } else if (cameraViewMap.size() == 1) {

            for (CameraView cameraView : cameraViewMap.values()) {
                cameraView.updateSize(width-60,height-60);
                setLeftAnchor(cameraView, 30.0);
                setTopAnchor(cameraView, 30.0);

            }
        }
    }/*

    public void updateImage(Image image, int id) {
        Platform.runLater(()->{
            if(imageMap.containsKey(id)){
                imageMap.get(id).setImage(image);
            }else{
                ImageView imageView = new ImageView();
                imageView.setImage(image);
                imageView.setPreserveRatio(true);
                getChildren().add(imageView);
                System.out.println(getChildren().size());
                imageMap.put(id, imageView);
                updateImages(getPrefWidth(), getPrefHeight());
                //animation
            }



        });
    }
*/

    public void updateImage(ImageModel imageModel, Integer key) {
        cameraViewMap.get(key).updateImage(imageModel);
    }
}
