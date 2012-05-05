package dbg.electronics.pinarraysticker.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfContentByte;

import java.io.IOException;
import java.io.FileOutputStream;
import java.awt.*;

import dbg.electronics.pinarraysticker.GraphicTool;
import dbg.electronics.pinarraysticker.GrayCode;

/**
 * Created by IntelliJ IDEA.
 * User: Dmitry
 * Date: 25.04.2011
 * Time: 22:47:02
 * To change this template use File | Settings | File Templates.
 */
public class PdfLauncher {


  public static void main(String[] args) throws IOException, DocumentException {

    new PdfLauncher().createPdf(System.getProperty("outfile", "pins.pdf"));
  }

  public void createPdf(String filename) throws IOException, DocumentException {
    // step 1
      Document document = new Document(new Rectangle(650, 650));
      // step 2
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
      // step 3
      document.open();
      // step 4
      PdfContentByte canvas = writer.getDirectContent();
      Graphics2D g2 = canvas.createGraphics(650, 650);

      //drawPinArrays(g2);

      drawGrayCode(g2);

      g2.dispose();
      // step 5
      document.close();
  }

    private void drawPinArrays(Graphics2D g2) {
        GraphicTool gt = new GraphicTool();
        gt.paintComponent(g2);
    }

    private void drawGrayCode(Graphics2D g2) {
        GrayCode gt = new GrayCode();
        gt.paintComponent(g2);
    }


}
