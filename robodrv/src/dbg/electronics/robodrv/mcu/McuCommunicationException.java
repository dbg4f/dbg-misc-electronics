package dbg.electronics.robodrv.mcu;

public class McuCommunicationException extends Exception{

    public McuCommunicationException(String s) {
        super(s);
    }

    public McuCommunicationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public McuCommunicationException(Throwable throwable) {
        super(throwable);
    }
}
