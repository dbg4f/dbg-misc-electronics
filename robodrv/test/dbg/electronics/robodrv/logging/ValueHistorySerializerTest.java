package dbg.electronics.robodrv.logging;

import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class ValueHistorySerializerTest extends TestCase {


    public void testSerialize() throws Exception {

        ValueWithHistory v1 = new ValueWithHistory();
        ValueWithHistory v2 = new ValueWithHistory();

        v1.setName("v1");
        v2.setName("v2");


        v1.update(100, 1);
        v1.update(200, 2);
        v1.update(300, 3);

        v2.update(1000, 1);
        v2.update(5000, 4);

        v1.freeze();
        v2.freeze();

        v1.setSnapshot(v1.generateCurrentSeries(5));
        v2.setSnapshot(v2.generateCurrentSeries(5));

        ValueHistorySerializer serializer = new ValueHistorySerializer();
        serializer.setValues(Arrays.asList(v1, v2));

        serializer.save("test1");

        v1.getSnapshot().clear();
        v2.getSnapshot().clear();

        serializer.restore("test1");

        List<TimeSeries> s1 = v1.getCurrentSeries();
        List<TimeSeries> s2 = v2.getCurrentSeries();

        System.out.println("s1 = " + s1);
        System.out.println("s2 = " + s2);

        /*
        s1 = [TimeSeries{time=1, value=0}, TimeSeries{time=2, value=300}, TimeSeries{time=3, value=200}, TimeSeries{time=4, value=100}]
        s2 = [TimeSeries{time=1, value=5000}, TimeSeries{time=2, value=5000}, TimeSeries{time=3, value=5000}, TimeSeries{time=4, value=1000}]

        time,v1,v2,
        1,0,5000,
        2,300,5000,
        3,200,5000,
        4,100,1000,

         */

        assertEquals(s1.get(0).time, 1);
        assertEquals(s2.get(0).time, 1);
        assertEquals(s1.get(0).value, 0);
        assertEquals(s2.get(0).value, 5000);


        assertEquals(s1.get(3).time, 4);
        assertEquals(s2.get(3).time, 4);
        assertEquals(s1.get(3).value, 100);
        assertEquals(s2.get(3).value, 1000);

    }
}
