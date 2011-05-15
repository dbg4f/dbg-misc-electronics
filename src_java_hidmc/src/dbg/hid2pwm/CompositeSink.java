package dbg.hid2pwm;



public class CompositeSink implements StateChangeSink{
  private StateChangeSink[] sinks;

  public CompositeSink(StateChangeSink... sinks) {
    this.sinks = sinks;
  }

  public void trigger(InputState inputState) {
    for (StateChangeSink sink : sinks) {
      sink.trigger(inputState);
    }
  }

  public void display(String msg) {
    for (StateChangeSink sink : sinks) {
      sink.display(msg);
    }
  }
}
