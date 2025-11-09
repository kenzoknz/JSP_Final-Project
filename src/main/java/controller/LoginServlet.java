package controller;

import model.bean.User;
import model.bo.UserBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    
    private UserBO userBO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userBO = new UserBO();
        logger.info("LoginServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            return;
        }
        
        request.setAttribute("pageTitle", "Login");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");
        
        logger.debug("Login attempt for user: {}", usernameOrEmail);
        
        if (usernameOrEmail == null || password == null || 
            usernameOrEmail.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Username/Email and password are required");
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        User user = userBO.authenticateUser(usernameOrEmail.trim(), password);
        
        if (user != null) {
            logger.info("Login successful for user: {} ({})", usernameOrEmail, user.getRole());
            
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } else {
            logger.warn("Login failed for user: {}", usernameOrEmail);
            request.setAttribute("error", "Invalid username/email or password");
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}