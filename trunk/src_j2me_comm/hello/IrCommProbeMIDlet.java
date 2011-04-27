package hello;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class IrCommProbeMIDlet extends MIDlet implements CommandListener {

    private Command exitCommand; // The exit command
    private Display display;     // The display for this MIDlet

    public IrCommProbeMIDlet() {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);
    }

    public void startApp() {

        StringBuffer buf = new StringBuffer("Log:");

        try {
            CommConnection conn = (CommConnection) Connector.open("comm:IR0");
            buf.append("Connected\n");

            conn.setBaudRate(19200);

            OutputStream out = conn.openOutputStream();
            out.write("123ABC".getBytes());
            out.flush();


            InputStream in = conn.openDataInputStream();

            byte[] b = new byte[] {'A', 'B'};
            //in.read(b);
            buf.append("Read:").append(b[0]).append(b[1]);

            in.close();
            out.close();
            
            conn.close();
            buf.append("Closed\n");
        }
        catch(Exception e) {
            buf.append("Error: " + e.getMessage());
        }



        TextBox t = new TextBox("Hello", buf.toString(), 256, 0);

        t.addCommand(exitCommand);
        t.setCommandListener(this);

        display.setCurrent(t);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        } 
    }

}
