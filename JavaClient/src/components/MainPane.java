package components;

import actions.ConnectAction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.ButtonMonitor;
import models.ImageModel;

public class MainPane extends BorderPane {
    ImageGridView imageGridView;
    ControlPane controlPane;
    ObservableList<String> avalibaleCameras;
    public MainPane(ButtonMonitor buttonMonitor, Stage primaryStage){
        avalibaleCameras = FXCollections.observableArrayList("1","2","3","4","5","6","7","8");

        imageGridView = new ImageGridView(800, 600 - 100);
        setCenter(imageGridView);

        controlPane = new ControlPane(buttonMonitor, primaryStage, 800, avalibaleCameras);
        setBottom(controlPane);

        getStylesheets().add("css/main.css");

        controlPane.addEventHandler(ConnectEvent.CONNECT_EVENT, new ConnectHandler() {
            @Override
            public void onEvent(ConnectEvent event) {
                avalibaleCameras.filtered((curKey) -> false);
                imageGridView.connectCamera(event.key, event.address);
                System.out.println("Connect");
                buttonMonitor.addAction(new ConnectAction(event.address, event.key));
            }
        });


    }

    public void updateSize(double w, double h) {
        imageGridView.updateSize(w,h);
        controlPane.updateWidth(w);
    }

    public void updateImage(ImageModel imageModel, Integer key) {
        imageGridView.updateImage(imageModel,key);

    }
}
