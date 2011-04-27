package j3d.builders;

import j3d.primitives.Detail;
import j3d.RandomColor;

import javax.media.j3d.Transform3D;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * DetailsViewFilter
 * <p/>
 *
 * @author Dmitri Bogdel
 */
public class DetailsViewFilter {


  public List<Detail> applyColors(List<Detail> details) {
    for (Detail d : details) {
      d.getBox().setColor(RandomColor.newColor());
    }
    return details;
  }

  public List<Detail> filter(List<Detail> details) {

    List<Detail> filteredDetails = new ArrayList<Detail>(details);

    String fileName = System.getProperty("details.file", "");

    if (!fileName.equalsIgnoreCase("") && new File(fileName).exists()) {

      Set<String> stayDetails = new HashSet<String>();

      try {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while((line = reader.readLine()) != null) {                    
          stayDetails.add(line);
        }

        filteredDetails.clear();

        for (Detail d : details) {
          String det = d.getDetailType() + "/" + d.getName();
          if (stayDetails.contains(det)) {
            filteredDetails.add(d);
            System.out.println("" + det + ", " + d.getOrderedSizesString());
          }
        }

      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }

    return applyColors(filteredDetails);
  }

}
