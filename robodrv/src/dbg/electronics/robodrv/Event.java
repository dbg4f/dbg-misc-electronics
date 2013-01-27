package dbg.electronics.robodrv;

public class Event {

    public enum Content {
        TEST_INT_VALUE,
        HID_CONTROL_VALUE
    }

    private final Content content;

    private String hidKey;
    private String hidValue;

    private Integer value;

    public Event(String hidKey, String hidValue) {
        this.hidKey = hidKey;
        this.hidValue = hidValue;
        content = Content.HID_CONTROL_VALUE;
    }

    public Event(Integer value) {
        this.value = value;
        content = Content.TEST_INT_VALUE;
    }

    public Content getContent() {
        return content;
    }

    public Integer getValue() {
        return value;
    }

    public String getHidKey() {
        return hidKey;
    }

    public String getHidValue() {
        return hidValue;
    }

    @Override
    public String toString() {
        return "Event{" +
                "value=" + value +
                '}';
    }
}
