
package components;

import javafx.event.Event;
import javafx.event.EventType;

public class MultipleChoiceEvent extends Event {

    public static final EventType<MultipleChoiceEvent> MULTIPLE_CHOICE_EVENT= new EventType(ANY);

    private final int code;

    public MultipleChoiceEvent(EventType<? extends Event> eventType, int code) {
        super(eventType);
        this.code = code;
    }
    public void invokeHandler(MultipleChoiceHandler handler) {
        handler.onEvent(code);
    }
}