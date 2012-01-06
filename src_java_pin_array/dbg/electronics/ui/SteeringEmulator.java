package dbg.electronics.ui;

import dbg.electronics.McCommunicationException;
import dbg.electronics.McUtils;

import java.io.IOException;


class SteeringEmulator extends Steering {


    SteeringEmulator() throws IOException, McCommunicationException {
        super(null);
    }

    public void applyOffset(int offset) throws IOException, InterruptedException {

        int step = offset > 0 ? 1 : -1;

        int finalPos = currentPos + offset;

        System.out.println("calcMovement(finalPos) = " + calcMovement(finalPos));

        for (int i = currentPos; i != finalPos; i += step) {

            int range = McUtils.range(i, -12, 12, 0, 100, false);

            displayPos.show(range);

            Thread.sleep(50);

        }

        currentPos = finalPos;

    }

}
