package dbg.electronics.robodrv.util;

public class Circuit {

    static boolean toBoolean(int value) {
        if (value <0 || value > 1) {
            throw new IllegalArgumentException(value + " can be [0..1]");
        }
        return value != 0;
    }

    static int toInt(boolean value) {
        return value ? 1 : 0;
    }

    interface LogicFunction2 {
        int apply(int a, int b);
    }

    interface LogicFunction1 {
        int apply(int a);
    }

    class NOT implements LogicFunction1 {
        public int apply(int a) {
            return toInt(!toBoolean(a));
        }
    }

    class AND implements LogicFunction2 {

        public int apply(int a, int b) {
            return toInt(toBoolean(a) & toBoolean(b));
        }
    }

    class NAND implements LogicFunction2 {

        public int apply(int a, int b) {
            return toInt(!(toBoolean(a) & toBoolean(b)));
        }
    }

    class XOR implements LogicFunction2 {

        public int apply(int a, int b) {
            return toInt(toBoolean(a) ^ toBoolean(b));
        }
    }


    public static void main(String[] args) {


        new Circuit().testFullModes();

        System.out.println(new Circuit().getResultH(0, 0));
        System.out.println(new Circuit().getResultH(1, 0));
        System.out.println(new Circuit().getResultH(0, 1));
        System.out.println(new Circuit().getResultH(1, 1));

    }



    public String getResultXOR(int a, int b) {

        return "XOR(" + a + ", " + b +") = " + (new XOR().apply(a, b));

    }

    public String getResultH(int pwm, int rev) {


    /*
    int nsd1 = new XOR().apply(pwm, rev);
    int nsd2 = nsd1;

    int pwm1 = pwm;
    int pwm2 = new NOT().apply(pwm);
    */

        int nsd1 = pwm;
        int nsd2 = pwm;

        int pwm1 = new XOR().apply(pwm, rev);
        int pwm2 = new NOT().apply(pwm1);



        int h1 = getDrvH(pwm1, nsd1);
        int l1 = getDrvL(pwm1, nsd1);
        int h2 = getDrvH(pwm2, nsd2);
        int l2 = getDrvL(pwm2, nsd2);

        return "DRV(PWM=" + pwm + ", REV=" + rev +"): " +
                " H1=" + h1 + " L1=" + l1 +
                " H2=" + h2 + " L2=" + l2 + " " + getMode(h1, l1, h2, l2);

    }

    public String getResultDrv(int in, int nsd) {

        return "DRV(IN=" + in + ", !SD=" + nsd +"):  H=" + getDrvH(in, nsd) + " L=" + getDrvL(in, nsd);

    }

    private int getDrvH(int in, int nsd) {
        return (new AND().apply(in, nsd));
    }

    private int getDrvL(int in, int nsd) {
        return (new AND().apply(new NOT().apply(in), nsd));
    }


    enum HBridgeMode {

        LEFT(new int[][] {
                {1, 0, 0, 1}
        }
        ),
        RIGHT(new int[][] {
                {0, 1, 1, 0}
        }
        ),
        SHORT_CIRCUIT(new int[][] {
                {1, 1, 0, 0},
                {1, 1, 1, 0},
                {1, 1, 0, 1},
                {0, 0, 1, 1},
                {0, 1, 1, 1},
                {1, 0, 1, 1},
                {1, 1, 1, 1}
        }
        ),
        BRAKE(new int[][] {
                {1, 0, 1, 0},
                {0, 1, 0, 1},
        }
        ),
        COAST(new int[][] {
                {0, 0, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 1, 0},
                {0, 1, 0, 0},
                {1, 0, 0, 0},
        }
        );

        public final int matchedModes[][];

        HBridgeMode(int[][] matchedModes) {
            this.matchedModes = matchedModes;
        }


    }


    public void testFullModes() {

        for (int i=0; i<=15; i++) {

            int h1 = ((i & (1 << 3)) == 0) ? 0 : 1;
            int l1 = ((i & (1 << 2)) == 0) ? 0 : 1;
            int h2 = ((i & (1 << 1)) == 0) ? 0 : 1;
            int l2 = ((i & (1 << 0)) == 0) ? 0 : 1;

            System.out.println(h1 + " " + l1 + " " + h2 + " " + l2 + " " + getMode(h1, l1, h2, l2));

            drawMode(h1, l1, h2, l2);

            System.out.println();

        }


    }




    private void drawMode(int h1, int l1, int h2, int l2) {
        System.out.println((h1 == 0 ? "*" : "|") + " " + (h2 == 0 ? "*" : "|"));
        System.out.println("---");
        System.out.println((l1 == 0 ? "*" : "|") + " " + (l2 == 0 ? "*" : "|"));
    }

    private HBridgeMode getMode(int h1, int l1, int h2, int l2) {

        for (HBridgeMode bridgeMode : HBridgeMode.values()) {
            for (int[] mode : bridgeMode.matchedModes) {
                if (h1 == mode[0] && l1 == mode[1] && h2 == mode[2] && l2 == mode[3]) {
                    return bridgeMode;
                }
            }
        }

        throw new IllegalArgumentException("HBridgeMode combination not found " + h1 + " " + l1 + " " + h2 + " " + l2);


    }






}


