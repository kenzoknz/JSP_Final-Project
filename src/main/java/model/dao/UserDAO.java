package model.dao;

import config.DBConnection;
import model.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    
    private static final String INSERT_USER_SQL = 
        "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
    
    private static final String SELECT_USER_BY_ID_SQL = 
        "SELECT id, username, email, password_hash, created_at, updated_at FROM users WHERE id = ?";
    
    private static final String SELECT_USER_BY_EMAIL_SQL = 
        "SELECT id, username, email, password_hash, created_at, updated_at FROM users WHERE email = ?";
    
    private static final String SELECT_USER_BY_USERNAME_SQL = 
        "SELECT id, username, email, password_hash, created_at, updated_at FROM users WHERE username = ?";
    
    private static final String SELECT_ALL_USERS_SQL = 
        "SELECT id, username, email, password_hash, created_at, updated_at FROM users ORDER BY created_at DESC";
    
    private static final String UPDATE_USER_SQL = 
        "UPDATE users SET username = ?, email = ?, password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String DELETE_USER_SQL = 
        "DELETE FROM users WHERE id = ?";
    
    private static final String COUNT_USERS_SQL = 
        "SELECT COUNT(*) FROM users";
    
    private static final String LOGIN_SQL = 
        "SELECT id, username, email, password_hash, created_at, updated_at FROM users WHERE email = ? AND password_hash = ?";
    
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
    
    public User findById(int id) {
        if (id <= 0) {
            logger.warn("Attempted to find user with invalid ID: {}", id);
            return null;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("User found by ID: {}", id);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", id, e);
        }
        
        logger.debug("User not found by ID: {}", id);
        return null;
    }
    
    public User login(String email, String passwordHash) {
        if (email == null || passwordHash == null || 
            email.trim().isEmpty() || passwordHash.trim().isEmpty()) {
            logger.warn("Login attempted with null/empty credentials");
            return null;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(LOGIN_SQL)) {
            
            statement.setString(1, email.trim());
            statement.setString(2, passwordHash.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.info("Login successful for user: {}", email);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error during login for user: {}", email, e);
        }
        
        logger.warn("Login failed for user: {}", email);
        return null;
    }
    
    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);
            }
            
            logger.debug("Retrieved {} users from database", users.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving all users", e);
        }
        
        return users;
    }
    
    public boolean update(User user) {
        if (user == null || user.getId() <= 0) {
            logger.warn("Attempted to update invalid user");
            return false;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setInt(4, user.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("User updated successfully: ID {}", user.getId());
                return true;
            } else {
                logger.warn("No user found with ID: {}", user.getId());
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating user: ID {}", user.getId(), e);
            return false;
        }
    }
    
    public boolean delete(int userId) {
        if (userId <= 0) {
            logger.warn("Attempted to delete user with invalid ID: {}", userId);
            return false;
        }
        
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            
            statement.setInt(1, userId);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("User deleted successfully: ID {}", userId);
                return true;
            } else {
                logger.warn("No user found with ID: {}", userId);
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Error deleting user: ID {}", userId, e);
            return false;
        }
    }
    
    public int getUserCount() {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_USERS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                logger.debug("Total users in database: {}", count);
                return count;
            }
            
        } catch (SQLException e) {
            logger.error("Error counting users", e);
        }
        
        return 0;
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
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return user;
    }
}