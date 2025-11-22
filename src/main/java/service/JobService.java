package service;

import model.bean.Job;
import model.bean.Job.JobStatus;
import model.dao.JobDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

// Service layer for managing conversion jobs
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    
    private final JobDAO jobDAO;
    private final FileConverterService converterService;
    
    public JobService() {
        this.jobDAO = new JobDAO();
        this.converterService = new FileConverterService();
    }
    
    public JobService(JobDAO jobDAO, FileConverterService converterService) {
        this.jobDAO = jobDAO;
        this.converterService = converterService;
    }
    
    // Create new conversion job
    public Job createJob(int userId, String inputPath, String originalFilename, long fileSize) {
        logger.info("Creating new job for user {}: {}", userId, originalFilename);
        
        // Determine file type from filename
        String fileType = converterService.getFileType(originalFilename);
        
        // Validate file type
        if (!converterService.isFileTypeSupported(fileType)) {
            logger.error("Unsupported file type: {}", fileType);
            return null;
        }
        
        // Create job object
        Job job = new Job();
        job.setUserId(userId);
        job.setType(fileType);
        job.setStatus(JobStatus.PENDING);
        job.setInputPath(inputPath);
        job.setOriginalFilename(originalFilename);
        job.setFileSize(fileSize);
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        boolean created = jobDAO.createJob(job);
        
        if (created) {
            logger.info("Successfully created job with ID: {}", job.getId());
            return job;
        } else {
            logger.error("Failed to create job for user {}", userId);
            return null;
        }
    }
    
    // Process conversion job
    public boolean processJob(int jobId, String outputDirectory) {
        logger.info("Processing job ID: {}", jobId);
        
        // Get job from database
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            logger.error("Job not found: {}", jobId);
            return false;
        }
        
        // Update status to IN_PROGRESS
        jobDAO.updateJobStatus(jobId, JobStatus.IN_PROGRESS, null);
        
        try {
            // Generate output filename
            String outputFilename = generateOutputFilename(job.getOriginalFilename());
            String outputPath = outputDirectory + File.separator + outputFilename;
            
            // Perform conversion
            converterService.convertToPdf(job.getInputPath(), outputPath, job.getType());
            
            // Update job with output path
            jobDAO.updateJobOutputPath(jobId, outputPath);
            
            // Update status to COMPLETED
            jobDAO.updateJobStatus(jobId, JobStatus.COMPLETED, null);
            
            logger.info("Successfully processed job {}: {}", jobId, outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing job " + jobId, e);
            
            // Update status to FAILED with error message
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName();
            }
            
            jobDAO.updateJobStatus(jobId, JobStatus.FAILED, errorMessage);
            return false;
        }
    }
    
    // Get all jobs for user
    public List<Job> getUserJobs(int userId) {
        logger.debug("Fetching jobs for user {}", userId);
        return jobDAO.getJobsByUserId(userId);
    }
    
    // Get job by ID
    public Job getJob(int jobId) {
        return jobDAO.getJobById(jobId);
    }
    
    // Get all pending jobs
    public List<Job> getPendingJobs() {
        return jobDAO.getJobsByStatus(JobStatus.PENDING);
    }
    
    // Get jobs by status
    public List<Job> getJobsByStatus(JobStatus status) {
        return jobDAO.getJobsByStatus(status);
    }
    
    // Get JobDAO instance
    public JobDAO getJobDAO() {
        return jobDAO;
    }
    
    // Delete job and files
    public boolean deleteJob(int jobId, boolean deleteFiles) {
        logger.info("Deleting job {}, deleteFiles={}", jobId, deleteFiles);
        
        if (deleteFiles) {
            Job job = jobDAO.getJobById(jobId);
            if (job != null) {
                // Delete input file
                if (job.getInputPath() != null) {
                    File inputFile = new File(job.getInputPath());
                    if (inputFile.exists()) {
                        boolean deleted = inputFile.delete();
                        logger.debug("Deleted input file: {} - {}", inputFile, deleted);
                    }
                }
                
                // Delete output file
                if (job.getOutputPath() != null) {
                    File outputFile = new File(job.getOutputPath());
                    if (outputFile.exists()) {
                        boolean deleted = outputFile.delete();
                        logger.debug("Deleted output file: {} - {}", outputFile, deleted);
                    }
                }
            }
        }
        
        return jobDAO.deleteJob(jobId);
    }
    
    // Cancel pending job
    public boolean cancelJob(int jobId) {
        logger.info("Cancelling job: {}", jobId);
        
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            logger.warn("Job not found: {}", jobId);
            return false;
        }
        
        // Only allow canceling PENDING jobs
        if (job.getStatus() != JobStatus.PENDING) {
            logger.warn("Cannot cancel job {} - status is {}", jobId, job.getStatus());
            return false;
        }
        
        // Update status to FAILED with cancellation message
        boolean updated = jobDAO.updateJobStatus(jobId, JobStatus.FAILED, "Job cancelled by user");
        
        if (updated) {
            logger.info("Job {} cancelled successfully", jobId);
        } else {
            logger.error("Failed to cancel job {}", jobId);
        }
        
        return updated;
    }
    
    // Count user jobs
    public int countUserJobs(int userId) {
        return jobDAO.countJobsByUser(userId);
    }
    
    // Generate output filename from original
    private String generateOutputFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "output.pdf";
        }
        
        // Remove extension and add .pdf
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            return originalFilename.substring(0, lastDot) + ".pdf";
        }
        
        return originalFilename + ".pdf";
    }
}
