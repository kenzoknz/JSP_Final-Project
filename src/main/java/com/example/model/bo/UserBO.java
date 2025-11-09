package com.example.model.bo;

import com.example.model.bean.User;
import com.example.model.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * User Business Object (BO)
 * 
 * This class contains business logic for User operations.
 * It acts as an intermediary between the Controller and DAO layers,
 * handling business rules, validation, and data processing.
 * 
 * Responsibilities:
 * - Input validation and business rule enforcement
 * - Password hashing and security operations
 * - Complex business logic operations
 * - Coordination between multiple DAOs if needed
 * - Data transformation and preparation
 * 
 * @author JSP Final Project Team
 * @version 2.0
 */
public class UserBO {
    private static final Logger logger = LoggerFactory.getLogger(UserBO.class);
    
    private UserDAO userDAO;
    
    /**
     * Constructor - Initialize DAO
     */
    public UserBO() {
        this.userDAO = new UserDAO();
    }
    
    // ====================================================
    // Public Business Methods
    // ====================================================
    
    /**
     * Get all users (business logic wrapper around DAO)
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        logger.debug("Business layer: Getting all users");
        
        List<User> users = userDAO.listAll();
        
        // Apply any business logic transformations here
        // For example: filter active users, sort by criteria, etc.
        
        logger.info("Business layer: Retrieved {} users", users.size());
        return users;
    }
    
    /**
     * Get user by ID with business logic
     * 
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        // Business validation
        if (userId <= 0) {
            logger.warn("Business layer: Invalid user ID: {}", userId);
            return null;
        }
        
        logger.debug("Business layer: Getting user by ID: {}", userId);
        return userDAO.findById(userId);
    }
    
    /**
     * Get user by email with business logic
     * 
     * @param email User email
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        // Business validation
        if (!isValidEmail(email)) {
            logger.warn("Business layer: Invalid email format: {}", email);
            return null;
        }
        
        logger.debug("Business layer: Getting user by email: {}", email);
        return userDAO.findByEmail(email);
    }
    
    /**
     * Create new user with full business logic and validation
     * 
     * @param username Username
     * @param email Email address
     * @param password Plain text password
     * @return "SUCCESS" if successful, error message otherwise
     */
    public String createUser(String username, String email, String password) {
        logger.debug("Business layer: Creating user: {}", email);
        
        // 1. Validate input
        String validationError = validateUserInput(username, email, password);
        if (validationError != null) {
            return validationError;
        }
        
        // 2. Check business rules
        if (userDAO.emailExists(email)) {
            return "Email already exists in the system";
        }
        
        if (userDAO.usernameExists(username)) {
            return "Username already exists in the system";
        }
        
        // 3. Apply business logic
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return "Password processing failed";
        }
        
        // 4. Create user object
        User user = new User(username, email, hashedPassword);
        
        // 5. Save to database
        int userId = userDAO.insert(user);
        
        if (userId > 0) {
            logger.info("Business layer: User created successfully with ID: {}", userId);
            return "SUCCESS";
        } else {
            logger.error("Business layer: Failed to create user: {}", email);
            return "Failed to create user. Please try again.";
        }
    }
    
    /**
     * Update user with business logic
     * 
     * @param userId User ID to update
     * @param username New username
     * @param email New email
     * @param password New password (can be null/empty to keep existing)
     * @return "SUCCESS" if successful, error message otherwise
     */
    public String updateUser(int userId, String username, String email, String password) {
        logger.debug("Business layer: Updating user ID: {}", userId);
        
        // 1. Validate user exists
        User existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            return "User not found";
        }
        
        // 2. Validate input (special validation for updates)
        String validationError = validateUserInputForUpdate(username, email, password);
        if (validationError != null) {
            return validationError;
        }
        
        // 3. Check business rules (email/username uniqueness)
        User userWithEmail = userDAO.findByEmail(email);
        if (userWithEmail != null && userWithEmail.getId() != userId) {
            return "Email already exists for another user";
        }
        
        User userWithUsername = userDAO.findByUsername(username);
        if (userWithUsername != null && userWithUsername.getId() != userId) {
            return "Username already exists for another user";
        }
        
        // 4. Apply business logic
        existingUser.setUsername(username);
        existingUser.setEmail(email);
        
        // Only update password if provided
        if (password != null && !password.trim().isEmpty()) {
            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                return "Password processing failed";
            }
            existingUser.setPasswordHash(hashedPassword);
        }
        
        // 5. Save to database
        boolean updated = userDAO.update(existingUser);
        
        if (updated) {
            logger.info("Business layer: User updated successfully: ID {}", userId);
            return "SUCCESS";
        } else {
            logger.error("Business layer: Failed to update user: ID {}", userId);
            return "Failed to update user. Please try again.";
        }
    }
    
    /**
     * Delete user with business logic
     * 
     * @param userId User ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        logger.debug("Business layer: Deleting user ID: {}", userId);
        
        // Business validation
        if (userId <= 0) {
            logger.warn("Business layer: Invalid user ID for deletion: {}", userId);
            return false;
        }
        
        // Check if user exists
        User user = userDAO.findById(userId);
        if (user == null) {
            logger.warn("Business layer: User not found for deletion: ID {}", userId);
            return false;
        }
        
        // Apply business rules (e.g., prevent deletion of admin users)
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            logger.warn("Business layer: Attempted to delete admin user: ID {}", userId);
            return false; // Could return error message instead
        }
        
        // Perform deletion
        boolean deleted = userDAO.delete(userId);
        
        if (deleted) {
            logger.info("Business layer: User deleted successfully: ID {}", userId);
        } else {
            logger.error("Business layer: Failed to delete user: ID {}", userId);
        }
        
        return deleted;
    }
    
    /**
     * Authenticate user login with business logic
     * 
     * @param email User email
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        logger.debug("Business layer: Authenticating user: {}", email);
        
        // Validate input
        if (email == null || password == null || 
            email.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("Business layer: Empty credentials provided");
            return null;
        }
        
        if (!isValidEmail(email)) {
            logger.warn("Business layer: Invalid email format for authentication: {}", email);
            return null;
        }
        
        // Hash password for comparison
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            logger.error("Business layer: Password hashing failed during authentication");
            return null;
        }
        
        // Authenticate with DAO
        User user = userDAO.login(email, hashedPassword);
        
        if (user != null) {
            logger.info("Business layer: Authentication successful for user: {}", email);
        } else {
            logger.warn("Business layer: Authentication failed for user: {}", email);
        }
        
        return user;
    }
    
    /**
     * Get total number of users
     * 
     * @return Number of users in system
     */
    public int getUserCount() {
        int count = userDAO.getUserCount();
        logger.debug("Business layer: Total user count: {}", count);
        return count;
    }
    
    // ====================================================
    // Private Utility Methods
    // ====================================================
    
    /**
     * Validate user input according to business rules
     * 
     * @param username Username
     * @param email Email address
     * @param password Password
     * @return Error message if validation fails, null if valid
     */
    private String validateUserInput(String username, String email, String password) {
        // Username validation
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }
        
        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters";
        }
        
        if (!isValidUsername(username)) {
            return "Username can only contain letters, numbers, and underscores";
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        
        // Password validation
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        
        if (password.length() > 100) {
            return "Password must be less than 100 characters";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Validate user input for update operations (password is optional)
     * 
     * @param username Username
     * @param email Email address
     * @param password Password (can be null/empty to keep existing)
     * @return Error message if validation fails, null if valid
     */
    private String validateUserInputForUpdate(String username, String email, String password) {
        // Username validation
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }
        
        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters";
        }
        
        if (!isValidUsername(username)) {
            return "Username can only contain letters, numbers, and underscores";
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        
        // Password validation (OPTIONAL for updates)
        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                return "Password must be at least 6 characters long";
            }
            
            if (password.length() > 100) {
                return "Password must be less than 100 characters";
            }
        }
        
        return null; // All validations passed
    }
    
    /**
     * Validate email format
     * 
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") > 0 && 
               email.lastIndexOf(".") > email.indexOf("@");
    }
    
    /**
     * Validate username format
     * 
     * @param username Username to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        // Username can only contain letters, numbers, and underscores
        return username.matches("^[a-zA-Z0-9_]+$");
    }
    
    /**
     * Hash password using SHA-256
     * 
     * @param password Plain text password
     * @return Hashed password or null if hashing fails
     */
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