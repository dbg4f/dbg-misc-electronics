package dbg.hid2pwm.ui;

import dbg.hid2pwm.StateChangeSink;
import dbg.hid2pwm.InputState;

import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 28.05.2011
 * Time: 19:25:49
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleInputSource implements Runnable {

  private static final Logger log = Logger.getLogger(ConsoleInputSource.class);

  private StateChangeSink sink;

  public void setSink(StateChangeSink sink) {
    this.sink = sink;
  }

  public void init() {
    new Thread(this).start();
  }

  public void run() {


    Scanner scanIn = new Scanner(System.in);

    while (!Thread.currentThread().isInterrupted()) {

      try {
        promptValues(scanIn);
      }
      catch (Exception e) {
        log.error("Failed to query new values: " + e.getMessage(), e);
      }


    }

  }

  private void promptValues(Scanner scanIn) {
    System.out.print("New values");

    int v1 = scanIn.nextInt();
    int v2 = scanIn.nextInt();

    sink.trigger(new InputState(v1, v2));
  }

}
