package dbg.electronics.robodrv.controllers;

import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.groovy.Functions;
import dbg.electronics.robodrv.head.Orchestrator;
import org.apache.log4j.Logger;

public class Steering implements Runnable {

    private static final Logger log = Logger.getLogger(Steering.class);

    public static final int CLOCK = 1;

    private Orchestrator orchestrator;

    private ClockableController controller;

    public Steering(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public void run() {

        controller = new ClockableController();

        //PidController regulator = new PidController(new PidWeights(30, 0, 0), new RangeRestriction(0, 255));

        //log.info("Regulator: " + regulator);

        MotorDrive motorDrive = null;

        while(!Thread.currentThread().isInterrupted()) {

            if (!Functions.adc) {
                continue;
            }

            try {

            int position = orchestrator.driveState.getCurrentRawPos();

            int commandPos = orchestrator.driveState.getCurrentTargetPos();

            if (motorDrive == null) {
                motorDrive = Functions.drive2.getChannelDrive(1);
                continue;
            }

            controller.setMotorDrive(motorDrive);

            controller.onClock(commandPos, position);

            orchestrator.driveState.updateValueWithHistory(commandPos);

            Thread.sleep(CLOCK);

            } catch (Exception e) {
                log.error("End of steering cycle due to error : " + e.getMessage(), e);
                break;
            }

        }

    }
}
