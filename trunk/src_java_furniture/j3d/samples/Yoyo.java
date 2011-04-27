package j3d.samples;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Geometry;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Point3f;
import javax.vecmath.Color3f;

/**
 * SimpleBox
* <p/>
*
* @author Dmitri Bogdel
*/
public class Yoyo extends Shape3D {

  public Yoyo() {

    this.setGeometry(createGeometry());

  }

  private Geometry createGeometry() {

    TriangleFanArray tfa;
    int N = 17;
    int totalN = 4 * (N + 1);
    Point3f coords[] = new Point3f[totalN];
    Color3f colors[] = new Color3f[totalN];
    Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    Color3f yellow = new Color3f(0.7f, 0.5f, 0.0f);
    int stripCounts[] = {N + 1, N + 1, N + 1, N + 1};
    float r = 0.6f;
    float w = 0.4f;
    int n;
    double a;
    float x, y;

    // set the central points for four triangle fan strips
    coords[0 * (N + 1)] = new Point3f(0.0f, 0.0f, w);
    coords[1 * (N + 1)] = new Point3f(0.0f, 0.0f, 0.0f);
    coords[2 * (N + 1)] = new Point3f(0.0f, 0.0f, 0.0f);
    coords[3 * (N + 1)] = new Point3f(0.0f, 0.0f, -w);

    colors[0 * (N + 1)] = red;
    colors[1 * (N + 1)] = yellow;
    colors[2 * (N + 1)] = yellow;
    colors[3 * (N + 1)] = red;

    for (a = 0, n = 0; n < N; a = 2.0 * Math.PI / (N - 1) * ++n) {
      x = (float)(r * Math.cos(a));
      y = (float)(r * Math.sin(a));
      coords[0 * (N + 1) + n + 1] = new Point3f(x, y, w);
      coords[1 * (N + 1) + N - n] = new Point3f(x, y, w);
      coords[2 * (N + 1) + n + 1] = new Point3f(x, y, -w);
      coords[3 * (N + 1) + N - n] = new Point3f(x, y, -w);

      colors[0 * (N + 1) + N - n] = red;
      colors[1 * (N + 1) + n + 1] = yellow;
      colors[2 * (N + 1) + N - n] = yellow;
      colors[3 * (N + 1) + n + 1] = red;
    }

    tfa = new TriangleFanArray(totalN, TriangleFanArray.COORDINATES | TriangleFanArray.COLOR_3, stripCounts);

    tfa.setCoordinates(0, coords);
    tfa.setColors(0, colors);

    return tfa;

  }

}
