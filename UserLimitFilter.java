import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Application-level concurrent-user cap. This is plain app code — it does
 * not rely on any Tomcat/container configuration. It tracks how many
 * distinct users currently hold a session and rejects new users once the
 * cap is hit, before the app even creates a session for them.
 */
public class UserLimitFilter implements Filter {

    private static final int MAX_ACTIVE_USERS = 50; // <-- tune this

    // Shared app-level counter of "logged in" users right now.
    static final AtomicInteger activeUsers = new AtomicInteger(0);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession existingSession = req.getSession(false);

        if (existingSession == null) {
            // Brand-new user — this is where the app enforces its own limit.
            if (activeUsers.get() >= MAX_ACTIVE_USERS) {
                resp.setStatus(503);
                resp.setContentType("text/html");
                resp.getWriter().write(
                    "<html><body><h1>Server at capacity</h1>" +
                    "<p>Max concurrent users (" + MAX_ACTIVE_USERS + ") reached. Please try again later.</p>" +
                    "</body></html>");
                return; // short-circuits before index.jsp / session creation
            }
            chain.doFilter(request, response);
            HttpSession newSession = req.getSession(false);
            if (newSession != null && newSession.isNew()) {
                activeUsers.incrementAndGet();
                newSession.setAttribute("countedByApp", true);
            }
        } else {
            // Returning user, already counted — always let through.
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
