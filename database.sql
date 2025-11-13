-- ====================================================
-- Database Configuration for JSP Final Project
-- ====================================================

-- Drop database if exists (caution: this will delete all data)
DROP DATABASE IF EXISTS projectdb;

-- Create database
CREATE DATABASE projectdb 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE projectdb;

-- ====================================================
-- Table: users
-- ====================================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin','user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- ====================================================
-- Insert sample data for testing
-- ====================================================
INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@example.com', SHA2('admin123', 256), 'admin'),
('john_doe', 'john@example.com', SHA2('password123', 256), 'user'),
('jane_smith', 'jane@example.com', SHA2('mypassword', 256), 'user'),
('test_user', 'test@example.com', SHA2('test123', 256), 'user');

-- ====================================================
-- Verify data
-- ====================================================
SELECT 
    id, 
    username, 
    email,
    role,
    created_at 
FROM users;

-- ====================================================
-- Database User (Optional - for production)
-- ====================================================
-- Create dedicated database user for the application
-- UNCOMMENT BELOW LINES IF YOU WANT TO CREATE A SPECIFIC USER

-- CREATE USER 'jsp_app'@'localhost' IDENTIFIED BY 'jsp_password123';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.* TO 'jsp_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ====================================================
-- Connection Test Query
-- ====================================================
-- This query can be used to test database connection
SELECT 'Database connection successful!' as message, NOW() as current_time;

-- ====================================================
-- Table Information
-- ====================================================
DESCRIBE users;

-- ====================================================
-- Usage Instructions:
-- ====================================================
/*
1. Make sure MySQL server is running
2. Connect to MySQL as root: mysql -u root -p
3. Run this script: source /path/to/database.sql
4. Or copy-paste the commands one by one

Alternative method using MySQL Workbench:
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open this file and execute it

Database Connection Details for Java Application:
- Database: projectdb
- Host: localhost
- Port: 3306 (default)
- Username: root (or jsp_app if you created it)
- Password: your_mysql_password

JDBC URL: jdbc:mysql://localhost:3306/projectdb?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
*/