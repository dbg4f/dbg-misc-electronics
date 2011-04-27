package j3d.primitives;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;

/**
 * OrthoPlate
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public enum OrthogonalPlate {
  XY {
    public Point3f[] create(Point3f startPos, Vector3f dim) {
      float d1 = dim.getX();
      float d2 = dim.getY();
      return createPoints(startPos, new float[][] {
        {0f, 0f, 0f},
        {d1, 0f, 0f},
        {d1, d2, 0f},
        {0f, d2, 0f},
      });
    }
    public Point3f shiftParallel(Point3f startPos, Vector3f dim) {
      return new Point3f(startPos.x, startPos.y, startPos.z + dim.getZ());
    }
    public float[] getPlateSizes(Vector3f dimensions) {
      return new float[]{dimensions.getX(), dimensions.getY()};
    }
    public Point2f makeProjection(Point3f point) {
      return new Point2f(point.getX(), point.getY());
    }
    public Vector2f makeProjection(Vector3f dim) {
      return new Vector2f(dim.getX(), dim.getY());
    }
  },

  XZ {
    public Point3f[] create(Point3f startPos, Vector3f dim) {
      float d1 = dim.getX();
      float d2 = dim.getZ();
      return createPoints(startPos, new float[][] {
        {0f, 0f, 0f},
        {d1, 0f, 0f},
        {d1, 0f, d2},
        {0f, 0f, d2},
      });
    }
    public Point3f shiftParallel(Point3f startPos, Vector3f dim) {
      return new Point3f(startPos.x, startPos.y + dim.getY(), startPos.z);
    }
    public float[] getPlateSizes(Vector3f dimensions) {
      return new float[]{dimensions.getX(), dimensions.getZ()};
    }
    public Point2f makeProjection(Point3f point) {
      return new Point2f(point.getX(), point.getZ());
    }
    public Vector2f makeProjection(Vector3f dim) {
      return new Vector2f(dim.getX(), dim.getZ());
    }
  },

  YZ {
    public Point3f[] create(Point3f startPos, Vector3f dim) {
      float d1 = dim.getY();
      float d2 = dim.getZ();
      return createPoints(startPos, new float[][] {
        {0f, 0f, 0f},
        {0f, d1, 0f},
        {0f, d1, d2},
        {0f, 0f, d2},
      });
    }
    public Point3f shiftParallel(Point3f startPos, Vector3f dim) {
      return new Point3f(startPos.x + dim.getX(), startPos.y, startPos.z);
    }
    public float[] getPlateSizes(Vector3f dimensions) {
      return new float[]{dimensions.getY(), dimensions.getZ()};
    }
    public Point2f makeProjection(Point3f point) {
      return new Point2f(point.getY(), point.getZ());
    }
    public Vector2f makeProjection(Vector3f dim) {
      return new Vector2f(dim.getY(), dim.getZ());  
    }
  };

  private static Point3f[] createPoints(Point3f startPos, float[][] add) {

    Point3f[] points = new Point3f[4];

    for (int i=0; i<4; i++) {
      points[i] = new Point3f(startPos.x + add[i][0], startPos.y + add[i][1], startPos.z + add[i][2]);
    }

    return points;
  }

  public abstract Point3f[] create(Point3f startPos, Vector3f dim);

  public abstract Point3f shiftParallel(Point3f startPos, Vector3f dim);

  public abstract float[] getPlateSizes(Vector3f dimensions);

  public abstract Point2f makeProjection(Point3f point);

  public abstract Vector2f makeProjection(Vector3f dim);

}
