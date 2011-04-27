package jftdi2;

import jprog.comm.LOG;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 28.08.2010
 * Time: 17:16:05
 * To change this template use File | Settings | File Templates.
 */
public class StatefulDriver {

  private DeviceWrapper wrapper;

  private byte counter = 0;

  public StatefulDriver(DeviceWrapper wrapper) {
    this.wrapper = wrapper;
  }

  public void work() throws IOException, InterruptedException {
    for (int i=100; i<255; i+=5) {
      Thread.sleep(10);
      wrapper.writePwm0(i);
    }

    Thread.sleep(1500);

    for (int i=255; i>100; i-=5) {
      Thread.sleep(10);
      wrapper.writePwm0(i);
    }

    Thread.sleep(2000);
    
  }
}
