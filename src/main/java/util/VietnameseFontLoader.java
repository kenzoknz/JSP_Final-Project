package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class VietnameseFontLoader {
    private static final Logger logger = LoggerFactory.getLogger(VietnameseFontLoader.class);
    
    private static final String[] VIETNAMESE_FONT_PATHS = {
        "C:/Windows/Fonts/times.ttf",
        "C:/Windows/Fonts/timesbd.ttf",
        "C:/Windows/Fonts/arial.ttf",
        "C:/Windows/Fonts/arialuni.ttf",
        "C:/Windows/Fonts/tahoma.ttf",
        "C:/Windows/Fonts/calibri.ttf",
        "C:/Windows/Fonts/verdana.ttf"
    };
    
    public static PDType0Font loadVietnameseFont(PDDocument document) throws IOException {
        logger.info("Loading Vietnamese-compatible font...");
        
        for (String fontPath : VIETNAMESE_FONT_PATHS) {
            File fontFile = new File(fontPath);
            if (fontFile.exists() && fontFile.canRead()) {
                try {
                    PDType0Font font = PDType0Font.load(document, fontFile);
                    logger.info("Successfully loaded font: {}", fontPath);
                    return font;
                } catch (IOException e) {
                    logger.warn("Failed to load font: {} - {}", fontPath, e.getMessage());
                }
            }
        }
        
        String customFontPath = System.getenv("VIETNAMESE_FONT_PATH");
        if (customFontPath != null) {
            File customFont = new File(customFontPath);
            if (customFont.exists()) {
                try {
                    PDType0Font font = PDType0Font.load(document, customFont);
                    logger.info("Successfully loaded custom font: {}", customFontPath);
                    return font;
                } catch (IOException e) {
                    logger.warn("Failed to load custom font: {}", customFontPath);
                }
            }
        }
        
        try {
            InputStream fontStream = VietnameseFontLoader.class.getResourceAsStream("/fonts/arial.ttf");
            if (fontStream != null) {
                PDType0Font font = PDType0Font.load(document, fontStream);
                logger.info("Successfully loaded font from classpath");
                return font;
            }
        } catch (Exception e) {
            logger.warn("Failed to load font from classpath");
        }
        
        throw new IOException(
            "No Vietnamese-compatible font found. Please ensure Arial, Times New Roman, " +
            "or Arial Unicode MS is installed, or set VIETNAMESE_FONT_PATH environment variable."
        );
    }
    
    public static PDType0Font loadFontFromPath(PDDocument document, String fontPath) throws IOException {
        File fontFile = new File(fontPath);
        if (!fontFile.exists()) {
            throw new IOException("Font file not found: " + fontPath);
        }
        return PDType0Font.load(document, fontFile);
    }
    
    public static boolean supportsVietnamese(String fontPath) {
        File fontFile = new File(fontPath);
        if (!fontFile.exists()) {
            return false;
        }
        
        String fileName = fontFile.getName().toLowerCase();
        return fileName.contains("arial") || 
               fileName.contains("times") || 
               fileName.contains("tahoma") ||
               fileName.contains("calibri") ||
               fileName.contains("unicode");
    }
}
