package code.ponfee.view.web;

import java.io.File;
import java.util.Locale;
import org.springframework.web.servlet.view.JstlView;

public class CustomelView extends JstlView {
    @Override
    public boolean checkResource(Locale locale) throws Exception {
        File file = new File(this.getServletContext().getRealPath("/") + getUrl());
        return file.exists();
    }
}
