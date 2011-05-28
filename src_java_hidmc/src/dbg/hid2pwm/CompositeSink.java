package dbg.hid2pwm;

import java.util.List;

public class CompositeSink implements StateChangeSink{

  private List<StateChangeSink> sinks;

  public void setSinks(List<StateChangeSink> sinks) {
    this.sinks = sinks;
  }

  public void trigger(InputState inputState) {
    for (StateChangeSink sink : sinks) {
      sink.trigger(inputState);
    }
  }

}
