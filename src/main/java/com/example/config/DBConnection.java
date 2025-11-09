package com.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database Connection Manager using Singleton pattern with HikariCP connection pooling
 * 
 * This class provides:
 * - Singleton instance for database connection management
 * - Connection pooling for better performance
 * - Proper resource management
 * - Configuration flexibility
 * 
 * @author JSP Final Project Team
 * @version 1.0
 */
public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    
    // Singleton instance
    private static volatile DBConnection instance;
    private static final Object lock = new Object();
    
    // HikariCP DataSource for connection pooling
    private HikariDataSource dataSource;
    
    // Database configuration constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projectdb?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456"; // Change this to your MySQL password
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection pool settings
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE = 5;
    private static final long CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final long IDLE_TIMEOUT = 600000; // 10 minutes
    private static final long MAX_LIFETIME = 1800000; // 30 minutes
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private DBConnection() {
        initializeDataSource();
    }
    
    /**
     * Get singleton instance of DBConnection
     * Thread-safe implementation using double-checked locking
     * 
     * @return DBConnection instance
     */
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize HikariCP DataSource with configuration
     */
    private void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            
            // Database connection settings
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName(DB_DRIVER);
            
            // Connection pool settings
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            
            // Connection test settings
            config.setConnectionTestQuery("SELECT 1");
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // Pool name for monitoring
            config.setPoolName("JSP-Project-Pool");
            
            // Additional settings for MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Could not initialize database connection pool", e);
        }
    }
    
    /**
     * Get database connection from pool
     * 
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        
        Connection connection = dataSource.getConnection();
        logger.debug("Connection obtained from pool. Active connections: {}", 
                    dataSource.getHikariPoolMXBean().getActiveConnections());
        
        return connection;
    }
    
    /**
     * Test database connection
     * 
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection.isValid(5); // 5 seconds timeout
            logger.info("Database connection test: {}", isValid ? "SUCCESS" : "FAILED");
            return isValid;
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get DataSource for advanced usage
     * 
     * @return HikariDataSource instance
     */
    public DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Close connection pool (call during application shutdown)
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
    
    /**
     * Get connection pool statistics for monitoring
     * 
     * @return String with pool statistics
     */
    public String getPoolStats() {
        if (dataSource != null) {
            return String.format(
                "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        }
        return "Pool not initialized";
    }
}