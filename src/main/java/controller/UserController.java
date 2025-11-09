package controller;

import model.bo.UserBO;
import model.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@WebServlet(name = "UserController", urlPatterns = {"/users", "/users/*"})
public class UserController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserBO userBO;

    @Override
    public void init() throws ServletException {
        super.init();
        userBO = new UserBO();
        logger.info("UserController initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action != null ? action : "") {
                case "view":
                    viewUser(request, response);
                    break;
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling GET request", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action != null ? action : "") {
                case "create":
                    createUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "login":
                    loginUser(request, response);
                    break;
                default:
                    logger.warn("Unknown action: {}", action);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.error("Error handling POST request", e);
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Listing all users");

        List<User> users = userBO.getAllUsers();
        int userCount = userBO.getUserCount();

        request.setAttribute("users", users);
        request.setAttribute("userCount", userCount);
        request.setAttribute("pageTitle", "User Management");

        logger.info("Retrieved {} users for listing", users.size());

        request.getRequestDispatcher("/views/userList.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.debug("Showing add user form");
        request.setAttribute("pageTitle", "Add New User");
        request.getRequestDispatcher("/views/addUser.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            logger.warn("Edit user requested without ID parameter");
            request.setAttribute("error", "User ID is required");
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            User user = userBO.getUserById(userId);

            if (user == null) {
                logger.warn("User not found with ID: {}", userId);
                request.setAttribute("error", "User not found");
                response.sendRedirect(request.getContextPath() + "/users");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("pageTitle", "Edit User - " + user.getUsername());

            logger.debug("Showing edit form for user: {}", user.getId());

            request.getRequestDispatcher("/views/editUser.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", idParam);
            request.setAttribute("error", "Invalid user ID format");
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void viewUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            logger.warn("View user requested without ID parameter");
            request.setAttribute("error", "User ID is required");
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);
            User user = userBO.getUserById(userId);

            if (user == null) {
                logger.warn("User not found with ID: {}", userId);
                request.setAttribute("error", "User not found");
                response.sendRedirect(request.getContextPath() + "/users");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("pageTitle", "User Details - " + user.getUsername());

            logger.debug("Viewing user: {}", user.getId());

            request.getRequestDispatcher("/views/viewUser.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", idParam);
            request.setAttribute("error", "Invalid user ID format");
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        logger.debug("Creating new user: {}", email);

        String result = userBO.createUser(username, email, password);

        if ("SUCCESS".equals(result)) {
            logger.info("User created successfully: {}", email);
            response.sendRedirect(request.getContextPath() + "/users?message=User created successfully!");
        } else {
            logger.error("Failed to create user: {} - {}", email, result);
            request.setAttribute("error", result);
            request.getRequestDispatcher("/views/addUser.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/users?error=User ID is required");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);

            String result = userBO.updateUser(userId, username, email, password);

            if ("SUCCESS".equals(result)) {
                logger.info("User updated successfully: ID {}", userId);
                response.sendRedirect(request.getContextPath() + "/users?action=view&id=" + userId + "&message=User updated successfully!");
            } else {
                logger.error("Failed to update user: ID {} - {}", userId, result);

                User user = userBO.getUserById(userId);
                request.setAttribute("user", user);
                request.setAttribute("error", result);

                request.getRequestDispatcher("/views/editUser.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", idParam);
            response.sendRedirect(request.getContextPath() + "/users?error=Invalid user ID format");
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
                logger.info("User deleted successfully: ID {}", userId);
                request.setAttribute("success", "User deleted successfully!");
            } else {
                logger.warn("Failed to delete user: ID {}", userId);
                request.setAttribute("error", "Failed to delete user. User may not exist.");
            }

            response.sendRedirect(request.getContextPath() + "/users");

        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", idParam);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        User user = userBO.authenticateUser(email, password);

        if (user != null) {
            logger.info("Login successful for user: {}", email);
            request.getSession().setAttribute("user", user);
            request.setAttribute("success", "Login successful!");
            response.sendRedirect(request.getContextPath() + "/users");
        } else {
            logger.warn("Login failed for user: {}", email);
            request.setAttribute("error", "Invalid email or password");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}