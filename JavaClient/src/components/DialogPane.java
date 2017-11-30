package components;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class DialogPane extends Pane {
    boolean open = false;
    public DialogPane(ObservableList<String> avalibaleCameras){
        setMinWidth(250);
        setMinHeight(150);
        getStyleClass().add("dialog-pane");
        Text infoText = new Text("Connect Camera");

        setScaleX(0);
        setScaleY(0);


        Text argusText = new Text("argus");
        HBox cameraOptionBox = new HBox(5);
        cameraOptionBox.setPadding(new Insets(0,10,0,10));
        Button cancelButton = new Button("Cancel");
        Button connectButton = new Button("Connect");
        ComboBox<String> cameraChooser = new ComboBox(avalibaleCameras);

        cameraChooser.getSelectionModel().select(0);

        infoText.getStyleClass().add("header");
        argusText.getStyleClass().add("argus-text");
        cameraOptionBox.getStyleClass().add("camera-chooser");
        cancelButton.getStyleClass().add("cancel");

        cameraOptionBox.setAlignment(Pos.CENTER_LEFT);


        cameraOptionBox.getChildren().add(argusText);
        cameraOptionBox.getChildren().add(cameraChooser);
        getChildren().add(cameraOptionBox);
        getChildren().add(infoText);
        getChildren().add(cancelButton);
        getChildren().add(connectButton);

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                close();
            }
        });

        connectButton.setOnMouseClicked(event -> {
            close();
            int key = Integer.valueOf(cameraChooser.getSelectionModel().getSelectedItem());
            String address = "argus-" + key;
            this.fireEvent(new ConnectEvent(ConnectEvent.CONNECT_EVENT,key,address));
        });

        infoText.relocate(15,20);
        cameraOptionBox.relocate(15 , 50);
        cancelButton.relocate(15,100);
        connectButton.relocate(120,100);

    }
    public void show(){
        open = true;
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(150));

            }

            protected void interpolate(double frac) {
                setScaleX(frac);
                setScaleY(frac);
                setTranslateX((1-frac) * getMinHeight()/2);
                setTranslateY((1-frac) * getHeight()/2);

            }

        };

        animation.play();

    }

    public void close(){
        open = false;
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(150));
            }

            protected void interpolate(double frac) {
                setScaleX(1-frac);
                setScaleY(1-frac);
                setTranslateX((frac) * getMinHeight()/2);
                setTranslateY((frac) * getHeight()/2);
            }

        };
        animation.play();
    }

    public boolean isOpen() {
        return open;
    }
}
