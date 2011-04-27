package j3d.samples;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import j3d.primitives.OrthogonalBox;
import j3d.primitives.OrthogonalWireBox;
import j3d.samples.Coord;


public class SceneObserver extends Applet {

  public BranchGroup createSceneGraph() {

    BranchGroup objRoot = new BranchGroup();

    Transform3D rotate = new Transform3D();
    Transform3D tempRotate = new Transform3D();

    rotate.rotX(Math.PI/4.0d);
    tempRotate.rotY(Math.PI/6.0d);
    rotate.mul(tempRotate);

    TransformGroup objRotate = new TransformGroup(rotate);

    TransformGroup objSpin = new TransformGroup();
    objSpin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    objRoot.addChild(objRotate);
    objRotate.addChild(objSpin);
    
    //objSpin.addChild(new Yoyo());
    //objSpin.addChild(new ColorCube(0.4));

    /*
    objSpin.addChild(new SimpleBox(0.016f, 0.4f, 2.5f, -0.75f,  -1f+0f,  0f));
    objSpin.addChild(new SimpleBox(0.016f, 0.4f, 2.5f,  0.75f,  -1f+0f,  0f));

    objSpin.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  -1f+0f,  0f));
    objSpin.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  -1f+0.4f,  0f));
     */


    //objSpin.addChild(new SimpleBox(0.016f, 0.4f, 0.8f));

    final OrthogonalBox orthogonalBox = new OrthogonalWireBox(new Point3f(0.2f, 0.2f, 0.2f), new Vector3f(0.3f, 0.5f, 0.7f));
    /*
    orthogonalBox.setPlateColor(OrthogonalPlate.XY, new Color3f(0f, 0f, 0f));
    orthogonalBox.setPlateColor(OrthogonalPlate.XZ, new Color3f(1f, 1f, 0f));
    orthogonalBox.setPlateColor(OrthogonalPlate.YZ, new Color3f(1f, 0f, 1f));

    orthogonalBox.rebuild();
     */
    objSpin.addChild(orthogonalBox);

    objSpin.addChild(new Coord());


    Transform3D yAxis = new Transform3D();
    Alpha rotationAlpha = new Alpha(-1, 16000);

    RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objSpin, yAxis,  0.0f, (float) Math.PI*2.0f);
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    rotator.setSchedulingBounds(bounds);
    objSpin.addChild(rotator);

    Background background = new Background(1.0f, 1.0f, 1.0f);
    background.setApplicationBounds(bounds);
    objRoot.addChild(background);
    

    objRoot.compile();

    return objRoot;
  }

  public SceneObserver() {
    setLayout(new BorderLayout());
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);

    BranchGroup scene = createSceneGraph();

    SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

    simpleU.getViewingPlatform().setNominalViewingTransform();

    simpleU.addBranchGraph(scene);
  }

  public static void main(String[] args) {

    int m = 1;

    new MainFrame(new SceneObserver(), m*256, m*256);
  }

}