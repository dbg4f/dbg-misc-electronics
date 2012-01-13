package j3d;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.applet.MainFrame;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Color3f;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.*;


import j3d.builders.LivingRoomBuilder;
import j3d.builders.RoomBuilder;
import j3d.builders.DetailsViewFilter;
import j3d.primitives.Detail;
import j3d.primitives.OrthogonalBox;
import j3d.primitives.OrthogonalPlate;

public class NavObserver extends Applet {

  class SceneKeyListener implements KeyListener {

      final SimpleUniverse su;
      final Canvas3D canvas3d;

      public SceneKeyListener(SimpleUniverse su, Canvas3D canvas3d) {
        this.su = su;
        this.canvas3d = canvas3d;
      }

      public void keyTyped(KeyEvent event) {
      }

      public void keyPressed(KeyEvent event) {
        if (event.getKeyChar() == ' ') {
          Transform3D t3d = new Transform3D();
          su.getViewingPlatform().getViewPlatformTransform().getTransform(t3d);
          new ViewPointStorage().save(t3d);
        }
        if (event.getKeyChar() == 'p') {
          new ProjectionDisplayer(new DetailsViewFilter().filter(new RoomBuilder().createDetails()));
        }
      }

      public void keyReleased(KeyEvent event) {
      }
    }

    public BranchGroup createSceneGraph(final SimpleUniverse su) {
	// Create the root of the branch graph

        BranchGroup objRoot = new BranchGroup();

        new LivingRoomBuilder().createEnv(objRoot);


        TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
        //vpTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);


        vpTrans.setTransform(new ViewPointStorage().getViewTransform());
        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(vpTrans);

        /*

        int delay = 2000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            Transform3D t3d = new Transform3D();
            su.getViewingPlatform().getViewPlatformTransform().getTransform(t3d);
            //System.out.println("T3D = " + t3d);
            Matrix4f m = new Matrix4f();
            t3d.get(m);
            System.out.println("m = " + m);
          }
        };
        new Timer(delay, taskPerformer).start();
        */

        BoundingSphere bounds = new BoundingSphere(new Point3d(), 1000.0);
        keyNavBeh.setSchedulingBounds(bounds);
        objRoot.addChild(keyNavBeh);

        Background background = new Background(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bounds);
        objRoot.addChild(background);


        AmbientLight light = new AmbientLight(new Color3f(0f, 1f, 0f));
        light.setInfluencingBounds(new BoundingSphere());
        objRoot.addChild(light);


        objRoot.compile();

        return objRoot;
    }

  public NavObserver() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);

        add("Center", canvas3D);

        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        canvas3D.addKeyListener(new SceneKeyListener(simpleU, canvas3D));

        addKeyListener(new SceneKeyListener(simpleU, canvas3D));

        BranchGroup scene = createSceneGraph(simpleU);

        simpleU.addBranchGraph(scene);
      

    }

    public static void main(String[] args) {

        int k = 4;

        new MainFrame(new NavObserver(), k*256, k*256);
    }

  // TODO: report :
  // main view (3D)
  // set of assembly views (3D) with detail sizes
  // details registry with sizes and sqares, all and grouped by size
  // main coupe doors window with sizes
  // details positions at the plate, length of cutting lines (required length of edge)
  // avoid overlapping of text at the plate

}
