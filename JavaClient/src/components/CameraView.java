package components;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.ImageModel;

public class CameraView extends Pane {
    ImageView imageView;
    boolean loading = true;
    public CameraView(int key , String address){

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        setStyle("-fx-background-color: #000;");
        Text text = new Text("Connecting to " + address );
        text.getStyleClass().add("loading-text");
        getChildren().add(text);
        text.relocate(50,50);


    }

    public void updateSize(double width , double height){
        setMinHeight(height);
        setMaxHeight(height);
        setMinWidth(width);
        setMaxWidth(width);
        imageView.setFitHeight(getMinHeight());
        imageView.setFitWidth(getMinWidth());
        if(!loading){
            relocateImage();
        }
    }
    private void relocateImage(){
        double imageWidth = imageView.getBoundsInParent().getWidth();
        double imageHeight= imageView.getBoundsInParent().getHeight();
        double offsetLeft = (getMinWidth() - imageWidth)/2;
        double offsetTop  = (getMinHeight() - imageHeight)/2;
        imageView.relocate(offsetLeft,offsetTop);

    }
    public void updateImage(ImageModel imageModel){
        Platform.runLater(()->{
            imageView.setImage(imageModel.image);
            System.out.println("CamerView got image");
            if(loading){
                getChildren().add(imageView);
                relocateImage();
                loading = false;
                setStyle("-fx-background-color: transparent;");
            }
        });
    }


}