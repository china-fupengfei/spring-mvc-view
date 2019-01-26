package code.ponfee.view.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import code.ponfee.commons.json.Jsons;
import code.ponfee.commons.model.Result;

/**
 * 全局异常处理
 * //----------------------------------
 *    <!-- 异常、错误处理 -->
 *    <servlet>
 *        <servlet-name>globalExceptionHandler</servlet-name>
 *        <servlet-class>com.fangdd.auth.web.GlobalExceptionHandler</servlet-class>
 *        <init-param>
 *            <param-name>handlerType</param-name>
 *            <param-value>application/json</param-value>
 *        </init-param>
 *    </servlet>
 *    <servlet-mapping>
 *        <servlet-name>globalExceptionHandler</servlet-name>
 *        <url-pattern>/globalExceptionHandler</url-pattern>
 *    </servlet-mapping>
 *    <error-page>
 *        <error-code>404</error-code>
 *        <location>/globalExceptionHandler</location>
 *    </error-page>
 *    <error-page>
 *        <exception-type>java.lang.Throwable</exception-type>
 *        <location>/globalExceptionHandler</location>
 *    </error-page>
 *    <error-page>
 *        <error-code>403</error-code>
 *        <location>/globalExceptionHandler</location>
 *    </error-page>
 *
 * @author fupf
 */
/*@WebServlet(
    name = "code.ponfee.view.web.GlobalExceptionHandler",
    urlPatterns = {"/globalExceptionHandler"},
    initParams = {
        @WebInitParam(name="handlerType", value="application/json")
    },
    asyncSupported=true
)*/
public class GlobalExceptionHandler extends HttpServlet {
    /** */
    private static final long serialVersionUID = 6067653829035388068L;

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String DEFAULT_HANDLER_TYPE = "application/json";

    private String handlerType;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Throwable throwable = (Throwable) req.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) req.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) req.getAttribute("javax.servlet.error.servlet_name");
        String requestUri = (String) req.getAttribute("javax.servlet.error.request_uri");
        //Class<?> message = (Class<?>) req.getAttribute("javax.servlet.error.message");
        //Class<?> type = (Class<?>) req.getAttribute("javax.servlet.error.exception_type");
        logger.info(throwable + "," + statusCode + "'" + servletName + "," + requestUri);

        try {
            if (throwable != null) {
                if (throwable instanceof WebException || throwable instanceof IllegalArgumentException) {
                    logger.info(null, throwable);
                } else {
                    logger.error(null, throwable);
                }
                throw throwable;
            } else {
                throw new WebException(404, "unknown exception");
            }
        } catch (Throwable e) {
            PrintWriter writer;
            switch (handlerType) {
                case "application/json":
                case "text/html":
                case "text/plain":
                    int code;
                    if (e instanceof WebException) {
                        code = ((WebException) e).getCode();
                        if (code < 1) {
                            code = 500;
                        }
                    } else {
                        code = 500;
                    }
                    resp.setContentType(handlerType + ";charset=UTF-8");
                    try {
                        writer = resp.getWriter();
                        writer.print(Jsons.NORMAL.string(Result.failure(code, e.getMessage())));
                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {
                        logger.error(null, ex);
                    }
                    break;
                default:
                    break; // do nothing
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        handlerType = config.getInitParameter("handlerType");
        if (StringUtils.isBlank(handlerType)) handlerType = DEFAULT_HANDLER_TYPE;
    }

}
