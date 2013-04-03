package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.Range;

/**
 */
public class EnumValueWithHistory<T extends Enum<T>> extends ValueWithHistory {

    private final T[] constants;

    public EnumValueWithHistory(Enum<T> sampleValue) {
        constants = sampleValue.getDeclaringClass().getEnumConstants();
        setValueRange(new Range(0, constants.length-1));
    }

    public void update(Enum<T> value, long timestamp) {
        makeCorner(value, timestamp);
        super.update(value.ordinal(), timestamp);
    }

    public void update(Enum<T> value) {
        makeCorner(value);
        super.update(value.ordinal());
    }

    private void makeCorner(Enum<T> value, long timestamp) {
        if (value.ordinal() != currentValue) {
            update(currentValue, timestamp);
        }
    }

    private void makeCorner(Enum<T> value) {
        if (value.ordinal() != currentValue) {
            update(currentValue);
        }
    }

    private T getByOrdinal(int ordinal) {
        if (ordinal < constants.length) {
            return constants[ordinal];
        }
        return null;
    }

    @Override
    public String getFormattedValue(int value) {
        T enumValue = getByOrdinal(value);
        return enumValue != null ? enumValue.name() : "??";
    }
}
