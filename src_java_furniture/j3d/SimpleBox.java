package j3d;

import javax.media.j3d.*;
import javax.vecmath.*;


public class SimpleBox extends Shape3D {

  public SimpleBox(float l, float w, float d) {

    this.setGeometry(boxBodyGeometry(l, w, d, 0f, 0f, 0f));
    this.setAppearance(cartAppearance());

  }

  public SimpleBox(float l, float w, float d, float x, float y, float z) {

    this.setGeometry(boxBodyGeometry(l, w, d, x, y, z));
    this.setAppearance(cartAppearance());

  }


  private Geometry boxBodyGeometry(float l, float w, float h, float x, float y, float z) {

    QuadArray qa;
    int N = 4 * 6; // 4 vert/quad * 6 quads
    Point3f coords[] = new Point3f[N];
    Color3f colors[] = new Color3f[N];

    Color3f red1 = new Color3f(0.7f, 0.0f, 0.0f);
    Color3f red2 = new Color3f(0.8f, 0.0f, 0.0f);
    Color3f red3 = new Color3f(0.9f, 0.0f, 0.0f);
    
    Color3f yellow1 = new Color3f(0.8f, 0.6f, 0.0f);
    Color3f yellow2 = new Color3f(0.7f, 0.5f, 0.0f);
    Color3f yellow3 = new Color3f(0.8f, 0.8f, 0.1f);
    Color3f green = new Color3f(0.0f, 0.5f, 0.0f);
    Color3f green2 = new Color3f(0.0f, 0.4f, 0.1f);

    coords[0] = new Point3f(x + l, y + 0, z + 0);
    coords[1] = new Point3f(x + 0, y + 0, z + 0);
    coords[2] = new Point3f(x + 0, y + 0, z + w);
    coords[3] = new Point3f(x + l, y + 0, z + w);

    colors[0] = yellow3;
    colors[1] = yellow3;
    colors[2] = yellow3;
    colors[3] = yellow3;

    coords[4] = coords[0];
    coords[5] = coords[1];
    coords[6] = new Point3f(x + 0, y + h, z + 0);
    coords[7] = new Point3f(x + l, y + h, z + 0);

    if (coords[7] == coords[0]) {
      System.out.println("damn");
    }

    colors[4] = red1;
    colors[5] = red1;
    colors[6] = red1;
    colors[7] = red1;

    coords[8] = coords[1];
    coords[9] = coords[2];
    coords[10] = new Point3f(x + 0, y + h, z + w);
    coords[11] = new Point3f(x + 0, y + h, z + 0);

    colors[8] = yellow1;
    colors[9] = yellow1;
    colors[10] = yellow1;
    colors[11] = yellow1;

    coords[12] = coords[2];
    coords[13] = coords[3];
    coords[14] = new Point3f(x + l, y + h, z + w);
    coords[15] = new Point3f(x + 0, y + h, z + w);

    colors[12] = red2;
    colors[13] = red2;
    colors[14] = red2;
    colors[15] = red2;

    coords[16] = coords[3];
    coords[17] = coords[0];
    coords[18] = new Point3f(x + l, y + h, z + 0);
    coords[19] = new Point3f(x + l, y + h, z + w);

    colors[16] = yellow2;
    colors[17] = yellow2;
    colors[18] = yellow2;
    colors[19] = yellow2;

    coords[20] = coords[6];//new Point3f(x + l, y + 0, z + 0);
    coords[21] = coords[7];//new Point3f(x + 0, y + 0, z + 0);
    coords[22] = coords[14];//new Point3f(x + 0, y + 0, z + w);
    coords[23] = coords[15];//new Point3f(x + l, y + 0, z + w);

    colors[20] = yellow3;
    colors[21] = yellow3;
    colors[22] = yellow3;
    colors[23] = yellow3;


    qa = new QuadArray(N, QuadArray.COORDINATES |QuadArray.COLOR_3);
    qa.setCoordinates(0, coords);
    qa.setColors(0, colors);

    return qa;

  } // end of method boxBodyGeometry in class SimpleBox

  private Appearance cartAppearance() {

    Appearance appear = new Appearance();
    PolygonAttributes polyAttrib = new PolygonAttributes();
    polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
    appear.setPolygonAttributes(polyAttrib);

    return appear;

  }


}