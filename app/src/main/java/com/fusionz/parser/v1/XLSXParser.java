package com.fusionz.parser.v1;

import com.fusionz.datastructures.DocumentStructure;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class XLSXParser implements Parser {

    @Override
    public String parseFullText(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            StringBuilder fullText = new StringBuilder();

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        fullText.append(getCellValue(cell)).append("\t");
                    }
                    fullText.append("\n");
                }
            }
            return fullText.toString();
        }
    }

    @Override
    public DocumentStructure parse(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            DocumentStructure docStructure = new DocumentStructure();
            docStructure.setPages(new HashMap<>());

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                DocumentStructure.Page page = new DocumentStructure.Page();
                page.setPageNumber(sheetIndex + 1);
                page.setTables(new ArrayList<>());

                // Extract tables
                DocumentStructure.Table docTable = new DocumentStructure.Table();
                docTable.setHeader(new ArrayList<>());
                docTable.setRows(new ArrayList<>());

                for (Row row : sheet) {
                    DocumentStructure.Row docRow = new DocumentStructure.Row();
                    docRow.setCells(new ArrayList<>());

                    for (Cell cell : row) {
                        String cellValue = getCellValue(cell);
                        docRow.getCells().add(cellValue);
                    }

                    if (row.getRowNum() == 0) {
                        docTable.getHeader().addAll(docRow.getCells());
                    } else {
                        docTable.getRows().add(docRow);
                    }
                }

                page.getTables().add(docTable);
                docStructure.getPages().put(page.getPageNumber(), page);
            }

            return docStructure;
        }
    }

    private static String getCellValue(Cell cell) {
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
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
