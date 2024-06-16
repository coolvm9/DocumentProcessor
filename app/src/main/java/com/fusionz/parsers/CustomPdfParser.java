package com.fusionz.parsers;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomExcelParser implements DocumentParser {

    private final Tokenizer tokenizer;

    public CustomExcelParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Document parse(InputStream fis) {
        List<EnhancedTextSegment> content = new ArrayList<>();
        try (
                Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellText = getCellText(cell);
                        if (cellText != null && !cellText.isEmpty()) {
                            int rowNumber = row.getRowNum();
                            int colNumber = cell.getColumnIndex();
                            Metadata metadata = new Metadata();
                            metadata.put("sheetName", sheetName);
                            metadata.put("rowNumber", String.valueOf(rowNumber));
                            metadata.put("colNumber", String.valueOf(colNumber));

                            content.add(new EnhancedTextSegment(cellText, metadata));
                        }
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        // Combine the content into a single string (for demonstration purposes)
        StringBuilder combinedContent = new StringBuilder();
        for (EnhancedTextSegment text : content) {
            combinedContent.append(text.text()).append("\n");
        }

        // Create the metadata
        Metadata metadata = new Metadata();
        metadata.put("fileType", "Excel");
        metadata.put("fileName", "Excel File Name"); // Modify this line as per your Metadata constructor


        // Create and return the Document object with the metadata
        return new Document(combinedContent.toString(), metadata);
    }
    private String getCellText(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /*public List<EnhancedTextSegment> splitIntoSegments(String content, int chunkSize, int overlap) {
        DocumentSplitter splitter = DocumentSplitters.recursive( chunkSize, overlap,tokenizer);
        List<TextSegment> segments = splitter.(content);

        List<EnhancedTextSegment> enhancedSegments = new ArrayList<>();
        for (TextSegment segment : segments) {
            // Assuming some default values for metadata
            Metadata metadata = new Metadata("", "");
            enhancedSegments.add(new EnhancedTextSegment(segment.getText(), metadata));
        }

        return enhancedSegments;
    }*/
}