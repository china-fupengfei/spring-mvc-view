package code.ponfee.view.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * session监听
 * @author fupf
 */
public class SessionListener implements HttpSessionListener
{

    @Override
    public void sessionCreated(HttpSessionEvent event)
    {}

    /**
     * session销毁前调用的方法
     * @param event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        HttpSession session = event.getSession();
        /**
         * 在sessionContext中移除
         */
        SessionContext.removeSession(session);
    }

    public static class SessionContext
    {
        private static final Map<String, HttpSession> SESSION_MAP = new HashMap<String, HttpSession>();

        private SessionContext()
        {}

        public static void addSession(HttpSession session)
        {
            SESSION_MAP.put(session.getId(), session);
        }

        public static void removeSession(HttpSession session)
        {
            SESSION_MAP.remove(session.getId());
        }

        public static HttpSession getSession(String sessionId)
        {
            return SESSION_MAP.get(sessionId);
        }

    }
}
