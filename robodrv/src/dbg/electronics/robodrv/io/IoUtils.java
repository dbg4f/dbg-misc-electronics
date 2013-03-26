package dbg.electronics.robodrv.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IoUtils {

    public static List<String> readLines(String fileName) throws IOException {

        List<String> lines = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }

}
