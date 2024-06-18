package com.fusionz.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Range;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

        // Step 2: Manipulate HTML with JSoup (optional)
        Document jsoupDoc = Jsoup.parse(htmlContent);
        // Example manipulation: Add a new paragraph
        jsoupDoc.body().appendElement("p").text("This is a new paragraph added via JSoup.");
        String manipulatedHtml = jsoupDoc.html();

        // Step 3: Convert HTML to .docx
        XWPFDocument docx = new XWPFDocument();
        Document manipulatedJsoupDoc = Jsoup.parse(manipulatedHtml);
        Elements paragraphs = manipulatedJsoupDoc.body().select("p");
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
