package j3d.samples;


import java.applet.Applet;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.applet.MainFrame;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3f;

/**
 * PrintFromButton tests renderOffScreenBuffer from a button
 */
public class PrintFromButton extends Applet implements ActionListener {

  OffScreenCanvas3D1 c;

  private SimpleUniverse u = null;

  public BranchGroup createSceneGraph(Raster drawRaster) {
    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();

    // spin object has composited transformation matrix
    Transform3D spin = new Transform3D();
    Transform3D tempspin = new Transform3D();

    spin.rotX(Math.PI / 4.0d);
    tempspin.rotY(Math.PI / 5.0d);
    spin.mul(tempspin);
    spin.setScale(0.7);
    spin.setTranslation(new Vector3d(-0.4, 0.3, 0.0));

    TransformGroup objTrans = new TransformGroup(spin);
    objRoot.addChild(objTrans);

    // Create a simple shape leaf node, add it to the scene graph.
    // ColorCube is a Convenience Utility class
    objTrans.addChild(new ColorCube(0.4));

    //Create a raster obj
    Shape3D shape = new Shape3D(drawRaster);
    objRoot.addChild(shape);

    // Let Java 3D perform optimizations on this scene graph.
    objRoot.compile();

    return objRoot;
  }

  public PrintFromButton() {
  }

  public void init() {
    setLayout(new BorderLayout());
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    BufferedImage bImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);

    ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
    buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

    Raster drawRaster = new Raster(new Point3f(0.0f, 0.0f, 0.0f),
                                   Raster.RASTER_COLOR,
                                   0, 0, 200, 200, buffer, null);
    drawRaster.setCapability(Raster.ALLOW_IMAGE_WRITE);
    // create the main scene graph
    BranchGroup scene = createSceneGraph(drawRaster);
    // create the on-screen canvas
    Canvas3D d = new Canvas3D(config, false);
    add("Center", d);
    // create a simple universe
    u = new SimpleUniverse(d);
    // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    u.getViewingPlatform().setNominalViewingTransform();
    // create an off Screen Buffer
    c = new OffScreenCanvas3D1(config, true, drawRaster);
    // set the offscreen to match the onscreen
    Screen3D sOn = d.getScreen3D();
    Screen3D sOff = c.getScreen3D();
    sOff.setSize(sOn.getSize());
    sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth());
    sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight());
    // attach the same view to the offscreen canvas
    u.getViewer().getView().addCanvas3D(c);
    // create the gui
    Button b = new Button("Print");
    b.addActionListener(this);
    Panel p = new Panel();
    p.add(b);
    add("North", p);
    u.addBranchGraph(scene);
  }

  public void destroy() {
    u.cleanup();
  }

  public void actionPerformed(ActionEvent e) {
    // perform the print action
    c.print(true);
  }

  public static void main(String argv[]) {
    new MainFrame(new PrintFromButton(), 500, 500);
  }
}

