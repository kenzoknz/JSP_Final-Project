package controller;

import model.dao.JobDAO;
import model.bean.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/job-status")
public class JobStatusServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(JobStatusServlet.class);
    private JobDAO jobDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        jobDAO = new JobDAO();
        logger.info("JobStatusServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jobIdParam = request.getParameter("jobId");
        
        if (jobIdParam == null || jobIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing jobId parameter\"}");
            return;
        }
        
        try {
            long jobId = Long.parseLong(jobIdParam);
            
            Job job = jobDAO.getJobById((int) jobId);
            
            if (job == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Job not found\"}");
                return;
            }
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"jobId\":").append(job.getId()).append(",");
            json.append("\"status\":\"").append(job.getStatus()).append("\",");
            json.append("\"fileName\":\"").append(escapeJson(job.getOriginalFilename())).append("\",");
            json.append("\"fileSize\":").append(job.getFileSize()).append(",");
            json.append("\"createdAt\":\"").append(job.getCreatedAt()).append("\",");
            
            if (job.getFinishedAt() != null) {
                json.append("\"completedAt\":\"").append(job.getFinishedAt()).append("\",");
            }
            
            if (job.getErrorMessage() != null && !job.getErrorMessage().isEmpty()) {
                json.append("\"errorMessage\":\"").append(escapeJson(job.getErrorMessage())).append("\",");
            }
            
            if (job.getOutputPath() != null && !job.getOutputPath().isEmpty()) {
                json.append("\"outputFilePath\":\"").append(escapeJson(job.getOutputPath())).append("\",");
            }
            
            int progress = calculateProgress(job);
            json.append("\"progress\":").append(progress);
            
            json.append("}");
            
            response.getWriter().write(json.toString());
            logger.debug("Job status returned for job ID: {}", jobId);
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid jobId format\"}");
            logger.warn("Invalid jobId format: {}", jobIdParam);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\"}");
            logger.error("Error retrieving job status", e);
        }
    }
    
    private int calculateProgress(Job job) {
        switch (job.getStatus()) {
            case PENDING:
                return 0;
            case IN_PROGRESS:
                return 50;
            case COMPLETED:
                return 100;
            case FAILED:
                return 0;
            default:
                return 0;
        }
    }
    
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    @Override
    public void destroy() {
        logger.info("JobStatusServlet destroyed");
        super.destroy();
    }
}
