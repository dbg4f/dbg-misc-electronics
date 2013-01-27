package dbg.electronics.robodrv;


public interface EventListener<T> {

    void onEvent(T event);

}
