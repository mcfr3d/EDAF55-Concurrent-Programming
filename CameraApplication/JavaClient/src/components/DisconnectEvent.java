
package components;

import javafx.event.Event;
import javafx.event.EventType;

public class DisconnectEvent extends Event {

    public static final EventType<DisconnectEvent> DISCONNECT_EVENT= new EventType("Disconenction Event");

    public final int key;


    public DisconnectEvent(EventType<? extends Event> eventType, int key) {
        super(eventType);
        this.key = key;
    }
    public void invokeHandler(DisconnectHandler handler) {
        handler.onEvent(key);
    }
}