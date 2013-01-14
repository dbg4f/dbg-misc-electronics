package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.*;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;

public class MultiBufferFullScreen implements InputListener, Threaded {

    private DashboardPainter dashboardPainter;

    private Frame mainFrame;

    private GraphicsDevice device;

    private Thread listeningThread;

    public MultiBufferFullScreen() {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        device = env.getDefaultScreenDevice();

        //start();

    }

    @Override
    public void launch() {

        listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    start();
                } catch (Exception e) {
                    Orchestrator.getInstance().onFailure(new Failure("Dashboard failure", e));
                }

            }
        });

        listeningThread.start();

    }

    @Override
    public void terminate() {

        if (listeningThread != null) {

            listeningThread.interrupt();

        }

    }

    public void start() {
        try {

            dashboardPainter = new DashboardPainter();

            GraphicsConfiguration gc = device.getDefaultConfiguration();

            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);

            device.setFullScreenWindow(mainFrame);

            Rectangle bounds = mainFrame.getBounds();

            mainFrame.createBufferStrategy(2);

            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();

            mainFrame.addKeyListener(new DashboardKeyListener(Orchestrator.getInstance()));

            while(!Thread.currentThread().isInterrupted()) {
                Graphics g = bufferStrategy.getDrawGraphics();
                if (!bufferStrategy.contentsLost()) {

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, bounds.width, bounds.height);

                    dashboardPainter.paint(g);

                    bufferStrategy.show();
                    g.dispose();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    @Override
    public void onEvent(InputEvent event) {
        if (event.getContent() == InputEvent.Content.TEST_INT_VALUE) {
            dashboardPainter.dashboardData.setTestValuePercent(event.getValue());
        }
        else if (event.getContent() == InputEvent.Content.HID_CONTROL_VALUE) {
            dashboardPainter.dashboardData.updateJs(event.getHidKey(), event.getHidValue());
        }

    }


}
