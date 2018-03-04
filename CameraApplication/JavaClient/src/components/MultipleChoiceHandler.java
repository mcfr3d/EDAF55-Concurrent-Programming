package components;

import components.MultipleChoiceEvent;
import javafx.event.EventHandler;

public abstract class MultipleChoiceHandler implements EventHandler<MultipleChoiceEvent> {

    public abstract void onEvent(int code);


    @Override
    public void handle(MultipleChoiceEvent event) {
        event.invokeHandler(this);
    }
}