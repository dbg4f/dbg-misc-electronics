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

    private BlockingQueue<CommandResponse> responseBlockingQueue = new ArrayBlockingQueue<CommandResponse>(100);

    public void setResponseTimeoutSeconds(int responseTimeoutSeconds) {
        this.responseTimeoutSeconds = responseTimeoutSeconds;
    }

    public void setBytesWriter(McuBytesWriter bytesWriter) {
        this.bytesWriter = bytesWriter;
    }

    public void setNextListener(McuReportListener nextListener) {
        this.nextListener = nextListener;
    }

    public CommandResponse execute(McuCommand cmd) throws IOException, InterruptedException, McuCommunicationException {

        responseBlockingQueue.clear();

        log.info("Command to send: " + cmd);

        immediateSend(cmd);

        long ts = System.currentTimeMillis();

        CommandResponse response = responseBlockingQueue.poll(responseTimeoutSeconds, TimeUnit.SECONDS);

        long execTime = System.currentTimeMillis() - ts;
        
        if (response == null) {
            throw new McuCommunicationException("Timeout for command " + cmd + " " + cmd.toRawBytesString());
        }

        response.setExecTime(execTime);

        if (response.sequence != cmd.getSequence()) {
            throw new McuCommunicationException("Sequence mismatch for command " + cmd + " " + cmd.toRawBytesString() + " response " + response);
        }
        
        log.info("Cmd=" + cmd + " response=" + response + " execTime=" + execTime);

        return response;

    }

    public void sendOnly(McuCommand cmd) throws IOException {
        log.info("Cmd=" + cmd + " sending, no response expected");
        immediateSend(cmd);
    }

    private void immediateSend(McuCommand cmd) throws IOException {
        bytesWriter.write(cmd.getRawCommand());
    }

    public void onCommandResponse(byte sequence, byte[] params) {

        nextListener.onCommandResponse(sequence, params);

        try {
            responseBlockingQueue.put(new CommandResponse(sequence, params));
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
