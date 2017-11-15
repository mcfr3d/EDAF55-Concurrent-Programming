package components;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class ControlPane extends VBox {

    public ControlPane(){
        setAlignment(Pos.CENTER);
        MultipleChoiceView syncControl = new MultipleChoiceView(new String[]{"Sync" , "Async"},0);
        MultipleChoiceView modeControl = new MultipleChoiceView(new String[]{"Movie" , "Auto" , "Idle"},1);
        getChildren().add(syncControl);
        getChildren().add(modeControl);
        setMinHeight(100);
        setMaxHeight(100);
        setSpacing(10);

    }
}
