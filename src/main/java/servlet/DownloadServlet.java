package servlet;

import model.bean.User;
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

/**
 * Servlet for downloading converted PDF files
 * Provides secure access to converted files for logged-in users
 */
@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DownloadServlet.class);
    
    private static final String OUTPUT_DIR = "converted";
    private static final int BUFFER_SIZE = 4096;
    
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
        String fileName = request.getParameter("file");
        
        if (fileName == null || fileName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name not specified");
            return;
        }
        
        // Security check - prevent directory traversal attacks
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            logger.warn("Potential directory traversal attempt by user {}: {}", user.getId(), fileName);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid file name");
            return;
        }
        
        // Ensure file has .pdf extension
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only PDF files can be downloaded");
            return;
        }
        
        try {
            // Construct file path
            String outputPath = getServletContext().getRealPath("") + File.separator + OUTPUT_DIR;
            String filePath = outputPath + File.separator + fileName;
            
            Path file = Paths.get(filePath);
            
            // Check if file exists
            if (!Files.exists(file)) {
                logger.warn("File not found: {} for user {}", filePath, user.getId());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }
            
            // Additional security check - ensure file is within output directory
            Path outputDir = Paths.get(outputPath).toRealPath();
            Path requestedFile = file.toRealPath();
            if (!requestedFile.startsWith(outputDir)) {
                logger.warn("Unauthorized file access attempt by user {}: {}", user.getId(), requestedFile);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized file access");
                return;
            }
            
            // Get file info
            long fileSize = Files.size(file);
            String mimeType = getServletContext().getMimeType(fileName);
            if (mimeType == null) {
                mimeType = "application/pdf";
            }
            
            // Set response headers
            response.setContentType(mimeType);
            response.setContentLengthLong(fileSize);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + getCleanFileName(fileName) + "\"");
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
            
            logger.info("File downloaded successfully: {} by user {}", fileName, user.getId());
            
        } catch (IOException e) {
            logger.error("Error downloading file: {} for user {}", fileName, user.getId(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error downloading file");
        }
    }
    
    /**
     * Clean filename to prevent issues with special characters
     */
    private String getCleanFileName(String fileName) {
        // Remove UUID prefix if present (format: uuid_originalname.pdf)
        if (fileName.contains("_")) {
            String[] parts = fileName.split("_", 2);
            if (parts.length > 1) {
                // Check if first part looks like a UUID (simple check)
                if (parts[0].length() == 36 && parts[0].contains("-")) {
                    return parts[1];
                }
            }
        }
        
        // Return original name if no UUID prefix found
        return fileName;
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect POST requests to GET
        doGet(request, response);
    }
}