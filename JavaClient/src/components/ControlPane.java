package components;

import actions.ConnectAction;
import actions.MotionAction;
import actions.SyncAction;
import constants.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.ButtonMonitor;

import javax.swing.*;
import java.util.AbstractMap;

public class ControlPane extends BorderPane {

    public ControlPane(ButtonMonitor buttonMonitor , Stage primaryStage){

        VBox multipleChoiceControl = new VBox();
        Button addButton = new Button("+");
        addButton.getStyleClass().addAll("camera-button","add-button" );
        addButton.setAlignment(Pos.CENTER);
        setRight(addButton);
        setPadding(new Insets(0,100,0,100));
        multipleChoiceControl.setAlignment(Pos.CENTER);


        AbstractMap.SimpleEntry[] syncList = {new AbstractMap.SimpleEntry(Constants.SyncMode.SYNC,"Sync"),
                                    new AbstractMap.SimpleEntry(Constants.SyncMode.ASYNC,"Async")};
        MultipleChoiceView syncControl = new MultipleChoiceView(syncList,0);

        AbstractMap.SimpleEntry[] modeList = {new AbstractMap.SimpleEntry(Constants.MotionMode.IDLE,"IDLE"),
                                        new AbstractMap.SimpleEntry(Constants.MotionMode.AUTO,"Auto"),
                                        new AbstractMap.SimpleEntry(Constants.MotionMode.MOVIE,"MOVIE"),
                                        };

        MultipleChoiceView motionControl = new MultipleChoiceView(modeList,1);

        motionControl.addEventHandler(MultipleChoiceEvent.MULTIPLE_CHOICE_EVENT, new MultipleChoiceHandler() {
            @Override
            public void onEvent(int code) {
                buttonMonitor.addAction(new MotionAction(code));
            }
        });
        syncControl.addEventHandler(MultipleChoiceEvent.MULTIPLE_CHOICE_EVENT, new MultipleChoiceHandler() {
            @Override
            public void onEvent(int code) {
                buttonMonitor.addAction(new SyncAction(code));
            }
        });

        addButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        final Stage dialog = new Stage();
                        dialog.initStyle(StageStyle.UNDECORATED);
                        dialog.setResizable(false);
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(primaryStage);
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
                        cancel.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                            }
                        });
                        connect.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                dialog.close();
                                buttonMonitor.addAction(new ConnectAction("argus-" + numberList.getSelectionModel().getSelectedItem()));
                            }
                        });
                        Scene dialogScene = new Scene(dialogBorderPane, 300, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    }
                });

        multipleChoiceControl.getChildren().add(syncControl);
        multipleChoiceControl.getChildren().add(motionControl);
        multipleChoiceControl.setMinHeight(100);
        multipleChoiceControl.setMaxHeight(100);
        multipleChoiceControl.setSpacing(10);

        setCenter(multipleChoiceControl);

    }
}
