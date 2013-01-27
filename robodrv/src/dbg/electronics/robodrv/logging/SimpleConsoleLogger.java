package dbg.electronics.robodrv.logging;


public class SimpleConsoleLogger implements SimpleLogger {

    private final String name;

    private static final long startTs = System.currentTimeMillis();

    public SimpleConsoleLogger(String name) {
        this.name = name;
    }

    @Override
    public void info(String text) {
        message("INFO", text, null);
    }

    @Override
    public void debug(String text) {
        message("DEBUG", text, null);
    }

    @Override
    public void error(String text) {
        message("ERROR", text, null);
    }

    @Override
    public void error(String text, Throwable throwable) {
        message("ERROR", text, throwable);
    }

    protected void message(String level, String text, Throwable throwable) {

        long ts = System.currentTimeMillis() - startTs;


        //System.out.println(level + " " + text + " " + throwable);

        System.out.println(String.format("%06d.%04d %s %s - %s %s", ts/1000, ts%1000, name, level, text, throwable != null ? throwable.getMessage() : ""));

    }

}
