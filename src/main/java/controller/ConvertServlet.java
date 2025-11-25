package controller;

import model.bean.Job;
import model.bean.User;
import service.FileConverterService;
import service.JobService;
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
import java.util.UUID;

@WebServlet(name = "ConvertServlet", urlPatterns = {"/convert"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
    maxFileSize = 1024 * 1024 * 100,  // 100 MB
    maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class ConvertServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ConvertServlet.class);
    
    private static final String UPLOAD_DIR = "uploads";
    private static final String OUTPUT_DIR = "converted";
    
    private JobService jobService;
    private FileConverterService converterService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        jobService = new JobService();
        converterService = new FileConverterService();
        
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
        
        request.getRequestDispatcher("/submit.jsp").forward(request, response);
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
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Please select a file to convert");
                request.getRequestDispatcher("/submit.jsp").forward(request, response);
                return;
            }
            
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                request.setAttribute("error", "Invalid file name");
                request.getRequestDispatcher("/submit.jsp").forward(request, response);
                return;
            }
            
            String fileType = converterService.getFileType(fileName);
            if (!converterService.isFileTypeSupported(fileType)) {
                request.setAttribute("error", "Unsupported file type. Please upload DOCX, XLSX, or TXT files only.");
                request.getRequestDispatcher("/submit.jsp").forward(request, response);
                return;
            }
            
            String uniqueId = UUID.randomUUID().toString();
            String uniqueFileName = uniqueId + "_" + fileName;
            
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            
            String inputFilePath = uploadPath + File.separator + uniqueFileName;
            
            try (InputStream inputStream = filePart.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(inputFilePath)) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            logger.info("File uploaded: {} for user {}", uniqueFileName, user.getId());
            
            Job job = jobService.createJob(user.getId(), inputFilePath, fileName, filePart.getSize());
            
            if (job == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to create conversion job\"}");
                return;
            }
            
            logger.info("Job queued successfully: {} for user {}", job.getId(), user.getId());
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                "{\"status\":\"queued\",\"jobId\":%d,\"message\":\"Your file has been queued for conversion. Processing will begin shortly.\"}",
                job.getId()
            ));

            
        } catch (Exception e) {
            logger.error("Error processing file conversion request", e);
            request.setAttribute("error", "Server error: " + e.getMessage());
            request.getRequestDispatcher("/submit.jsp").forward(request, response);
        }
    }
    
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim();
                if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }
                return fileName;
            }
        }
        return null;
    }
    
    private void createDirectoryIfNotExists(String dirName) {
        try {
            String fullPath = getServletContext().getRealPath("") + File.separator + dirName;
            Path path = Paths.get(fullPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory: {}", fullPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", dirName, e);
        }
    }
}