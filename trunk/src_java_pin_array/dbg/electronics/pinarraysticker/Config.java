/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dbg.electronics.pinarraysticker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static dbg.electronics.pinarraysticker.Scale.MM;


/**
 *
 * @author Bogdel
 */
public class Config {

    int width = 2;
    int height = 5;
    
    int woffset         = MM(1.3);
    int hoffset         = MM(1.5);
    int fontSize        = MM(2.54);
    int step            = MM(2.54);
    int distance        = MM(3);
    int distanceText    = MM(2);
    int textLen         = MM(15);
    int vstep           = (int)(1.0 * fontSize); 
     
    String[] text;
    
    public void read(String fileName) throws IOException {
      
        Properties p = new Properties();
        p.load(new FileInputStream(fileName));
        
        width = Integer.valueOf(p.getProperty("width"));
        height = Integer.valueOf(p.getProperty("height"));
        text = p.getProperty("text").split(";");
           
    }
    
    public String getText(int row, int col) {
        
        String t = "";
        if (text != null) {
           
            
            
            int pos = col == 0 ? row : 2 * height - row - 1;
            
            t = (pos < text.length) ? text[pos] : ""; 
            
        }
        
        return t;
    }

    
    
}
