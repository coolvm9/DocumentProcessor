package com.fusionz.utils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;

public class DocToDocxConverter {

    public static void convertDocToDocx(File file) throws Exception {
        // Step 1: Convert .doc to HTML
        FileInputStream fis = new FileInputStream(file);
        HWPFDocument document = new HWPFDocument(fis);
        WordToHtmlConverter converter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        converter.processDocument(document);
        org.w3c.dom.Document htmlDocument = converter.getDocument();
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementation impl = registry.getDOMImplementation("LS");
        DOMImplementationLS implLS = (DOMImplementationLS) impl;
        LSSerializer serializer = implLS.createLSSerializer();
        String htmlContent = serializer.writeToString(htmlDocument);

        // Write HTML to file
        String htmlFilePath = file.getAbsolutePath().replace(".doc", ".html");
        try (FileOutputStream htmlOut = new FileOutputStream(htmlFilePath)) {
            htmlOut.write(htmlContent.getBytes());
        }

        // Step 2: Convert HTML to .docx
        XWPFDocument docx = new XWPFDocument();
        Document jsoupDoc = Jsoup.parse(htmlContent);
        Elements paragraphs = jsoupDoc.body().select("p");
        for (Element paragraph : paragraphs) {
            XWPFParagraph docxParagraph = docx.createParagraph();
            docxParagraph.createRun().setText(paragraph.text());
        }

        // Save .docx to the same location
        String outputFilePath = file.getAbsolutePath().replace(".doc", ".docx");
        try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
            docx.write(out);
        }
    }

    public static void main(String[] args) {
        try {
            File docFile = new File("path/to/your/document.doc");
            convertDocToDocx(docFile);
            System.out.println("Conversion completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}