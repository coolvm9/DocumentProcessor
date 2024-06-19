package com.fusionz.createtest;

import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatePptxDocument {
    public static void main(String[] args) throws IOException {
        try {
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


            Path resourceDirectory = Paths.get("app","src", "main", "resources");
            if (!Files.exists(resourceDirectory)) {
                Files.createDirectories(resourceDirectory);
            }
            try (FileOutputStream out = new FileOutputStream(resourceDirectory.resolve("test_presentation.pptx").toFile())) {
                ppt.write(out);

            }
            ppt.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}