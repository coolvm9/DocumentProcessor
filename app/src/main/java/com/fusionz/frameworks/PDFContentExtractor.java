package com.fusionz.frameworks;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFContentExtractor extends PDFTextStripper {

    private List<String> elements;

    public PDFContentExtractor() throws IOException {
        super();
        elements = new ArrayList<>();
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (isTableRow(string)) {
            elements.add("Table Row: " + string.trim());
        } else {
            elements.add("Paragraph: " + string.trim());
        }
    }

    private boolean isTableRow(String line) {
        // Heuristic to identify table rows (you can customize this based on your document structure)
        return line.split("\\s{2,}").length > 2;
    }

    public List<String> extractContent(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            this.setSortByPosition(true);
            this.setStartPage(0);
            this.setEndPage(document.getNumberOfPages());
            this.getText(document);
            return elements;
        }
    }

    public static void main(String[] args) {
        try {
            String pdfFilePath = "path/to/your/pdf/document.pdf";
            PDFContentExtractor extractor = new PDFContentExtractor();
            List<String> elements = extractor.extractContent(pdfFilePath);

            System.out.println("Extracted Content:");
            for (String element : elements) {
                System.out.println(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
