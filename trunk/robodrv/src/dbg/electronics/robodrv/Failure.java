package dbg.electronics.robodrv;


public class Failure {

    private String text;
    private Throwable throwable;

    public Failure(String text) {
        this.text = text;
    }

    public Failure(String text, Throwable throwable) {
        this.text = text;
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "Failure{" +
                "text='" + text + '\'' +
                ", throwable=" + throwable +
                '}';
    }
}
