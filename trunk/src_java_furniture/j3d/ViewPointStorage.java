package j3d;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ViewPointStorage
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class ViewPointStorage {



  public Transform3D getViewTransform() {

    String fileName = System.getProperty("view.file", "");

    if (!fileName.equalsIgnoreCase("") && new File(fileName).exists()) {
      Transform3D t = new Transform3D();
      t.set(loadFromFile(fileName));
      return t;
    }
    else {
      Vector3f translate = new Vector3f();
      final Transform3D T3D = new Transform3D();

      Transform3D rotateY = new Transform3D();
      rotateY.rotY(Math.PI);

      Transform3D rotateX = new Transform3D();
      rotateX.rotX(-Math.PI/2.0);


      translate.set( 1f, 5f, 1.8f);
      T3D.setTranslation(translate);
      T3D.mul(rotateY);
      T3D.mul(rotateX);

      return T3D;

    }


  }

  private String round(float value) {
    return String.valueOf(value);
    //return new BigDecimal(value).setScale(3, RoundingMode.HALF_EVEN).toString();
  }

  private String matrixToString(Matrix4f m) {
    float[] f1 = new float[4];
    float[] f2 = new float[4];
    float[] f3 = new float[4];
    float[] f4 = new float[4];

    m.getRow(0, f1);
    m.getRow(1, f2);
    m.getRow(2, f3);
    m.getRow(3, f4);

    return
      round(f1[0]) + "," + round(f1[1]) + "," + round(f1[2]) + "," + round(f1[3]) + "\r\n" +
      round(f2[0]) + "," + round(f2[1]) + "," + round(f2[2]) + "," + round(f2[3]) + "\r\n" +
      round(f3[0]) + "," + round(f3[1]) + "," + round(f3[2]) + "," + round(f3[3]) + "\r\n" +
      round(f4[0]) + "," + round(f4[1]) + "," + round(f4[2]) + "," + round(f4[3]) + "\r\n";
  }

  public void save(Transform3D t) {
    String fileName = System.getProperty("view.save.file", "");

    Matrix4f m = new Matrix4f();
    t.get(m);
    String matrixString = matrixToString(m);

    if (!fileName.equalsIgnoreCase("") && new File(fileName).exists()) {
      try {
        FileWriter wr = new FileWriter(fileName);
        wr.write(matrixString);
        wr.close();

      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      System.out.println(matrixString);
    }

  }

  private Matrix4f loadFromFile(String fileName) {
    Matrix4f m = new Matrix4f();
    try {
      BufferedReader rd = new BufferedReader(new FileReader(fileName));
      String line1 = rd.readLine();
      String line2 = rd.readLine();
      String line3 = rd.readLine();
      String line4 = rd.readLine();

      String[] r1 = line1.split(",");
      String[] r2 = line2.split(",");
      String[] r3 = line3.split(",");
      String[] r4 = line4.split(",");

      m.setRow(0, new float[] {Float.parseFloat(r1[0]), Float.parseFloat(r1[1]), Float.parseFloat(r1[2]), Float.parseFloat(r1[3])});
      m.setRow(1, new float[] {Float.parseFloat(r2[0]), Float.parseFloat(r2[1]), Float.parseFloat(r2[2]), Float.parseFloat(r2[3])});
      m.setRow(2, new float[] {Float.parseFloat(r3[0]), Float.parseFloat(r3[1]), Float.parseFloat(r3[2]), Float.parseFloat(r3[3])});
      m.setRow(3, new float[] {Float.parseFloat(r4[0]), Float.parseFloat(r4[1]), Float.parseFloat(r4[2]), Float.parseFloat(r4[3])});

      System.out.println("m from file = " + m);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return m;
  }


}
