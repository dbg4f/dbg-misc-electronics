package jftdi2;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 28.08.2010
 * Time: 17:17:11
 * To change this template use File | Settings | File Templates.
 */
public interface DeviceWrapper {

  void write(int pwm1, int pwm2, int dir) throws IOException, InterruptedException;

  void echo(int value)  throws IOException, InterruptedException;

  void writePwm0(int value)  throws IOException, InterruptedException;

  public int readPwm0() throws IOException, InterruptedException;

}
