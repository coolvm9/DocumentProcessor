package com.fusionz;


import com.opencsv.CSVWriter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public class DocumentProcessor {

    public static void main(String[] args) {

        String directoryPath = "path/to/your/directory";
        String csvOutputFilePath = "path/to/your/directory/out.csv";

        int CHNK_SIZE = 128;
        int OVERLAP = 64;

        PathMatcher msFilesPathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{doc,docx,ppt,xls,msg}");
        PathMatcher pdfFilesPathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{pdf}");
        Tokenizer tokenizer = new HuggingFaceTokenizer();
        ApachePoiDocumentParser poiParser = new ApachePoiDocumentParser();
        ApachePdfBoxDocumentParser pdfParser = new ApachePdfBoxDocumentParser();
        List<Document> pdfDocuments = FileSystemDocumentLoader.loadDocuments(directoryPath,
                pdfFilesPathMatcher,
                pdfParser);
        List<Document> msDocuments = FileSystemDocumentLoader.loadDocuments(directoryPath,
                msFilesPathMatcher,
                poiParser);
        List<Document> documents = new ArrayList<>();
        documents.addAll(pdfDocuments);
        documents.addAll(msDocuments);
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvOutputFilePath))) {
            for (Document document : documents) {
                String fileName = document.metadata().getString(Document.FILE_NAME);
                try {
                    List<TextSegment> segments = chunkText(tokenizer, document.text(), CHNK_SIZE, OVERLAP);
                    int i = 0;
                    for (TextSegment segment : segments) {
                        writer.writeNext(new String[]{fileName, String.valueOf(i), i + "", segment.text()});
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process file: " + fileName);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<TextSegment> chunkText(Tokenizer tokenizer, String text, int chunkSize, int overlap) {
        List<TextSegment> segments;
        DocumentSplitter splitter;
        try {
            splitter = DocumentSplitters.recursive( chunkSize, overlap, tokenizer);
            segments = splitter.split(Document.document(text));
            // catch any exceptions
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return segments;
    }


}