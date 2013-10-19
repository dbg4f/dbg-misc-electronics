package dbg.electronics.robodrv.io;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

    public static String time(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

}
