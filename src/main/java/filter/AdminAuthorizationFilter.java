package filter;

import model.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AdminAuthorizationFilter", urlPatterns = {"/admin/*"})
public class AdminAuthorizationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthorizationFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AdminAuthorizationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        logger.debug("Admin authorization check for: {}", requestURI);
        
        HttpSession session = httpRequest.getSession(false);
        
        if (session == null) {
            logger.warn("No session found for admin area access: {}", requestURI);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            logger.warn("No user found in session for admin area access: {}", requestURI);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        if (!user.isAdmin()) {
            logger.warn("Non-admin user attempted to access admin area: {} ({})", user.getUsername(), requestURI);
            httpResponse.sendRedirect(contextPath + "/submit.jsp?error=Access denied. Admin privileges required.");
            return;
        }
        
        logger.debug("Admin access granted: {} accessing {}", user.getUsername(), requestURI);
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("AdminAuthorizationFilter destroyed");
    }
}