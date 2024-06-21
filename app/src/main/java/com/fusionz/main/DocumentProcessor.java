package com.fusionz.main;


import com.fusionz.parser.v0.PDFParser;
import com.fusionz.parser.v0.Parser;
import com.fusionz.parser.v0.XLSXParser;
import com.fusionz.parser.v0.PPTXParser;
import com.fusionz.utils.FileUtils;
import com.opencsv.CSVWriter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentProcessor {

    public static void main(String[] args) {
        String directoryPath = "path/to/your/directory";
        String csvOutputFilePath = "path/to/your/directory/out.csv";

        int CHNK_SIZE = 128;
        int OVERLAP = 64;
        List<String> msFileTypes = List.of("doc", "docx", "ppt", "xls", "xlsx", "msg");
        List<String> pptFileTypes = List.of("ppt", "pptx");
        List<String> excelFileTypes = List.of("xls", "xlsx");
        List<String> pdfFileTypes = List.of("pdf");

        Tokenizer tokenizer = new HuggingFaceTokenizer();

        Parser poiParser = new PPTXParser("path/to/tessdata");
        Parser excelParser = new XLSXParser(tokenizer);
        Parser pptParser = new PPTXParser("path/to/tessdata");

        Parser pdfParser = new PDFParser("path/to/tessdata", tokenizer);

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvOutputFilePath))) {
            String[] header = { "File Name", "Segment Number", "Segment Text" };
            writer.writeNext(header);
            // PDF files are processed using the pdfParser and tokenizer
            processDocuments(directoryPath, pdfFileTypes, pdfParser, tokenizer, CHNK_SIZE, OVERLAP, writer);
            // Excel files are processed using the poiParser and tokenizer
            processDocuments(directoryPath, excelFileTypes, excelParser, tokenizer, CHNK_SIZE, OVERLAP, writer);
            // PPT files are processed using the poiParser and tokenizer
            processDocuments(directoryPath, pptFileTypes, poiParser, tokenizer, CHNK_SIZE, OVERLAP, writer);
            // MS Office files are processed using the poiParser and tokenizer
            processDocuments(directoryPath, msFileTypes, poiParser, tokenizer, CHNK_SIZE, OVERLAP, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processDocuments(String directoryPath, List<String> fileTypes, Parser parser, Tokenizer tokenizer, int chunkSize, int overlap, CSVWriter writer) {
        // Get all files from the directory based on the path matcher
        // add try catch block
        List<File> files;
        try {
            files = FileUtils.getFilesRecursively(directoryPath, fileTypes);
        for (File file : files) {
            Document document = Document.document(parser.parseFullText(file.getAbsolutePath()));
            String fileName = file.getName();
            Metadata metadata = document.metadata();
            metadata.put(Document.FILE_NAME, fileName);
            metadata.put(Document.ABSOLUTE_DIRECTORY_PATH, file.getAbsolutePath());
            try {
                List<TextSegment> segments = chunkText(tokenizer, document.text(), chunkSize, overlap);
                int i = 0;
                for (TextSegment segment : segments) {
                    writer.writeNext(new String[]{fileName, String.valueOf(i), segment.text()});
                    i++;
                }
            } catch (Exception e) {
                System.err.println("Failed to process file: " + fileName);
                e.printStackTrace();
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<TextSegment> chunkText(Tokenizer tokenizer, String text, int chunkSize, int overlap) {
        List<TextSegment> segments;
        DocumentSplitter splitter;
        try {
            splitter = DocumentSplitters.recursive(chunkSize, overlap, tokenizer);
            segments = splitter.split(Document.document(text));
            // catch any exceptions
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return segments;
    }


}