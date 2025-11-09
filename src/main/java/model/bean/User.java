package model.bean;

import java.sql.Timestamp;
import java.util.Objects;

public class User {
    
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public User() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    public User(int id, String username, String email, String passwordHash, 
                Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        updateTimestamp();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        updateTimestamp();
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    private void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public boolean isNew() {
        return id == 0;
    }
    
    public String getDisplayName() {
        return username != null ? username : email;
    }
    
    @Override
    public String toString() {
        return String.format(
            "User{id=%d, username='%s', email='%s', createdAt=%s, updatedAt=%s}",
            id, username, email, createdAt, updatedAt
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        
        if (id > 0 && user.id > 0) {
            return id == user.id;
        }
        
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return id > 0 ? Objects.hash(id) : Objects.hash(email);
    }
    
    public User copy() {
        return new User(id, username, email, passwordHash, createdAt, updatedAt);
    }
}