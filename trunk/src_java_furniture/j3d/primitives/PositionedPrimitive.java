package j3d.primitives;

import javax.vecmath.Point3f;
import javax.vecmath.Color3f;
import javax.media.j3d.Shape3D;

/**
 * PositionedPrimitive
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class PositionedPrimitive extends Shape3D {

  protected Point3f position;
  protected Color3f defaultColor = new Color3f(1f, 0f, 0f);

  public void setPosition(Point3f position) {
    this.position = position;
  }

  public void setDefaultColor(Color3f defaultColor) {
    this.defaultColor = defaultColor;
  }

  public Point3f getPosition() {
    return position;
  }
}
