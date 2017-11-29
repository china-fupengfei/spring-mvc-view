package code.ponfee.view.web;

import java.io.File;
import java.util.Locale;
import org.springframework.web.servlet.view.JstlView;

/**
 * <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
 *   <property name="viewClass" value="code.ponfee.view.web.CustomelView" />
 *   <property name="prefix" value="/WEB-INF/view/jsp/" />
 *   <property name="suffix" value=".jsp" />
 *   <property name="order" value="1" />
 * </bean>
 * @author Ponfee
 */
public class CustomelView extends JstlView {

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        File file = new File(this.getServletContext().getRealPath("/") + getUrl());
        return file.exists();
    }
}
