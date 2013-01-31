package dbg.electronics.robodrv;

/**
 * Created: 1/18/13  10:13 PM
 */
public class OutOfRange extends IllegalArgumentException {
    public OutOfRange(String s) {
        super(s);
    }
}
