package util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for converting DOCX, XLSX, and TXT files to PDF
 * Uses Apache POI for file reading and iText for PDF generation
 */
public class DocxToPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(DocxToPdfConverter.class);
    
    // Supported file types
    public enum FileType {
        DOCX, XLSX, TXT, UNSUPPORTED
    }
    
    /**
     * Determine file type based on extension
     */
    public static FileType getFileType(String filename) {
        if (filename == null) return FileType.UNSUPPORTED;
        
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".docx")) {
            return FileType.DOCX;
        } else if (lowerName.endsWith(".xlsx")) {
            return FileType.XLSX;
        } else if (lowerName.endsWith(".txt")) {
            return FileType.TXT;
        }
        
        return FileType.UNSUPPORTED;
    }
    
    /**
     * Convert file to PDF based on file type
     * @param inputPath Path to input file
     * @param outputPath Path where PDF will be saved
     * @return true if conversion successful, false otherwise
     */
    public static boolean convertToPdf(String inputPath, String outputPath) {
        try {
            FileType fileType = getFileType(inputPath);
            
            switch (fileType) {
                case DOCX:
                    return convertDocxToPdf(inputPath, outputPath);
                case XLSX:
                    return convertXlsxToPdf(inputPath, outputPath);
                case TXT:
                    return convertTxtToPdf(inputPath, outputPath);
                default:
                    logger.error("Unsupported file type for conversion: {}", inputPath);
                    return false;
            }
            
        } catch (Exception e) {
            logger.error("Error converting file {} to PDF", inputPath, e);
            return false;
        }
    }
    
    /**
     * Convert DOCX to PDF using Apache POI and iText
     */
    private static boolean convertDocxToPdf(String inputPath, String outputPath) {
        logger.info("Converting DOCX to PDF: {} -> {}", inputPath, outputPath);
        
        try (FileInputStream fis = new FileInputStream(inputPath);
             XWPFDocument document = new XWPFDocument(fis);
             FileOutputStream fos = new FileOutputStream(outputPath)) {
            
            // Create PDF document
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document pdfDocument = new Document(pdfDoc);
            
            // Set font that supports Vietnamese characters
            PdfFont font = PdfFontFactory.createFont("Helvetica", "UTF-8");
            
            // Add document title
            Paragraph title = new Paragraph("Word Document Conversion")
                .setFont(font)
                .setFontSize(16)
                .setBold();
            pdfDocument.add(title);
            
            // Add file info
            Paragraph info = new Paragraph("Source: " + new File(inputPath).getName())
                .setFont(font)
                .setFontSize(10);
            pdfDocument.add(info);
            
            pdfDocument.add(new Paragraph(" ")); // Space
            
            // Extract and add paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    Paragraph pdfParagraph = new Paragraph(text)
                        .setFont(font)
                        .setFontSize(12);
                    pdfDocument.add(pdfParagraph);
                }
            }
            
            pdfDocument.close();
            logger.info("Successfully converted DOCX to PDF: {}", outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error converting DOCX to PDF: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
    
    /**
     * Convert XLSX to PDF using Apache POI and iText
     */
    private static boolean convertXlsxToPdf(String inputPath, String outputPath) {
        logger.info("Converting XLSX to PDF: {} -> {}", inputPath, outputPath);
        
        try (FileInputStream fis = new FileInputStream(inputPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(outputPath)) {
            
            // Create PDF document
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document pdfDocument = new Document(pdfDoc);
            
            // Set font that supports Vietnamese characters
            PdfFont font = PdfFontFactory.createFont("Helvetica", "UTF-8");
            
            // Add document title
            Paragraph title = new Paragraph("Excel Document Conversion")
                .setFont(font)
                .setFontSize(16)
                .setBold();
            pdfDocument.add(title);
            
            // Add file info
            Paragraph info = new Paragraph("Source: " + new File(inputPath).getName())
                .setFont(font)
                .setFontSize(10);
            pdfDocument.add(info);
            
            pdfDocument.add(new Paragraph(" ")); // Space
            
            // Process each sheet
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                
                // Add sheet title
                Paragraph sheetTitle = new Paragraph("Sheet: " + sheet.getSheetName())
                    .setFont(font)
                    .setFontSize(14)
                    .setBold();
                pdfDocument.add(sheetTitle);
                
                // Create table for sheet data
                int maxCols = getMaxColumnsInSheet(sheet);
                if (maxCols > 0) {
                    Table table = new Table(maxCols);
                    // table.setWidthPercent(100); // Skip table width for now
                    
                    // Add rows to table
                    int rowCount = 0;
                    for (Row row : sheet) {
                        if (rowCount++ > 50) break; // Limit to first 50 rows
                        
                        for (int colIndex = 0; colIndex < maxCols; colIndex++) {
                            Cell cell = row.getCell(colIndex);
                            String cellText = getCellValueAsString(cell);
                            
                            com.itextpdf.layout.element.Cell pdfCell = 
                                new com.itextpdf.layout.element.Cell()
                                    .add(new Paragraph(cellText).setFont(font).setFontSize(10));
                            table.addCell(pdfCell);
                        }
                    }
                    
                    pdfDocument.add(table);
                }
                
                pdfDocument.add(new Paragraph(" ")); // Space between sheets
            }
            
            pdfDocument.close();
            logger.info("Successfully converted XLSX to PDF: {}", outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error converting XLSX to PDF: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
    
    /**
     * Convert TXT to PDF using iText
     */
    private static boolean convertTxtToPdf(String inputPath, String outputPath) {
        logger.info("Converting TXT to PDF: {} -> {}", inputPath, outputPath);
        
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            // Read text content
            String content = Files.readString(Paths.get(inputPath), StandardCharsets.UTF_8);
            
            // Create PDF document
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document pdfDocument = new Document(pdfDoc);
            
            // Set font that supports Vietnamese characters
            PdfFont font = PdfFontFactory.createFont("Helvetica", "UTF-8");
            
            // Add document title
            Paragraph title = new Paragraph("Text Document Conversion")
                .setFont(font)
                .setFontSize(16)
                .setBold();
            pdfDocument.add(title);
            
            // Add file info
            Paragraph info = new Paragraph("Source: " + new File(inputPath).getName())
                .setFont(font)
                .setFontSize(10);
            pdfDocument.add(info);
            
            pdfDocument.add(new Paragraph(" ")); // Space
            
            // Add content paragraphs
            String[] paragraphs = content.split("\n");
            for (String paragraphText : paragraphs) {
                if (paragraphText.trim().isEmpty()) {
                    pdfDocument.add(new Paragraph(" ").setFont(font)); // Empty line
                } else {
                    Paragraph paragraph = new Paragraph(paragraphText.trim())
                        .setFont(font)
                        .setFontSize(12);
                    pdfDocument.add(paragraph);
                }
            }
            
            pdfDocument.close();
            logger.info("Successfully converted TXT to PDF: {}", outputPath);
            return true;
            
        } catch (Exception e) {
            logger.error("Error converting TXT to PDF: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
    
    /**
     * Helper method to get maximum number of columns in a sheet
     */
    private static int getMaxColumnsInSheet(XSSFSheet sheet) {
        int maxCols = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > maxCols) {
                maxCols = row.getLastCellNum();
            }
        }
        return maxCols;
    }
    
    /**
     * Helper method to get cell value as string
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf((int) cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }
    
    /**
     * Validate input file exists and output directory is writable
     */
    public static boolean validatePaths(String inputPath, String outputPath) {
        try {
            // Check input file exists
            Path input = Paths.get(inputPath);
            if (!Files.exists(input)) {
                logger.error("Input file does not exist: {}", inputPath);
                return false;
            }
            
            // Check output directory exists and is writable
            Path output = Paths.get(outputPath);
            Path outputDir = output.getParent();
            if (outputDir != null && !Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating paths: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
    
    /**
     * Get estimated output file size (rough approximation)
     */
    public static long getEstimatedPdfSize(String inputPath) {
        try {
            long inputSize = Files.size(Paths.get(inputPath));
            // PDF is usually larger than source due to formatting
            // This is a rough estimation
            return Math.round(inputSize * 1.5);
        } catch (Exception e) {
            logger.warn("Could not estimate PDF size for: {}", inputPath, e);
            return 0;
        }
    }
}