package dbg.electronics.robodrv;

import dbg.electronics.robodrv.head.Orchestrator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {

    public static void main(String[] args) {

        //Orchestrator.getInstance().start();


        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"robodrv-commons.xml"});

        Orchestrator orchestrator = (Orchestrator) context.getBean("orchestrator");

        orchestrator.start();




    }

}
