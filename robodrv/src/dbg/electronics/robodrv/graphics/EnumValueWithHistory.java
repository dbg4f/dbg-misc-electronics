package dbg.electronics.robodrv.graphics;

/**
 */
public class EnumValueWithHistory<T extends Enum> extends ValueWithHistory {

    private final T[] constants;

    public EnumValueWithHistory(Enum<T> sampleValue) {
        constants = sampleValue.getDeclaringClass().getEnumConstants();
    }

    public void update(Enum<T> value, long timestamp) {
        super.update(value.ordinal(), timestamp);
    }

    public void update(Enum<T> value) {
        super.update(value.ordinal());
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
