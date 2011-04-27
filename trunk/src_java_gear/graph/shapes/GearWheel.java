package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;
import graph.shapes.primitives.PArc;

import static java.lang.Math.PI;
import java.util.List;

/**
 * GearWheel
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class GearWheel extends Circle {
  public final static String CVS_ID = "$Id: $";

  protected int ab;
  protected int z;
  protected double ps;
  protected double startAlpha = 0.0;

  protected double alphaP = 0.0;
  protected double alphaS = 0.0;
  protected double alphaA = 0.0;
  protected double eb = 0.0;
  protected double sa = 0.0;
  protected double x = 0.0;
  protected double m = 0.0;
  protected double invAngle2 = 2.0 * PI / 5.0;
  protected double ha = 0.0;
  private int rc = 20;
  protected static final double ANGLE_DIV = (20.0 / 360.0) * 2.0 * PI;

  public GearWheel(String name, Point c, int r, int ab, int z, double ps) {
    super(name, c, r);
    this.ab = ab;
    this.z = z;
    this.ps = ps > 1.0 ? 1.0 : ps;
    this.ps = ps < 0.0 ? 0.0 : ps;
    displace();
  }

  public void setStartAlpha(double startAlpha) {
    this.startAlpha = startAlpha;
  }

  public static double inv(double a) {
    return Math.tan(a) - a;
  }

  private void displace() {
    alphaP = 2.0 * Math.PI / (double)z;
    alphaS = ps * alphaP;

    double alphaE = (1.0 - ps) * alphaP;
    double alphaInvolute = Math.acos((double)getRadius() / (double)(getRadius() + ab));
    alphaA = alphaS - 2.0 * inv(alphaInvolute);
    invAngle2 = alphaInvolute + inv(alphaInvolute);

    sa = ((double)getRadius() + ab) * alphaA;
    eb = ((double)getRadius() * alphaE);

    double r = (double)getRadius() / Math.cos(ANGLE_DIV);

    m = (2.0 * (double)getRadius()) / (z * Math.cos(ANGLE_DIV));

    double alphaDiv = alphaS - 2.0 * inv(ANGLE_DIV);
    double delta = r * alphaDiv / m - (PI / 2.0);

    x = delta / (2.0 * Math.tan(ANGLE_DIV));

    double machineAngle = InvoluteRev.rev((alphaS - alphaE) / 4.0);

    double rm = this.r / Math.cos(machineAngle);

    ha = ((this.r + ab) - rm) / m;

  }

  public double eb() {
    return eb;
  }

  public double sa() {
    return sa;
  }


  private void drawCenter() {
    primitives.addAll(new Circle("WHEEL_C", c, rc).render());
    primitives.addAll(new Segment("WHEEL_C", new Point(c.x, c.y + rc), new Point(c.x, c.y - rc)).render());
    primitives.addAll(new Segment("WHEEL_C", new Point(c.x - rc, c.y), new Point(c.x + rc, c.y)).render());
  }

  public List<GraphicPrimitive> render() {

    primitives.clear();

    Circle c2 = new Circle("MAIN_CIRCLE", c, getRadius() + ab);
    //Circle c2 = new Circle("MAIN_CIRCLE", c, getRadius() + ab - (int)(ha * m));

    double invAngle1 = 0.0;
    int invSteps = 100;

    primitives.addAll(super.render());
    //primitives.addAll(c2.render());

    double a = startAlpha;

    Circle c = new Circle("GEAR_OUTLINE", super.c, super.getRadius());

    for (int i = 0; i < z; i++, a += alphaP) {
      double shift1 = a + 0.0;
      double shift2 = -(a + alphaS);
      primitives.addAll(new CircleInvolute("G1", c, invAngle1, invAngle2, invSteps, shift1, true).render());
      primitives.addAll(new CircleInvolute("G1", c, invAngle1, invAngle2, invSteps, shift2, false).render());
      primitives.add(new PArc(c2.c.x, c2.c.y, c2.r, -(shift1 + (alphaS - alphaA) / 2.0), -alphaA));
    }

    drawCenter();

    return primitives;

  }

  public String toString() {
    return "GearWheel{" +
           "ab=" + ab +
           ", z=" + z +
           ", ps=" + ps +
           ", startAlpha=" + startAlpha +
           ", alphaP=" + alphaP +
           ", alphaS=" + alphaS +
           ", alphaA=" + alphaA +
           ", eb=" + eb +
           ", sa=" + sa +
           ", x=" + x +
           ", m=" + m +
           ", invAngle2=" + invAngle2 +
           ", rc=" + rc +
           '}';
  }


}
