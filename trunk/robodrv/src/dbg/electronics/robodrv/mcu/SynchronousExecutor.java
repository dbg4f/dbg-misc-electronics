package dbg.electronics.robodrv.mcu;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousExecutor implements McuReportListener {

    private static final Logger log = Logger.getLogger(SynchronousExecutor.class);

    private McuBytesWriter bytesWriter;

    private McuReportListener nextListener;
    
    private int responseTimeoutSeconds = 1;

    class Response {
        final byte sequence;
        final byte[] params;
        Response(byte sequence, byte[] params) {
            this.sequence = sequence;
            this.params = params;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "sequence=" + sequence +
                    ", params=" + McuUtils.bytesToString(params) +
                    '}';
        }
    }

    private BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<Response>(100);

    public void setResponseTimeoutSeconds(int responseTimeoutSeconds) {
        this.responseTimeoutSeconds = responseTimeoutSeconds;
    }

    public void setBytesWriter(McuBytesWriter bytesWriter) {
        this.bytesWriter = bytesWriter;
    }

    public void setNextListener(McuReportListener nextListener) {
        this.nextListener = nextListener;
    }

    public byte[] execute(McuCommand cmd) throws IOException, InterruptedException, McuCommunicationException {

        responseBlockingQueue.clear();

        immediateSend(cmd);

        long ts = System.currentTimeMillis();

        Response response = responseBlockingQueue.poll(responseTimeoutSeconds, TimeUnit.SECONDS);

        long execTime = System.currentTimeMillis() - ts;
        
        if (response == null) {
            throw new McuCommunicationException("Timeout for command " + cmd + " " + cmd.toRawBytesString());
        }

        if (response.sequence != cmd.getSequence()) {
            throw new McuCommunicationException("Sequence mismatch for command " + cmd + " " + cmd.toRawBytesString() + " response " + response);
        }
        
        log.info("Cmd=" + cmd + " response=" + response + " execTime=" + execTime);

        return response.params;

    }

    public void immediateSend(McuCommand cmd) throws IOException {
        bytesWriter.write(cmd.getRawCommand());
    }

    public void onCommandResponse(byte sequence, byte[] params) {

        nextListener.onCommandResponse(sequence, params);

        try {
            responseBlockingQueue.put(new Response(sequence, params));
        } catch (InterruptedException e) {
            // not significant
        }
    }

    @Override
    public void onAdcValue(int channel, byte value) {
        nextListener.onAdcValue(channel, value);
    }

    @Override
    public void onCounterUpdate(byte value) {
        nextListener.onCounterUpdate(value);
    }

    @Override
    public void onMcuReset() {
        nextListener.onMcuReset();
    }
}
