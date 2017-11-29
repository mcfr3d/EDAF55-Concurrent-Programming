package components;

import actions.ConnectAction;
import actions.MotionAction;
import actions.SyncAction;
import constants.Constants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

        AbstractMap.SimpleEntry[] modeList = {new AbstractMap.SimpleEntry(Constants.MotionMode.IDLE,"Idle"),
                                        new AbstractMap.SimpleEntry(Constants.MotionMode.AUTO,"Auto"),
                                        new AbstractMap.SimpleEntry(Constants.MotionMode.MOVIE,"Movie"),
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
                        final Stage dialog = new ConnectDialog(primaryStage);
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
