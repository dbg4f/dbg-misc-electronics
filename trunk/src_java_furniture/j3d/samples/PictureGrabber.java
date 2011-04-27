package j3d.samples;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * PictureGrabber
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class PictureGrabber extends Canvas3D {

   private boolean takePicture = false;
   private int width;
   private int height;
   private ImageComponent2D offscreenBuffer;
   private BufferedImage pic;
   private BufferedImage rbuffer;
   private Raster raster;

   private static GraphicsConfiguration getOffscreenGraphicsConfig() {

      GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
      GraphicsConfiguration gcfg =
              GraphicsEnvironment.getLocalGraphicsEnvironment().
              getDefaultScreenDevice().getBestConfiguration(template);
      return gcfg;
   }

   private PictureGrabber( GraphicsConfiguration c ) {
      super(getOffscreenGraphicsConfig(),true);
   }

   public PictureGrabber(GraphicsConfiguration config, int width, int height) {

      super(config,true);
      this.width = width;
      this.height = height;
      this.getScreen3D().setSize(width,height);
      this.getScreen3D().setPhysicalScreenWidth(0.0254/90.0*width);
      this.getScreen3D().setPhysicalScreenHeight(0.0254/90.0*height);

      // create the offscreen buffer

      pic = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
      ImageComponent2D ib = new ImageComponent2D(ImageComponent.FORMAT_RGBA,pic);
      this.setOffScreenBuffer(ib);

      // create a raster for grabbing frames.  Set it up to have an integer z-buffer

      rbuffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
      ImageComponent2D rb = new ImageComponent2D(ImageComponent.FORMAT_RGB,rbuffer);
      DepthComponentInt dc = new DepthComponentInt(width,height);
      raster = new Raster(new Point3f(-1.0f,-1.0f,-1.0f),
        Raster.RASTER_COLOR_DEPTH,
        0,0,
        width-1,height-1,rb,dc);

   }

   /**
    * This will take the picture immediatly by forcing a frame draw.  This
    * is only acceptable if the canvas is an offscreen canvas.
    */

   public BufferedImage takePicture() {
      this.renderOffScreenBuffer();
      this.waitForOffScreenRendering();
      // this works... its returns an image
      BufferedImage bImage = this.getOffScreenBuffer().getImage();
       return bImage;
     

     //return pic;
   }

   /**
    * Our post swap is used to grab an image of the canvas into a raster and then
    * use the depth component to mask out the background from the image.
    */
   public void postSwap() {

      GraphicsContext3D ctx = getGraphicsContext3D();
      ctx.readRaster(raster);
      pic = raster.getImage().getImage();

      System.out.println("Got thumbnail");
   }

}
