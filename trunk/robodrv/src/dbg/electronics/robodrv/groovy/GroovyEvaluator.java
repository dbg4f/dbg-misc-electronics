package dbg.electronics.robodrv.groovy;

import dbg.electronics.robodrv.graphics.TextCommandEvaluator;
import dbg.electronics.robodrv.mcu.M16Reg;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.util.Date;

public class GroovyEvaluator implements TextCommandEvaluator {

    private Functions functions;


    public void setFunctions(Functions functions) {
        this.functions = functions;
    }

    GroovyShell shell;

    public GroovyEvaluator() {

        Binding binding = new Binding();

        //binding.setProperty("t", this);

        CompilerConfiguration configuration = new CompilerConfiguration();

        configuration.setScriptBaseClass(Functions.class.getName());

        ImportCustomizer customizer = new ImportCustomizer();

        customizer.addStaticStars(M16Reg.class.getName());

        configuration.addCompilationCustomizers(customizer);

        shell = new GroovyShell(binding, configuration);

    }

    public static void main(String[] args) {

        GroovyEvaluator evaluator = new GroovyEvaluator();

        evaluator.evaluate("time11()");

    }


    @Override
    public String evaluate(String command) {
        try {
            //command = "t.time1";
            return String.valueOf(shell.evaluate(command));
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }


}
