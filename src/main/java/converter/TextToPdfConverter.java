package converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.VietnameseFontLoader;
import org.apache.poi.util.IOUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Convert TXT files to PDF with Vietnamese font support
public class TextToPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(TextToPdfConverter.class);
    
    // Page settings
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 50;
    private static final float LINE_SPACING = 15;
    private static final float FONT_SIZE = 11;
    
    // Convert TXT to PDF
    public void convert(String inputPath, String outputPath) throws IOException {
        logger.info("Starting TXT to PDF conversion: {} -> {}", inputPath, outputPath);
        // allow larger POI byte arrays
        IOUtils.setByteArrayMaxOverride(100 * 1024 * 1024); // 100 MB
        
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new IOException("Input file not found: " + inputPath);
        }
        
        // Read all lines from text file (UTF-8 encoding for Vietnamese)
        List<String> lines = readAllLines(inputFile);
        logger.info("Read {} lines from TXT file", lines.size());
        
        try (PDDocument pdf = new PDDocument()) {
            // Load Vietnamese-compatible font
            PDType0Font font = VietnameseFontLoader.loadVietnameseFont(pdf);
            
            // Create PDF pages and write content
            PDPage currentPage = new PDPage(PDRectangle.A4);
            pdf.addPage(currentPage);
            
            PDPageContentStream contentStream = new PDPageContentStream(pdf, currentPage);
            float yPosition = PAGE_HEIGHT - MARGIN;
            
            for (String line : lines) {
                // Handle long lines that need wrapping
                List<String> wrappedLines = wrapText(line, font, FONT_SIZE, PAGE_WIDTH - 2 * MARGIN);
                
                for (String wrappedLine : wrappedLines) {
                    // Check if we need a new page
                    if (yPosition < MARGIN + LINE_SPACING) {
                        contentStream.close();
                        currentPage = new PDPage(PDRectangle.A4);
                        pdf.addPage(currentPage);
                        contentStream = new PDPageContentStream(pdf, currentPage);
                        yPosition = PAGE_HEIGHT - MARGIN;
                    }
                    
                    // render a wrapped line (control chars already removed)
                    contentStream.beginText();
                    contentStream.setFont(font, FONT_SIZE);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    contentStream.showText(wrappedLine);
                    contentStream.endText();
                    
                    yPosition -= LINE_SPACING;
                }
            }
            
            contentStream.close();
            
            // Save PDF
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            pdf.save(outputFile);
            
            logger.info("Successfully converted TXT to PDF: {}", outputPath);
        }
    }
    
    // Read lines (UTF-8) and remove control characters
    private List<String> readAllLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.replaceAll("\\p{Cntrl}", ""));
            }
        }
        return lines;
    }
    
    // Wrap text to fit maxWidth
    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // Empty line
        if (text == null || text.trim().isEmpty()) {
            lines.add("");
            return lines;
        }
        
        // Split by spaces for word wrapping
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;
            
            if (width > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Word itself is too long, break it at character level
                    lines.addAll(breakLongWord(word, font, fontSize, maxWidth));
                }
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
    
    // Break a long word into chunks that fit
    private List<String> breakLongWord(String word, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (char c : word.toCharArray()) {
            String testLine = currentLine.toString() + c;
            float width = font.getStringWidth(testLine) / 1000 * fontSize;
            
            if (width > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(String.valueOf(c));
            } else {
                currentLine.append(c);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
}
