package jprog.comm;

public class Commands {

    public static final byte[] SYNC =               HEX.parseData("AC530000");
    public static final byte[] ERASE =              HEX.parseData("AC800000");
    public static final byte[] READ_SIGNATURE =     HEX.parseData("30000000");
    public static final byte[] READ_SIGNATURE_0 =   HEX.parseData("30000000");
    public static final byte[] READ_SIGNATURE_1 =   HEX.parseData("30000100");
    public static final byte[] READ_SIGNATURE_2 =   HEX.parseData("30000200");
    public static final byte[] READ_FUSES =         HEX.parseData("50000000");
    public static final byte[] READ_FUSES_HIGH =    HEX.parseData("58080000");
    public static final byte[] READ_FUSES_EXT =     HEX.parseData("50080000");
    public static final byte[] READ_LOCK =          HEX.parseData("58000000");
    public static final byte[] READ_PROG_MEM =      HEX.parseData("20000000");
    public static final byte[] LOAD_PAGE =          HEX.parseData("40000000");
    public static final byte[] WRITE_PAGE =         HEX.parseData("4C000000");
    public static final byte[] WRITE_FUSES =        HEX.parseData("ACA00000");

    private static final String CORRECT_SYNC_RESPONSE = "\\w\\w\\w\\w53\\w\\w";

    private CommandSender commandSender;

    public Commands(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    private byte[] cmd(byte[] src) {
        return new byte[]{src[0], src[1], src[2], src[3]};
    }

    private void wait(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sync() {
        String result = "";
        for (int i = 0; i < 1; i++) {
            result = HEX.hexLine(commandSender.process(SYNC));
            LOG.logger.info("Sync result = " + result);
            if (result.matches(CORRECT_SYNC_RESPONSE)) {
                return;
            }
        }
        throw new RuntimeException("Sync failed. Last response = " + result);
    }

    public void erase() {
        String result = HEX.hexLine(commandSender.process(ERASE));
        LOG.logger.info("Erase result = " + result);
        wait(100);
    }

    public byte[] readSignature() {

        byte[] signature = new byte[] {0, 0, 0};

        for (int i = 0; i < signature.length; i++) {
            byte[] c = cmd(READ_SIGNATURE);
            c[2] = (byte) i;
            byte[] response = commandSender.process(c);
            signature[i] = response[3];
        }

        //LOG.logger.info("Signature result = " + HEX.hexLine(signature));

        return signature;
    }

    public void writeFuse(byte b){
      byte[] c = cmd(WRITE_FUSES);
      c[3] = b;
      commandSender.process(c);
    }

    public byte readFuse() {
        return commandSender.process(READ_FUSES)[3];
    }

    public byte readFuseHigh() {
        return commandSender.process(READ_FUSES_HIGH)[3];
    }

    public byte readFuseExt() {
        return commandSender.process(READ_FUSES_EXT)[3];
    }

    public byte readLock() {
        return commandSender.process(READ_LOCK)[3];
    }

    public byte[] readProgramMemory(int wordsNum) {
        byte[] result = new byte[wordsNum * 2];

        for (int i = 0; i < wordsNum; i++) {
            byte[] cmdRd = cmd(READ_PROG_MEM);
            cmdRd[2] = (byte) (i & 0xFF);
            cmdRd[1] = (byte) ((i & 0x300) >> 8);
            cmdRd[0] = 0x20;
            result[2 * i] = commandSender.process(cmdRd)[3];
            cmdRd[0] = 0x28;
            result[2 * i + 1] = commandSender.process(cmdRd)[3];

            LOG.logger.info("A:" + i + " " + HEX.hexLine(new byte[] {result[2 * i], result[2 * i + 1]}));

        }

        return result;
    }

    public void loadFlashPage(byte[] bytes, int startPos) {
        byte addr = 0;
        for (int i = startPos; i < (startPos + 32); i += 2, addr++) {
            byte[] cmdLd = cmd(LOAD_PAGE);
            cmdLd[3] = bytes[i];
            cmdLd[2] = addr;
            commandSender.process(cmdLd);
            cmdLd[3] = bytes[i + 1];
            cmdLd[0] = 0x48;
            commandSender.process(cmdLd);

            LOG.logger.info("i = " + i);
        }



    }


    public void writeProgramMemory(byte[] bytes) {
        if ((bytes.length % 32) != 0) {
            bytes = HEX.align32(bytes);
            LOG.logger.info("Buffer was aligned");
        }

        int pages = bytes.length / 32;

        LOG.logger.info("Pages count = " + pages);

        for (int i=0; i<pages; i++) {
            loadFlashPage(bytes, i * 32);
            byte[] cmdWr = cmd(WRITE_PAGE);
            cmdWr[2] = (byte)((i & 0x0F) << 4);
            cmdWr[1] = (byte)((i & 0x30) >> 4);
            commandSender.process(cmdWr);
            LOG.logger.info("Page " + i + "/" + pages + "written");
        }

    }

}
