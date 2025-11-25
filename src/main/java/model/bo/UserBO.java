package model.bo;

import model.bean.User;
import model.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserBO {
    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);

    private UserDAO userDAO;

    public UserBO() {
        this.userDAO = new UserDAO();
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
        user.setRole("user");

        int userId = userDAO.insert(user);

        if (userId > 0) {
            logger.info("Business layer: User created successfully with ID: {}", userId);
            return "SUCCESS";
        } else {
            logger.error("Business layer: Failed to create user: {}", email);
            return "Failed to create user. Please try again.";
        }
    }

    public User authenticateUser(String usernameOrEmail, String password) {
        logger.debug("Business layer: Authenticating user: {}", usernameOrEmail);

        if (usernameOrEmail == null || password == null ||
            usernameOrEmail.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("Business layer: Empty credentials provided");
            return null;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            logger.error("Business layer: Password hashing failed during authentication");
            return null;
        }

        User user = userDAO.login(usernameOrEmail, hashedPassword);

        if (user != null) {
            logger.info("Business layer: Authentication successful for user: {}", usernameOrEmail);
        } else {
            logger.warn("Business layer: Authentication failed for user: {}", usernameOrEmail);
        }

        return user;
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