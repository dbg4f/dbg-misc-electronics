package dbg.electronics.robodrv.groovy;

import groovy.ui.Console;

public class GroovyLauncher {

    public static void main(String[] args) {


        GroovyLauncher launcher = new GroovyLauncher();

        Console console = new Console();
        console.setVariable("var1", launcher);
        console.run();




    }


    public String test1() {
        return "AAA";
    }


}
