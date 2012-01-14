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
import java.util.List;


import j3d.builders.FilterFarme;
import j3d.builders.LivingRoomBuilder;
import j3d.builders.RoomBuilder;
import j3d.builders.DetailsViewFilter;
import j3d.primitives.Detail;
import j3d.primitives.OrthogonalBox;
import j3d.samples.RedrawListener;

public class NavObserver extends Applet implements RedrawListener {

    private List<Detail> details;
    private BranchGroup scene;
    private SimpleUniverse simpleU;
    private BranchGroup branchGroup;

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

    public BranchGroup createSceneGraph(final SimpleUniverse su, BranchGroup branchGroup, List<Detail> details, Transform3D viewTransform) {
	// Create the root of the branch graph




        TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();

        vpTrans.setTransform(viewTransform);
        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(vpTrans);


        BoundingSphere bounds = new BoundingSphere(new Point3d(), 1000.0);
        keyNavBeh.setSchedulingBounds(bounds);
        branchGroup.addChild(keyNavBeh);

        Background background = new Background(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bounds);
        branchGroup.addChild(background);


        AmbientLight light = new AmbientLight(new Color3f(0f, 1f, 0f));
        light.setInfluencingBounds(new BoundingSphere());
        branchGroup.addChild(light);




        return branchGroup;
    }

    private List<Detail> createDetails() {

        LivingRoomBuilder builder = new LivingRoomBuilder();

        List<Detail> details = builder.createDetails();



        builder.setAppearance(details);
        return details;
    }

    private void attachDetailsToGraph(BranchGroup branchGroup, List<Detail> details) {
        for (Detail detail : details) {
        if (detail.isVisible()) {
            OrthogonalBox box = detail.getBox();
            branchGroup.addChild(box.cloneBox());
        }
      }
    }



    public NavObserver() {

        details = createDetails();

        FilterFarme.launch(details, this);

        //details = detailsUI;

        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);

        add("Center", canvas3D);

        simpleU = new SimpleUniverse(canvas3D);

        canvas3D.addKeyListener(new SceneKeyListener(simpleU, canvas3D));

        addKeyListener(new SceneKeyListener(simpleU, canvas3D));


        branchGroup = new BranchGroup();

        attachDetailsToGraph(branchGroup, details);

        scene = createSceneGraph(simpleU, branchGroup, details, new ViewPointStorage().getViewTransform());

        scene.setCapability(BranchGroup.ALLOW_DETACH);
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        scene.compile();

        simpleU.addBranchGraph(scene);


        redraw();

        redraw();


    }

    public void onRedraw() {
        redraw();
    }

    private void redraw() {

        Transform3D t3d = new Transform3D();

        simpleU.getViewingPlatform().getViewPlatformTransform().getTransform(t3d);

        scene.detach();

        scene.removeChild(branchGroup);

        final BranchGroup branchGroup2 = new BranchGroup();

        attachDetailsToGraph(branchGroup2, details);

       // Transform3D viewTransform = new ViewPointStorage().getViewTransform();



        BranchGroup scene1 = createSceneGraph(simpleU, branchGroup2, details, t3d);

        scene1.setCapability(BranchGroup.ALLOW_DETACH);
        scene1.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);


        scene1.compile();

        simpleU.addBranchGraph(scene1);

        scene = scene1;

        //

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
