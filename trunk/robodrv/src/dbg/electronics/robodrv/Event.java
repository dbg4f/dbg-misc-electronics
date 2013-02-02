package dbg.electronics.robodrv;

public class Event {

    public enum EventCode {
        SHUTDOWN
    }

    private final EventCode eventCode;


    public Event(EventCode eventCode) {
        this.eventCode = eventCode;
    }

    public EventCode getEventCode() {
        return eventCode;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventCode=" + eventCode +
                '}';
    }
}
