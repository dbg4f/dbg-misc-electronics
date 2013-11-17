package dbg.electronics.robodrv.mcu;

import java.io.IOException;

public class RegBitSetter {

    int[] bits = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};

    public RegBitSetter set(int bit, int value) {

        if (bit < 0 || bit > 7) {
            throw new IllegalArgumentException("bits must be in interval [0..7] " + bit);
        }

        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("bit value must be in interval [0..1] " + value);
        }

        bits[bit] = value;

        return this;
    }

    public void applyRegValue(McuRegisterAccess registerAccess, M16Reg reg) throws InterruptedException, IOException, McuCommunicationException {
        registerAccess.writeReg(reg, apply(registerAccess.readReg(reg)));
    }

    public void applyRegValueNoRead(McuRegisterAccess registerAccess, M16Reg reg) throws InterruptedException, IOException, McuCommunicationException {
        registerAccess.writeReg(reg, apply(0));
    }

    public int apply(int value) throws InterruptedException, IOException, McuCommunicationException {

        for (int i=0; i<bits.length; i++) {

            int mask = 1 << (bits.length - i - 1);

            if (bits[i] == 1) {
                value |= mask;
            }
            else if (bits[i] == 0) {
                value &= (~mask);
            }
        }

        return value;

    }


}
