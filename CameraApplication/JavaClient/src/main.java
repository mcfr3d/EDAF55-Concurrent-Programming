import components.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import models.ButtonMonitor;
import models.CameraMonitor;
import threads.ButtonHandler;
import threads.SyncThread;


public class main extends Application {
    static int port;
    public static void main(String[] args) {
        if(args.length > 0){
            port = Integer.valueOf(args[0]);
        }else{
            port = 6666;
        }
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Realtime Cameras");

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        CameraMonitor cameraMonitor = new CameraMonitor();

        ButtonMonitor buttonMonitor = new ButtonMonitor();
        ButtonHandler buttonHandler = new ButtonHandler(cameraMonitor,buttonMonitor);

        MainPane mainPane = new MainPane(buttonMonitor,primaryStage,port);

        SyncThread syncThread = new SyncThread(cameraMonitor,mainPane);


        primaryStage.setOnCloseRequest((event -> {cameraMonitor.kill();
            System.exit(0);
        }));



        primaryStage.setScene(new Scene(mainPane, 800 ,600));

        syncThread.start();
        buttonHandler.start();
        primaryStage.show();

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            mainPane.updateSize(newVal.doubleValue(), mainPane.getHeight() - 100);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            mainPane.updateSize(mainPane.getWidth(), mainPane.getHeight() - 100);
        });



    }
}