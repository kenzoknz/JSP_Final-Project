package com.example.model.bean;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * User Bean (JavaBean)
 * 
 * This class represents a User entity that corresponds to the 'users' table in database.
 * It follows JavaBean conventions with private fields and public getter/setter methods.
 * 
 * Table structure mapping:
 * - id (INT PRIMARY KEY AUTO_INCREMENT)
 * - username (VARCHAR(50) NOT NULL UNIQUE)
 * - email (VARCHAR(100) NOT NULL UNIQUE) 
 * - password_hash (VARCHAR(255) NOT NULL)
 * - created_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)
 * - updated_at (TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
 * 
 * @author JSP Final Project Team
 * @version 2.0 (Refactored for MVC structure)
 */
public class User {
    
    // Private fields corresponding to database columns
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default constructor
     */
    public User() {
        // Initialize with current timestamp
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Constructor with basic user information (for registration)
     * 
     * @param username User's username
     * @param email User's email address
     * @param passwordHash Hashed password
     */
    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    /**
     * Full constructor (typically used when loading from database)
     * 
     * @param id User ID
     * @param username User's username
     * @param email User's email address
     * @param passwordHash Hashed password
     * @param createdAt Creation timestamp
     * @param updatedAt Last update timestamp
     */
    public User(int id, String username, String email, String passwordHash, 
                Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // ====================================================
    // Getter and Setter methods
    // ====================================================
    
    /**
     * Get user ID
     * @return user ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set user ID
     * @param id user ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Set username
     * @param username username (must be unique)
     */
    public void setUsername(String username) {
        this.username = username;
        updateTimestamp();
    }
    
    /**
     * Get email address
     * @return email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set email address
     * @param email email address (must be unique)
     */
    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }
    
    /**
     * Get password hash
     * @return hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Set password hash
     * @param passwordHash hashed password (never store plain text passwords)
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        updateTimestamp();
    }
    
    /**
     * Get creation timestamp
     * @return creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Set creation timestamp
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get last update timestamp
     * @return last update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Set last update timestamp
     * @param updatedAt last update timestamp
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ====================================================
    // Utility methods
    // ====================================================
    
    /**
     * Update the updatedAt timestamp to current time
     */
    private void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Check if this is a new user (not yet saved to database)
     * @return true if user is new (id = 0), false otherwise
     */
    public boolean isNew() {
        return id == 0;
    }
    
    /**
     * Get display name for user
     * @return username or email if username is null
     */
    public String getDisplayName() {
        return username != null ? username : email;
    }
    
    // ====================================================
    // Object overrides
    // ====================================================
    
    /**
     * String representation of User object
     */
    @Override
    public String toString() {
        return String.format(
            "User{id=%d, username='%s', email='%s', createdAt=%s, updatedAt=%s}",
            id, username, email, createdAt, updatedAt
        );
    }
    
    /**
     * Equals method for User comparison
     * Two users are equal if they have the same id (and id > 0) or same email
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        
        // If both users have valid IDs, compare by ID
        if (id > 0 && user.id > 0) {
            return id == user.id;
        }
        
        // Otherwise, compare by email (which should be unique)
        return Objects.equals(email, user.email);
    }
    
    /**
     * Hash code for User object
     */
    @Override
    public int hashCode() {
        return id > 0 ? Objects.hash(id) : Objects.hash(email);
    }
    
    /**
     * Create a copy of this User object (shallow copy)
     * @return new User object with same values
     */
    public User copy() {
        return new User(id, username, email, passwordHash, createdAt, updatedAt);
    }
}