package components;

import components.MultipleChoiceEvent;
import javafx.event.EventHandler;

public abstract class ConnectHandler implements EventHandler<ConnectEvent> {

    public abstract void onEvent(ConnectEvent event);


    @Override
    public void handle(ConnectEvent event) {
        event.invokeHandler(this);
    }
}