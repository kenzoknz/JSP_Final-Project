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

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/submit.jsp", "/admin/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        logger.debug("Authentication check for: {}", requestURI);
        
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        if (user == null) {
            logger.debug("User not authenticated, redirecting to login: {}", requestURI);
            
            String loginURL = contextPath + "/login";
            if (!requestURI.equals(loginURL)) {
                String redirectURL = loginURL + "?redirect=" + java.net.URLEncoder.encode(requestURI, "UTF-8");
                httpResponse.sendRedirect(redirectURL);
                return;
            }
        } else {
            logger.debug("User authenticated: {} ({})", user.getUsername(), user.getRole());
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }
}