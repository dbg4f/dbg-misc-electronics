package dbg.electronics.robodrv.groovy;


import dbg.electronics.robodrv.mcu.McuRegisterAccess;
import groovy.lang.Script;

import java.util.Date;

public class Functions extends Script {

    private McuRegisterAccess mcuRegisterAccess;

    public void setMcuRegisterAccess(McuRegisterAccess mcuRegisterAccess) {
        this.mcuRegisterAccess = mcuRegisterAccess;
    }

    public String time() {

        String t = new Date().toString();

        System.out.println("t = " + t);

        return t;
    }


    @Override
    public Object run() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
