package components;

import javafx.application.Platform;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
    LinkedHashMap<Integer, ImageView> imageMap;

    public ImageGridView(double width, double height) {

        imageMap = new LinkedHashMap<>();
        updateSize(width, height);

    }


    public void updateSize(double width, double height) {
        setPrefSize(width, height);
        updateImages(width, height);

    }

    private void updateImages(double width, double height) {
        if (imageMap.size() > 2) {

        } else if (imageMap.size() == 2) {
            int counter = 0;
            for (ImageView imageView : imageMap.values()) {
                imageView.setFitWidth(width/2 );
                imageView.setFitHeight(height/2 );
                double offsetLeft = (width/2)*counter +  ((width/2) - imageView.getBoundsInParent().getWidth())/2;
                double offsetTop  = (height - imageView.getBoundsInParent().getHeight()) / 2;
                setLeftAnchor(imageView, offsetLeft);
                setTopAnchor(imageView, offsetTop);
                ++counter;


            }
        } else if (imageMap.size() == 1) {

            for (ImageView imageView : imageMap.values()) {
                imageView.setFitWidth(width - 60);
                imageView.setFitHeight(height - 60);
                double offsetLeft = (width - imageView.getBoundsInParent().getWidth()) / 2;
                double offsetTop = (height - imageView.getBoundsInParent().getHeight()) / 2;
                setLeftAnchor(imageView, offsetLeft);
                setTopAnchor(imageView, offsetTop);

            }
        }
    }

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

    public void connectCamera(int key) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        getChildren().add(imageView);
        System.out.println(getChildren().size());
        imageMap.put(key, imageView);
        updateImages(getPrefWidth(), getPrefHeight());
    }
}
