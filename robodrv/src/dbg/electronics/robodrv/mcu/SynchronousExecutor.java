package dbg.electronics.robodrv.mcu;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousExecutor implements McuCommandResponseListener {

    private McuBytesWriter bytesWriter;
    
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

    public byte[] execute(McuCommand cmd) throws IOException, InterruptedException, McuCommunicationException {

        responseBlockingQueue.clear();

        bytesWriter.write(cmd.getRawCommand());

        Response response = responseBlockingQueue.poll(responseTimeoutSeconds, TimeUnit.SECONDS);

        if (response == null) {
            throw new McuCommunicationException("Timeout for command " + cmd + " " + cmd.toRawBytesString());
        }

        if (response.sequence != cmd.getSequence()) {
            throw new McuCommunicationException("Sequence mismatch for command " + cmd + " " + cmd.toRawBytesString() + " response " + response);
        }

        return response.params;

    }

    @Override
    public void onCommandResponse(byte sequence, byte[] params) {
        try {
            responseBlockingQueue.put(new Response(sequence, params));
        } catch (InterruptedException e) {
            // not significant
        }
    }
}
