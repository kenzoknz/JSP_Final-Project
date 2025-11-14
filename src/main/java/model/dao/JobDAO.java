package model.dao;

import config.DBConnection;
import model.bean.Job;
import model.bean.Job.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Job entity
 * Handles all database operations for jobs table
 */
public class JobDAO {
    private static final Logger logger = LoggerFactory.getLogger(JobDAO.class);
    
    // SQL Queries
    private static final String INSERT_JOB = 
        "INSERT INTO jobs (user_id, type, status, input_path, original_filename, file_size, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
    private static final String UPDATE_JOB_STATUS = 
        "UPDATE jobs SET status = ?, started_at = ?, finished_at = ?, error_message = ? WHERE id = ?";
        
    private static final String UPDATE_JOB_OUTPUT_PATH = 
        "UPDATE jobs SET output_path = ? WHERE id = ?";
        
    private static final String SELECT_JOB_BY_ID = 
        "SELECT * FROM jobs WHERE id = ?";
        
    private static final String SELECT_JOBS_BY_USER_ID = 
        "SELECT * FROM jobs WHERE user_id = ? ORDER BY created_at DESC";
        
    private static final String SELECT_ALL_JOBS = 
        "SELECT * FROM jobs ORDER BY created_at DESC";
        
    private static final String SELECT_JOBS_BY_STATUS = 
        "SELECT * FROM jobs WHERE status = ? ORDER BY created_at DESC";
        
    private static final String DELETE_JOB = 
        "DELETE FROM jobs WHERE id = ?";
        
    private static final String COUNT_JOBS_BY_USER = 
        "SELECT COUNT(*) FROM jobs WHERE user_id = ?";

    /**
     * Create a new job record in database
     */
    public boolean createJob(Job job) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_JOB, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, job.getUserId());
            stmt.setString(2, job.getType());
            stmt.setString(3, job.getStatus().getValue());
            stmt.setString(4, job.getInputPath());
            stmt.setString(5, job.getOriginalFilename());
            stmt.setLong(6, job.getFileSize());
            stmt.setTimestamp(7, job.getCreatedAt());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        job.setId(generatedKeys.getInt(1));
                        logger.info("Created new job with ID: {}", job.getId());
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error creating job", e);
        }
        return false;
    }

    /**
     * Update job status and timestamps
     */
    public boolean updateJobStatus(int jobId, JobStatus status, String errorMessage) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_JOB_STATUS)) {
            
            stmt.setString(1, status.getValue());
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (status == JobStatus.IN_PROGRESS) {
                stmt.setTimestamp(2, now);  // started_at
                stmt.setNull(3, Types.TIMESTAMP);  // finished_at
            } else if (status == JobStatus.COMPLETED || status == JobStatus.FAILED) {
                stmt.setNull(2, Types.TIMESTAMP);  // started_at (keep existing)
                stmt.setTimestamp(3, now);  // finished_at
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
                stmt.setNull(3, Types.TIMESTAMP);
            }
            
            stmt.setString(4, errorMessage);
            stmt.setInt(5, jobId);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Updated job {} status to {}", jobId, status.getValue());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating job status", e);
            return false;
        }
    }

    /**
     * Update job output path when conversion is completed
     */
    public boolean updateJobOutputPath(int jobId, String outputPath) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_JOB_OUTPUT_PATH)) {
            
            stmt.setString(1, outputPath);
            stmt.setInt(2, jobId);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Updated job {} output path to: {}", jobId, outputPath);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error updating job output path", e);
            return false;
        }
    }

    /**
     * Get job by ID
     */
    public Job getJobById(int jobId) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_JOB_BY_ID)) {
            
            stmt.setInt(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToJob(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting job by ID", e);
        }
        return null;
    }

    /**
     * Get all jobs for a specific user
     */
    public List<Job> getJobsByUserId(int userId) {
        List<Job> jobs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_JOBS_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting jobs by user ID", e);
        }
        
        return jobs;
    }

    /**
     * Get all jobs (for admin)
     */
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_JOBS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Error getting all jobs", e);
        }
        
        return jobs;
    }

    /**
     * Get jobs by status
     */
    public List<Job> getJobsByStatus(JobStatus status) {
        List<Job> jobs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_JOBS_BY_STATUS)) {
            
            stmt.setString(1, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapResultSetToJob(rs));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting jobs by status", e);
        }
        
        return jobs;
    }

    /**
     * Delete a job
     */
    public boolean deleteJob(int jobId) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_JOB)) {
            
            stmt.setInt(1, jobId);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted job with ID: {}", jobId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting job", e);
            return false;
        }
    }

    /**
     * Count jobs by user
     */
    public int countJobsByUser(int userId) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_JOBS_BY_USER)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error counting jobs by user", e);
        }
        
        return 0;
    }

    /**
     * Helper method to map ResultSet to Job object
     */
    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        
        job.setId(rs.getInt("id"));
        job.setUserId(rs.getInt("user_id"));
        job.setType(rs.getString("type"));
        job.setStatus(JobStatus.fromString(rs.getString("status")));
        job.setInputPath(rs.getString("input_path"));
        job.setOutputPath(rs.getString("output_path"));
        job.setOriginalFilename(rs.getString("original_filename"));
        job.setFileSize(rs.getLong("file_size"));
        job.setErrorMessage(rs.getString("error_message"));
        job.setCreatedAt(rs.getTimestamp("created_at"));
        job.setStartedAt(rs.getTimestamp("started_at"));
        job.setFinishedAt(rs.getTimestamp("finished_at"));
        
        return job;
    }
}