package code.ponfee.view.startup;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

public class SpringStartupInitializer implements WebApplicationInitializer {

    @Order(1)
    @Override
    public void onStartup(ServletContext context) {
        // do something to init
        System.out.println("*******************SpringStartupInitializer init*******************");
    }

}
