package j3d.primitives;

import com.sun.j3d.utils.image.TextureLoader;

import javax.vecmath.*;
import javax.media.j3d.*;
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
public class OrthogonalBox extends PositionedPrimitive {

  protected Map<OrthogonalPlate, Map<BoxSide, Color3f>> sideColors = new LinkedHashMap<OrthogonalPlate, Map<BoxSide, Color3f>>();

  private Vector3f dim;

  private Color3f color;

  public OrthogonalBox(Point3f pos, Vector3f dim) {
    this.dim = dim;
    this.position = pos;
    applyDefaultColor(defaultColor);
    this.setGeometry(createGeometry(pos, dim));
    //this.setAppearance(createTexAppearance());
    this.setAppearance(createSimpleAppearance());
  }

  public Vector3f getDim() {
    return dim;
  }

  public Color3f getColor() {
    return color;
  }

  public void rebuild() {
    this.setGeometry(createGeometry(position, dim));
    this.setAppearance(createSimpleAppearance());
  }

  public void rebuildWired() {
    this.setGeometry(createWiredGeometry(position, dim));
    this.setAppearance(createSimpleAppearance());
  }

  protected Geometry createGeometry(Point3f pos, Vector3f dim) {
    List<Point3f> points = new ArrayList<Point3f>();
    List<Color3f> colors = new ArrayList<Color3f>();
    //List<TexCoord2f> texture = new ArrayList<TexCoord2f>();

    for (OrthogonalPlate plate : OrthogonalPlate.values()) {

      for (BoxSide side : BoxSide.values()) {
        points.addAll(Arrays.asList(plate.create(side.shiftStartPos(pos, dim, plate), dim)));
        Color3f c = sideColors.get(plate).get(side);
        colors.addAll(Arrays.asList(c, c, c, c));
        //texture.addAll(Arrays.asList(new TexCoord2f(0f, 0f), new TexCoord2f(0f, 0f), new TexCoord2f(0f, 0f), new TexCoord2f(0f, 0f)));
      }
    }

    QuadArray qa = new QuadArray(points.size(), QuadArray.COORDINATES | QuadArray.COLOR_3 /*| QuadArray.TEXTURE_COORDINATE_2*/);

    int vertIndex = 0;

    for (int i=0; i<points.size()/4; i++) {
        //qa.setTextureCoordinate(0, vertIndex++, new TexCoord2f(1f, 0f));
        //qa.setTextureCoordinate(0, vertIndex++, new TexCoord2f(1f, 0f));
        //qa.setTextureCoordinate(0, vertIndex++, new TexCoord2f(0f, 0f));
        //qa.setTextureCoordinate(0, vertIndex++, new TexCoord2f(0f, 0f));
    }
                
    qa.setCoordinates(0, points.toArray(new Point3f[points.size()]));
    qa.setColors(0, colors.toArray(new Color3f[colors.size()]));

    return qa;
  }

  public void applyDefaultColor(Color3f color) {
    for (OrthogonalPlate plate : OrthogonalPlate.values()) {
      sideColors.put(plate, new LinkedHashMap<BoxSide, Color3f>());
      for (BoxSide side : BoxSide.values()) {
        sideColors.get(plate).put(side, color);
      }
    }
  }

  public void setPlateColor(OrthogonalPlate orientation, BoxSide side, Color3f color) {
    sideColors.get(orientation).put(side, color);
  }

  public void setColor(Color3f color) {
    for(OrthogonalPlate orthogonalPlate : OrthogonalPlate.values()) {
      for (BoxSide side : BoxSide.values()) {
        sideColors.get(orthogonalPlate).put(side, color);
      }
    }
    this.color = color;
  }

  public void setPlateColor(OrthogonalPlate orientation, Color3f color) {
    for (BoxSide side : BoxSide.values()) {
      setPlateColor(orientation, side, color);
    }
  }


  private Appearance createSimpleAppearance() {
      Appearance appear = new Appearance();
      PolygonAttributes polyAttrib = new PolygonAttributes();
      polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
      appear.setPolygonAttributes(polyAttrib);
      return appear;
  }

    protected Geometry createWiredGeometry(Point3f pos, Vector3f dim) {
      List<Point3f> points = new ArrayList<Point3f>();
      List<Color3f> colors = new ArrayList<Color3f>();

      for (OrthogonalPlate plate : OrthogonalPlate.values()) {

        for (BoxSide side : BoxSide.values()) {
          final Point3f[] fs = plate.create(side.shiftStartPos(pos, dim, plate), dim);
          points.addAll(Arrays.asList(fs[0], fs[1], fs[1], fs[2], fs[2], fs[3], fs[3], fs[0]));
          Color3f c = sideColors.get(plate).get(side);
          colors.addAll(Arrays.asList(c, c, c, c, c, c, c, c));
        }
      }


      LineArray la = new LineArray(points.size(), QuadArray.COORDINATES | QuadArray.COLOR_3);

      la.setCoordinates(0, points.toArray(new Point3f[points.size()]));
      la.setColors(0, colors.toArray(new Color3f[colors.size()]));

      return la;
    }


  private Appearance createTexAppearance() {

      Appearance appear = new Appearance();

      String filename = "1070.jpg";
      TextureLoader loader = new NewTextureLoader(filename);
      ImageComponent2D image = loader.getImage();

      if(image == null) {
            throw new RuntimeException("load failed for texture: "+filename);
      }

      // can't use parameterless constuctor
      Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
      texture.setImage(0, image);

      appear.setTexture(texture);

      

    /*
      */
    return appear;

  }

}
