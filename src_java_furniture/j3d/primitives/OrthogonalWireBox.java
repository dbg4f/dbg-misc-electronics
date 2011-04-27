package j3d.primitives;

import javax.vecmath.Point3f;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.LineArray;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Box
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class OrthogonalWireBox extends OrthogonalBox {

  public OrthogonalWireBox(Point3f pos, Vector3f dim) {
    super(pos, dim);
  }


}