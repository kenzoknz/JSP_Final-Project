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
import java.util.List;

@WebServlet(name = "AdminController", urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private UserBO userBO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userBO = new UserBO();
        logger.info("AdminController initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.isAdmin()) {
            logger.warn("Non-admin user attempted to access admin area: {}", currentUser.getUsername());
            response.sendRedirect(request.getContextPath() + "/submit.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "view":
                    viewUser(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling admin GET request", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/submit.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                case "update":
                    updateUser(request, response);
                    break;
                case "create":
                    createUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    logger.warn("Unknown admin action: {}", action);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.error("Error handling admin POST request", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        }
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        logger.debug("Admin listing all users");
        
        List<User> users = userBO.getAllUsers();
        int userCount = userBO.getUserCount();
        
        request.setAttribute("users", users);
        request.setAttribute("userCount", userCount);
        request.setAttribute("pageTitle", "Admin - User Management");
        
        logger.info("Admin retrieved {} users for listing", users.size());
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
    
    private void viewUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            logger.warn("Admin view user requested without ID parameter");
            response.sendRedirect(request.getContextPath() + "/admin/users?error=User ID is required");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            User user = userBO.getUserById(userId);
            
            if (user == null) {
                logger.warn("Admin requested non-existent user: {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin/users?error=User not found");
                return;
            }
            
            request.setAttribute("user", user);
            request.setAttribute("pageTitle", "Admin - View User: " + user.getUsername());
            
            logger.debug("Admin viewing user: {}", user.getId());
            
            request.getRequestDispatcher("/admin/viewUser.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.warn("Admin provided invalid user ID: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=Invalid user ID");
        }
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            logger.warn("Admin edit user requested without ID parameter");
            response.sendRedirect(request.getContextPath() + "/admin/users?error=User ID is required");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            User user = userBO.getUserById(userId);
            
            if (user == null) {
                logger.warn("Admin edit requested for non-existent user: {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin/users?error=User not found");
                return;
            }
            
            request.setAttribute("user", user);
            request.setAttribute("pageTitle", "Admin - Edit User: " + user.getUsername());
            
            logger.debug("Admin showing edit form for user: {}", user.getId());
            
            request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            logger.warn("Admin provided invalid user ID for edit: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=Invalid user ID");
        }
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=User ID is required");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            User existingUser = userBO.getUserById(userId);
            
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=User not found");
                return;
            }
            
            String result = userBO.updateUser(userId, username, email, password, role);
            
            if ("SUCCESS".equals(result)) {
                logger.info("Admin updated user successfully: ID {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin/users?message=User updated successfully");
            } else {
                logger.error("Admin failed to update user: ID {} - {}", userId, result);
                
                User user = userBO.getUserById(userId);
                request.setAttribute("user", user);
                request.setAttribute("error", result);
                
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Admin provided invalid user ID for update: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=Invalid user ID");
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }
        
        try {
            int userId = Integer.parseInt(idParam);
            
            boolean deleted = userBO.deleteUser(userId);
            
            if (deleted) {
                logger.info("Admin deleted user successfully: ID {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin/users?message=User deleted successfully");
            } else {
                logger.warn("Admin failed to delete user: ID {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin/users?error=Failed to delete user");
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Admin provided invalid user ID for deletion: {}", idParam);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        logger.debug("Admin showing add user form");
        request.setAttribute("pageTitle", "Admin - Add New User");
        request.getRequestDispatcher("/admin/addUser.jsp").forward(request, response);
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        logger.debug("Admin creating new user: {}", email);
        
        String result = userBO.createUser(username, email, password, role);
        
        if ("SUCCESS".equals(result)) {
            logger.info("Admin created user successfully: {}", email);
            response.sendRedirect(request.getContextPath() + "/admin/users?message=User created successfully");
        } else {
            logger.error("Admin failed to create user: {} - {}", email, result);
            request.setAttribute("error", result);
            request.getRequestDispatcher("/admin/addUser.jsp").forward(request, response);
        }
    }
}