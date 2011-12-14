package dbg.electronics;

/**
 * Created by IntelliJ IDEA.
 * User: dmitry
 * Date: 12/13/11
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class McWatchdog implements Runnable {


    boolean entered = false;
    boolean signaled = false;

    private McLogger logger = ConsoleLogger.getInstance();

    public void launch() {
        new Thread(this).start();
    }

    public void enter() {
        entered = true;
        signaled = false;
    }

    public void leave() {
        entered = false;
        signaled = false;
    }

    private void check() {

        if (!entered) {
            return;
        }

        if (!signaled) {
            signaled = true;
        }
        else {
            logger.error("Timeout");
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            check();
        }
    }
}
