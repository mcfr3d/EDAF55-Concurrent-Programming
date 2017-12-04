package components;

import javafx.event.EventHandler;

public abstract class DisconnectHandler implements EventHandler<DisconnectEvent> {

    public abstract void onEvent(int key);


    @Override
    public void handle(DisconnectEvent event) {
        event.invokeHandler(this);
    }
}