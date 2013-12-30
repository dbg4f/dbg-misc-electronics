package dbg.electronics.robodrv.pid;

/**
 */
public class PidTest {

    public static void main(String[] args) {

        PidRegulator regulator = new PidRegulator(new PidWeights(8, 1, 5), new RangeRestriction(0, 255));

        double currentPos = 150;
        double commandPos = 180;

        System.out.println("regulator = " + regulator);

        for (int i=0; i<40; i++) {

            double value = regulator.getValue(commandPos - currentPos);

            System.out.println("value = " + value + " state=" + regulator.getLastTriplet());

            currentPos++;

        }





    }

}
