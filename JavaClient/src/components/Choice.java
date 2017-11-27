package components;

import javafx.scene.control.Button;

public class Choice extends javafx.scene.control.Button {
    boolean active = false;
    public Choice(String text) {
        setText(text);


        getStyleClass().add("choice");
        setOnMouseEntered((event) -> {
            getStyleClass().add("active");
        });
        setOnMouseExited((event)->{
            getStyleClass().remove("active");

        });

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
