package model.bean;

import java.sql.Timestamp;

/**
 * Job model for tracking file conversion jobs
 * Represents the jobs table in database
 */
public class Job {
    
    /**
     * Job status enum matching database ENUM type
     */
    public enum JobStatus {
        PENDING("PENDING"),
        IN_PROGRESS("IN_PROGRESS"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");
        
        private final String value;
        
        JobStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static JobStatus fromString(String text) {
            for (JobStatus status : JobStatus.values()) {
                if (status.value.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            return PENDING; // Default fallback
        }
    }
    
    // Fields matching database schema
    private int id;
    private int userId;
    private String type; // DOCX, XLSX, TXT
    private JobStatus status;
    private String inputPath;
    private String outputPath;
    private String originalFilename;
    private long fileSize;
    private String errorMessage;
    private Timestamp createdAt;
    private Timestamp startedAt;
    private Timestamp finishedAt;
    
    // Constructors
    public Job() {}
    
    public Job(int userId, String type, String inputPath, String originalFilename, long fileSize) {
        this.userId = userId;
        this.type = type;
        this.inputPath = inputPath;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.status = JobStatus.PENDING;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public JobStatus getStatus() {
        return status;
    }
    
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    public String getInputPath() {
        return inputPath;
    }
    
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }
    
    public Timestamp getFinishedAt() {
        return finishedAt;
    }
    
    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }
    
    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", originalFilename='" + originalFilename + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
