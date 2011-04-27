package j3d;

import javax.media.j3d.*;
import javax.vecmath.*;


public class Room extends Shape3D {


  public Room(float x, float y, float z) {

    this.setGeometry(boxRoomGeometry(x, y, z));
    this.setAppearance(cartAppearance());

  }


  private Geometry boxRoomGeometry(float x, float y, float z) {

    QuadArray qa;
    int N = 4 * 3; // 4 vert/quad * 6 quads

    float w = 2.54f;

    Point3f coords[] = new Point3f[N];
    Color3f colors[] = new Color3f[N];

    Color3f yellow1 = new Color3f(0.9f, 0.9f, 0.4f);
    Color3f yellow2 = new Color3f(0.7f, 0.5f, 0.2f);

    coords[0] = new Point3f(x - 0, y + 0, z);
    coords[1] = new Point3f(x - w, y + 0, z);
    coords[2] = new Point3f(x - w, y + w, z);
    coords[3] = new Point3f(x - 0, y + w, z);

    colors[0] = yellow1;
    colors[1] = yellow1;
    colors[2] = yellow1;
    colors[3] = yellow1;

    coords[4] = new Point3f(x - 0, y + 0, z + 0);
    coords[5] = new Point3f(x - 0, y + w, z + 0);
    coords[6] = new Point3f(x - 0, y + w, z + w);
    coords[7] = new Point3f(x - 0, y + 0, z + w);

    if (coords[7] == coords[0]) {
      System.out.println("damn");
    }

    colors[4] = yellow1;
    colors[5] = yellow1;
    colors[6] = yellow1;
    colors[7] = yellow1;

    coords[8]  = new Point3f(x - 0, y + 0, z + 0);
    coords[9]  = new Point3f(x - w, y + 0, z + 0);
    coords[10] = new Point3f(x - w, y + 0, z + w);
    coords[11] = new Point3f(x - 0, y + 0, z + w);

    colors[8] = yellow2;
    colors[9] = yellow2;
    colors[10] = yellow2;
    colors[11] = yellow2;

    qa = new QuadArray(N, QuadArray.COORDINATES |QuadArray.COLOR_3);
    qa.setCoordinates(0, coords);
    qa.setColors(0, colors);

    return qa;

  } // end of method boxRoomGeometry in class SimpleBox

  private Appearance cartAppearance() {

    Appearance appear = new Appearance();
    PolygonAttributes polyAttrib = new PolygonAttributes();
    polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
    appear.setPolygonAttributes(polyAttrib);

    return appear;

  }


}