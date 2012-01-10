package dbg.electronics.ui;


import dbg.electronics.McCommunicationException;
import dbg.electronics.McConnection;
import dbg.electronics.McUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class VectorSteering implements VectorMovementHandler, Runnable {

    private static Logger log = Logger.getLogger(VectorSteering.class);

    private RemoteDisplay displayControl;

    private Steering steering;

    private int steeringPos = 0;
    private final int xMin;
    private final int yMin;

    public VectorSteering(int xMin, int yMin, Steering steering) throws IOException {

        this.xMin = xMin;
        this.yMin = yMin;

        displayControl = new RemoteDisplay("127.0.0.1", 5555);

        this.steering = steering;

        new Thread(this).start();

    }



    public static void main(String[] args) throws IOException, InterruptedException, McCommunicationException {

        log.debug("Start");

        RateDisplay.launch(5555);

        RateDisplay.launch(5556);

        Socket socket = new Socket("127.0.0.1", 4444);

        McConnection mc = new McConnection(socket);

        //VectorSteering steering = new VectorSteering(-200, 200);
        VectorSteering steering = new VectorSteering(0, 255, new Steering(mc));

        InputStick stick = new InputStick(steering);

        MouseVectorTracker panel = new MouseVectorTracker(steering);

    }

    public void onMove(int x, int y) {

        int controlPos = McUtils.range(x, xMin, yMin, 0, 100, false);

        log.debug("JS move: " + x + " / " + controlPos);

        try {


            displayControl.show(controlPos);

            synchronized (this) {
                steeringPos = McUtils.range(x, xMin, yMin, -18, 18, false);
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

                    log.debug("Start: apply offset [" + emulatorCurrentPos + ".." + steeringPos + "]");

                    steering.applyOffset(steeringPos - emulatorCurrentPos);

                    log.debug("End: apply offset [" + emulatorCurrentPos + ".." + steeringPos + "]");
                }

                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}

