package components;

import actions.DisconnectAction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.ImageModel;

public class CameraView extends Pane {
    private ImageView imageView;
    boolean loading = true;
    private int key;
    Button disconnectButton;
    Text delayTime;
    Text loadingText;
    public CameraView(int key , String address){

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        disconnectButton = new Button();
        delayTime  = new Text();
        setStyle("-fx-background-color: #000;");
        loadingText = new Text("Connecting to " + address );
        loadingText.getStyleClass().add("loading-text");
        disconnectButton.getStyleClass().add("disconnect-button");
        loadingText.getStyleClass().add("loading-text");
        getChildren().add(loadingText);
        this.key = key;

        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
        delayTime.relocate(offsetLeft + 15 , offsetTop + 15);
        disconnectButton.relocate(offsetLeft+imageWidth - 40 , offsetTop + 40);

    }
    public void updateImage(ImageModel imageModel){
        Platform.runLater(()->{
            imageView.setImage(imageModel.image);
            System.out.println("CamerView got image");
            if(loading){
                getChildren().add(imageView);
                getChildren().add(disconnectButton);
                getChildren().add(delayTime);
                relocateImage();
                loading = false;
                setStyle("-fx-background-color: transparent;");
            }else{
                delayTime.setText(String.valueOf(System.currentTimeMillis() - imageModel.timeStamp));
            }
        });
    }



}