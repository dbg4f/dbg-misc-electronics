package dbg.hid2pwm;

import dbg.hid2pwm.InputState;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 1.03.2011
 * Time: 20:43:54
 * To change this template use File | Settings | File Templates.
 */
public interface StateChangeSink {

  void trigger(InputState inputState);

  void display(String msg);

}
