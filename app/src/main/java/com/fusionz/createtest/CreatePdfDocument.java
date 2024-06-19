package com.fusionz.createtest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatePdfDocument {

    public static void main(String[] args) throws IOException {
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

        Path resourceDirectory = Paths.get("app","src", "main", "resources");
        if (!Files.exists(resourceDirectory)) {
            Files.createDirectories(resourceDirectory);
        }

        document.save(resourceDirectory.resolve("test_document.pdf").toFile());
        document.close();
    }
}
