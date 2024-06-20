package com.fusionz;

import com.fusionz.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @BeforeEach
    void setUp() throws IOException {
        // Set up test files if needed
        Files.createDirectories(Paths.get("src/test/resources"));
        Files.write(Paths.get("src/test/resources/test_document.docx"), "Test content for docx".getBytes());
        Files.write(Paths.get("src/test/resources/test_document.doc"), "Test content for doc".getBytes());
        Files.write(Paths.get("src/test/resources/test_output.html"), "<html><body>This is a test</body></html>".getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test files if needed
        Files.deleteIfExists(Paths.get("src/test/resources/test_document.docx"));
        Files.deleteIfExists(Paths.get("src/test/resources/test_document.doc"));
        Files.deleteIfExists(Paths.get("src/test/resources/test_output.html"));
        Files.deleteIfExists(Paths.get("src/test/resources/test_output.docx"));
        Files.deleteIfExists(Paths.get("src/test/resources/test_converted.docx"));
        Files.deleteIfExists(Paths.get("src/test/resources/output.txt"));
    }

    @Test
    void testGetFileContent() throws IOException {
        String filePath = "src/test/resources/test_document.docx";
        String content = FileUtils.getFileContent(filePath);
        assertEquals("Test content for docx", content);
    }

    @Test
    void testConvertDocToHtml() throws Exception {
        String docFilePath = "src/test/resources/test_document.doc";
        String htmlContent = FileUtils.convertDocToHtml(docFilePath);
        assertTrue(htmlContent.contains("<html>") && htmlContent.contains("</html>"));
    }

    @Test
    void testConvertHtmlToDocx() throws IOException {
        String htmlContent = "<html><body>This is a test</body></html>";
        String docxFilePath = "src/test/resources/test_output.docx";
        FileUtils.convertHtmlToDocx(htmlContent, docxFilePath);
        assertTrue(new File(docxFilePath).exists());
    }

    @Test
    void testConvertDocToDocx() throws IOException {
        String docFilePath = "src/test/resources/test_document.doc";
        String newDocxFilePath = "src/test/resources/test_converted.docx";
        FileUtils.convertDocToDocx(docFilePath, newDocxFilePath);
        assertTrue(new File(newDocxFilePath).exists());
    }

    @Test
    void testWriteToFileInResources() throws IOException {
        String resourceDir = "src/test/resources";
        String outputFileName = "output.txt";
        String outputContent = "This is a test content";
        FileUtils.writeToFileInResources(resourceDir, outputFileName, outputContent);
        String content = Files.readString(Paths.get(resourceDir, outputFileName));
        assertEquals(outputContent, content);
    }

    @Test
    void testGetFilesRecursively() throws IOException {
        String directoryPath = "src/test/resources";
        List<String> extensions = List.of(".docx", ".doc");
        List<File> files = FileUtils.getFilesRecursively(directoryPath, extensions);
        assertFalse(files.isEmpty());
        assertTrue(files.stream().allMatch(file -> file.getName().endsWith(".docx") || file.getName().endsWith(".doc")));
    }
}
