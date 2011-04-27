/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dbg.electronics.pinarraysticker;

/**
 *
 * @author Bogdel
 */
public class Scale {
    public static int MM(double value) {
        return (int)((value/25.4)*72.0);
    }
}
