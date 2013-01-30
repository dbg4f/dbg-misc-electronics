package dbg.electronics.robodrv;

import junit.framework.Assert;
import org.junit.Test;

public class RangeTest {

    @Test
    public void test1() throws OutOfRange {

        Range r1 = new Range(0, 100);
        Range r2 = new Range(-10, 0);

        Assert.assertSame(-5, r1.remapTo(50, r2));
        Assert.assertSame(-9, r1.remapTo(10, r2));

        Assert.assertSame(50, r2.remapTo(-5, r1));
        Assert.assertSame(10, r2.remapTo(-9, r1));

        Assert.assertSame(10, r1.remapTo(10, r1));

    }

}
