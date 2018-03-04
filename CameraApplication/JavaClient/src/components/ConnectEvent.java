
package components;

import javafx.event.Event;
import javafx.event.EventType;

public class ConnectEvent extends Event {

    public static final EventType<ConnectEvent> CONNECT_EVENT= new EventType("Connection Event");

    public final Integer key;
    public final String address;


    public ConnectEvent(EventType<? extends Event> eventType, Integer key,String address) {
        super(eventType);
        this.key = key;
        this.address = address;
    }
    public void invokeHandler(ConnectHandler handler) {
        handler.onEvent(this);
    }
}