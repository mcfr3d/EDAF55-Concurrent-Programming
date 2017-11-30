
package components;

import javafx.event.Event;
import javafx.event.EventType;

public class DisconnectEvent extends Event {

    public static final EventType<DisconnectEvent> DISCONNECT_EVENT= new EventType("Disconenction Event");

    public final String address;


    public DisconnectEvent(EventType<? extends Event> eventType, String address) {
        super(eventType);
        this.address = address;
    }
    public void invokeHandler(DisconnectHandler handler) {
        handler.onEvent(address);
    }
}