package dbg.testitext;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.FileOutputStream;

/**
 * This example was written by Bill Ensley and adapter by Bruno Lowagie.
 * It is part of the book 'iText in Action' by Manning Publications.
 * ISBN: 1932394796
 * http://www.1t3xt.com/docs/book.php
 * http://www.manning.com/lowagie/
 */

public class JTextPaneToPdf2 {

	int inch = Toolkit.getDefaultToolkit().getScreenResolution();

	float pixelToPoint = (float) 72 / (float) inch;

	JTextPane textPane;

	/**
	 * Constructs a JTextPaneToPdf object. This opens a frame that can be used
	 * as text editor.
	 */
	public JTextPaneToPdf2() {
		JFrame frame = new JFrame();
		textPane = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(textPane);

		JPanel north = new JPanel();
		JButton print = new JButton("Print");

    print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				paintToPDF(textPane);
			}
		});

		north.add(print);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(north, BorderLayout.NORTH);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(800, 500);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}

	/**
	 * Renders a JTextPane to PDF.
	 *
	 * @param args
	 *            no arguments needed here
	 */
	public static void main(String[] args) {

    new JTextPaneToPdf2();
	}

  static class Drawing2D extends JPanel {
    public Drawing2D() {
      JFrame frame = new JFrame();
      frame.getContentPane().add(this);
      frame.setBackground(Color.white);
      frame.setSize(600, 600);
      frame.setLocation(400, 300);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void paint(Graphics graphics) {
      graphics.drawLine(10, 10, 100, 100);
    }
  }

	public void paintToPDF(JTextPane ta) {
		try {
			ta.setBounds(0, 0, (int) convertToPixels(612 - 58), (int) convertToPixels(792 - 60));

			Document document = new Document();
			FileOutputStream fos = new FileOutputStream("editor.pdf");
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			document.setPageSize(new com.lowagie.text.Rectangle(612, 792));
			document.open();
			PdfContentByte cb = writer.getDirectContent();

			cb.saveState();
			cb.concatCTM(1, 0, 0, 1, 0, 0);

			Graphics2D g2 = cb.createGraphicsShapes(612, 792, true, .95f);

			AffineTransform at = new AffineTransform();
			at.translate(convertToPixels(20), convertToPixels(20));
			at.scale(pixelToPoint, pixelToPoint);

			g2.transform(at);

			g2.setColor(Color.WHITE);
			g2.fill(ta.getBounds());

			Rectangle alloc = getVisibleEditorRect(ta);

      g2.drawLine(10, 10, 100, 100);

			ta.getUI().getRootView(ta).paint(g2, alloc);


			g2.setColor(Color.BLACK);
			g2.draw(ta.getBounds());

			g2.dispose();
			cb.restoreState();
			document.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float convertToPoints(int pixels) {
		return (float) (pixels * pixelToPoint);
	}

	public float convertToPixels(int points) {
		return (float) (points / pixelToPoint);
	}

	protected Rectangle getVisibleEditorRect(JTextPane ta) {
		Rectangle alloc = ta.getBounds();
		if ((alloc.width > 0) && (alloc.height > 0)) {
			alloc.x = alloc.y = 0;
			Insets insets = ta.getInsets();
			alloc.x += insets.left;
			alloc.y += insets.top;
			alloc.width -= insets.left + insets.right;
			alloc.height -= insets.top + insets.bottom;
			return alloc;
		}
		return null;
	}


}