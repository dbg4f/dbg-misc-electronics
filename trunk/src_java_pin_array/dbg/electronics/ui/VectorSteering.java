package dbg.electronics.ui;


import dbg.electronics.McCommunicationException;
import dbg.electronics.McUtils;

import java.io.IOException;

public class VectorSteering implements VectorMovementHandler, Runnable {

    private RemoteDisplay displayControl;

    private SteeringEmulator steering;

    private int steeringPos = 0;
    private final int xMin;
    private final int yMin;

    public VectorSteering(int xMin, int yMin, SteeringEmulator steering) throws IOException {

        this.xMin = xMin;
        this.yMin = yMin;

        displayControl = new RemoteDisplay("127.0.0.1", 5555);

        this.steering = steering;

        new Thread(this).start();

    }



    public static void main(String[] args) throws IOException, InterruptedException, McCommunicationException {

        RateDisplay.launch(5555);

        RateDisplay.launch(5556);

        //VectorSteering steering = new VectorSteering(-200, 200);
        VectorSteering steering = new VectorSteering(0, 255, new SteeringEmulator());

        InputStick stick = new InputStick(steering);

        MouseVectorTracker panel = new MouseVectorTracker(steering);

    }

    public void onMove(int x, int y) {

        int controlPos = McUtils.range(x, xMin, yMin, 0, 100, false);

        try {


            displayControl.show(controlPos);

            synchronized (this) {
                steeringPos = McUtils.range(x, xMin, yMin, -12, 12, false);
            }

            //System.out.println("steeringPos = " + steeringPos + "/" + controlPos + ", now " + steering.getCurrentPos() );
            //System.out.println("ctrl=" + x + ", steering=" + steering.getCurrentPos() );

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            int emulatorCurrentPos = steering.getCurrentPos();

            try {

                if (emulatorCurrentPos != steeringPos) {
                    System.out.println("Apply offset [" + emulatorCurrentPos + ".." + steeringPos + "]");
                    steering.applyOffset(steeringPos - emulatorCurrentPos);
                }

                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}

