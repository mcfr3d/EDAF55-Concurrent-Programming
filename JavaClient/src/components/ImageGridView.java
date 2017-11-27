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

//        im1 = new ImageView();
//        im2 = new ImageView();
//        getChildren().addAll(im1, im2);
//        im1.setFitWidth(width / 2 - 60);
//        System.out.println("WIDTH: " + width);
//        im2.setFitWidth(width / 2 - 60);
//        System.out.println(im2.getFitWidth());
//        im1.setPreserveRatio(true);
//        im2.setPreserveRatio(true);
//        im1.setImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9XidFccUPuDXNQ8hin-sc38aGvujcQ6Gi3ZWqryqsD_st6SiNOg"));
//        Thread t = new Thread(this);
//        setImageGrid(im1, im2);
//        t.start();

        /*for(int n = 0 ; n<1 ; n++){
            ImageView imageView = new ImageView();
            imageMap.put(n,imageView);
            imageView.setImage(new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9XidFccUPuDXNQ8hin-sc38aGvujcQ6Gi3ZWqryqsD_st6SiNOg"));
            imageView.setPreserveRatio(true);
            getChildren().add(imageView);
        }*/


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
}
