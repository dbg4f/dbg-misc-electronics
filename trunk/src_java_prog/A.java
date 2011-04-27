

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 7.02.2010
 * Time: 22:15:07
 * To change this template use File | Settings | File Templates.
 */
public class A {

  public static void main(String[] args) {

    int b = 0x4;

    int pwm0H = b;
    int pwm0L = b;
    int pwm1H = b;
    int pwm1L = b;

    int pwm0 = ((pwm0H & 0xF) << 4) + (pwm0L & 0xF);
    //int pwm1 = ((pwm1H & 0x7) << 4) + (pwm1L & 0x7);

    int i = (pwm0);
    System.out.println("pwm0 = " + Integer.toHexString(i));



  }

}
