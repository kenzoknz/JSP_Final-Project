package converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.VietnameseFontLoader;

import java.io.*;
import java.util.List;

// Convert DOCX to PDF with Vietnamese support - Optimized for large files
public class DocxToPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(DocxToPdfConverter.class);
    
    // Increase POI byte array max size to handle large files (200MB)
    static {
        IOUtils.setByteArrayMaxOverride(200 * 1024 * 1024); // 200 MB
    }
    
    // Page settings
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 50;
    private static final float LINE_SPACING = 15;
    private static final float DEFAULT_FONT_SIZE = 12;
    
    // Convert DOCX file to PDF - Optimized version
    public void convert(String inputPath, String outputPath) throws IOException {
        logger.info("Starting optimized DOCX to PDF conversion: {} -> {}", inputPath, outputPath);
        long startTime = System.currentTimeMillis();
        
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new IOException("Input file not found: " + inputPath);
        }
        
        // Use buffered stream for better I/O performance
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile), 64 * 1024);
             XWPFDocument docx = new XWPFDocument(bis);
             PDDocument pdf = new PDDocument()) {
            
            // Load Vietnamese-compatible font once
            PDType0Font font = VietnameseFontLoader.loadVietnameseFont(pdf);
            
            // Extract paragraphs from DOCX
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            int totalParagraphs = paragraphs.size();
            logger.info("Found {} paragraphs in DOCX", totalParagraphs);
            
            // Create PDF pages and write content
            PDPage currentPage = new PDPage(PDRectangle.A4);
            pdf.addPage(currentPage);
            
            PDPageContentStream contentStream = new PDPageContentStream(pdf, currentPage);
            float yPosition = PAGE_HEIGHT - MARGIN;
            
            int processedCount = 0;
            for (XWPFParagraph paragraph : paragraphs) {
                processedCount++;
                
                // Log progress for large documents
                if (totalParagraphs > 500 && processedCount % 100 == 0) {
                    logger.info("Processing paragraph {}/{}", processedCount, totalParagraphs);
                }
                String text = extractTextFromParagraph(paragraph);
                
                // Skip empty paragraphs
                if (text.trim().isEmpty()) {
                    yPosition -= LINE_SPACING / 2;
                    continue;
                }
                
                // Determine font size from paragraph style
                float fontSize = DEFAULT_FONT_SIZE;
                if (!paragraph.getRuns().isEmpty()) {
                    XWPFRun firstRun = paragraph.getRuns().get(0);
                    @SuppressWarnings("deprecation")
                    int runFontSize = firstRun.getFontSize();
                    if (runFontSize > 0) {
                        fontSize = runFontSize;
                    }
                }
                
                // Handle text that might overflow the line
                List<String> lines = wrapText(text, font, fontSize, PAGE_WIDTH - 2 * MARGIN);
                
                for (String line : lines) {
                    // Check if we need a new page
                    if (yPosition < MARGIN + LINE_SPACING) {
                        contentStream.close();
                        currentPage = new PDPage(PDRectangle.A4);
                        pdf.addPage(currentPage);
                        contentStream = new PDPageContentStream(pdf, currentPage);
                        yPosition = PAGE_HEIGHT - MARGIN;
                    }
                    
                    // Write line to PDF (sanitize to remove control characters)
                    contentStream.beginText();
                    contentStream.setFont(font, fontSize);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    // Remove control characters that fonts cannot render
                    String cleanLine = line.replaceAll("\\p{Cntrl}", " ");
                    contentStream.showText(cleanLine);
                    contentStream.endText();
                    
                    yPosition -= LINE_SPACING;
                }
                
                // Add paragraph spacing
                yPosition -= LINE_SPACING / 2;
            }
            
            contentStream.close();
            
            // Save PDF
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            pdf.save(outputFile);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully converted DOCX to PDF in {}ms: {}", duration, outputPath);
        }
    }
    
    // Extract text from paragraph, remove control chars - Optimized
    private String extractTextFromParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) {
            return "";
        }
        
        // Pre-allocate StringBuilder with estimated capacity
        StringBuilder text = new StringBuilder(runs.size() * 50);
        for (XWPFRun run : runs) {
            String runText = run.getText(0);
            if (runText != null && !runText.isEmpty()) {
                // Remove control characters immediately to reduce string operations
                String cleanText = runText.replaceAll("\\p{Cntrl}", " ");
                text.append(cleanText);
            }
        }
        return text.toString();
    }
    
    // Wrap text to fit width
    private List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new java.util.ArrayList<>();
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
                    // Word itself is too long, break it
                    lines.add(word);
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
}
