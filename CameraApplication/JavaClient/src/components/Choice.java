package components;

import javafx.event.EventType;
import javafx.scene.control.Button;

public class Choice extends javafx.scene.control.Button {
    boolean active = false;
    int value;

    public Choice(String text, int value) {
        setText(text);
        this.value = value;

        getStyleClass().add("choice");
        setOnMouseEntered((event) -> {
            getStyleClass().add("active");
        });
        setOnMouseExited((event)->{
            getStyleClass().remove("active");

        });

    }

    public int getValue() {
        return value;
    }

    public void setActive(boolean active){
        this.active = active;
        if(active){
            getStyleClass().add("active");

        }else{
            getStyleClass().remove("active");
        }
    }
}
