import components.*;
import components.CameraCell;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


public class main extends Application {
    ListView<CameraModel> list = new ListView<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        BorderPane root = new BorderPane();
        primaryStage.setScene(new Scene(root, 1200, 800));
        ImageGridView spTest = new ImageGridView();

        ObservableList<CameraModel> cm = FXCollections.observableArrayList();
        for(int n = 0 ; n<100;n++){
            CameraModel m = new CameraModel("Rum " + n , "192.168.0."+n, n%2 == 0);
            cm.add(m);
        }
        BorderPane bp2 = new BorderPane(spTest);
        bp2.setBottom(new ControlPane());
        CameraListView cv = new CameraListView(cm);
        root.setLeft(cv);
        primaryStage.show();
        root.setCenter(bp2);


        primaryStage.getScene().getStylesheets().add("css/main.css");




        primaryStage.show();

    }
}