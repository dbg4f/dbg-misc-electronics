package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.InputEvent;
import dbg.electronics.robodrv.InputListener;
import dbg.electronics.robodrv.Orchestrator;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class MultiBufferFullScreen implements InputListener {

    private DashboardPainter dashboardPainter;

    private Frame mainFrame;

    private GraphicsDevice device;

    public MultiBufferFullScreen() {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        device = env.getDefaultScreenDevice();

        //start();

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
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        dashboardPainter.valuePercent = event.getValue();
    }


}
