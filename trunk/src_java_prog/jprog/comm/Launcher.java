package jprog.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: Bogdel
 * Date: 19.12.2007
 * Time: 21:18:02
 * To change this template use File | Settings | File Templates.
 */
public class Launcher {

    public static boolean verbose = true;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        
        
        LOG.logger.info("jprog started");

        System.setProperty("java.library.path", ".");
        System.loadLibrary("jprog");

        
        main_prog(args);
        
        //main_pwm(args);
    }
    
    
    public static void test1(Wrapper2 w, ProgAlgorithm alg) throws IOException {
        alg.sleep(100);
        
        w.write(new byte[] {(byte)0xFF}); // switch on 2nd mode
        
        alg.sleep(2000);

        w.write(new byte[] {(byte)0x5}); // count
        
        alg.sleep(100);
            
        w.write(new byte[] {(byte)0xFF}); // pwm width
        
        alg.sleep(3000);
        
    }

    public static void test2(Wrapper2 w, ProgAlgorithm alg) throws IOException {
        w.write(new byte[] {(byte)0x00}); // switch on 1st mode
        alg.sleep(100);

        w.write(new byte[] {(byte)0x80}); // width
        
        alg.sleep(2000);
            
        w.write(new byte[] {(byte)0xFF}); // pwm width
        
        alg.sleep(2000);
        
    }
    
    public static void test3(Wrapper2 w, ProgAlgorithm alg) throws IOException {
        
        w.write(new byte[] {(byte)0x00}); // switch on 1st mode
        alg.sleep(100);

        
        w.write(new byte[] {(byte)0x00}); 
        
        InputStreamReader isr = new InputStreamReader( System.in );

        BufferedReader stdin = new BufferedReader( isr );

       
        for (;;) {
            System.out.print( "new pwm: " );
            String input = stdin.readLine();
            byte b = (byte) Integer.valueOf(input).intValue();
            w.write(new byte[] {(byte)b}); // non-steppin PWM
            if (b == 77) {
                break;
                
            }
        }
        
    }
    

    
    public static void test4(Wrapper2 w, ProgAlgorithm alg) throws IOException {
        
        w.write(new byte[] {(byte)0x00}); // switch on 1st mode
        alg.sleep(100);

        
        w.write(new byte[] {(byte)0x00}); 
        
        InputStreamReader isr = new InputStreamReader( System.in );

        BufferedReader stdin = new BufferedReader( isr );

       
        for (;;) {
            System.out.print( "new pwm: " );
            
            for (int i=110; i<255; i+=1) {
                w.write(new byte[] {(byte)i}); // non-steppin PWM
                alg.sleep(50);
            }

            
            alg.sleep(2000);
            
            
            for (int i=255; i>110; i-=1) {
                w.write(new byte[] {(byte)i}); // non-steppin PWM
                alg.sleep(50);
            }
            
            
        }
        
    }
    
    
    public static void main_pwm(String[] args) throws IOException, InterruptedException {
        
        LOG.logger.info("pwm mode");
        
        Wrapper2 w =new Wrapper2();
        
        ProgAlgorithm alg = new ProgAlgorithm(w);
        
        w.open("COM1");        
        
        //alg.waitReset();

        //alg.sleep(100);
        
                
        //alg.pumpOut();
        
        
        test4(w, alg);

        
        
               
        //alg.sleep(2000);
        
        //List resp = alg.blockingIO(new byte[] {(byte)0x80}, 10000);
        
        //alg.sleep(100);
        
        //resp = alg.blockingIO(new byte[] {(byte)0x20}, 10000);
        
        //alg.sleep(100);
        
        //resp = alg.blockingIO(new byte[] {(byte)0x80}, 10000);
        
        
        
        //w.write(new byte[] {(byte)0xFF}); // non-steppin PWM
        //alg.sleep(10);
       
        /*
        //alg.sleep(4000);
        
        */
       
        //w.write(new byte[] {(byte)0xdd}); // non-steppin PWM
        alg.sleep(100);

        //w.write(new byte[] {(byte)0x03}); // non-steppin PWM
        alg.sleep(100);

        //w.write(new byte[] {(byte)0x03}); // non-steppin PWM
        alg.sleep(100);

        
        //LOG.logger.info("PWM started");
        
        //alg.sleep(20000);
        //List response = alg.blockingIO(new byte[]{(byte)0x01}, 2000);
        
        //LOG.logger.info("PWM resp: " + HEX.dumpHex(alg.unbox(response)));
        
        
        
    }
    
    
    public static void main_prog(String[] args) throws IOException, InterruptedException {

        byte[] code = readFile("C:\\Projects\\repos\\mc\\pwmBasics\\pwmbasics.hex");
        
        LOG.logger.info("code = " + HEX.dumpHex(code));

        Wrapper2 w =new Wrapper2();
        
        ProgAlgorithm alg = new ProgAlgorithm(w);
        
        w.open("COM6");        
        
        alg.start();

        Commands cmd = new Commands(alg);
       
        cmd.sync();
        
        readExtraCfg(cmd);
        
        //cmd.erase();

        //writeEeprom(code, cmd);
        
        //////////////////writeTest(cmd);

        //readTest(cmd);

    }

    private static void readTest(Commands cmd) {
        byte[] mem = cmd.readProgramMemory(512);

        String dump = HEX.dumpHex(mem);

        LOG.logger.info("dump = " + dump);

        System.out.println("dump = " + dump);
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
    
    private static void writeTest(Commands cmd) {
        byte[] m  = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};

        m = HEX.align32(m);

        String mm = HEX.dumpHex(m);

        System.out.println("mm = " + mm);

        cmd.writeProgramMemory(m);

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
