package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String username = "";
            Object userObj = session.getAttribute("user");
            if (userObj != null) {
                username = ((model.bean.User) userObj).getUsername();
            }
            
            session.invalidate();
            logger.info("User logged out successfully: {}", username);
        }
        
        response.sendRedirect(request.getContextPath() + "/login?message=You have been logged out successfully");
    }
}