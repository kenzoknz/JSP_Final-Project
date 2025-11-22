package worker;

import model.bean.Job;
import service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

// Background worker for processing queued conversion jobs - Optimized
public class JobQueueWorker {
    private static final Logger logger = LoggerFactory.getLogger(JobQueueWorker.class);
    
    private final JobService jobService;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService processingPool;
    private final String outputDirectory;
    private final int pollingIntervalSeconds;
    private final int maxConcurrentJobs;
    
    // Timeout for individual job processing (10 minutes)
    private static final long JOB_TIMEOUT_MINUTES = 10;
    
    private volatile boolean running = false;
    private final ConcurrentHashMap<Integer, Future<?>> runningJobs = new ConcurrentHashMap<>();
    
    // Create new job queue worker with optimized thread pools
    public JobQueueWorker(JobService jobService, String outputDirectory, 
                          int pollingIntervalSeconds, int maxConcurrentJobs) {
        this.jobService = jobService;
        this.outputDirectory = outputDirectory;
        this.pollingIntervalSeconds = pollingIntervalSeconds;
        this.maxConcurrentJobs = maxConcurrentJobs;
        
        // Separate scheduler for polling (1 thread) and processing pool for jobs
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Use ThreadPoolExecutor with bounded queue for better resource management
        this.processingPool = new ThreadPoolExecutor(
            maxConcurrentJobs, // core pool size
            maxConcurrentJobs * 2, // max pool size
            60L, TimeUnit.SECONDS, // keep-alive time
            new LinkedBlockingQueue<>(100), // bounded queue
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        
        logger.info("JobQueueWorker initialized with core threads: {}, max threads: {}", 
            maxConcurrentJobs, maxConcurrentJobs * 2);
    }
    
    // Start the worker
    public void start() {
        if (running) {
            logger.warn("Job queue worker is already running");
            return;
        }
        
        running = true;
        
        // Schedule periodic job processing
        scheduler.scheduleWithFixedDelay(
            this::processQueue,
            0, // Initial delay
            pollingIntervalSeconds,
            TimeUnit.SECONDS
        );
        
        logger.info("Job queue worker started - polling every {} seconds, max concurrent jobs: {}", 
            pollingIntervalSeconds, maxConcurrentJobs);
    }
    
    // Stop worker gracefully
    public void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        
        logger.info("Stopping job queue worker...");
        
        // Cancel all running jobs
        for (Future<?> future : runningJobs.values()) {
            future.cancel(true);
        }
        runningJobs.clear();
        
        // Shutdown both thread pools
        scheduler.shutdown();
        processingPool.shutdown();
        
        try {
            // Wait for scheduler to terminate
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Scheduler did not terminate in time, forcing shutdown");
                scheduler.shutdownNow();
            }
            
            // Wait for processing pool to terminate
            if (!processingPool.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warn("Processing pool did not terminate in time, forcing shutdown");
                processingPool.shutdownNow();
                
                if (!processingPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.error("Processing pool failed to terminate");
                }
            }
            
            logger.info("Job queue worker stopped successfully");
            
        } catch (InterruptedException e) {
            logger.error("Worker shutdown interrupted", e);
            scheduler.shutdownNow();
            processingPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Check if worker is running
    public boolean isRunning() {
        return running && !scheduler.isShutdown();
    }
    
    // Process job queue periodically with concurrency control
    private void processQueue() {
        if (!running) {
            return;
        }
        
        try {
            // Clean up completed jobs
            runningJobs.entrySet().removeIf(entry -> entry.getValue().isDone());
            
            // Check if we have capacity for more jobs
            int currentRunning = runningJobs.size();
            if (currentRunning >= maxConcurrentJobs) {
                logger.debug("Max concurrent jobs ({}) reached, {} jobs currently running", 
                    maxConcurrentJobs, currentRunning);
                return;
            }
            
            // Get pending jobs from database
            List<Job> pendingJobs = jobService.getPendingJobs();
            
            if (pendingJobs.isEmpty()) {
                logger.debug("No pending jobs in queue");
                return;
            }
            
            logger.info("Found {} pending jobs in queue, {} currently running", 
                pendingJobs.size(), currentRunning);
            
            // Process jobs up to the concurrent limit
            int jobsToSubmit = Math.min(pendingJobs.size(), maxConcurrentJobs - currentRunning);
            
            for (int i = 0; i < jobsToSubmit && i < pendingJobs.size(); i++) {
                if (!running) {
                    break;
                }
                
                Job job = pendingJobs.get(i);
                
                // Submit job with timeout
                Future<?> future = processingPool.submit(() -> processJobWithTimeout(job));
                runningJobs.put(job.getId(), future);
                
                logger.info("Submitted job {} for processing (running: {}/{})", 
                    job.getId(), runningJobs.size(), maxConcurrentJobs);
            }
            
        } catch (Exception e) {
            logger.error("Error processing job queue", e);
        }
    }
    
    // Process job with timeout wrapper
    private void processJobWithTimeout(Job job) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> processJob(job));
        
        try {
            future.get(JOB_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            logger.error("Job {} timed out after {} minutes", job.getId(), JOB_TIMEOUT_MINUTES);
            future.cancel(true);
            
            try {
                jobService.getJobDAO().updateJobStatus(
                    job.getId(), 
                    Job.JobStatus.FAILED, 
                    "Job timed out after " + JOB_TIMEOUT_MINUTES + " minutes"
                );
            } catch (Exception ex) {
                logger.error("Failed to update timeout status for job {}", job.getId(), ex);
            }
        } catch (InterruptedException e) {
            logger.warn("Job {} was interrupted", job.getId());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.error("Job {} threw exception", job.getId(), e.getCause());
        } finally {
            executor.shutdownNow();
            runningJobs.remove(job.getId());
        }
    }
    
    // Process single job
    private void processJob(Job job) {
        int jobId = job.getId();
        
        try {
            logger.info("Processing job {}: {} ({})", jobId, job.getOriginalFilename(), job.getType());
            
            // Process the job using JobService
            boolean success = jobService.processJob(jobId, outputDirectory);
            
            if (success) {
                logger.info("Job {} completed successfully", jobId);
            } else {
                logger.error("Job {} failed", jobId);
            }
            
        } catch (Exception e) {
            logger.error("Error processing job " + jobId, e);
            
            // Try to mark job as failed
            try {
                jobService.getJobDAO().updateJobStatus(
                    jobId, 
                    Job.JobStatus.FAILED, 
                    "Worker error: " + e.getMessage()
                );
            } catch (Exception ex) {
                logger.error("Failed to update job status for failed job " + jobId, ex);
            }
        }
    }
    
    /**
     * Get current queue statistics
     */
    public QueueStats getStats() {
        try {
            int pendingCount = jobService.getPendingJobs().size();
            int inProgressCount = jobService.getJobsByStatus(Job.JobStatus.IN_PROGRESS).size();
            
            return new QueueStats(pendingCount, inProgressCount, isRunning());
            
        } catch (Exception e) {
            logger.error("Error getting queue stats", e);
            return new QueueStats(0, 0, isRunning());
        }
    }
    
    /**
     * Queue statistics
     */
    public static class QueueStats {
        private final int pendingJobs;
        private final int processingJobs;
        private final boolean workerRunning;
        
        public QueueStats(int pendingJobs, int processingJobs, boolean workerRunning) {
            this.pendingJobs = pendingJobs;
            this.processingJobs = processingJobs;
            this.workerRunning = workerRunning;
        }
        
        public int getPendingJobs() {
            return pendingJobs;
        }
        
        public int getProcessingJobs() {
            return processingJobs;
        }
        
        public boolean isWorkerRunning() {
            return workerRunning;
        }
        
        @Override
        public String toString() {
            return String.format("QueueStats{pending=%d, processing=%d, running=%s}", 
                pendingJobs, processingJobs, workerRunning);
        }
    }
}
