package dbg.testitext;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author bogdel
 */
public class Main {

  static int inch = Toolkit.getDefaultToolkit().getScreenResolution();

  static float pixelToPoint = (float) 72 / (float) inch;


  public static void main(String[] args) throws FileNotFoundException, DocumentException {

    Document doc = new Document(PageSize.A4, 36, 36, 36, 36);

    OutputStream outStream = new FileOutputStream("out.pdf");

    PdfWriter writer = PdfWriter.getInstance(doc, outStream);


    doc.open();

    PdfContentByte cb = writer.getDirectContent();
    PdfTemplate tp = cb.createTemplate(200, 200);
    Graphics2D g2 = tp.createGraphicsShapes(200, 200);


    g2.drawLine(10, 10, 100, 100);
    g2.drawString("AAA", 50, 100);
    g2.drawRect(15, 15, 95, 95);

    AffineTransform at = new AffineTransform();
    at.translate(convertToPixels(20), convertToPixels(20));
    at.scale(pixelToPoint, pixelToPoint);

    g2.transform(at);


    g2.dispose();

    cb.addTemplate(tp, 36, 500);
    //doc.add(Chunk.NEXTPAGE);

    doc.close();    



  }

  public static float convertToPixels(int points) {
    return (float) (points / pixelToPoint);
  }


}
