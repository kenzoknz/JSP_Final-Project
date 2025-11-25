package service;

import model.bean.Job;
import model.bean.Job.JobStatus;
import model.dao.JobDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

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
    
    public Job createJob(int userId, String inputPath, String originalFilename, long fileSize) {
        logger.info("Creating new job for user {}: {}", userId, originalFilename);
        
        String fileType = converterService.getFileType(originalFilename);
        
        if (!converterService.isFileTypeSupported(fileType)) {
            logger.error("Unsupported file type: {}", fileType);
            return null;
        }
        
        Job job = new Job();
        job.setUserId(userId);
        job.setType(fileType);
        job.setStatus(JobStatus.PENDING);
        job.setInputPath(inputPath);
        job.setOriginalFilename(originalFilename);
        job.setFileSize(fileSize);
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        boolean created = jobDAO.createJob(job);
        
        if (created) {
            logger.info("Successfully created job with ID: {}", job.getId());
            return job;
        } else {
            logger.error("Failed to create job for user {}", userId);
            return null;
        }
    }
    
    public boolean processJob(int jobId, String outputDirectory) {
        logger.info("Processing job ID: {}", jobId);
        
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            logger.error("Job not found: {}", jobId);
            return false;
        }
        
        jobDAO.updateJobStatus(jobId, JobStatus.IN_PROGRESS, null);
        
        try {
            String outputFilename = generateOutputFilename(job.getOriginalFilename());
            String outputPath = outputDirectory + File.separator + outputFilename;
            
            converterService.convertToPdf(job.getInputPath(), outputPath, job.getType());
            
            jobDAO.updateJobOutputPath(jobId, outputPath);
            
            jobDAO.updateJobStatus(jobId, JobStatus.COMPLETED, null);
            
            logger.info("Successfully processed job {}: {}", jobId, outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing job " + jobId, e);
            
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName();
            }
            
            jobDAO.updateJobStatus(jobId, JobStatus.FAILED, errorMessage);
            return false;
        }
    }
    
    public List<Job> getUserJobs(int userId) {
        logger.debug("Fetching jobs for user {}", userId);
        return jobDAO.getJobsByUserId(userId);
    }
    
    public Job getJob(int jobId) {
        return jobDAO.getJobById(jobId);
    }
    
    public List<Job> getPendingJobs() {
        return jobDAO.getJobsByStatus(JobStatus.PENDING);
    }
    
    public List<Job> getPendingJobsBySize() {
        return jobDAO.getPendingJobsBySize();
    }
    
    public List<Job> getJobsByStatus(JobStatus status) {
        return jobDAO.getJobsByStatus(status);
    }
    
    public JobDAO getJobDAO() {
        return jobDAO;
    }
    
    public boolean deleteJob(int jobId, boolean deleteFiles) {
        logger.info("Deleting job {}, deleteFiles={}", jobId, deleteFiles);
        
        if (deleteFiles) {
            Job job = jobDAO.getJobById(jobId);
            if (job != null) {
                if (job.getInputPath() != null) {
                    File inputFile = new File(job.getInputPath());
                    if (inputFile.exists()) {
                        boolean deleted = inputFile.delete();
                        logger.debug("Deleted input file: {} - {}", inputFile, deleted);
                    }
                }
                
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
    
    public boolean cancelJob(int jobId) {
        logger.info("Cancelling job: {}", jobId);
        
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            logger.warn("Job not found: {}", jobId);
            return false;
        }
        
        if (job.getStatus() != JobStatus.PENDING) {
            logger.warn("Cannot cancel job {} - status is {}", jobId, job.getStatus());
            return false;
        }
        
        boolean updated = jobDAO.updateJobStatus(jobId, JobStatus.FAILED, "Job cancelled by user");
        
        if (updated) {
            logger.info("Job {} cancelled successfully", jobId);
        } else {
            logger.error("Failed to cancel job {}", jobId);
        }
        
        return updated;
    }
    
    public int countUserJobs(int userId) {
        return jobDAO.countJobsByUser(userId);
    }
    
    private String generateOutputFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "output.pdf";
        }
        
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            return originalFilename.substring(0, lastDot) + ".pdf";
        }
        
        return originalFilename + ".pdf";
    }
}
