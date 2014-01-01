package dbg.electronics.robodrv.controllers;

/**
 */
public class PidWeights {

    public final double Kp;
    public final double Ki;
    public final double Kd;

    public PidWeights(double kp, double ki, double kd) {
        Kp = kp;
        Ki = ki;
        Kd = kd;
    }



    @Override
    public String toString() {
        return "PidWeights{" +
                "Kp=" + Kp +
                ", Ki=" + Ki +
                ", Kd=" + Kd +
                '}';
    }

}
