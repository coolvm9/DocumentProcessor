package com.fusionz.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static void main(String[] args) {
        try {
            // Test getFileContent
            String filePath = "src/main/resources/test_document.docx";
            String content = getFileContent(filePath);
            System.out.println("File Content: " + content);

            // Test convertDocToHtml
            String docFilePath = "src/main/resources/test_document.doc";
            String htmlContent = convertDocToHtml(docFilePath);
            System.out.println("HTML Content: " + htmlContent);

            // Test convertHtmlToDocx
            String docxFilePath = "src/main/resources/test_output.docx";
            convertHtmlToDocx(htmlContent, docxFilePath);
            System.out.println("Converted HTML to DOCX");

            // Test convertDocToDocx
            String newDocxFilePath = "src/main/resources/test_converted.docx";
            convertDocToDocx(docFilePath, newDocxFilePath);
            System.out.println("Converted DOC to DOCX");

            // Test writeToFileInResources
            String resourceDir = "src/main/resources";
            String outputFileName = "output.txt";
            String outputContent = "This is a test content";
            writeToFileInResources(resourceDir, outputFileName, outputContent);
            System.out.println("Written to file in resources");

            List<String> extensions = List.of(".pdf", ".doc", ".docx");
            List<File> files = getFilesRecursively("src/main/resources", extensions);
            for (File file : files) {
                System.out.println(file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get file content as a String.
     *
     * @param filePath the path to the file
     * @return the file content as a String
     * @throws IOException if an I/O error occurs
     */
    public static String getFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    /**
     * Convert .doc file to HTML.
     *
     * @param docFilePath the path to the .doc file
     * @return the HTML content as a String
     * @throws Exception if an error occurs during conversion
     */
    public static String convertDocToHtml(String docFilePath) throws Exception {
        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(docFilePath));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Convert HTML to .docx.
     *
     * @param htmlContent  the HTML content as a String
     * @param docxFilePath the path to the .docx file to be created
     * @throws IOException if an I/O error occurs
     */
    public static void convertHtmlToDocx(String htmlContent, String docxFilePath) throws IOException {
        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText(htmlContent);
        try (FileOutputStream out = new FileOutputStream(docxFilePath)) {
            document.write(out);
        }
    }

    /**
     * Convert .doc file to .docx.
     *
     * @param docFilePath  the path to the .doc file
     * @param docxFilePath the path to the .docx file to be created
     * @throws IOException if an I/O error occurs
     */
    public static void convertDocToDocx(String docFilePath, String docxFilePath) throws IOException {
        try (HWPFDocument doc = new HWPFDocument(new FileInputStream(docFilePath));
             XWPFDocument docx = new XWPFDocument()) {
            Range range = doc.getRange();
            docx.createParagraph().createRun().setText(range.text());
            try (FileOutputStream out = new FileOutputStream(docxFilePath)) {
                docx.write(out);
            }
        }
    }

    /**
     * Write content to a file in the resources directory.
     *
     * @param resourceDirectory the resources directory path
     * @param fileName          the name of the file to be created
     * @param content           the content to be written
     * @throws IOException if an I/O error occurs
     */
    public static void writeToFileInResources(String resourceDirectory, String fileName, String content) throws IOException {
        Path path = Paths.get(resourceDirectory, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }



    public static List<File> getFilesRecursively(String directoryPath, List<String> byFileExtension) throws IOException {
        List<File> fileList = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);
        List<Path> paths;
        try (Stream<Path> stream = Files.walk(startPath)) {
            paths = stream.toList();
        }
        for (Path path : paths) {
            if (Files.isRegularFile(path)) {
                String fileName = path.getFileName().toString();
                for (String extension : byFileExtension) {
                    if (fileName.endsWith(extension)) {
                        fileList.add(path.toFile());
                        break;
                    }
                }
            }
        }

        return fileList;
    }

    public static void convertDocToDocx(File docFile) throws IOException {
        if (!docFile.getName().endsWith(".doc")) {
            throw new IllegalArgumentException("Input file must be a .doc file");
        }
        try(HWPFDocument doc = new HWPFDocument(new FileInputStream(docFile));
            XWPFDocument docx = new XWPFDocument();) {
            Range range = doc.getRange();
            for (int i = 0; i < range.numParagraphs(); i++) {
                Paragraph para = range.getParagraph(i);
                XWPFParagraph xwpfParagraph = docx.createParagraph();
                XWPFRun run = xwpfParagraph.createRun();
                run.setText(para.text());
            }
            // Save the .docx file
            String docxFilePath = docFile.getAbsolutePath().replace(".doc", ".docx");
            try (FileOutputStream out = new FileOutputStream(new File(docxFilePath))) {
                docx.write(out);
            }
            // Close the documents
            doc.close();
            docx.close();
        }
    }

}