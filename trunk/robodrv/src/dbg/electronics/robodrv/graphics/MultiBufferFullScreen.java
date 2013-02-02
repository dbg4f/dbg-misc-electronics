package dbg.electronics.robodrv.graphics;

import dbg.electronics.robodrv.*;
import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.head.Orchestrator;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;

public class MultiBufferFullScreen extends GenericThread {

    private List<DashboardWidget> widgets;

    private Frame mainFrame;

    private GraphicsDevice device;

    private DashboardKeyListener keyListener;


    public void setKeyListener(DashboardKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    public MultiBufferFullScreen() {

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        device = env.getDefaultScreenDevice();

    }

    public void setWidgets(List<DashboardWidget> widgets) {
        this.widgets = widgets;
    }

    @Override
    public void startWork() {
        try {

            //dashboardPainter = new DashboardPainter();

            GraphicsConfiguration gc = device.getDefaultConfiguration();

            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);

            device.setFullScreenWindow(mainFrame);

            Rectangle bounds = mainFrame.getBounds();

            mainFrame.createBufferStrategy(2);

            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();

            mainFrame.addKeyListener(keyListener);

            while(!Thread.currentThread().isInterrupted()) {
                Graphics g = bufferStrategy.getDrawGraphics();
                if (!bufferStrategy.contentsLost()) {

                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, bounds.width, bounds.height);

                    Graphics2D g2 = (Graphics2D) g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    for (DashboardWidget widget : widgets) {
                        widget.onDraw(g2);
                    }


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
            //mainFrame.dispose();
        }
    }


}
