package dbg.electronics.ui;


class SteeringMovement {

    final boolean dir;
    final boolean overCenter;
    final int stepsAbs;

    SteeringMovement(boolean dir, boolean overCenter, int stepsAbs) {
        this.dir = dir;
        this.overCenter = overCenter;
        this.stepsAbs = stepsAbs;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "dir=" + dir +
                ", overCenter=" + overCenter +
                ", stepsAbs=" + stepsAbs +
                '}';
    }
}
