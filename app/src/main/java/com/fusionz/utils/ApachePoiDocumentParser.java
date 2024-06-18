package com.fusionz.utils;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApachePoiDocumentParser {

    public List<TextSegment> getTextSegments(File file) throws IOException {
        List<TextSegment> textSegments = new ArrayList<>();
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".docx")) {
            textSegments.addAll(parseDocx(file));
        } else if (fileName.endsWith(".pptx")) {
            textSegments.addAll(parsePptx(file));
        } else if (fileName.endsWith(".xlsx")) {
            textSegments.addAll(parseXlsx(file));
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }

        return textSegments;
    }

    private List<TextSegment> parseDocx(File file) throws IOException {
        List<TextSegment> textSegments = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        XWPFDocument document = new XWPFDocument(fis);

        // Extract paragraphs
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            textSegments.add(new TextSegment(paragraph.getText(), "paragraph"));
        }

        // Extract tables
        List<XWPFTable> tables = document.getTables();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                StringBuilder rowText = new StringBuilder();
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    rowText.append(cell.getText()).append("\t");
                }
                textSegments.add(new TextSegment(rowText.toString().trim(), "table"));
            }
        }

        document.close();
        fis.close();
        return textSegments;
    }

    private List<TextSegment> parsePptx(File file) throws IOException {
        List<TextSegment> textSegments = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        XMLSlideShow ppt = new XMLSlideShow(fis);

        List<XSLFShape> shapes;
        Iterator<XSLFShape> shapeIterator;
        Iterator<XSLFTableRow> rowIterator;
        Iterator<XSLFTableCell> cellIterator;

        for (int i = 0; i < ppt.getSlides().size(); i++) {
            shapes = ppt.getSlides().get(i).getShapes();
            shapeIterator = shapes.iterator();
            while (shapeIterator.hasNext()) {
                XSLFShape shape = shapeIterator.next();
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    textSegments.add(new TextSegment(textShape.getText(), "paragraph"));
                } else if (shape instanceof XSLFTable) {
                    XSLFTable table = (XSLFTable) shape;
                    rowIterator = table.iterator();
                    while (rowIterator.hasNext()) {
                        XSLFTableRow row = rowIterator.next();
                        StringBuilder rowText = new StringBuilder();
                        cellIterator = row.iterator();
                        while (cellIterator.hasNext()) {
                            XSLFTableCell cell = cellIterator.next();
                            rowText.append(cell.getText()).append("\t");
                        }
                        textSegments.add(new TextSegment(rowText.toString().trim(), "table"));
                    }
                }
            }
        }

        ppt.close();
        fis.close();
        return textSegments;
    }

    private List<TextSegment> parseXlsx(File file) throws IOException {
        List<TextSegment> textSegments = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                org.apache.poi.ss.usermodel.Row row = rowIterator.next();
                StringBuilder rowText = new StringBuilder();
                Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    org.apache.poi.ss.usermodel.Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case STRING:
                            rowText.append(cell.getStringCellValue()).append("\t");
                            break;
                        case NUMERIC:
                            rowText.append(cell.getNumericCellValue()).append("\t");
                            break;
                        case BOOLEAN:
                            rowText.append(cell.getBooleanCellValue()).append("\t");
                            break;
                        case FORMULA:
                            rowText.append(cell.getCellFormula()).append("\t");
                            break;
                        default:
                            rowText.append("\t");
                            break;
                    }
                }
                textSegments.add(new TextSegment(rowText.toString().trim(), "table"));
            }
        }

        workbook.close();
        fis.close();
        return textSegments;
    }

    public static void main(String[] args) {
        try {
            File docxFile = new File("path/to/your/document.docx");
            File pptxFile = new File("path/to/your/presentation.pptx");
            File xlsxFile = new File("path/to/your/spreadsheet.xlsx");

            ApachePoiDocumentParser parser = new ApachePoiDocumentParser();
            List<TextSegment> docxSegments = parser.getTextSegments(docxFile);
            List<TextSegment> pptxSegments = parser.getTextSegments(pptxFile);
            List<TextSegment> xlsxSegments = parser.getTextSegments(xlsxFile);

            System.out.println("Extracted Content from DOCX:");
            for (TextSegment segment : docxSegments) {
                System.out.println(segment);
            }

            System.out.println("Extracted Content from PPTX:");
            for (TextSegment segment : pptxSegments) {
                System.out.println(segment);
            }

            System.out.println("Extracted Content from XLSX:");
            for (TextSegment segment : xlsxSegments) {
                System.out.println(segment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TextSegment {
    private final String text;
    private final String type;

    public TextSegment(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TextSegment{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}