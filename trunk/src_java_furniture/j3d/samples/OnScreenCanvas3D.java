package j3d.samples;

import javax.media.j3d.Canvas3D;
import java.awt.*;

/**
 * OnScreenCanvas3D
 * <p/>
 *
 * @author Dmitri Bogdel
 */
class OnScreenCanvas3D extends Canvas3D {

  OffScreenCanvas3D1 c;

  boolean print = false;

  boolean imageReady = false;

  public OnScreenCanvas3D(GraphicsConfiguration gconfig, boolean offscreenflag) {
    super(gconfig, offscreenflag);
  }

  public void setOffScreenCanvas(OffScreenCanvas3D1 c) {
    this.c = c;
  }

  public void setImageReady() {
    imageReady = true;
  }

  public void postSwap() {
    if (imageReady && !print) {
      c.print(false);
      print = true;
    }
  }
}
