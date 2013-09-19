package dbg.electronics.robodrv.logging;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {


    static class AdcValue {
        Date time;
        int channel;
        int value;

        AdcValue(Date time, int channel, int value) {
            this.time = time;
            this.channel = channel;
            this.value = value;
        }

        @Override
        public String toString() {
            return "AdcValue{" +
                    "time=" + time +
                    ", channel=" + channel +
                    ", value=" + value +
                    '}';
        }
    }




    public static void main(String[] args) throws IOException, ParseException {

        // 2013.09.18 22:50:49.643 [Thread-0] INFO  Test1 - ADC [0] 212 -> 204

        LogParser parser = new LogParser();

        List<String> lines = parser.readLines();

        List<AdcValue> adcValues = new ArrayList<AdcValue>();

        //Pattern regex = Pattern.compile("(\\d{4}.\\d{2}.\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}) *. INFO  Test1 - ADC .*");
        Pattern regex = Pattern.compile("(\\d{4}.\\d{2}.\\d{2} \\d{2}:\\d{2}.\\d{2}.\\d{3}).* INFO  Test1 - ADC \\[(\\d)\\] \\d* -> (\\d*)");

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

        for (String line : lines) {

            Matcher matcher = regex.matcher(line);

            if (matcher.matches()) {
                adcValues.add(new AdcValue(
                        df.parse(matcher.group(1)),
                        Integer.valueOf(matcher.group(2)).intValue(),
                        Integer.valueOf(matcher.group(3)).intValue()));
            }

        }


        int[] channelValues = new int[]{0, 0, 0, 0};

        long startTs = adcValues.get(0).time.getTime();

        FileWriter wr = new FileWriter("docs/motor.csv");

        for (AdcValue adcValue : adcValues) {

            channelValues[adcValue.channel] = adcValue.value;

            long t = adcValue.time.getTime() - startTs;

            wr.write(String.format("%08d,%d,%d,%d,%d\n", t, channelValues[0], channelValues[1], channelValues[2], channelValues[3]));

        }

        wr.close();


    }

    private List<String> readLines() throws IOException {
        InputStream fis;
        BufferedReader br;
        String line;

        List<String> lines = new ArrayList<String>();

        fis = new FileInputStream("docs/motor.txt");
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        while ((line = br.readLine()) != null) {
            if (line.length() > 0) {
                lines.add(line);
            }
        }
        br.close();

        return lines;
    }

}
