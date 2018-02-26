package code.ponfee.view.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import code.ponfee.commons.collect.Collects;
import code.ponfee.commons.io.Files;
import code.ponfee.commons.web.WebUtils;

/**
 * 权限拦截器
 * @author fupf
 */
public class AuthenticateInterceptor extends HandlerInterceptorAdapter {

    private static final String URL_PREFIX = "";

    private static Logger logger = LoggerFactory.getLogger(AuthenticateInterceptor.class);

    /**
     * 执行方法前进行权限的校验拦截
     * @throws Exception 
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*if (handler instanceof ResourceHttpRequestHandler) {
            return super.preHandle(request, response, handler);
        }*/
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        HandlerMethod hm = (HandlerMethod) handler;
        Class<?> controller = hm.getBean().getClass();
        Method method = hm.getMethod();

        Set<Rule> rules = new HashSet<Rule>();
        Authority authC = controller.getAnnotation(Authority.class);
        Authority authM = method.getAnnotation(Authority.class);
        if (authC != null) rules.addAll(Arrays.asList(authC.rule()));
        if (authM != null) rules.addAll(Arrays.asList(authM.rule()));

        if (rules.isEmpty() || rules.contains(Rule.NON)) return true;

        String token = WebUtils.getSessionTrace();
        if (!rules.contains(Rule.NON) && StringUtils.isBlank(token)) {
            // 校验token是否存在
            logger.warn("get user token fail");
        } else if (rules.contains(Rule.URL)) {
            // 验证url权限
            for (String url : resolveMapping(controller, method)) {
                if (doAuthenticate(token, url)) return true;
            }
        } else {
            return true;
        }

        Type type = (authM != null && authM.type() != null) ? authM.type() : authC.type();
        String fail = (authM != null && authM.fail() != null) ? authM.fail() : authC.fail();
        permissionDenied(request, response, type, fail);
        return false;
    }

    private boolean doAuthenticate(String token, String url) {
        // 1、校验token
        // 2、根据token获取用户信息
        // 3、校验用户是否有url对应的权限
        return true;
    }

    /**
     * 无效权限的处理
     */
    private void permissionDenied(HttpServletRequest req, HttpServletResponse resp, Type type, String fail) {
        resp.setCharacterEncoding(Files.UTF_8);
        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
            switch (type) {
                case JSON:
                case HTML:
                case PLAIN:
                    resp.setContentType(type.media() + ";charset=" + Files.DEFAULT_CHARSET);
                    writer.print(fail);
                    writer.flush();
                    writer.close();
                    break;
                case TOP:
                    resp.setContentType("text/html;charset=" + Files.DEFAULT_CHARSET);
                    writer.print("<script type=\"text/javascript\">");
                    writer.print("top.location.href='" + req.getContextPath() + fail + "';");
                    writer.print("</script>");
                    writer.flush();
                    writer.close();
                    break;
                case ALERT:
                    resp.setContentType("text/html;charset=" + Files.DEFAULT_CHARSET);
                    writer.print("<script type=\"text/javascript\">");
                    writer.print("alert('" + fail + "');history.back();");
                    writer.print("</script>");
                    writer.flush();
                    writer.close();
                    break;
                case REDIRECT:
                    resp.sendRedirect(req.getContextPath() + fail);
                    break;
                case FORWARD:
                    req.getRequestDispatcher(fail).forward(req, resp);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    break;
            }
        } catch (IOException e) {
            logger.warn("response writer exception", e);
        } catch (ServletException e) {
            logger.warn("request forward exception", e);
        } finally {
            if (writer != null) writer.close();
        }
    }

    /**
     * 解析
     * @param clazz
     * @param method
     * @param isContainEntrust
     * @return
     */
    public static Set<String> resolveMapping(Class<?> clazz, Method method) {
        Set<String> urls = new HashSet<String>();

        // 权限
        String[] blank = { "" };
        RequestMapping am = clazz.getAnnotation(RequestMapping.class);
        RequestMapping mm = method.getAnnotation(RequestMapping.class);
        String[] actions = (am != null && am.value().length > 0) ? am.value() : blank;
        String[] methods = (mm != null && mm.value().length > 0) ? mm.value() : blank;
        String params = StringUtils.join(Collects.concat(am.params(), mm.params()), "&");
        String url = null;
        for (String a : actions) {
            for (String m : methods) {
                url = a + m + (params.length() == 0 ? "" : "?" + params);
                if (url.startsWith(URL_PREFIX)) url = url.substring(URL_PREFIX.length());
                urls.add(url);
            }
        }

        // 委托权限
        Authority authC = clazz.getAnnotation(Authority.class);
        Authority authM = method.getAnnotation(Authority.class);
        if (authC != null) Collections.addAll(urls, authC.entrust());
        if (authM != null) Collections.addAll(urls, authM.entrust());

        return urls;
    }

}
