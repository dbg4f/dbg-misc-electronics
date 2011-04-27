package j3d;

import j3d.builders.RoomBuilder;
import j3d.primitives.Detail;
import j3d.primitives.DetailType;
import j3d.primitives.PlacedDetail;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PlainSpec
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class PlainSpec {


  public static void main(String[] args) throws IOException {


    java.util.List<Detail> details = new RoomBuilder().createDetails();


    for (Detail d : details) {      
      System.out.print("" + d.getDetailType() + "/" + d.getName() + "," + d.getOrderedSizes()[0] + "," + d.getOrderedSizes()[1] + ",4000,4000,0,0,0\r\n");
    }

    System.out.println("-------------------------");

    PlainSpec ps = new PlainSpec();

    Map<DetailType, List<Detail>> detByType = ps.getDetailsByType(details);

    Map<String, List<Detail>> detBySize = ps.groupBySize(details, detByType);

    ps.print(detBySize);

    ps.writeSvg(detBySize);


  }
  
  public Map<DetailType, List<Detail>> getDetailsByType(List<Detail> details) {
    Map<DetailType, List<Detail>> detByType = new LinkedHashMap<DetailType, List<Detail>>();

    for (DetailType t : DetailType.values()) {
      detByType.put(t, new ArrayList<Detail>());
    }

    for (Detail d : details) {
      detByType.get(d.getDetailType()).add(d);
    }
    return detByType;
  }


  public List<PlacedDetail> placeDetails(Map<DetailType, List<Detail>> detByType) {
    List<PlacedDetail> placedDetails = new ArrayList<PlacedDetail>();

    PlacedDetail prevDet = null;

    for (Map.Entry<DetailType, java.util.List<Detail>> entry : detByType.entrySet()) {
      if (entry.getKey() == DetailType.ROOM_WALL) {
        continue;
      }

      for (Detail detail : entry.getValue()) {

        PlacedDetail placedDetail = new PlacedDetail(detail);

        placedDetails.add(placedDetail);
                       
        if (prevDet == null) {
          placedDetail.setX(50);
          placedDetail.setY(50);
        }
        else {
          placedDetail.placeRight(prevDet);
        }

        prevDet = placedDetail;
      }
    }

    return placedDetails;
  }

  protected void print(Map<String, List<Detail>> detailsBySize) {

    for (Map.Entry<String, List<Detail>> entry : detailsBySize.entrySet()) {
      Map<DetailType,Set<String>> details = new HashMap<DetailType, Set<String>>();
      for (Detail d : entry.getValue()) {
        if (!details.containsKey(d.getDetailType())) {
          details.put(d.getDetailType(), new LinkedHashSet<String>());
        }
        details.get(d.getDetailType()).add(d.getName());
      }
      int d1 = Math.round(1000f*entry.getValue().get(0).getSize()[0]);
      int d2 = Math.round(1000f*entry.getValue().get(0).getSize()[1]);
      BigDecimal s = new BigDecimal(d1 * d2).divide(new BigDecimal(1000000), 2, RoundingMode.HALF_EVEN).abs();
      BigDecimal allS = s.multiply(new BigDecimal(entry.getValue().size()));
      System.out.println(entry.getKey() + ";" + entry.getValue().size() + ";" +  s +  ";" + allS + ";" + details);
    }
    

  }

  public Map<String, List<Detail>> groupBySize(List<Detail> details, Map<DetailType, List<Detail>> detByType) {
    Map<String, List<Detail>> detBySize = new LinkedHashMap<String, List<Detail>>();

    float S = 0f;
    float L = 0f;

    for (Detail d : details) {

      if (d.getDetailType() != DetailType.ROOM_WALL) {
        float[] sizes = d.getSize();
        float x = Math.abs(sizes[0]);
        float y = Math.abs(sizes[1]);
        float s = x * y;
        System.out.println(d.getDetailType().name() + "," + d.getName() + "," + Math.round(x * 1000f) + "," + Math.round(y * 1000f) + "," + s);
        S += s;
        L += 2f*x + 2f*y;
        detByType.get(d.getDetailType()).add(d);

        int d1 = Math.round(x *1000f);
        int d2 = Math.round(y *1000f);

        String sizeKey = d1 + "x" + d2;

        if (!detBySize.containsKey(sizeKey)) {
          detBySize.put(sizeKey, new ArrayList<Detail>());
        }

        detBySize.get(sizeKey).add(d);

      }

    }

    System.out.println("S = " + S + ", onePlate=" + 2.8 * 2.078);
    System.out.println("L = " + L);
    return detBySize;
  }

  protected void writeSvg(Map<String, List<Detail>> detBySize) throws IOException {
    String rect = "<rect x=\"%1$s\" y=\"%2$s\" width=\"%3$s\" height=\"%4$s\" style=\"fill:green;stroke:black;stroke-width:1;opacity:0.5\"/>";

    String text = "<text id=\"TextElement\" x=\"%1$s\" y=\"%2$s\" style=\"font-family:Verdana;font-size:24\">%3$s</text>\n";

    String svg = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
               "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
               "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
               "\n" +
               "<svg width=\"100%\" height=\"100%\" version=\"1.1\"\n" +
               "xmlns=\"http://www.w3.org/2000/svg\">\n" +
               "\n" +
               "%SVG%" +
               "\n" +
               "</svg>";


    int x = 20;

    StringBuffer body = new StringBuffer();

    for (Map.Entry<String, List<Detail>> entry : detBySize.entrySet()) {
      String s = entry.getKey() + " = " + entry.getValue().size() + "  ";


      for (Detail d : entry.getValue()) {
        s += (d.getDetailType() + "/" + d.getName() + ", ");
      }

      if (entry.getValue().size() != 0) {
        Detail d = entry.getValue().get(0);
        float[] sizes = d.getSize();
        float s1 = Math.abs(sizes[0]) * 1000f;
        float s2 = Math.abs(sizes[1]) * 1000f;
        int d1 = Math.round(s1) > Math.round(s2) ? Math.round(s1) : Math.round(s2);
        int d2 = Math.round(s1) > Math.round(s2) ? Math.round(s2) : Math.round(s1);
        int y = 10;
        for (Detail dd : entry.getValue()) {
          body.append(String.format(rect, y , x+10, d1/10, d2/10));
          y += (d1/10 + 10);
        }

        x += (d2/10 + 30);
      }

      body.append(String.format(text, 10, x, s));
      x += 10;
    }


    body.append(String.format(rect, 10 , x+10,  280, 207));
    body.append(String.format(rect, 300 , x+10, 280, 207));
    body.append(String.format(rect, 590 , x+10, 280, 207));

    FileWriter wr = new FileWriter("list.svg");
    wr.write(svg.replaceAll("%SVG%", body.toString()));
    wr.close();
  }
}
