package dbg.electronics.ui;


class SteeringMovement {

    final boolean dir;
    final boolean overCenter;
    final int stepsAbs;
    final int stepsToCenter;

    SteeringMovement(boolean dir, boolean overCenter, int stepsAbs, int stepsToCenter) {
        this.dir = dir;
        this.overCenter = overCenter;
        this.stepsAbs = stepsAbs;
        this.stepsToCenter = stepsToCenter;
    }

    @Override
    public String toString() {
        return "SteeringMovement{" +
                "dir=" + dir +
                ", overCenter=" + overCenter +
                ", stepsAbs=" + stepsAbs +
                ", stepsToCenter=" + stepsToCenter +
                '}';
    }
}
