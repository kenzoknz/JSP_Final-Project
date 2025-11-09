package model.bo;

import model.bean.User;
import model.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserBO {
    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);

    private UserDAO userDAO;

    public UserBO() {
        this.userDAO = new UserDAO();
    }

    public List<User> getAllUsers() {
        logger.debug("Business layer: Getting all users");

        List<User> users = userDAO.listAll();

        logger.info("Business layer: Retrieved {} users", users.size());
        return users;
    }

    public User getUserById(int userId) {
        if (userId <= 0) {
            logger.warn("Business layer: Invalid user ID: {}", userId);
            return null;
        }

        logger.debug("Business layer: Getting user by ID: {}", userId);
        return userDAO.findById(userId);
    }

    public User getUserByEmail(String email) {
        if (!isValidEmail(email)) {
            logger.warn("Business layer: Invalid email format: {}", email);
            return null;
        }

        logger.debug("Business layer: Getting user by email: {}", email);
        return userDAO.findByEmail(email);
    }

    public String createUser(String username, String email, String password) {
        logger.debug("Business layer: Creating user: {}", email);

        String validationError = validateUserInput(username, email, password);
        if (validationError != null) {
            return validationError;
        }

        if (userDAO.emailExists(email)) {
            return "Email already exists in the system";
        }

        if (userDAO.usernameExists(username)) {
            return "Username already exists in the system";
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return "Password processing failed";
        }

        User user = new User(username, email, hashedPassword);

        int userId = userDAO.insert(user);

        if (userId > 0) {
            logger.info("Business layer: User created successfully with ID: {}", userId);
            return "SUCCESS";
        } else {
            logger.error("Business layer: Failed to create user: {}", email);
            return "Failed to create user. Please try again.";
        }
    }

    public String updateUser(int userId, String username, String email, String password) {
        logger.debug("Business layer: Updating user ID: {}", userId);

        User existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            return "User not found";
        }

        String validationError = validateUserInputForUpdate(username, email, password);
        if (validationError != null) {
            return validationError;
        }

        User userWithEmail = userDAO.findByEmail(email);
        if (userWithEmail != null && userWithEmail.getId() != userId) {
            return "Email already exists for another user";
        }

        User userWithUsername = userDAO.findByUsername(username);
        if (userWithUsername != null && userWithUsername.getId() != userId) {
            return "Username already exists for another user";
        }

        existingUser.setUsername(username);
        existingUser.setEmail(email);

        if (password != null && !password.trim().isEmpty()) {
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                return "Password processing failed";
            }
            existingUser.setPasswordHash(hashedPassword);
        }

        boolean updated = userDAO.update(existingUser);

        if (updated) {
            logger.info("Business layer: User updated successfully: ID {}", userId);
            return "SUCCESS";
        } else {
            logger.error("Business layer: Failed to update user: ID {}", userId);
            return "Failed to update user. Please try again.";
        }
    }

    public boolean deleteUser(int userId) {
        logger.debug("Business layer: Deleting user ID: {}", userId);

        if (userId <= 0) {
            logger.warn("Business layer: Invalid user ID for deletion: {}", userId);
            return false;
        }

        User user = userDAO.findById(userId);
        if (user == null) {
            logger.warn("Business layer: User not found for deletion: ID {}", userId);
            return false;
        }

        if ("admin".equalsIgnoreCase(user.getUsername())) {
            logger.warn("Business layer: Attempted to delete admin user: ID {}", userId);
            return false;
        }

        boolean deleted = userDAO.delete(userId);

        if (deleted) {
            logger.info("Business layer: User deleted successfully: ID {}", userId);
        } else {
            logger.error("Business layer: Failed to delete user: ID {}", userId);
        }

        return deleted;
    }

    public User authenticateUser(String email, String password) {
        logger.debug("Business layer: Authenticating user: {}", email);

        if (email == null || password == null ||
            email.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("Business layer: Empty credentials provided");
            return null;
        }

        if (!isValidEmail(email)) {
            logger.warn("Business layer: Invalid email format for authentication: {}", email);
            return null;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            logger.error("Business layer: Password hashing failed during authentication");
            return null;
        }

        User user = userDAO.login(email, hashedPassword);

        if (user != null) {
            logger.info("Business layer: Authentication successful for user: {}", email);
        } else {
            logger.warn("Business layer: Authentication failed for user: {}", email);
        }

        return user;
    }

    public int getUserCount() {
        int count = userDAO.getUserCount();
        logger.debug("Business layer: Total user count: {}", count);
        return count;
    }

    private String validateUserInput(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }

        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters";
        }

        if (!isValidUsername(username)) {
            return "Username can only contain letters, numbers, and underscores";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        if (!isValidEmail(email)) {
            return "Invalid email format";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }

        if (password.length() > 100) {
            return "Password must be less than 100 characters";
        }

        return null;
    }

    private String validateUserInputForUpdate(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }

        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters";
        }

        if (!isValidUsername(username)) {
            return "Username can only contain letters, numbers, and underscores";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        if (!isValidEmail(email)) {
            return "Invalid email format";
        }

        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                return "Password must be at least 6 characters long";
            }

            if (password.length() > 100) {
                return "Password must be less than 100 characters";
            }
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return email.contains("@") && email.contains(".") &&
               email.indexOf("@") > 0 &&
               email.lastIndexOf(".") > email.indexOf("@");
    }

    private boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        return username.matches("^[a-zA-Z0-9_]+$");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            return null;
        }
    }
}