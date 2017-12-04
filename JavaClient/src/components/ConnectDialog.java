package components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConnectDialog extends Stage {
    public ConnectDialog(Stage primaryStage){
        initStyle(StageStyle.UNDECORATED);
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(primaryStage);
        BorderPane dialogBorderPane = new BorderPane();
        dialogBorderPane.setTop(new Text("Connect to Camera!"));
        HBox dialogHbox = new HBox();
        dialogBorderPane.setCenter(dialogHbox);
        dialogHbox.getChildren().add(new Text("argus- "));
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "1","2","3","4","5","6","7","8"
                );
        ComboBox<String> numberList = new ComboBox<>(options);
        dialogHbox.getChildren().add(numberList);
        HBox bottomBox = new HBox();
        dialogBorderPane.setBottom(bottomBox);
        Button cancel = new Button("Cancel");
        Button connect = new Button("Connect");
        bottomBox.getChildren().add(cancel);
        bottomBox.getChildren().add(connect);
        cancel.setOnAction(event -> close());
        connect.setOnAction(event -> {
            close();
            //buttonMonitor.addAction(new ConnectAction("argus-" + numberList.getSelectionModel().getSelectedItem()));
        });
        Scene dialogScene = new Scene(dialogBorderPane, 300, 200);
        setScene(dialogScene);

    }
}
