package code.ponfee.view.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * web容器启动后执行
 * 
 * @author Ponfee
 */
public class WebappStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("*******************AppServletContextListener init*******************");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }

}
