package util;

import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for converting DOCX files to PDF using docx4j
 * Handles Vietnamese fonts properly to avoid character encoding issues
 */
public class DocxToPdfConverterDocx4j {
    
    private static final Logger logger = LoggerFactory.getLogger(DocxToPdfConverterDocx4j.class);
    
    /**
     * Convert DOCX file to PDF using docx4j
     * @param inputPath Path to DOCX file
     * @param outputPath Path where PDF will be saved
     * @return true if conversion successful, false otherwise
     */
    public static boolean convertDocxToPdf(String inputPath, String outputPath) {
        try {
            logger.info("Converting DOCX to PDF using docx4j: {} -> {}", inputPath, outputPath);
            
            // Validate input file
            if (!Files.exists(Paths.get(inputPath))) {
                logger.error("Input file does not exist: {}", inputPath);
                return false;
            }
            
            // Create output directory if it doesn't exist
            File outputFile = new File(outputPath);
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Load the DOCX document
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new FileInputStream(inputPath));
            
            // Configure font mapping for Vietnamese characters
            configureFontMapping(wordMLPackage);
            
            // Convert to PDF using docx4j
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                Docx4J.toPDF(wordMLPackage, fos);
            }
            
            logger.info("Successfully converted DOCX to PDF using docx4j: {}", outputPath);
            return true;
            
        } catch (Docx4JException e) {
            logger.error("docx4j conversion error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error converting DOCX to PDF: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
    
    /**
     * Configure font mapping for proper Vietnamese character rendering
     */
    private static void configureFontMapping(WordprocessingMLPackage wordMLPackage) {
        try {
            logger.info("Configuring font mapping for Vietnamese characters");
            
            // Discover physical fonts on the system
            PhysicalFonts.discoverPhysicalFonts();
            
            // Create font mapper with identity mapping
            Mapper fontMapper = new IdentityPlusMapper();
            wordMLPackage.setFontMapper(fontMapper);
            
            // Map common Vietnamese fonts to available system fonts
            mapVietnameseFonts(fontMapper);
            
            // Set encoding and font substitution
            configureEncoding(wordMLPackage, fontMapper);
            
            logger.info("Font mapping configured successfully");
            
        } catch (Exception e) {
            logger.warn("Could not configure font mapping, using default: {}", e.getMessage());
            // Even if font mapping fails, docx4j can still attempt conversion
        }
    }
    
    /**
     * Configure encoding and font substitution for Vietnamese
     */
    private static void configureEncoding(WordprocessingMLPackage wordMLPackage, Mapper fontMapper) {
        try {
            // Set default encoding to UTF-8
            System.setProperty("org.docx4j.fonts.microsoft.font.substitutions", "true");
            System.setProperty("docx4j.jaxb.formatted.output", "true");
            
            // Configure font substitution for Vietnamese characters
            String[] vietnameseCompatibleFonts = {
                "Arial Unicode MS", 
                "Times New Roman", 
                "Arial", 
                "Tahoma", 
                "Calibri",
                "Verdana",
                "DejaVu Sans",
                "Liberation Sans"
            };
            
            // Find the best Vietnamese compatible font
            for (String fontName : vietnameseCompatibleFonts) {
                if (PhysicalFonts.get(fontName) != null) {
                    // Map Vietnamese character ranges to this font
                    fontMapper.put("VietFont", PhysicalFonts.get(fontName));
                    logger.info("Using font '{}' for Vietnamese text", fontName);
                    break;
                }
            }
            
        } catch (Exception e) {
            logger.warn("Could not configure encoding: {}", e.getMessage());
        }
    }
    
    /**
     * Map Vietnamese fonts to available system fonts
     */
    private static void mapVietnameseFonts(Mapper fontMapper) {
        // Common Vietnamese fonts and their alternatives
        String[][] fontMappings = {
            {"Times New Roman", "Times New Roman", "Times", "serif"},
            {"Arial", "Arial", "Helvetica", "sans-serif"},
            {"Calibri", "Calibri", "Arial", "sans-serif"},
            {"Tahoma", "Tahoma", "Arial", "sans-serif"},
            {"Verdana", "Verdana", "Arial", "sans-serif"},
            {"MS Sans Serif", "Arial", "Helvetica", "sans-serif"},
            {"MS Serif", "Times New Roman", "Times", "serif"}
        };
        
        for (String[] mapping : fontMappings) {
            String targetFont = mapping[0];
            
            // Try to find the best available font
            for (int i = 1; i < mapping.length; i++) {
                String candidateFont = mapping[i];
                if (PhysicalFonts.get(candidateFont) != null) {
                    fontMapper.put(targetFont, PhysicalFonts.get(candidateFont));
                    logger.debug("Mapped font '{}' to '{}'", targetFont, candidateFont);
                    break;
                }
            }
        }
        
        // Set default fallback fonts for unmapped fonts
        setDefaultFallbackFont(fontMapper);
    }
    
    /**
     * Set default fallback font for unmapped fonts
     */
    private static void setDefaultFallbackFont(Mapper fontMapper) {
        // List of preferred fallback fonts that support Vietnamese
        String[] fallbackFonts = {
            "Arial Unicode MS",
            "Times New Roman", 
            "Arial",
            "Tahoma",
            "Verdana",
            "DejaVu Sans"
        };
        
        for (String fallbackFont : fallbackFonts) {
            if (PhysicalFonts.get(fallbackFont) != null) {
                fontMapper.put("__DEFAULT__", PhysicalFonts.get(fallbackFont));
                logger.info("Set default fallback font to: {}", fallbackFont);
                break;
            }
        }
    }
    
    /**
     * Check if a DOCX file can be converted
     */
    public static boolean canConvert(String filePath) {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                return false;
            }
            
            String fileName = new File(filePath).getName().toLowerCase();
            return fileName.endsWith(".docx");
            
        } catch (Exception e) {
            logger.error("Error checking if file can be converted: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Get estimated PDF size (rough approximation)
     */
    public static long getEstimatedPdfSize(String inputPath) {
        try {
            long inputSize = Files.size(Paths.get(inputPath));
            // PDF with proper formatting is usually 2-3 times larger than DOCX
            return Math.round(inputSize * 2.5);
        } catch (Exception e) {
            logger.warn("Could not estimate PDF size for: {}", inputPath);
            return 0;
        }
    }
    
    /**
     * Validate file paths before conversion
     */
    public static boolean validatePaths(String inputPath, String outputPath) {
        try {
            // Check input file
            if (!Files.exists(Paths.get(inputPath))) {
                logger.error("Input file does not exist: {}", inputPath);
                return false;
            }
            
            if (!canConvert(inputPath)) {
                logger.error("Input file is not a supported DOCX file: {}", inputPath);
                return false;
            }
            
            // Check output directory
            File outputFile = new File(outputPath);
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    logger.error("Cannot create output directory: {}", outputDir.getAbsolutePath());
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating paths: {} -> {}", inputPath, outputPath, e);
            return false;
        }
    }
}