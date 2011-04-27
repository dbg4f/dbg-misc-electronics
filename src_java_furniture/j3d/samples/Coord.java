package j3d.samples;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Geometry;
import javax.media.j3d.LineArray;
import javax.vecmath.Point3f;
import javax.vecmath.Color3f;
import java.awt.*;

/**
 * Coord
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class Coord extends Shape3D {

  public Coord() {
    this.setGeometry(creareGeometry());
  }

  private Geometry creareGeometry() {

    LineArray axisXYZ = new LineArray(2+2+10, LineArray.COORDINATES | LineArray.COLOR_3);

    //LineArray axisXLines = new LineArray(2, LineArray.COORDINATES);
    //axisBG.addChild(new Shape3D(axisXLines));

    axisXYZ.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
    axisXYZ.setCoordinate(1, new Point3f(3.0f, 0.0f, 0.0f));

    final Color3f red = new Color3f(Color.red);
    final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
    final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);

    axisXYZ.setColor(0, red);
    axisXYZ.setColor(1, red);

    // create line for Y axis
    //LineArray axisYLines = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
    //axisBG.addChild(new Shape3D(axisYLines));

    axisXYZ.setCoordinate(0+2, new Point3f(0.0f, -1.0f, 0.0f));
    axisXYZ.setCoordinate(1+2, new Point3f(0.0f, 3.0f, 0.0f));

    axisXYZ.setColor(0+2, green);
    axisXYZ.setColor(1+2, green);

    // create line for Z axis
    Point3f z1 = new Point3f(0.0f, 0.0f, -1.0f);
    Point3f z2 = new Point3f(0.0f, 0.0f, 1.0f);

    //LineArray axisZLines = new LineArray(10, LineArray.COORDINATES | LineArray.COLOR_3);

    //axisBG.addChild(new Shape3D(axisZLines));

    axisXYZ.setCoordinate(0+4, z1);
    axisXYZ.setCoordinate(1+4, z2);
    axisXYZ.setCoordinate(2+4, z2);
    axisXYZ.setCoordinate(3+4, new Point3f(0.1f, 0.1f, 0.9f));
    axisXYZ.setCoordinate(4+4, z2);
    axisXYZ.setCoordinate(5+4, new Point3f(-0.1f, 0.1f, 0.9f));
    axisXYZ.setCoordinate(6+4, z2);
    axisXYZ.setCoordinate(7+4, new Point3f(0.1f, -0.1f, 0.9f));
    axisXYZ.setCoordinate(8+4, z2);
    axisXYZ.setCoordinate(9+4, new Point3f(-0.1f, -0.1f, 0.9f));

    Color3f colors[] = new Color3f[10];

    colors[0] = new Color3f(0.0f, 1.0f, 1.0f);
    for (int v = 1; v < 10; v++) {
      colors[v] = blue;
    }

    axisXYZ.setColors(0+4, colors);

    return axisXYZ;

  }
}
