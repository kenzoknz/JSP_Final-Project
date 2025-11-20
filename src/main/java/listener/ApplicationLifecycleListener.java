package listener;

import service.JobService;
import worker.JobQueueWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

// Manage background job processing lifecycle
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLifecycleListener.class);
    
    private static final String WORKER_ATTRIBUTE = "jobQueueWorker";
    private static final String OUTPUT_DIR = "converted";
    
    // Worker configuration - Optimized for better throughput
    private static final int POLLING_INTERVAL_SECONDS = 3; // Check queue every 3 seconds (faster polling)
    private static final int MAX_CONCURRENT_JOBS = 6; // Process up to 6 jobs concurrently (increased from 3)
    
    private JobQueueWorker worker;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        logger.info("=== Application Starting ===");
        logger.info("Initializing job queue worker...");
        
        try {
            // Get output directory path
            String outputPath = context.getRealPath("") + File.separator + OUTPUT_DIR;
            
            // Ensure output directory exists
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
                logger.info("Created output directory: {}", outputPath);
            }
            
            // Create JobService
            JobService jobService = new JobService();
            
            // Create and start worker
            worker = new JobQueueWorker(
                jobService,
                outputPath,
                POLLING_INTERVAL_SECONDS,
                MAX_CONCURRENT_JOBS
            );
            
            worker.start();
            
            // Store worker in servlet context for access by other components
            context.setAttribute(WORKER_ATTRIBUTE, worker);
            
            logger.info("Job queue worker started successfully");
            logger.info("Configuration: polling interval={}s, max concurrent jobs={}", 
                POLLING_INTERVAL_SECONDS, MAX_CONCURRENT_JOBS);
            
        } catch (Exception e) {
            logger.error("Failed to start job queue worker", e);
            throw new RuntimeException("Application initialization failed", e);
        }
        
        logger.info("=== Application Started ===");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== Application Shutting Down ===");
        logger.info("Stopping job queue worker...");
        
        try {
            if (worker != null) {
                worker.stop();
                logger.info("Job queue worker stopped successfully");
            }
            
        } catch (Exception e) {
            logger.error("Error stopping job queue worker", e);
        }
        
        logger.info("=== Application Stopped ===");
    }
    
    // Get worker from servlet context
    public static JobQueueWorker getWorker(ServletContext context) {
        return (JobQueueWorker) context.getAttribute(WORKER_ATTRIBUTE);
    }
}
