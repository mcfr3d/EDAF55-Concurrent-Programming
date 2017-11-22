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
import models.CameraMonitor;
import threads.SyncThread;


public class main extends Application {
    ListView<CameraModel> list = new ListView<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");


        BorderPane root = new BorderPane();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        ObservableList<CameraModel> cm = FXCollections.observableArrayList();
        for(int n = 0 ; n<100;n++){
            CameraModel m = new CameraModel("Rum " + n , "192.168.0."+n, n%2 == 0);
            cm.add(m);
        }
        primaryStage.setScene(new Scene(root, 800 ,600));
        CameraListView cv = new CameraListView(cm);



        //root.setLeft(cv);
        primaryStage.show();
        ImageGridView spTest = new ImageGridView(root.getWidth() - cv.getWidth()  , root.getHeight() - 100);

        BorderPane bp2 = new BorderPane(spTest);
        bp2.setBottom(new ControlPane());

        root.setCenter(bp2);
        CameraMonitor cameraMonitor = new CameraMonitor();
        SyncThread syncThread = new SyncThread(cameraMonitor,spTest);
        syncThread.start();
        cameraMonitor.connectCamera("127.0.0.1" , 5000);
        //cameraMonitor.connectCamera("127.0.0.1" , 5000);

        primaryStage.getScene().getStylesheets().add("css/main.css");

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println(newVal);
            spTest.updateSize(newVal.doubleValue() - cv.getWidth() , root.getHeight() - 100);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            spTest.updateSize(root.getWidth() - cv.getWidth() , root.getHeight() - 100);
        });

        primaryStage.setOnCloseRequest((event -> {cameraMonitor.kill();
            System.exit(0);
        }));
        primaryStage.show();

    }

}