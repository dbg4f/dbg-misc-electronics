package dbg.electronics.robodrv;

import dbg.electronics.robodrv.head.Threaded;

/**
 * Created: 1/27/13  9:12 PM
 */
public abstract class GenericThread implements Runnable, Threaded {

    private Thread thread;

    @Override
    public void run() {
        startWork();
    }

    @Override
    public void launch() {
        thread = new Thread(this);
        thread.start();
    }


    public abstract void startWork();

    @Override
    public void terminate() {
        thread.interrupt();
    }
}
