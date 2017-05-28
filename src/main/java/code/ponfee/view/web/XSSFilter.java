package code.ponfee.view.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSS漏洞过滤器
 * @author fupf
 */
public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {}

    /**
     * XSS漏洞解决方案
     * @author fupf
     */
    public static final class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
        HttpServletRequest orgRequest = null;

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            orgRequest = request;
        }

        /**
         * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
         * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
         * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
         */
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(htmlEncode(name));
            if (value != null) {
                value = htmlEncode(value);
            }
            return value;
        }

        /**
         * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
         * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
         * getHeaderNames 也可能需要覆盖
         */
        @Override
        public String getHeader(String name) {

            String value = super.getHeader(htmlEncode(name));
            if (value != null) {
                value = htmlEncode(value);
            }
            return value;
        }

        /**
         * 将容易引起xss漏洞的半角字符直接替换成全角字符
         * @param s
         * @return
         */
        @SuppressWarnings("unused")
        @Deprecated
        private String xssEncode(String s) {
            if (s == null || s.isEmpty()) {
                return s;
            }
            StringBuilder sb = new StringBuilder(s.length() + 16);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '>':
                        sb.append("＞");// 转义大于号   
                        break;
                    case '<':
                        sb.append("＜");// 转义小于号   
                        break;
                    case '\'':
                        sb.append("＇");// 转义单引号   
                        break;
                    case '"':
                        sb.append("＂");// 转义双引号   
                        break;
                    /*
                     * case '&': sb.append("＆");// 转义& break; case 10: case 13: break;
                     */
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }

        /**
         * 转义成html实体
         * @param source
         * @return
         */
        private String htmlEncode(String source) {
            if (source == null) {
                return "";
            }
            //return StringEscapeUtils.escapeHtml3(s);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < source.length(); i++) {
                char c = source.charAt(i);
                switch (c) {
                    case '<':
                        buffer.append("&lt;");
                        break;
                    case '>':
                        buffer.append("&gt;");
                        break;
                    case '"':
                        buffer.append("&quot;");
                        break;
                    case '\'':
                        buffer.append("&#39;");
                        break;

                    //case '&':
                    //    buffer.append("&amp;");
                    //    break;
                    //case '%':
                    //    buffer.append("&#37;");
                    //    break;
                    //case ';':
                    //    buffer.append("&#59;");
                    //    break;
                    //case '(':
                    //    buffer.append("&#40;");
                    //    break;
                    //case ')':
                    //    buffer.append("&#41;");
                    //    break;
                    //case '+':
                    //    buffer.append("&#43;");
                    //    break;
                    //case 10:
                    //case 13:
                    //    break;

                    default:
                        buffer.append(c);
                        break;
                }
            }
            return buffer.toString();
        }

        /**
         * 获取最原始的request
         * @return
         */
        public HttpServletRequest getOrgRequest() {
            return orgRequest;
        }

        /**
         * 获取最原始的request的静态方法
         * @return
         */
        public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
            if (req instanceof XssHttpServletRequestWrapper) {
                return ((XssHttpServletRequestWrapper) req).getOrgRequest();
            }

            return req;
        }
    }
}
