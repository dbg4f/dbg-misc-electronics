package dbg.electronics.robodrv;

import dbg.electronics.robodrv.head.Threaded;

import java.io.IOException;

/**
 * Created: 1/27/13  9:12 PM
 */
public abstract class GenericThread implements Runnable, Threaded {

    private Thread thread;

    @Override
    public void run() {
        try {
            startWork();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void launch() {
        thread = new Thread(this);
        thread.start();
    }


    public abstract void startWork() throws IOException;

    @Override
    public void terminate() {
        thread.interrupt();
    }
}
