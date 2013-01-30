package dbg.electronics.robodrv;

/**
 * Created: 1/18/13  10:10 PM
 */
public class Range {

    public final int min;
    public final int max;

    public Range(int min, int max) {

        if (min > max) {
            throw new IllegalArgumentException("min > max: " + min + " " + max);
        }

        this.min = min;
        this.max = max;
    }

    public boolean isMin(int value) {
        return value == min;
    }

    public boolean isMax(int value) {
        return value == max;
    }

    public int length() {
        return Math.abs(max - min);
    }

    public boolean isBorder(int value) {
        return isMin(value) || isMax(value);
    }

    public void validate(int value) throws OutOfRange {
        if (value < min || value > max) {
            throw new OutOfRange(value + " is not in " + toString());
        }
    }

    public int truncate(int value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }


    public int remapTo(int value, Range targetRange) throws OutOfRange {
        validate(value);
        int distanceFromStart = Math.abs(value - min);
        double lengthRatio = (double)targetRange.length() / (double)length();
        return targetRange.min + (int)Math.round(distanceFromStart * lengthRatio);
    }

    @Override
    public String toString() {
        return "Range{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
