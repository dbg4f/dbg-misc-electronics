package dbg.electronics.robodrv.utils;

import dbg.electronics.robodrv.util.BinUtils;
import junit.framework.TestCase;

public class BinUtilsTest extends TestCase {

    public void testBinUtils() throws Exception {

        assertEquals(0x00, BinUtils.asNumber("00000000"));
        assertEquals(0x01, BinUtils.asNumber("00000001"));
        assertEquals(0x02, BinUtils.asNumber("00000010"));
        assertEquals(0x03, BinUtils.asNumber("00000011"));
        assertEquals(0x55, BinUtils.asNumber("01010101"));

        assertEquals("01010101", BinUtils.asString(0x55, 8));

    }



}
