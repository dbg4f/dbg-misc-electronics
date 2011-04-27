package j3d.samples;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.*;

class ImageDisplayer extends JFrame implements ActionListener {
  BufferedImage bImage;

  private class ImagePanel extends JPanel {
    public void paint(Graphics g) {
      //g.setColor(Color.black);
      //g.fillRect(0, 0, getSize().width, getSize().height);
      g.drawImage(bImage, 0, 0, this);
    }

    private ImagePanel() {
      setPreferredSize(new Dimension(bImage.getWidth(), bImage
          .getHeight()));
    }
  }

  private JMenuItem printItem;

  private JMenuItem closeItem;

  public void actionPerformed(ActionEvent event) {
    Object target = event.getSource();

    if (target == printItem) {
      new ImagePrinter(bImage).print();
    } else if (target == closeItem) {
      this.removeAll();
      this.setVisible(false);
      bImage = null;
    }
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    printItem = new JMenuItem("Print...");
    printItem.addActionListener(this);
    closeItem = new JMenuItem("Close");
    closeItem.addActionListener(this);
    fileMenu.add(printItem);
    fileMenu.add(new JSeparator());
    fileMenu.add(closeItem);
    menuBar.add(fileMenu);
    return menuBar;
  }

  ImageDisplayer(BufferedImage bImage) {
    this.bImage = bImage;
    this.setTitle("Off-screen Canvas3D Snapshot");

    // Create and initialize menu bar
    this.setJMenuBar(createMenuBar());

    // Create scroll pane, and embedded image panel
    ImagePanel imagePanel = new ImagePanel();
    JScrollPane scrollPane = new JScrollPane(imagePanel);
    scrollPane.getViewport().setPreferredSize(new Dimension(700, 700));

    // Add scroll pane to the frame and make it visible
    this.getContentPane().add(scrollPane);
    this.pack();
    this.setVisible(true);
  }
}
