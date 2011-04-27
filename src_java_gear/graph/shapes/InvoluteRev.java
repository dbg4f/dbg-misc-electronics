package graph.shapes;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * InvoluteRev
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class InvoluteRev {
  public final static String CVS_ID = "$Id: $";

  private Map<Double, Double> invCache = new HashMap<Double, Double>();

  private static final double CACHE_ANGLE_START = 0.0;
  private static final double CACHE_ANGLE_STEP = 0.001;
  private static final double CACHE_ANGLE_END = Math.PI;

  private static InvoluteRev r = new InvoluteRev();

  private InvoluteRev() {
    fillInvCache();
  }


  public static double rev(double value) {
    return r.invRev(value);
  }

  private double invRev(double value) {
    double currentDiff = 1000.0;

    double currentAngle = 0.0;

    for (Iterator<Double> iterator = invCache.keySet().iterator(); iterator.hasNext();) {
      Double resultValue = iterator.next();

      double diff = Math.abs(value - resultValue.doubleValue());

      if (diff < currentDiff) {
        currentDiff = diff;
        currentAngle = invCache.get(resultValue);
      }

    }

    return currentAngle;

  }

  private void fillInvCache() {
    double a = CACHE_ANGLE_START;
    while (a < CACHE_ANGLE_END) {
      invCache.put(new Double(GearWheel.inv(a)), new Double(a));
      a += CACHE_ANGLE_STEP;
    }
  }


}
