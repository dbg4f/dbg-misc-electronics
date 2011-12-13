package dbg.electronics;

public class McCommunicationException extends Exception {

    public McCommunicationException(String s) {
        super(s);
    }

    public McCommunicationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public McCommunicationException(Throwable throwable) {
        super(throwable);
    }
}
