package com.fusionz.createtest;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateDocxDocument {

    public static void main(String[] args) throws IOException {
        XWPFDocument document = new XWPFDocument();

        for (int i = 0; i < 3; i++) {

            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Title of the Document");
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // Create a spacer
            XWPFParagraph spacer = document.createParagraph();
            XWPFRun spacerRun = spacer.createRun();
            spacerRun.addBreak(); // Adds a line break
// or
            spacerRun.setText(" "); // Adds a space

            XWPFParagraph title1 = document.createParagraph();
            XWPFRun title1Run = title.createRun();
            titleRun.setText("Title of the Section");
            titleRun.setBold(true);
            titleRun.setFontSize(14);

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("This is a paragraph on page " + (i + 1));

            XWPFTable table = document.createTable();

            // Create header row
            XWPFTableRow headerRow = table.getRow(0);
            for (int col = 0; col < 3; col++) {
                XWPFTableCell cell = headerRow.getCell(col);
                if (cell == null) {
                    cell = headerRow.createCell();
                }
                cell.setText("Header " + (col + 1));
                // Set cell color, text color and text style
                cell.setColor("C0C0C0"); // Set cell color to light gray
                XWPFParagraph p = cell.getParagraphs().get(0);
                XWPFRun r = p.createRun();
                r.setBold(true);
                r.setColor("000000"); // Set text color to black
            }

            // Create the rest of the rows
            for (int row = 0; row < 3; row++) {
                XWPFTableRow tableRow = table.createRow();
                for (int col = 0; col < 3; col++) {
                    XWPFTableCell cell = tableRow.getCell(col);
                    if (cell == null) {
                        cell = tableRow.createCell();
                    }
                    cell.setText("Cell " + (row + 1) + "," + (col + 1) + " on page " + (i + 1));
                }
            }

            if (i < 2) {
                document.createParagraph().setPageBreak(true);
            }
        }
        Path resourceDirectory = Paths.get("app", "src", "main", "resources");
        if (!Files.exists(resourceDirectory)) {
            Files.createDirectories(resourceDirectory);
        }

        try (FileOutputStream out = new FileOutputStream(resourceDirectory.resolve("test_document.docx").toFile())) {
            document.write(out);
        }
        document.close();
    }
}