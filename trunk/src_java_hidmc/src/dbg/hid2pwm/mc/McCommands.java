package dbg.hid2pwm.mc;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 4.03.2011
 * Time: 20:51:32
 * To change this template use File | Settings | File Templates.
 */
public interface McCommands {

  void setPwm0(byte value) throws IOException, InterruptedException;

  byte getPwm0() throws IOException, InterruptedException;

  void setPwm1(byte value) throws IOException, InterruptedException;

  byte getPwm1() throws IOException, InterruptedException;

  void setDir(byte value) throws IOException, InterruptedException;

  byte getDir() throws IOException, InterruptedException;

  byte getIntC() throws IOException, InterruptedException;

  void schedulePwm1(byte count) throws IOException, InterruptedException;

  void cancelSchedule() throws IOException, InterruptedException;

  byte getScheduleStatus() throws IOException, InterruptedException;

  

}
