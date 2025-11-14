package model.bean;

import java.sql.Timestamp;

/**
 * Model class representing a file conversion job
 * Maps to the jobs table in database
 */
public class Job {
    private int id;
    private int userId;
    private String type;
    private JobStatus status;
    private String inputPath;
    private String outputPath;
    private String originalFilename;
    private long fileSize;
    private String errorMessage;
    private Timestamp createdAt;
    private Timestamp startedAt;
    private Timestamp finishedAt;

    // Enum for job status
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

        public static JobStatus fromString(String status) {
            for (JobStatus jobStatus : JobStatus.values()) {
                if (jobStatus.getValue().equalsIgnoreCase(status)) {
                    return jobStatus;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    // Enum for file types
    public enum FileType {
        DOCX("DOCX", ".docx"),
        XLSX("XLSX", ".xlsx"),
        TXT("TXT", ".txt");

        private final String type;
        private final String extension;

        FileType(String type, String extension) {
            this.type = type;
            this.extension = extension;
        }

        public String getType() {
            return type;
        }

        public String getExtension() {
            return extension;
        }

        public static FileType fromExtension(String filename) {
            if (filename == null) return null;
            
            filename = filename.toLowerCase();
            for (FileType fileType : FileType.values()) {
                if (filename.endsWith(fileType.getExtension())) {
                    return fileType;
                }
            }
            return null;
        }
    }

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

    // Utility methods
    public String getStatusDisplay() {
        switch (status) {
            case PENDING:
                return "Chờ xử lý";
            case IN_PROGRESS:
                return "Đang xử lý";
            case COMPLETED:
                return "Hoàn thành";
            case FAILED:
                return "Lỗi";
            default:
                return status.getValue();
        }
    }

    public String getTypeName() {
        switch (type) {
            case "DOCX":
                return "Word Document";
            case "XLSX":
                return "Excel Spreadsheet";
            case "TXT":
                return "Text File";
            default:
                return type;
        }
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public boolean isCompleted() {
        return status == JobStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == JobStatus.FAILED;
    }

    public boolean isInProgress() {
        return status == JobStatus.IN_PROGRESS;
    }

    public boolean isPending() {
        return status == JobStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", status=" + status +
                ", originalFilename='" + originalFilename + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                ", finishedAt=" + finishedAt +
                '}';
    }
}