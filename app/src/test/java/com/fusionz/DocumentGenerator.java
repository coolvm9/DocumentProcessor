package com.fusionz;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;

import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocumentGenerator {
    public static void main(String[] args) {
        try {

            Path resourceDirectory = Paths.get("app","src", "test", "resources");
            Files.createDirectories(resourceDirectory);

            createDocxFile(resourceDirectory.resolve("test_document.docx"));
            createPPTXFile(resourceDirectory.resolve("test_presentation.pptx"));
            createPDFFile(resourceDirectory.resolve("test_document.pdf"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void createDocxFile(Path filePath) throws IOException {
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
        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            document.write(out);
        }
        document.close();
    }

    public static void createPDFFile(Path filePath) throws IOException {
        PDDocument document = new PDDocument();

        for (int i = 0; i < 3; i++) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("This is a paragraph on page " + (i + 1));
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(50, 730);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    contentStream.showText("Cell " + (row + 1) + "," + (col + 1) + " on page " + (i + 1) + " ");
                }
                contentStream.newLine();
            }
            contentStream.endText();

            contentStream.close();
        }
        document.save(filePath.toFile());
        document.close();
    }

    public static void createPPTXFile(Path filePath) throws IOException {
            XMLSlideShow ppt = new XMLSlideShow();
            for (int i = 0; i < 3; i++) {
                XSLFSlide slide = ppt.createSlide();

                // Create two text boxes and set their position, size, and text
                for (int j = 0; j < 2; j++) {
                    XSLFTextBox shape = slide.createTextBox();
                    shape.setAnchor(new Rectangle2D.Double(50, 50 + j * 100, 300, 50));
                    shape.setText("Title " + (j + 1) + " on slide " + (i + 1));
                    XSLFTextParagraph paragraph = shape.addNewTextParagraph();
                    paragraph.addNewTextRun().setText("This is a paragraph " + (j + 1) + " on slide " + (i + 1));
                }

                // Create two tables and set their position and size
                for (int j = 0; j < 2; j++) {
                    XSLFTable table = slide.createTable();
                    table.setAnchor(new Rectangle2D.Double(50, 200 + j * 200, 300, 150));

                    // Create the header row
                    XSLFTableRow headerRow = table.addRow();
                    headerRow.setHeight(50); // set height of the row
                    for (int col = 0; col < 3; col++) {
                        XSLFTableCell th = headerRow.addCell();
                        XSLFTextParagraph p = th.addNewTextParagraph();
                        p.addNewTextRun().setText("Header " + (col + 1));

                        // Set border style and color
                        th.setBorderWidth(TableCell.BorderEdge.bottom, 1.0);
                        th.setBorderColor(TableCell.BorderEdge.bottom, java.awt.Color.BLACK);
                    }

                    // Create the rest of the rows
                    for (int row = 1; row < 3; row++) {
                        XSLFTableRow tableRow = table.addRow();
                        tableRow.setHeight(50); // set height of the row
                        for (int col = 0; col < 3; col++) {
                            XSLFTableCell cell = tableRow.addCell();
                            XSLFTextParagraph p = cell.addNewTextParagraph();
                            p.addNewTextRun().setText("Test Data " + row + "," + (col + 1));

                            // Set border style and color
                            cell.setBorderWidth(TableCell.BorderEdge.bottom, 1.0);
                            cell.setBorderColor(TableCell.BorderEdge.bottom, java.awt.Color.BLACK);
                        }
                    }
                }
            }
            try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                ppt.write(out);
            }
            ppt.close();
    }
}
