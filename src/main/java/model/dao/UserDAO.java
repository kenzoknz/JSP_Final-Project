package model.dao;

import config.DBConnection;
import model.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    
    private static final String INSERT_USER_SQL = 
        "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_USER_BY_EMAIL_SQL = 
        "SELECT id, username, email, password_hash, role, created_at, updated_at FROM users WHERE email = ?";
    
    private static final String SELECT_USER_BY_USERNAME_SQL = 
        "SELECT id, username, email, password_hash, role, created_at, updated_at FROM users WHERE username = ?";
    
    private static final String LOGIN_SQL = 
        "SELECT id, username, email, password_hash, role, created_at, updated_at FROM users WHERE (username = ? OR email = ?) AND password_hash = ?";
    
    public int insert(User user) {
        if (user == null) {
            logger.warn("Attempted to insert null user");
            return -1;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                logger.error("Creating user failed, no rows affected");
                return -1;
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setId(userId);
                    logger.info("User created successfully with ID: {}", userId);
                    return userId;
                } else {
                    logger.error("Creating user failed, no ID obtained");
                    return -1;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", user.getEmail(), e);
            return -1;
        }
    }
    
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to find user with null/empty email");
            return null;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_EMAIL_SQL)) {
            
            statement.setString(1, email.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("User found by email: {}", email);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
        }
        
        logger.debug("User not found by email: {}", email);
        return null;
    }
    
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Attempted to find user with null/empty username");
            return null;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {
            
            statement.setString(1, username.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("User found by username: {}", username);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        }
        
        logger.debug("User not found by username: {}", username);
        return null;
    }
    
    public User login(String usernameOrEmail, String passwordHash) {
        if (usernameOrEmail == null || passwordHash == null || 
            usernameOrEmail.trim().isEmpty() || passwordHash.trim().isEmpty()) {
            logger.warn("Login attempted with null/empty credentials");
            return null;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(LOGIN_SQL)) {
            
            statement.setString(1, usernameOrEmail.trim());
            statement.setString(2, usernameOrEmail.trim());
            statement.setString(3, passwordHash.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.info("Login successful for user: {}", usernameOrEmail);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error during login for user: {}", usernameOrEmail, e);
        }
        
        logger.warn("Login failed for user: {}", usernameOrEmail);
        return null;
    }
    
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
    
    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }
    
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return user;
    }
}