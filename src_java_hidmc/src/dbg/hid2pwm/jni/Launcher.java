package dbg.hid2pwm.jni;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 18.02.2011
 * Time: 21:53:00
 * To change this template use File | Settings | File Templates.
 */
public class Launcher {
  /*
  public static void main1(String[] args) throws Exception {

    Device d = new Device();

    d.init();

    byte[] rep = d.report();

    System.out.println("rep. = " + rep.length);

    String repText = reportText(rep);

    System.out.println(repText);

    while (!Thread.currentThread().isInterrupted()) {
      String newText = reportText(d.report());

      if (!newText.equalsIgnoreCase(repText)) {
        System.out.println(newText);
        repText = newText;
      }

      Thread.sleep(10);
    }


  }
    */
  private static String reportText(byte[] arr) {
    StringBuffer buf = new StringBuffer();

    for (byte b : arr) {
      buf.append(String.format(" %1$02X", b));
    }

    return buf.toString();
  }
}
