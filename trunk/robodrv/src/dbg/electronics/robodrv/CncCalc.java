package dbg.electronics.robodrv;

/**
 */
public class CncCalc {

    static class CalibrationCase {

        final double near;
        final double far;
        final double x;
        final double y;

        double a;
        double b;

        CalibrationCase(double near, double far, double x, double y) {
            this.near = near;
            this.far = far;
            this.x = x;
            this.y = y;
        }


        @Override
        public String toString() {
            return "CalibrationCase{" +
                    "near=" + near +
                    ", far=" + far +
                    ", x=" + x +
                    ", y=" + y +
                    ", a=" + a +
                    ", b=" + b +
                    '}';
        }
    }


    /*
    Lnea􀁕 = 86
Lfa􀁕 = 165
0.2145 0.14615 204 52
0.3120 0.2990 157 23
0.4355 0.3687 129 25
0.3715 0.2768 144 80
0.5245 0.3687 100 74
0.2275 0.0866 194 116
0.2805 0.1122 173 136
0.3675 0.1950 144 130
0.4880 0.3062 104 119
0.5255 0.3290 090 116
     */

    static CalibrationCase[] CASES = new CalibrationCase[] {

            new CalibrationCase(0.2145, 0.14615,204, 52 ),
            new CalibrationCase(0.3120, 0.2990, 157, 23 ),
            new CalibrationCase(0.4355, 0.3687, 129, 25 ),
            new CalibrationCase(0.3715, 0.2768, 144, 80 ),
            new CalibrationCase(0.5245, 0.3687, 100, 74 ),
            new CalibrationCase(0.2275, 0.0866, 194, 116),
            new CalibrationCase(0.2805, 0.1122, 173, 136),
            new CalibrationCase(0.3675, 0.1950, 144, 130),
            new CalibrationCase(0.4880, 0.3062, 104, 119),
            new CalibrationCase(0.5255, 0.3290,  90, 116),

};


    public static double NEAR = 86.0;
    public static double FAR = 165.0;

    public static void main(String[] args) {

        for (CalibrationCase aCase : CASES) {
            aCase.a = calcAN(aCase.x, aCase.y);
            aCase.b = calcBN(aCase.x, aCase.y);

            System.out.println("aCase = " + aCase);

        }


    }

    static double calcR(double x, double y) {
        return Math.sqrt(x*x + y*y);
    }

    static double angle(double x, double y) {
        return Math.atan(y/x);
    }

    static double angleBySides(double a, double b, double c) {
        return Math.acos((b*b + c*c - a*a) / (2.0 * b * c));
    }

    static double calcAN(double x, double y) {
        double r = calcR(x, y);
        return angle(x, y) + angleBySides(FAR, NEAR, r);
    }

    static double calcBN(double x, double y) {
        double r = calcR(x, y);
        return angleBySides(r, NEAR, FAR);
    }





}
