package jtdiprog;

import jprog.comm.*;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException {

        LOG.logger.info("jprog started");

        main_prog();

    }


    public static void main_prog() throws IOException, InterruptedException {

        byte[] code = readFile("C:\\Projects\\repos\\mc\\pwmBasics\\pwmbasics.hex");

        LOG.logger.info("code = " + HEX.dumpHex(code));

        JftdiIo io =new JftdiIo();

        ProgAlgorithm alg = new ProgAlgorithm(io);

        alg.start();

        Commands cmd = new Commands(alg);

        cmd.sync();

        cmd.writeFuse(HEX.parseHexByte("6C"));

        readExtraCfg(cmd);


//        cmd.erase();

//        writeEeprom(code, cmd);


    }


    private static byte[] readFile(String fileName) throws IOException {
        HEX hex = new HEX();
        return hex.readFile(fileName);
    }

    private static void writeEeprom(byte[] code, Commands cmd) {
        code = HEX.align32(code);
        LOG.logger.info("Write code = " + HEX.dumpHex(code));
        cmd.writeProgramMemory(code);
        LOG.logger.info("Write complete");
    }


    private static void readExtraCfg(Commands cmd) {
        String signature = HEX.hexLine(cmd.readSignature());
        LOG.logger.info("signature = " + signature);

        String fuse = HEX.formatByte(cmd.readFuse());
        LOG.logger.info("fuse = " + fuse);

        String fuseHi = HEX.formatByte(cmd.readFuseHigh());
        LOG.logger.info("fuseHi = " + fuseHi);

        String fuseExt = HEX.formatByte(cmd.readFuseExt());
        LOG.logger.info("fuseExt = " + fuseExt);

        String lock = HEX.formatByte(cmd.readLock());
        LOG.logger.info("lock = " + lock);
    }


}