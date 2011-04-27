package graph.shapes;

import graph.shapes.primitives.GraphicPrimitive;

import java.util.List;

/**
 * GearPair
 * <p/>
 * $Source:  $
 *
 * @author Dmitri Bogdel
 * @version $Id:  $
 */
public class GearPair extends Shape {
  public final static String CVS_ID = "$Id: $";

  private GearWheel w1;
  private GearWheel w2;

  private int gap;
  private double alphaW;
  private double aw;
  private double epsilonA;

  public GearPair(Point cw1, int z1, int z2, double ps1, double ps2, int r1, int r2, int ab1, int ab2) {
    super("GPAIR");

    GearWheel w1 = new GearWheel("W1", cw1, r1, ab1, z1, ps1);
    GearWheel w2 = new GearWheel("W2", cw1, r2, ab2, z2, ps2);

    initPair(w1, w2);

    this.w2 = new GearWheel("W2", new Point((int)Math.round(w1.c.x + aw), w1.c.y), r2, ab2, z2, ps2);

  }

  public GearPair(GearWheel w1, GearWheel w2, int gap) {
    super("GPAIR");
    this.w1 = w1;
    this.w2 = w2;

    this.aw = gap;
    alphaW = Math.acos((w1.m * (w1.z + w2.z) * Math.cos(GearWheel.ANGLE_DIV)) / (2.0 * (w1.r + w2.r + gap)));

    eps();
  }

  public GearPair(GearWheel w1, GearWheel w2) {
    super("GPAIR");
    initPair(w1, w2);
  }


  private void initPair(GearWheel w1, GearWheel w2) {
    this.w1 = w1;
    this.w2 = w2;

    double invAlphaW = GearWheel.inv(GearWheel.ANGLE_DIV) + ((2.0 * Math.tan(GearWheel.ANGLE_DIV) * (w1.x + w2.x)) / (((double)w1.z + (double)w2.z)));

    alphaW = InvoluteRev.rev(invAlphaW);
    aw = w1.m * ((double)(w1.z + w2.z)) * (Math.cos(GearWheel.ANGLE_DIV) / Math.cos(alphaW)) / 2.0;
    gap = (int)Math.round(aw - (double)(w1.r + w2.r));

    eps();
  }

  private void eps() {

    double alphaA1 = Math.acos((double)w1.r / (double)(w1.r + w1.ab));
    double alphaA2 = Math.acos((double)w2.r / (double)(w2.r + w2.ab));

    double z1 = (double)w1.z;
    double z2 = (double)w2.z;

    epsilonA = (z1 * (Math.tan(alphaA1) - Math.tan(alphaW)) + z2 * (Math.tan(alphaA2) - Math.tan(alphaW))) / (2.0 * Math.PI);
  }

  public void align(double a1, double a2) {
    w1.setStartAlpha(a1);
    w2.setStartAlpha(a2);
  }

  public double getAlphaW() {
    return alphaW;
  }


  public double getAw() {
    return aw;
  }

  public double getEpsilonA() {
    return epsilonA;
  }

  public int getGap() {
    return gap;
  }

  public String report() {
    StringBuffer rep = new StringBuffer(1000);

    double zmin1 = 2.0 * w1.ha / (Math.sin(GearWheel.ANGLE_DIV) * Math.sin(GearWheel.ANGLE_DIV));
    double zmin2 = 2.0 * w2.ha / (Math.sin(GearWheel.ANGLE_DIV) * Math.sin(GearWheel.ANGLE_DIV));

    double xmin1 = w1.ha * (1.0 - w1.z / zmin1);
    double xmin2 = w2.ha * (1.0 - w2.z / zmin2);


    rep.append("z1         = ").append(w1.z).append("\n");
    rep.append("z2         = ").append(w2.z).append("\n");

    rep.append("r1         = ").append(w1.r).append("\n");
    rep.append("r2         = ").append(w2.r).append("\n");

    rep.append("ab1        = ").append(w1.ab).append("\n");
    rep.append("ab2        = ").append(w2.ab).append("\n");

    rep.append("x1         = ").append(w1.x).append("\n");
    rep.append("x2         = ").append(w2.x).append("\n");

    rep.append("m1         = ").append(w1.m).append("\n");
    rep.append("m2         = ").append(w2.m).append("\n");

    rep.append("ps1        = ").append(w1.ps).append("\n");
    rep.append("ps2        = ").append(w2.ps).append("\n");

    rep.append("h/a1       = ").append(w1.ha).append("\n");
    rep.append("h/a2       = ").append(w2.ha).append("\n");

    rep.append("z/min/1    = ").append(zmin1).append("\n");
    rep.append("z/min/2    = ").append(zmin2).append("\n");

    rep.append("x/min/1    = ").append(xmin1).append("\n");
    rep.append("x/min/2    = ").append(xmin2).append("\n");

    rep.append("Alpha/w    = ").append(alphaW).append("\n");
    rep.append("a/w        = ").append(aw).append("\n");
    rep.append("gap        = ").append(gap).append("\n");
    rep.append("epsilon/a  = ").append(epsilonA).append("\n");

    return rep.toString();
  }

  /*
  public static void main(String[] args) {

    GearPair p = new GearPair(null, null, 0);

    double a = 0.723;

    double invA = GearWheel.inv(a);

    double rev = p.invRev(invA);

    System.out.println("invA = " + invA);
    System.out.println("rev = " + rev);
    System.out.println("a = " + a);

  }
  */

  public List<GraphicPrimitive> render() {

    primitives.clear();

    primitives.addAll(w1.render());
    primitives.addAll(w2.render());

    return primitives;
  }
}
