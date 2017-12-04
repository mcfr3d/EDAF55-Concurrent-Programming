package components;

import actions.MotionAction;
import actions.SyncAction;
import constants.Constants;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.ButtonMonitor;

import java.util.AbstractMap;

public class ControlPane extends AnchorPane {
    VBox multipleChoiceControl;
    DialogPane dialogPane;

    public ControlPane(ButtonMonitor buttonMonitor , Stage primaryStage, double width, ObservableList<Integer> avalibaleCameras){

        multipleChoiceControl = new VBox();

        Button addButton = new Button("+");
        addButton.getStyleClass().addAll("camera-button","add-button" );
        addButton.setAlignment(Pos.CENTER);

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


        multipleChoiceControl.getChildren().add(syncControl);
        multipleChoiceControl.getChildren().add(motionControl);

        multipleChoiceControl.setMinHeight(100);
        multipleChoiceControl.setMinWidth(225);
        multipleChoiceControl.setMaxHeight(100);
        multipleChoiceControl.setMaxWidth(225);
        multipleChoiceControl.setSpacing(10);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.4));

        addButton.setEffect(dropShadow);

        getChildren().add(multipleChoiceControl);
        getChildren().add(addButton);

        double addButtonOffsetRight = 100;
        double addButtonOffsetTop = 10;

        setRightAnchor(addButton , addButtonOffsetRight);
        setTopAnchor(addButton , addButtonOffsetTop);

        dialogPane = new DialogPane(avalibaleCameras);
        getChildren().add(dialogPane);

        setRightAnchor(dialogPane , addButtonOffsetRight);
        setTopAnchor(dialogPane , addButtonOffsetTop - 160);

        updateWidth(width);

        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!dialogPane.isOpen()){
                    dialogPane.show();
                }
            }
        });
        /*dialogPane.addEventHandler(ConnectEvent.CONNECT_EVENT, new ConnectHandler() {
            @Override
            public void onEvent(ConnectEvent event) {
                fireEvent(event);
            }
        });*/
    }
    public void updateWidth(double w){
        setPrefWidth(w);
        double multipleChoiceOffset = (getPrefWidth()/2) - multipleChoiceControl.getMinWidth()/2;
        setLeftAnchor(multipleChoiceControl, multipleChoiceOffset);
    }
}
