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
import java.io.IOException;
import java.util.List;

@WebServlet(name = "JobServlet", urlPatterns = {"/jobs"})
public class JobServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(JobServlet.class);
    
    private JobService jobService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        jobService = new JobService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp?message=Please login to view job history");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        try {
            List<Job> jobs = jobService.getUserJobs(user.getId());
            
            logger.info("Retrieved {} jobs for user {}", jobs.size(), user.getId());
            
            request.setAttribute("jobs", jobs);
            
            String message = request.getParameter("message");
            if (message != null && !message.isEmpty()) {
                request.setAttribute("success", message);
            }
            
            request.getRequestDispatcher("/myJobs.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error retrieving jobs for user " + user.getId(), e);
            request.setAttribute("error", "Error loading job history: " + e.getMessage());
            request.getRequestDispatcher("/myJobs.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        String action = request.getParameter("action");
        
        if ("cancel".equals(action)) {
            String jobIdStr = request.getParameter("jobId");
            
            if (jobIdStr != null && !jobIdStr.isEmpty()) {
                try {
                    int jobId = Integer.parseInt(jobIdStr);
                    
                    // Verify job belongs to user
                    Job job = jobService.getJob(jobId);
                    if (job != null && job.getUserId() == user.getId()) {
                        if (job.getStatus() == Job.JobStatus.PENDING) {
                            boolean cancelled = jobService.cancelJob(jobId);
                            
                            if (cancelled) {
                                logger.info("Cancelled job {} by user {}", jobId, user.getId());
                                response.sendRedirect(request.getContextPath() + "/jobs?message=Job cancelled successfully");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/jobs?message=Failed to cancel job");
                            }
                        } else {
                            response.sendRedirect(request.getContextPath() + "/jobs?message=Cannot cancel job - already " + job.getStatus());
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/jobs?message=Job not found or unauthorized");
                    }
                    
                } catch (NumberFormatException e) {
                    logger.error("Invalid job ID: " + jobIdStr, e);
                    response.sendRedirect(request.getContextPath() + "/jobs?message=Invalid job ID");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/jobs");
            }
            return;
        }
        
        if ("delete".equals(action)) {
            String jobIdStr = request.getParameter("jobId");
            
            if (jobIdStr != null && !jobIdStr.isEmpty()) {
                try {
                    int jobId = Integer.parseInt(jobIdStr);
                    
                    // Verify job belongs to user
                    Job job = jobService.getJob(jobId);
                    if (job != null && job.getUserId() == user.getId()) {
                        boolean deleted = jobService.deleteJob(jobId, true);
                        
                        if (deleted) {
                            logger.info("Deleted job {} by user {}", jobId, user.getId());
                            response.sendRedirect(request.getContextPath() + "/jobs?message=Job deleted successfully");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/jobs?message=Failed to delete job");
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/jobs?message=Job not found or unauthorized");
                    }
                    
                } catch (NumberFormatException e) {
                    logger.error("Invalid job ID: " + jobIdStr, e);
                    response.sendRedirect(request.getContextPath() + "/jobs?message=Invalid job ID");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/jobs");
            }
        } else {
            doGet(request, response);
        }
    }
}
