package dbg.electronics.robodrv.pid;

/**
 */
public class PidRegulator {

    private PidWeights weights;
    private RangeRestriction integralRestriction;

    private double integral = 0;
    private double previousError = 0;
    private int iterations;
    private PidValuesTriplet lastTriplet;

    public PidRegulator(PidWeights weights) {
        this.weights = weights;
    }

    public PidRegulator(PidWeights weights, RangeRestriction integralRestriction) {
        this.weights = weights;
        this.integralRestriction = integralRestriction;
    }

    public double getValue(double currentError) {

        iterations++;

        lastTriplet = new PidValuesTriplet(getProportionalTerm(currentError), updateIntegralTerm(currentError), updateDerivativeTerm(currentError));

        return lastTriplet.sum();

    }

    private double getProportionalTerm(double currentError) {
        return weights.Kp * currentError;
    }

    private double updateIntegralTerm(double currentError) {
        integral += currentError;
        double integralTerm =  integral;
        if (integralRestriction != null) {
            integralTerm = integralRestriction.apply(integral);
        }
        return weights.Ki *integralTerm;
    }

    private double updateDerivativeTerm(double currentError) {
        double derivativeTerm = (previousError - currentError) * weights.Kd;
        previousError = currentError;
        return derivativeTerm;
    }


    public void reset() {
        integral = 0;
        iterations = 0;
    }

    public PidValuesTriplet getLastTriplet() {
        return lastTriplet;
    }

    @Override
    public String toString() {
        return "PidRegulator{" +
                "weights=" + weights +
                ", integralRestriction=" + integralRestriction +
                ", integral=" + integral +
                ", previousError=" + previousError +
                ", iterations=" + iterations +
                ", lastTriplet=" + lastTriplet +
                '}';
    }
}
