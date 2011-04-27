package j3d.primitives;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * BoxSide
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public enum BoxSide {
  CLOSER_TO_CENTER {
    public Point3f shiftStartPos(Point3f start, Vector3f dim, OrthogonalPlate orientation) {
      return new Point3f(start);
    }
  },
  FARTEHR_TO_CENTER {
    public Point3f shiftStartPos(Point3f start, Vector3f dim, OrthogonalPlate orientation) {
      return orientation.shiftParallel(start, dim);
    }
  };

  public abstract Point3f shiftStartPos(Point3f start, Vector3f dim, OrthogonalPlate orientation);

}
