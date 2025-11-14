package util;

import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Hybrid converter for DOCX to PDF with Vietnamese font support
 * Uses both docx4j and iText approaches for maximum compatibility
 */
public class VietnamesePdfConverter {
    
    private static final Logger logger = LoggerFactory.getLogger(VietnamesePdfConverter.class);
    
    /**
     * Convert DOCX to PDF with Vietnamese font support
     * Try docx4j first, fallback to iText if needed
     */
    public static boolean convertDocxToPdf(String inputPath, String outputPath) {
        logger.info("Converting DOCX to PDF with Vietnamese support: {} -> {}", inputPath, outputPath);
        
        try {
            // Try docx4j approach first (preserves formatting better)
            if (convertWithDocx4j(inputPath, outputPath)) {
                logger.info("Successfully converted using docx4j");
                return true;
            }
            
            logger.warn("docx4j conversion failed, trying iText fallback");
            
            // Fallback to iText with Vietnamese font
            return convertWithITextVietnamese(inputPath, outputPath);
            
        } catch (Exception e) {
            logger.error("All conversion methods failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Convert using docx4j with enhanced Vietnamese font mapping
     */
    private static boolean convertWithDocx4j(String inputPath, String outputPath) {
        try {
            // Load DOCX document
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new FileInputStream(inputPath));
            
            // Configure Vietnamese font mapping
            configureVietnameseFont(wordMLPackage);
            
            // Convert to PDF
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                Docx4J.toPDF(wordMLPackage, fos);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.warn("docx4j conversion failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Convert using iText with Vietnamese-compatible fonts
     */
    private static boolean convertWithITextVietnamese(String inputPath, String outputPath) {
        try {
            logger.info("Using iText fallback with Vietnamese fonts");
            
            // Read DOCX content
            String textContent = extractTextFromDocx(inputPath);
            
            // Create PDF with Vietnamese font
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                PdfWriter writer = new PdfWriter(fos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);
                
                // Try to use a Vietnamese-compatible font
                PdfFont font = getVietnameseFont();
                
                // Add document title
                document.add(new Paragraph("Document Conversion")
                    .setFont(font)
                    .setFontSize(16)
                    .setBold());
                
                document.add(new Paragraph("Source: " + new File(inputPath).getName())
                    .setFont(font)
                    .setFontSize(10));
                
                document.add(new Paragraph(" ")); // Space
                
                // Add content with Vietnamese font support
                String[] paragraphs = textContent.split("\n");
                for (String para : paragraphs) {
                    if (!para.trim().isEmpty()) {
                        document.add(new Paragraph(para.trim())
                            .setFont(font)
                            .setFontSize(12));
                    }
                }
                
                document.close();
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("iText Vietnamese conversion failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Configure Vietnamese font mapping for docx4j
     */
    private static void configureVietnameseFont(WordprocessingMLPackage wordMLPackage) {
        try {
            // Discover system fonts
            PhysicalFonts.discoverPhysicalFonts();
            
            // Create mapper
            Mapper fontMapper = new IdentityPlusMapper();
            wordMLPackage.setFontMapper(fontMapper);
            
            // Vietnamese-compatible fonts in order of preference
            String[] vietnameseFonts = {
                "Times New Roman",
                "Arial Unicode MS", 
                "Arial",
                "Tahoma",
                "Calibri",
                "Verdana",
                "DejaVu Sans",
                "Liberation Sans",
                "Noto Sans"
            };
            
            // Map fonts
            for (String font : vietnameseFonts) {
                if (PhysicalFonts.get(font) != null) {
                    fontMapper.put("Times New Roman", PhysicalFonts.get(font));
                    fontMapper.put("Arial", PhysicalFonts.get(font));
                    fontMapper.put("Calibri", PhysicalFonts.get(font));
                    fontMapper.put("default", PhysicalFonts.get(font));
                    logger.info("Mapped Vietnamese fonts to: {}", font);
                    break;
                }
            }
            
            // Configure docx4j properties for Vietnamese
            System.setProperty("docx4j.jaxb.formatted.output", "true");
            System.setProperty("org.docx4j.fonts.microsoft.font.substitutions", "true");
            
        } catch (Exception e) {
            logger.warn("Font mapping configuration failed: {}", e.getMessage());
        }
    }
    
    /**
     * Get Vietnamese-compatible font for iText
     */
    private static PdfFont getVietnameseFont() {
        try {
            // Try system fonts that support Vietnamese
            String[] fontPaths = {
                "C:/Windows/Fonts/times.ttf",
                "C:/Windows/Fonts/arial.ttf", 
                "C:/Windows/Fonts/calibri.ttf",
                "C:/Windows/Fonts/tahoma.ttf"
            };
            
            for (String fontPath : fontPaths) {
                if (Files.exists(Paths.get(fontPath))) {
                    return PdfFontFactory.createFont(fontPath, "Identity-H");
                }
            }
            
            // Fallback to built-in font with Unicode support
            return PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
        } catch (Exception e) {
            logger.warn("Could not load Vietnamese font, using default: {}", e.getMessage());
            try {
                return PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (Exception e2) {
                throw new RuntimeException("Could not create any font", e2);
            }
        }
    }
    
    /**
     * Extract text content from DOCX file
     */
    private static String extractTextFromDocx(String inputPath) throws Exception {
        StringBuilder content = new StringBuilder();
        
        try (FileInputStream fis = new FileInputStream(inputPath);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    content.append(text).append("\n");
                }
            }
        }
        
        return content.toString();
    }
    
    /**
     * Validate file paths
     */
    public static boolean validatePaths(String inputPath, String outputPath) {
        try {
            if (!Files.exists(Paths.get(inputPath))) {
                logger.error("Input file does not exist: {}", inputPath);
                return false;
            }
            
            String fileName = new File(inputPath).getName().toLowerCase();
            if (!fileName.endsWith(".docx")) {
                logger.error("Input file is not DOCX: {}", inputPath);
                return false;
            }
            
            // Create output directory if needed
            File outputFile = new File(outputPath);
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Path validation failed: {}", e.getMessage(), e);
            return false;
        }
    }
}