package com.fusionz.parser.v1;

import com.fusionz.datastructures.DocumentStructure;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DOCXParser implements Parser {

    @Override
    public String parseFullText(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder fullText = new StringBuilder();
            for (XWPFParagraph para : document.getParagraphs()) {
                fullText.append(para.getText()).append("\n");
            }
            return fullText.toString();
        }
    }

    @Override
    public DocumentStructure parse(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             XWPFDocument document = new XWPFDocument(fis)) {

            DocumentStructure docStructure = new DocumentStructure();
            docStructure.setPages(new HashMap<>());
            int pageNumber = 1;

            DocumentStructure.Page page = new DocumentStructure.Page();
            page.setPageNumber(pageNumber);
            page.setParagraphs(new ArrayList<>());
            page.setTables(new ArrayList<>());

            // Extract paragraphs
            for (XWPFParagraph para : document.getParagraphs()) {
                DocumentStructure.Paragraph paragraph = new DocumentStructure.Paragraph();
                paragraph.setText(para.getText());
                page.getParagraphs().add(paragraph);
            }

            // Extract tables
            for (XWPFTable table : document.getTables()) {
                DocumentStructure.Table docTable = new DocumentStructure.Table();
                docTable.setHeader(new ArrayList<>());
                docTable.setRows(new ArrayList<>());

                List<XWPFTableRow> rows = table.getRows();
                if (!rows.isEmpty()) {
                    XWPFTableRow headerRow = rows.get(0);
                    for (XWPFTableCell headerCell : headerRow.getTableCells()) {
                        docTable.getHeader().add(headerCell.getText());
                    }

                    for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
                        XWPFTableRow row = rows.get(rowIndex);
                        DocumentStructure.Row docRow = new DocumentStructure.Row();
                        docRow.setCells(new ArrayList<>());
                        for (XWPFTableCell cell : row.getTableCells()) {
                            docRow.getCells().add(cell.getText());
                        }
                        docTable.getRows().add(docRow);
                    }
                }

                page.getTables().add(docTable);
            }

            docStructure.getPages().put(page.getPageNumber(), page);

            return docStructure;
        }
    }
}
