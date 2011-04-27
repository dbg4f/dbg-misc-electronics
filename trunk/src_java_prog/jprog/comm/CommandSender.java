/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jprog.comm;

import java.io.IOException;


/**
 *
 * @author Bogdel
 */
public interface CommandSender {

    byte[] process(byte[] command);

}
