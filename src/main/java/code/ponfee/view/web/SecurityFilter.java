package code.ponfee.view.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全字符过滤，对可能攻击的非法字符进行处理，处理的漏洞包括： 
 *  *加密会话（SSL）Cookie 中缺少Secure 属性 
 *  *sql注入 
 *  * 跨站点脚本编制 
 *  *跨站点请求伪造（CSRF）
 * @author fupf
 */
public class SecurityFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private static final String INVALID_PARAM = "invalid_param";

    // sql关键字
    private static final String[] INJ_STRS = (" ' | and |exec |insert |select |delete |update | count "
        + "| * | % | chr | mid | master |truncate | char |declare | ; | or | - | + | , ").split("\\|");

    // 需要拦截的JS字符关键字
    private static final String[] SAFELESS = { "<script", "</script", "<iframe", "</iframe", "<frame",
        "</frame", "set-cookie", "%3cscript", "%3c/script", "%3ciframe", "%3c/iframe", "%3cframe",
        "%3c/frame", "src=\"javascript:", "<body", "</body", "%3cbody", "%3c/body", "javascript:",
        "<img src", " style='", "%20style='", "alert("/* , "<", ">", "</", "/>", "%3c", "%3e", "%3c/", "/%3e" */ };

    @Override
    public void init(FilterConfig cfg) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (!sqlInjectHandler(request) || !xssHandler(request) /* || !csrfHandler(request) */) {
            logger.warn("非法的请求参数：" + request.getAttribute(INVALID_PARAM));
            //forwardErrPage(request, response, "安全校验未通过，请勿输入非法字符！");
            throw new WebException(403, "非法请求");
        } else chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    /**
     * 防止sql注入
     * @param
     * @return
     */
    private boolean sqlInjectHandler(HttpServletRequest request) {
        Iterator<String[]> params = request.getParameterMap().values().iterator();//获取所有的表单参数
        String[] value;
        String str;
        int i, j;
        while (params.hasNext()) {
            value = params.next();
            for (i = 0; i < value.length; i++) {
                str = value[i].toLowerCase(); //统一转为小写
                for (j = 0; j < INJ_STRS.length; j++) {
                    if (str.indexOf(INJ_STRS[j]) > -1) {
                        request.setAttribute(INVALID_PARAM, INJ_STRS[j]);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 漏洞解决：【跨站点脚本编制】
     * @param
     * @return
     */
    private boolean xssHandler(HttpServletRequest request) {
        Enumeration<?> pms = request.getParameterNames();
        if (isSafe(request.getRequestURI())) {
            while (pms.hasMoreElements()) {
                String cache = request.getParameter((String) pms.nextElement());
                if (!isSafe(cache)) {
                    request.setAttribute(INVALID_PARAM, cache);
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean isSafe(String str) {
        if (str != null && str.trim().length() > 0) {
            for (String s : SAFELESS) {
                if (str.toLowerCase().contains(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 漏洞解决：【跨站点请求伪造（CSRF）】
     * @param
     * @return
     * @throws
     */
    @SuppressWarnings("unused")
    private boolean csrfHandler(HttpServletRequest request) {
        return request.getSession().getId().equals(request.getParameter("jsessionid"));
    }

    /**
     * 注：慎用，SSL时才使用
     * 漏洞解决：【加密会话（SSL）Cookie 中缺少Secure 属性】
     * @param
     * @return
     * @throws
     */
    @SuppressWarnings("unused")
    private void httpsSecHandler(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = request.getCookies() == null ? null : request.getCookies()[0];
        if (cookie != null) {
            /*
             * cookie.setMaxAge(3600); cookie.setSecure(true); resp.addCookie(cookie);
             */
            //Servlet 2.5不支持在Cookie上直接设置HttpOnly属性
            cookie.setPath("/");
            cookie.setSecure(true);
            String sessionid = request.getSession().getId();
            StringBuffer buf = new StringBuffer();
            buf.append("JSESSIONID=" + sessionid + "; ");
            //String contextPath = request.getContextPath();
            //buf.append("Path=" + contextPath + "; ");
            buf.append("Path=/; ");
            buf.append("Secure; ");
            buf.append("HttpOnly; ");
            /*
             * Calendar cal = Calendar.getInstance(); cal.add(Calendar.HOUR, 1); Date date = cal.getTime(); Locale locale = Locale.CHINA; SimpleDateFormat sdf =
             * new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", locale); buf.append("Expires=" + sdf.format(date));
             */
            response.setHeader("SET-COOKIE", buf.toString());
        }
    }

    /**
     * 转向错误页面
     * @param request
     * @param response
     * @param errMsg
     * @throws ServletException
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private void forwardErrPage(HttpServletRequest request, HttpServletResponse response, String errMsg) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("errMsg", errMsg);
        request.getRequestDispatcher("/500").forward(request, response);
    }
}
