package j3d.samples;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.Raster;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ImageComponent;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * OffScreenCanvas3D
 * <p/>
 *
 * @author Dmitri Bogdel
 */
class OffScreenCanvas3D1 extends Canvas3D {

  Raster drawRaster;

  boolean printing = false;

  public OffScreenCanvas3D1(GraphicsConfiguration gconfig,
      boolean offscreenflag, Raster drawRaster) {

    super(gconfig, offscreenflag);
    this.drawRaster = drawRaster;
  }

  public void print(boolean toWait) {

    if (!toWait)
      printing = true;

    BufferedImage bImage = new BufferedImage(200, 200,BufferedImage.TYPE_INT_ARGB);

    ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
    buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

    this.setOffScreenBuffer(buffer);
    this.renderOffScreenBuffer();

    if (toWait) {
      this.waitForOffScreenRendering();
      drawOffScreenBuffer();
    }
  }

  public void postSwap() {

    if (printing) {
      super.postSwap();
      drawOffScreenBuffer();
      printing = false;
    }
  }

  void drawOffScreenBuffer() {

    BufferedImage bImage = this.getOffScreenBuffer().getImage();
    ImageComponent2D newImageComponent = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
    newImageComponent.setCapability( ImageComponent2D.ALLOW_IMAGE_READ );

    drawRaster.setImage(newImageComponent);

    //newImageComponent.getRenderedImage().

    save(bImage);


    FileOutputStream fileOut = null;
    try {
      fileOut = new FileOutputStream( "image.jpg" );

      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( fileOut );
      encoder.encode( newImageComponent.getImage() );
      fileOut.flush();
      fileOut.close();

    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    new ImageDisplayer(bImage);

  }

  private void save(BufferedImage img) {
    File file = new File("AAA111-" + ".bmp");
    try {

      int type = BufferedImage.TYPE_INT_RGB;  // other options
      BufferedImage dest = new BufferedImage(getWidth(), getHeight(), type);
      Graphics2D g2 = dest.createGraphics();
      g2.setBackground(Color.WHITE);
      //paintComponent(g2);
      g2.drawRenderedImage(img, new AffineTransform());
      g2.setColor(Color.GREEN);
      g2.drawLine(10, 10, 100, 100);
      g2.dispose();

      ImageIO.write(dest, "bmp", file);  // ignore returned boolean
      System.out.println("Save to bmp");




    }
    catch (IOException e) {
      System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
    }
  }

  public void keyReleased(KeyEvent e) {
  }


}
