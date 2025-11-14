package servlet;

import model.bean.Job;
import model.bean.User;
import model.dao.JobDAO;
import util.DocxToPdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Servlet for handling file upload and PDF conversion
 * Supports DOCX, XLSX, and TXT file conversion to PDF
 */
@WebServlet(name = "ConvertServlet", urlPatterns = {"/convert"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class ConvertServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ConvertServlet.class);
    
    // Upload and output directories
    private static final String UPLOAD_DIR = "uploads";
    private static final String OUTPUT_DIR = "converted";
    
    private JobDAO jobDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        jobDAO = new JobDAO();
        
        // Create upload and output directories if they don't exist
        createDirectoryIfNotExists(UPLOAD_DIR);
        createDirectoryIfNotExists(OUTPUT_DIR);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp?message=Please login to access conversion feature");
            return;
        }
        
        // Show the upload form
        request.getRequestDispatcher("/convert.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp?message=Please login to convert files");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        try {
            // Get uploaded file
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Please select a file to convert");
                request.getRequestDispatcher("/convert.jsp").forward(request, response);
                return;
            }
            
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "Invalid file name");
                request.getRequestDispatcher("/convert.jsp").forward(request, response);
                return;
            }
            
            // Validate file type
            DocxToPdfConverter.FileType fileType = DocxToPdfConverter.getFileType(fileName);
            if (fileType == DocxToPdfConverter.FileType.UNSUPPORTED) {
                request.setAttribute("error", "Unsupported file type. Please upload DOCX, XLSX, or TXT files only.");
                request.getRequestDispatcher("/convert.jsp").forward(request, response);
                return;
            }
            
            // Generate unique file names
            String uniqueId = UUID.randomUUID().toString();
            String originalFileName = uniqueId + "_" + fileName;
            String pdfFileName = uniqueId + "_" + getFileNameWithoutExtension(fileName) + ".pdf";
            
            // Define file paths
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            String outputPath = getServletContext().getRealPath("") + File.separator + OUTPUT_DIR;
            
            String inputFilePath = uploadPath + File.separator + originalFileName;
            String outputFilePath = outputPath + File.separator + pdfFileName;
            
            // Create job record
            Job job = new Job();
            job.setUserId(user.getId());
            job.setType(fileType.name()); // Set as string
            job.setStatus(Job.JobStatus.PENDING); // Set as enum
            job.setInputPath(inputFilePath);
            job.setOutputPath(outputFilePath);
            job.setOriginalFilename(fileName);
            job.setFileSize(filePart.getSize());
            job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Save job to database - get the generated ID
            boolean jobCreated = jobDAO.createJob(job);
            if (!jobCreated) {
                request.setAttribute("error", "Failed to create conversion job");
                request.getRequestDispatcher("/convert.jsp").forward(request, response);
                return;
            }
            
            // For this demo, we'll use a simple ID generation
            int jobId = (int) System.currentTimeMillis() % 100000;
            
            job.setId(jobId);
            
            // Save uploaded file
            try (InputStream inputStream = filePart.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(inputFilePath)) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            logger.info("File uploaded: {} for user {}", originalFileName, user.getId());
            
            // Update job status to in progress
            jobDAO.updateJobStatus(jobId, Job.JobStatus.IN_PROGRESS, null);
            
            // Perform conversion
            boolean conversionSuccess = false;
            String errorMessage = null;
            
            try {
                // Choose converter based on file type
                String extension = getFileExtension(originalFileName).toLowerCase();
                
                if ("docx".equals(extension)) {
                    // Use Vietnamese-optimized converter for DOCX files
                    if (!util.VietnamesePdfConverter.validatePaths(inputFilePath, outputFilePath)) {
                        throw new Exception("Invalid file paths for DOCX conversion");
                    }
                    conversionSuccess = util.VietnamesePdfConverter.convertDocxToPdf(inputFilePath, outputFilePath);
                } else {
                    // Use original converter for other file types (XLSX, TXT, etc.)
                    if (!DocxToPdfConverter.validatePaths(inputFilePath, outputFilePath)) {
                        throw new Exception("Invalid file paths for conversion");
                    }
                    conversionSuccess = DocxToPdfConverter.convertToPdf(inputFilePath, outputFilePath);
                }
                
                if (!conversionSuccess) {
                    errorMessage = "Conversion failed - unable to process file";
                }
                
            } catch (Exception e) {
                logger.error("Conversion failed for file: {}", inputFilePath, e);
                errorMessage = "Conversion error: " + e.getMessage();
            }
            
            // Update job status based on result
            if (conversionSuccess) {
                jobDAO.updateJobStatus(jobId, Job.JobStatus.COMPLETED, null);
                logger.info("Conversion completed successfully: {}", outputFilePath);
                
                // Redirect to success page with download link
                request.setAttribute("success", "File converted successfully!");
                request.setAttribute("downloadFile", pdfFileName);
                request.setAttribute("originalFile", fileName);
                request.getRequestDispatcher("/convert-success.jsp").forward(request, response);
                
            } else {
                jobDAO.updateJobStatus(jobId, Job.JobStatus.FAILED, errorMessage);
                logger.error("Conversion failed: {}", errorMessage);
                
                request.setAttribute("error", "Conversion failed: " + 
                    (errorMessage != null ? errorMessage : "Unknown error"));
                request.getRequestDispatcher("/convert.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            logger.error("Error processing file conversion request", e);
            request.setAttribute("error", "Server error: " + e.getMessage());
            request.getRequestDispatcher("/convert.jsp").forward(request, response);
        }
    }
    
    /**
     * Extract filename from file part
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim();
                // Remove quotes if present
                if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }
                return fileName;
            }
        }
        return null;
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }
    
    /**
     * Get filename without extension
     */
    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }
    
    /**
     * Create directory if it doesn't exist
     */
    private void createDirectoryIfNotExists(String dirName) {
        try {
            String fullPath = getServletContext().getRealPath("") + File.separator + dirName;
            Path path = Paths.get(fullPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory: {}", fullPath);
            }
        } catch (Exception e) {
            logger.error("Failed to create directory: {}", dirName, e);
        }
    }
}