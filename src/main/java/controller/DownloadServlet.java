package controller;

import model.bean.Job;
import model.bean.User;
import service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Download converted PDF files
@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DownloadServlet.class);
    
    private static final int BUFFER_SIZE = 4096;
    private JobService jobService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        jobService = new JobService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to download files");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String jobIdStr = request.getParameter("jobId");
        
        if (jobIdStr == null || jobIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID not specified");
            return;
        }
        
        try {
            int jobId = Integer.parseInt(jobIdStr);
            
            // Get job from database
            Job job = jobService.getJob(jobId);
            
            if (job == null) {
                logger.warn("Job not found: {} requested by user {}", jobId, user.getId());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found");
                return;
            }
            
            // Security check - verify job belongs to user
            if (job.getUserId() != user.getId()) {
                logger.warn("Unauthorized download attempt: Job {} by user {}", jobId, user.getId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to download this file");
                return;
            }
            
            // Check if job is completed
            if (job.getStatus() != Job.JobStatus.COMPLETED) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "File is not ready. Current status: " + job.getStatus().getValue());
                return;
            }
            
            // Check if output file exists
            String filePath = job.getOutputPath();
            if (filePath == null || filePath.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Output file path not found");
                return;
            }
            
            Path file = Paths.get(filePath);
            
            // Check if file exists
            if (!Files.exists(file)) {
                logger.warn("File not found: {} for job {}", filePath, jobId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found on server");
                return;
            }
            
            // Get file info
            long fileSize = Files.size(file);
            String mimeType = "application/pdf";
            
            // Generate download filename from original filename
            String downloadFileName = job.getOriginalFilename();
            if (downloadFileName != null) {
                // Replace extension with .pdf
                int lastDot = downloadFileName.lastIndexOf('.');
                if (lastDot > 0) {
                    downloadFileName = downloadFileName.substring(0, lastDot) + ".pdf";
                } else {
                    downloadFileName = downloadFileName + ".pdf";
                }
            } else {
                downloadFileName = "converted_" + jobId + ".pdf";
            }
            
            // Set response headers
            response.setContentType(mimeType);
            response.setContentLengthLong(fileSize);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // Stream file to response
            try (InputStream inputStream = Files.newInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                outputStream.flush();
            }
            
            logger.info("File downloaded successfully: Job {} ({}) by user {}", 
                jobId, downloadFileName, user.getId());
            
        } catch (NumberFormatException e) {
            logger.error("Invalid job ID: {}", jobIdStr, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid job ID");
        } catch (IOException e) {
            logger.error("Error downloading file for job: {}", jobIdStr, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading file");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect POST requests to GET
        doGet(request, response);
    }
}