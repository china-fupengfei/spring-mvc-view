package code.ponfee.view.auth;

import java.io.IOException;
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
 *
 * authentication  认证
 * authorization   鉴权
 *
 * @author fupf
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static final String URL_PREFIX = "";
    private static final String[] BLANK_STRING_ARRAY = { "" };

    private static Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    /**
     * 执行方法前进行权限的校验拦截
     * @throws Exception 
     */
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) 
        throws Exception {
        /*if (handler instanceof ResourceHttpRequestHandler) {
            return super.preHandle(request, response, handler);
        }*/
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(req, resp, handler);
        }

        HandlerMethod hm = (HandlerMethod) handler;
        Class<?> controller = hm.getBean().getClass();
        Method method = hm.getMethod();

        Set<Rule> rules = new HashSet<>();
        Authority authC = controller.getAnnotation(Authority.class);
        Authority authM = method.getAnnotation(Authority.class);
        if (authC != null) {
            rules.addAll(Arrays.asList(authC.rule()));
        }
        if (authM != null) {
            rules.addAll(Arrays.asList(authM.rule()));
        }

        if (rules.isEmpty() || rules.contains(Rule.NON)) {
            return true;
        }

        String token = WebUtils.getSessionTrace(req);
        if (!rules.contains(Rule.NON) && StringUtils.isEmpty(token)) {
            // 校验token是否存在
            logger.warn("get user token fail");
        } else if (rules.contains(Rule.URL)) {
            // 验证url权限
            for (String url : resolveMapping(controller, method)) {
                if (doAuthorization(token, url)) {
                    return true;
                }
            }
        } else {
            return true;
        }

        Type type = (authM != null && authM.type() != null) 
                    ? authM.type() : authC.type();

        String fail = (authM != null && authM.fail() != null) 
                    ? authM.fail() : authC.fail();

        permissionDenied(req, resp, type, fail);

        return false;
    }

    private boolean doAuthorization(String token, String url) {
        // TODO
        // 1、校验token
        // 2、根据token获取用户信息
        // 3、校验用户是否有url对应的权限
        return true;
    }

    /**
     * handle permission denied
     * @param req
     * @param resp
     * @param type
     * @param fail
     */
    private void permissionDenied(HttpServletRequest req, HttpServletResponse resp, 
                                  Type type, String fail) {
        try {
            resp.setCharacterEncoding(Files.UTF_8);
            switch (type) {
                case JSON:
                case HTML:
                case PLAIN:
                    WebUtils.response(resp, type.media(), fail, Files.UTF_8);
                    break;
                case TOP:
                    fail = "<script>top.location.href='" + req.getContextPath() + fail + "';</script>";
                    WebUtils.response(resp, "text/html", fail, Files.UTF_8);
                case ALERT:
                    fail = "<script>alert('" + fail + "');history.back();</script>";
                    WebUtils.response(resp, "text/html", fail, Files.UTF_8);
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
            logger.error("response writer exception", e);
        } catch (ServletException e) {
            logger.error("request forward exception", e);
        }
    }

    /**
     * resolve the request mapping
     * @param clazz
     * @param method
     * @return
     */
    public static Set<String> resolveMapping(Class<?> clazz, Method method) {
        Set<String> urls = new HashSet<>();

        // 权限
        RequestMapping am = clazz.getAnnotation(RequestMapping.class);
        RequestMapping mm = method.getAnnotation(RequestMapping.class);

        String[] actions = (am != null && am.value().length > 0) 
                           ? am.value() : Arrays.copyOf(BLANK_STRING_ARRAY, 1);

        String[] methods = (mm != null && mm.value().length > 0) 
                           ? mm.value() : Arrays.copyOf(BLANK_STRING_ARRAY, 1);

        String params = StringUtils.join(Collects.concat(String[]::new, am.params(), mm.params()), "&");
        String url = null;
        for (String a : actions) {
            for (String m : methods) {
                url = a + m + (params.length() == 0 ? "" : "?" + params);
                if (!URL_PREFIX.isEmpty() && url.startsWith(URL_PREFIX)) {
                    url = url.substring(URL_PREFIX.length());
                }
                urls.add(url);
            }
        }

        // 委托权限
        Authority authC = clazz.getAnnotation(Authority.class);
        Authority authM = method.getAnnotation(Authority.class);
        if (authC != null) {
            Collections.addAll(urls, authC.entrust());
        }
        if (authM != null) {
            Collections.addAll(urls, authM.entrust());
        }

        return urls;
    }

}
