package dbg.electronics.robodrv.mcu;

import dbg.electronics.robodrv.util.BinUtils;
import junit.framework.TestCase;

public class RegBitSetterTest extends TestCase {


    public void testSetBits() throws Exception {


        RegBitSetter regBitSetter = new RegBitSetter();

        regBitSetter.set(1, 0);
        regBitSetter.set(2, 1);
        regBitSetter.set(5, 1);
        regBitSetter.set(6, 0);

        int res = regBitSetter.apply(BinUtils.asNumber("01100111"));

        assertEquals("00100101", BinUtils.asString(res, 8));

    }



}
