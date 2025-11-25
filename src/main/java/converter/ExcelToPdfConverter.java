package converter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.VietnameseFontLoader;

import java.io.*;
import java.text.DecimalFormat;

public class ExcelToPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelToPdfConverter.class);
    
    static {
        IOUtils.setByteArrayMaxOverride(200 * 1024 * 1024);
    }
    
    private static final int MAX_ROWS_PER_SHEET = 5000;
    
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 40;
    private static final float ROW_HEIGHT = 15;
    private static final float FONT_SIZE = 10;
    private static final float CELL_PADDING = 3;
    
    private final DecimalFormat numberFormat = new DecimalFormat("#,##0.##");
    
    public void convert(String inputPath, String outputPath) throws IOException {
        logger.info("Starting optimized Excel to PDF conversion: {} -> {}", inputPath, outputPath);
        long startTime = System.currentTimeMillis();
        
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new IOException("Input file not found: " + inputPath);
        }
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile), 64 * 1024);
             Workbook workbook = WorkbookFactory.create(bis);
             PDDocument pdf = new PDDocument()) {
            
            PDType0Font font = VietnameseFontLoader.loadVietnameseFont(pdf);
            
            int numberOfSheets = workbook.getNumberOfSheets();
            logger.info("Found {} sheets in Excel file", numberOfSheets);
            
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                logger.info("Processing sheet: {}", sheetName);
                
                PDPage page = new PDPage(PDRectangle.A4);
                pdf.addPage(page);
                
                PDPageContentStream contentStream = new PDPageContentStream(pdf, page);
                float yPosition = PAGE_HEIGHT - MARGIN;
                
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE + 2);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                String cleanSheetName = sheetName.replaceAll("\\p{Cntrl}", " ");
                contentStream.showText(cleanSheetName);
                contentStream.endText();
                
                yPosition -= ROW_HEIGHT * 1.5f;
                
                Row firstRow = sheet.getRow(sheet.getFirstRowNum());
                int maxColumns = 0;
                if (firstRow != null) {
                    maxColumns = firstRow.getLastCellNum();
                }
                
                float availableWidth = PAGE_WIDTH - 2 * MARGIN;
                float columnWidth = maxColumns > 0 ? availableWidth / maxColumns : availableWidth;
                
                int rowCount = 0;
                int totalRows = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
                logger.info("Sheet '{}' has {} rows", sheetName, totalRows);
                
                for (Row row : sheet) {
                    if (row == null) continue;
                    
                    rowCount++;
                    if (rowCount > MAX_ROWS_PER_SHEET) {
                        logger.warn("Reached max row limit ({}) for sheet '{}', truncating...", MAX_ROWS_PER_SHEET, sheetName);
                        break;
                    }
                    
                    if (totalRows > 200 && rowCount % 100 == 0) {
                        logger.info("Processing row {}/{} in sheet '{}'", rowCount, Math.min(totalRows, MAX_ROWS_PER_SHEET), sheetName);
                    }
                    
                    if (yPosition < MARGIN + ROW_HEIGHT) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        pdf.addPage(page);
                        contentStream = new PDPageContentStream(pdf, page);
                        yPosition = PAGE_HEIGHT - MARGIN;
                    }
                    
                    float xPosition = MARGIN;
                    
                    for (int colIndex = 0; colIndex < maxColumns; colIndex++) {
                        Cell cell = row.getCell(colIndex);
                        String cellValue = getCellValueAsString(cell);
                        
                        String displayValue = truncateText(cellValue, font, FONT_SIZE, columnWidth - 2 * CELL_PADDING);
                        
                        contentStream.beginText();
                        contentStream.setFont(font, FONT_SIZE);
                        contentStream.newLineAtOffset(xPosition + CELL_PADDING, yPosition);
                        String cleanValue = displayValue.replaceAll("\\p{Cntrl}", " ");
                        contentStream.showText(cleanValue);
                        contentStream.endText();
                        
                        xPosition += columnWidth;
                    }
                    
                    yPosition -= ROW_HEIGHT;
                }
                
                contentStream.close();
            }
            
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            pdf.save(outputFile);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully converted Excel to PDF in {}ms: {}", duration, outputPath);
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        String value;
        
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString();
                } else {
                    value = numberFormat.format(cell.getNumericCellValue());
                }
                break;
                
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
                
            case FORMULA:
                try {
                    value = cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        value = numberFormat.format(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        value = cell.getCellFormula();
                    }
                }
                break;
                
            case BLANK:
                value = "";
                break;
                
            default:
                value = "";
        }
        
        return value.replaceAll("\\p{Cntrl}", " ");
    }
    
    private String truncateText(String text, PDType0Font font, float fontSize, float maxWidth) throws IOException {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        
        if (textWidth <= maxWidth) {
            return text;
        }
        
        String ellipsis = "...";
        float ellipsisWidth = font.getStringWidth(ellipsis) / 1000 * fontSize;
        float targetWidth = maxWidth - ellipsisWidth;
        
        for (int i = text.length() - 1; i >= 0; i--) {
            String truncated = text.substring(0, i);
            float width = font.getStringWidth(truncated) / 1000 * fontSize;
            if (width <= targetWidth) {
                return truncated + ellipsis;
            }
        }
        
        return ellipsis;
    }
}
