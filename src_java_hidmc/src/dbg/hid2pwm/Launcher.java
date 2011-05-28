package dbg.hid2pwm;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Launcher {

  private static final Logger log = Logger.getLogger(Launcher.class);

  public static void main(String[] args) throws Exception {

    XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("hidmc-components.xml"));

    PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
    cfg.setLocation(new ClassPathResource("hidmc.properties"));
    cfg.postProcessBeanFactory(beanFactory);    

    Host host = (Host)beanFactory.getBean("host");

    log.info("host = " + host);
  }

}
