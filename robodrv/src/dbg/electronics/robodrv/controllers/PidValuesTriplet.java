package dbg.electronics.robodrv.controllers;

/**
 */
public class PidValuesTriplet {

    public final double P;
    public final double I;
    public final double D;

    public PidValuesTriplet(double p, double i, double d) {
        P = p;
        I = i;
        D = d;
    }

    public double sum() {
        return P + I + D;
    }

    @Override
    public String toString() {
        return "PidValuesTriplet{" +
                "P=" + P +
                ", I=" + I +
                ", D=" + D +
                '}';
    }
}
