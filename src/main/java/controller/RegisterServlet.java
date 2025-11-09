package controller;

import model.bo.UserBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    
    private UserBO userBO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userBO = new UserBO();
        logger.info("RegisterServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("pageTitle", "Register");
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        logger.debug("Registration attempt for user: {}", email);
        
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        String result = userBO.createUser(username, email, password);
        
        if ("SUCCESS".equals(result)) {
            logger.info("User registration successful: {}", email);
            request.setAttribute("success", "Registration successful! Please login.");
            response.sendRedirect(request.getContextPath() + "/login?message=Registration successful! Please login.");
        } else {
            logger.warn("User registration failed: {} - {}", email, result);
            request.setAttribute("error", result);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}