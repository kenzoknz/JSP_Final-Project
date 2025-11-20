package service;

import converter.DocxToPdfConverter;
import converter.ExcelToPdfConverter;
import converter.TextToPdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// Service for orchestrating file conversion to PDF
public class FileConverterService {
    private static final Logger logger = LoggerFactory.getLogger(FileConverterService.class);
    
    private final DocxToPdfConverter docxConverter;
    private final ExcelToPdfConverter excelConverter;
    private final TextToPdfConverter textConverter;
    
    public FileConverterService() {
        this.docxConverter = new DocxToPdfConverter();
        this.excelConverter = new ExcelToPdfConverter();
        this.textConverter = new TextToPdfConverter();
    }
    
    // Convert file to PDF based on type
    public void convertToPdf(String inputPath, String outputPath, String fileType) throws IOException {
        logger.info("Converting {} file to PDF: {}", fileType, inputPath);
        
        String normalizedType = fileType.toLowerCase().trim();
        
        switch (normalizedType) {
            case "docx":
            case ".docx":
                docxConverter.convert(inputPath, outputPath);
                break;
                
            case "xlsx":
            case ".xlsx":
                excelConverter.convert(inputPath, outputPath);
                break;
                
            case "txt":
            case ".txt":
                textConverter.convert(inputPath, outputPath);
                break;
                
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType + 
                    ". Supported types: .docx, .xlsx, .txt");
        }
        
        logger.info("Successfully converted {} to PDF: {}", fileType, outputPath);
    }
    
    // Check if file type is supported
    public boolean isFileTypeSupported(String fileType) {
        String normalizedType = fileType.toLowerCase().trim();
        return normalizedType.equals("docx") || normalizedType.equals(".docx") ||
               normalizedType.equals("xlsx") || normalizedType.equals(".xlsx") ||
               normalizedType.equals("txt") || normalizedType.equals(".txt");
    }
    
    // Get file type from filename
    public String getFileType(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
