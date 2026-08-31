import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Decrements the app's active-user counter when a session naturally
 * expires or is invalidated, so freed slots become available again.
 */
public class UserCounterListener implements HttpSessionListener {
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if (se.getSession().getAttribute("countedByApp") != null) {
            UserLimitFilter.activeUsers.decrementAndGet();
        }
    }
}
