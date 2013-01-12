package dbg.electronics.robodrv;

public class InputEvent {


    private Integer value;

    public InputEvent(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "InputEvent{" +
                "value=" + value +
                '}';
    }
}
