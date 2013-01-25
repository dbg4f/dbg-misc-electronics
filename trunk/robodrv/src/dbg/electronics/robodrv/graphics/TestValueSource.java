package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.InputEvent;
import dbg.electronics.robodrv.InputListener;

/**
* Created with IntelliJ IDEA.
* User: dmitri
* Date: 1/12/13
* Time: 9:47 PM
* To change this template use File | Settings | File Templates.
*/
class TestValueSource {

    void launch(final InputListener inputListener) {

        new Thread(new Runnable() {
            public void run() {

                while (!Thread.currentThread().isInterrupted()) {

                    for (int i = 0; i < 100; i++) {
                        inputListener.onEvent(new InputEvent(i));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 100; i > 0; i--) {
                        inputListener.onEvent(new InputEvent(i));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }).start();


    }


}
