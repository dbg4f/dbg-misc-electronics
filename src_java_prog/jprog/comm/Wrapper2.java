package jprog.comm;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 21.03.2008
 * Time: 22:34:40
 * To change this template use File | Settings | File Templates.
 */
public class Wrapper2 implements IO{

    protected native void open(String name);

    protected native void writeBatch(byte[] array, int len);

    protected native byte waitForNextByte();

    protected native int getStatus();

    protected native String getLastErrorText();

    private void checkError(String comment) throws IOException {
        int status = getStatus();

        if (status != 0) {
            throw new IOException(comment + " " + getLastErrorText());
        }

    }

    public void write(byte[] buf) throws IOException {
        
        if (Launcher.verbose)
            LOG.logger.info("about to write(" + buf.length + ") = " + HEX.dumpHex(buf));
        else 
            LOG.logger.debug("about to write(" + buf.length + ") = " + HEX.dumpHex(buf));
        
        writeBatch(buf, buf.length);
        checkError("writeBatch");
    }

    public byte waitByte() throws IOException {
        byte b = waitForNextByte();
        checkError("waitForNextByte");
        return b;
    }

    class ReadThread extends Thread {
        public void run() {
            for (int i = 0; i < 3; i++) {
                byte b = waitForNextByte();
                try {
                    checkError("waitForNextByte");
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                LOG.logger.debug("next byte = " + b);
            }
        }

    }

}
