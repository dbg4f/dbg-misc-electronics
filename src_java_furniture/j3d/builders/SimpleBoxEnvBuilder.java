package j3d.builders;

import j3d.SimpleBox;
import j3d.Room;
import j3d.samples.Coord;

import javax.media.j3d.BranchGroup;

public class SimpleBoxEnvBuilder {
    public static void createEnv(BranchGroup objRoot) {
        float shiftDown = -1.8f;

        objRoot.addChild(new SimpleBox(0.016f, 0.4f, 2.5f, -0.75f, shiftDown +0f,  0f));
        objRoot.addChild(new SimpleBox(0.016f, 0.4f, 2.5f,  0.75f,  shiftDown+0f,  0f));

        objRoot.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  shiftDown+0f,  0f));

        objRoot.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  shiftDown+0.4f,  0f));

        objRoot.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  shiftDown+2.1f-0.016f,  0f));

        objRoot.addChild(new SimpleBox(1.5f, 0.4f, 0.016f,  -0.75f,  shiftDown+2.5f-0.016f,  0f));


        objRoot.addChild(new Room(0.001f+0.75f+0.016f, shiftDown, -0.001f));

        //objSpin.addChild(new SimpleBox(0.016f, 0.4f, 0.8f));

        objRoot.addChild(new Coord());
    }
}
