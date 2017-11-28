import components.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.ButtonMonitor;
import models.CameraMonitor;
import threads.ButtonHandler;
import threads.SyncThread;


public class main extends Application {
    ListView<CameraModel> list = new ListView<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realtime Cameras");


        BorderPane root = new BorderPane();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setScene(new Scene(root, 800 ,600));

        ImageGridView imageGridView = new ImageGridView(root.getWidth(), root.getHeight() - 100);

        root.setCenter(imageGridView);

        CameraMonitor cameraMonitor = new CameraMonitor();
        SyncThread syncThread = new SyncThread(cameraMonitor,imageGridView);
        syncThread.start();

        ButtonMonitor buttonMonitor = new ButtonMonitor();
        ButtonHandler buttonHandler = new ButtonHandler(cameraMonitor,buttonMonitor);
        buttonHandler.start();
        root.setBottom(new ControlPane(buttonMonitor, primaryStage));


        primaryStage.getScene().getStylesheets().add("css/main.css");

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println(newVal);
            imageGridView.updateSize(newVal.doubleValue(), root.getHeight() - 100);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageGridView.updateSize(root.getWidth(), root.getHeight() - 100);
        });

        primaryStage.setOnCloseRequest((event -> {cameraMonitor.kill();
            System.exit(0);
        }));
        primaryStage.show();

    }

}