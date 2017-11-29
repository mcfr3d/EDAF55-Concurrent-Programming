package components;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.ButtonMonitor;

public class MainPane extends BorderPane {
    ImageGridView imageGridView;
    public MainPane(ButtonMonitor buttonMonitor, Stage primaryStage){
        imageGridView = new ImageGridView(getWidth(), getHeight() - 100);
        setCenter(imageGridView);
        setBottom(new ControlPane(buttonMonitor, primaryStage));
        getStylesheets().add("css/main.css");


    }

    public void updateSize(double w, double h) {
        imageGridView.updateSize(w,h);
    }

    public void updateImage(Image image, Integer key) {
        imageGridView.updateImage(image,key);
    }
}
