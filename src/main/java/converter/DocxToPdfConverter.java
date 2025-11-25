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

public class DocxToPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(DocxToPdfConverter.class);
    
    static {
        IOUtils.setByteArrayMaxOverride(200 * 1024 * 1024);
    }
    
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 50;
    private static final float LINE_SPACING = 15;
    private static final float DEFAULT_FONT_SIZE = 12;
    
    public void convert(String inputPath, String outputPath) throws IOException {
        logger.info("Starting optimized DOCX to PDF conversion: {} -> {}", inputPath, outputPath);
        long startTime = System.currentTimeMillis();
        
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new IOException("Input file not found: " + inputPath);
        }
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile), 64 * 1024);
             XWPFDocument docx = new XWPFDocument(bis);
             PDDocument pdf = new PDDocument()) {
            
            PDType0Font font = VietnameseFontLoader.loadVietnameseFont(pdf);
            
            List<XWPFParagraph> paragraphs = docx.getParagraphs();
            int totalParagraphs = paragraphs.size();
            logger.info("Found {} paragraphs in DOCX", totalParagraphs);
            
            PDPage currentPage = new PDPage(PDRectangle.A4);
            pdf.addPage(currentPage);
            
            PDPageContentStream contentStream = new PDPageContentStream(pdf, currentPage);
            float yPosition = PAGE_HEIGHT - MARGIN;
            
            int processedCount = 0;
            for (XWPFParagraph paragraph : paragraphs) {
                processedCount++;
                
                if (totalParagraphs > 500 && processedCount % 100 == 0) {
                    logger.info("Processing paragraph {}/{}", processedCount, totalParagraphs);
                }
                String text = extractTextFromParagraph(paragraph);
                
                if (text.trim().isEmpty()) {
                    yPosition -= LINE_SPACING / 2;
                    continue;
                }
                
                float fontSize = DEFAULT_FONT_SIZE;
                if (!paragraph.getRuns().isEmpty()) {
                    XWPFRun firstRun = paragraph.getRuns().get(0);
                    @SuppressWarnings("deprecation")
                    int runFontSize = firstRun.getFontSize();
                    if (runFontSize > 0) {
                        fontSize = runFontSize;
                    }
                }
                
                List<String> lines = wrapText(text, font, fontSize, PAGE_WIDTH - 2 * MARGIN);
                
                for (String line : lines) {
                    if (yPosition < MARGIN + LINE_SPACING) {
                        contentStream.close();
                        currentPage = new PDPage(PDRectangle.A4);
                        pdf.addPage(currentPage);
                        contentStream = new PDPageContentStream(pdf, currentPage);
                        yPosition = PAGE_HEIGHT - MARGIN;
                    }
                    
                    contentStream.beginText();
                    contentStream.setFont(font, fontSize);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    String cleanLine = line.replaceAll("\\p{Cntrl}", " ");
                    contentStream.showText(cleanLine);
                    contentStream.endText();
                    
                    yPosition -= LINE_SPACING;
                }
                
                yPosition -= LINE_SPACING / 2;
            }
            
            contentStream.close();
            
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            pdf.save(outputFile);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully converted DOCX to PDF in {}ms: {}", duration, outputPath);
        }
    }
    
    private String extractTextFromParagraph(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs.isEmpty()) {
            return "";
        }
        
        StringBuilder text = new StringBuilder(runs.size() * 50);
        for (XWPFRun run : runs) {
            String runText = run.getText(0);
            if (runText != null && !runText.isEmpty()) {
                String cleanText = runText.replaceAll("\\p{Cntrl}", " ");
                text.append(cleanText);
            }
        }
        return text.toString();
    }
    
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
