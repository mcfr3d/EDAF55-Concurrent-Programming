package components;

import javafx.application.Platform;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Camera;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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

public class ImageGridView extends Pane {
    LinkedHashMap<Integer, CameraView> cameraViewMap;

    public ImageGridView(double width, double height) {
        cameraViewMap = new LinkedHashMap<>();
        updateSize(width, height);
    }


    public void updateSize(double width, double height) {
        setPrefSize(width, height);
        updateImages(width, height);
    }
    public void connectCamera(int key,String address) {
        CameraView cameraView = new CameraView(key, address);
        getChildren().add(cameraView);
        cameraViewMap.put(key, cameraView);
        System.out.println(cameraViewMap.size());
        updateImages(getPrefWidth(), getPrefHeight());
    }

    private void updateImages(double width, double height) {
        if (cameraViewMap.size() > 1) {
            int rows = (cameraViewMap.size()+1)/2;
            int counter = 0;
            double viewWidth = width/2 - 30;
            double viewHeight = height/rows - 40;
            for (CameraView cameraView: cameraViewMap.values()) {
                cameraView.updateSize(viewWidth,viewHeight);
                double offsetLeft = (counter % 2 == 0 ? 20 : 40) + (viewWidth * (counter % 2));
                double offsetTop  = 20 * (counter/2+1) + viewHeight * (counter/2);
                cameraView.relocate(offsetLeft,offsetTop);
                ++counter;
            }
        } else if (cameraViewMap.size() == 1) {

            for (CameraView cameraView : cameraViewMap.values()) {
                cameraView.updateSize(width-60,height-60);
                cameraView.relocate(30 , 30);

            }
        }
    }
    public void updateImage(ImageModel imageModel, Integer key) {
        cameraViewMap.get(key).updateImage(imageModel);
    }
}
