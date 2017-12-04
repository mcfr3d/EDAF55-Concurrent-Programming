package components;

import actions.DisconnectAction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.ImageModel;

import java.awt.event.MouseEvent;

public class CameraView extends Pane {
    private ImageView imageView;
    boolean loading = true;
    private int key;
    ImageView disconnectButton;
    Text delayTime;
    Text loadingText;
    public CameraView(int key , String address){
        this.key = key;

        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        disconnectButton = new ImageView();
        disconnectButton.setFitWidth(30);
        disconnectButton.setFitHeight(30);

        disconnectButton.setPreserveRatio(true);

        Image closeImage = new Image("resources/disconnect.png");

        disconnectButton.setImage(closeImage);

        delayTime  = new Text();
        setStyle("-fx-background-color: #000;");
        loadingText = new Text("Connecting to " + address );
        loadingText.getStyleClass().add("loading-text");

        disconnectButton.getStyleClass().add("disconnect-button");
        delayTime.getStyleClass().add("delay-text");
        loadingText.getStyleClass().add("loading-text");

        getChildren().add(loadingText);
        getChildren().add(disconnectButton);

        disconnectButton.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                fireEvent(new DisconnectEvent(DisconnectEvent.DISCONNECT_EVENT, key));

            }
        });


    }

    public void updateSize(double width , double height){
        setMinHeight(height);
        setMaxHeight(height);
        setMinWidth(width);
        setMaxWidth(width);
        imageView.setFitHeight(getMinHeight());
        imageView.setFitWidth(getMinWidth());
        if(loading){
            loadingText.relocate(width/2 - loadingText.getBoundsInLocal().getWidth()/2 ,
                    height/2 - loadingText.getBoundsInLocal().getHeight()/2 );
            disconnectButton.relocate(width - 45 , 15);

        }
        else{
            relocateImage();
        }
    }
    private void relocateImage(){
        double imageWidth = imageView.getBoundsInParent().getWidth();
        double imageHeight= imageView.getBoundsInParent().getHeight();
        double offsetLeft = (getMinWidth() - imageWidth)/2;
        double offsetTop  = (getMinHeight() - imageHeight)/2;
        imageView.relocate(offsetLeft,offsetTop);
        delayTime.relocate(offsetLeft + 15 , offsetTop + 20);
        disconnectButton.relocate(offsetLeft+imageWidth - 45 , offsetTop + 15);

    }
    public void updateImage(ImageModel imageModel){
        Platform.runLater(()->{
            imageView.setImage(imageModel.image);
            delayTime.setText(String.valueOf(System.currentTimeMillis() - imageModel.timeStamp));

            System.out.println("CamerView got image");
            if(loading){
                getChildren().add(imageView);
                getChildren().add(delayTime);
                getChildren().remove(loadingText);
                imageView.toBack();
                relocateImage();
                loading = false;
                setStyle("-fx-background-color: transparent;");
            }
        });
    }



}