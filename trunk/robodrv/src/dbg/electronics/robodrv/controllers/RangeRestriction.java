package dbg.electronics.robodrv.controllers;

/**
 */
public class RangeRestriction {

    private final double minValue;
    private final double maxValue;

    public RangeRestriction(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public double apply(double value) {
        if (value > maxValue) {
            return maxValue;
        }
        else if (value < minValue) {
            return minValue;
        }
        else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "RangeRestriction{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
