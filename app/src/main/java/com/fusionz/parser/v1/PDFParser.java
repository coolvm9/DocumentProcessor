package com.fusionz.parser.v1;

import com.fusionz.datastructures.DocumentStructure;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PDFParser implements Parser {

    @Override
    public String parseFullText(String filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }

    @Override
    public DocumentStructure parse(String filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            DocumentStructure docStructure = new DocumentStructure();
            docStructure.setPages(new HashMap<>());

            PDFTextStripper textStripper = new PDFTextStripper();
            BasicExtractionAlgorithm extractor = new BasicExtractionAlgorithm();

            for (int pageNumber = 0; pageNumber < document.getNumberOfPages(); pageNumber++) {
                PDPage pdfPage = document.getPage(pageNumber);
                DocumentStructure.Page page = new DocumentStructure.Page();
                page.setPageNumber(pageNumber + 1);
                page.setParagraphs(new ArrayList<>());
                page.setTables(new ArrayList<>());

                // Extract paragraphs
                textStripper.setStartPage(pageNumber + 1);
                textStripper.setEndPage(pageNumber + 1);
                String text = textStripper.getText(document);
                for (String paragraphText : text.split("\n\n")) {
                    DocumentStructure.Paragraph paragraph = new DocumentStructure.Paragraph();
                    paragraph.setText(paragraphText.trim());
                    page.getParagraphs().add(paragraph);
                }

                // Extract tables
                ObjectExtractor oe = new ObjectExtractor(document);
                Page tabulaPage = oe.extract(pageNumber + 1);
                List<Table> tables = extractor.extract(tabulaPage);
                for (Table table : tables) {
                    DocumentStructure.Table docTable = new DocumentStructure.Table();
                    docTable.setHeader(new ArrayList<>());
                    docTable.setRows(new ArrayList<>());

                    List<List<technology.tabula.RectangularTextContainer>> rows = table.getRows();
                    if (!rows.isEmpty()) {
                        for (technology.tabula.RectangularTextContainer headerCell : rows.get(0)) {
                            docTable.getHeader().add(headerCell.getText());
                        }
                    }
                    for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
                        DocumentStructure.Row row = new DocumentStructure.Row();
                        row.setCells(new ArrayList<>());
                        for (technology.tabula.RectangularTextContainer cell : rows.get(rowIndex)) {
                            row.getCells().add(cell.getText());
                        }
                        docTable.getRows().add(row);
                    }
                    page.getTables().add(docTable);
                }

                docStructure.getPages().put(page.getPageNumber(), page);
            }

            return docStructure;
        }
    }
}