package com.fusionz.parser;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequentialPDFParser {

    private List<String> elements;

    public SequentialPDFParser() {
        elements = new ArrayList<>();
    }

    public void extractContent(String pdfFilePath) throws IOException {
        PdfReader reader = new PdfReader(pdfFilePath);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
            // Extract paragraphs
            TextExtractionStrategy strategy = parser.processContent(pageNum, new LocationTextExtractionStrategy());
            String pageText = strategy.getResultantText();
            String[] lines = pageText.split("\n");

            for (String line : lines) {
                elements.add("Paragraph: " + line.trim());
            }

            // Extract tables
            strategy = parser.processContent(pageNum, new LocationTextExtractionStrategy());
            lines = strategy.getResultantText().split("\n");
            for (String line : lines) {
                if (line.contains("\t")) {  // Assuming that table rows have tabs separating columns
                    elements.add("Table Row: " + line.trim());
                }
            }
        }

        reader.close();
    }

    public List<String> getElements() {
        return elements;
    }

    public static void main(String[] args) {
        try {
            String pdfFilePath = "path/to/your/pdf/document.pdf";
            SequentialPDFParser parser = new SequentialPDFParser();
            parser.extractContent(pdfFilePath);

            System.out.println("Extracted Content:");
            for (String element : parser.getElements()) {
                System.out.println(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}