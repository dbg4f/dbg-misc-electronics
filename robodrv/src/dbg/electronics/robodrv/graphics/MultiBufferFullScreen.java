package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.*;
import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.head.Orchestrator;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class MultiBufferFullScreen extends GenericThread implements EventListener<Event> {

    private DashboardPainter dashboardPainter;

    private Frame mainFrame;

    private GraphicsDevice device;

    public MultiBufferFullScreen() {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        device = env.getDefaultScreenDevice();

    }

    @Override
    public void startWork() {
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
    public void onEvent(Event event) {
        if (event.getContent() == Event.Content.TEST_INT_VALUE) {
            dashboardPainter.dashboardData.setTestValuePercent(event.getValue());
        }
        else if (event.getContent() == Event.Content.HID_CONTROL_VALUE) {
            dashboardPainter.dashboardData.updateJs(event.getHidKey(), event.getHidValue());
        }

    }


}
