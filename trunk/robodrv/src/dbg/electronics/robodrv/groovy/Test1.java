package dbg.electronics.robodrv.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.io.IOException;

/**
 * @author Dmitri Bogdel
 */
public class Test1 {


    public static void main(String[] args) throws IOException {

        Binding binding = new Binding();

        binding.setProperty("t", new Functions());

        CompilerConfiguration configuration = new CompilerConfiguration();

        configuration.setScriptBaseClass(Functions.class.getName());

        ImportCustomizer customizer = new ImportCustomizer();

        customizer.addStaticStars(Reg.class.getName());

        configuration.addCompilationCustomizers(customizer/*, new ASTTransformationCustomizer()*/);


        GroovyShell shell = new GroovyShell(binding, configuration);


        shell.evaluate("show RA");

    }

    /**
     * @author Dmitri Bogdel
     */
    public static enum Reg {

        RA,
        RB

    }

    /**
     * @author Dmitri Bogdel
     */
    public static class Functions extends Script {




        public static int staticAdd(int x1, int x2) {
            return x1 + x2;
        }

        public int add(int x1, int x2) {
            return x1 + x2;
        }

        public String show(Reg reg) {
            System.out.println("reg = " + reg);
            return reg.name();
        }


        @Override
        public Object run() {
            return null;
        }
    }
}