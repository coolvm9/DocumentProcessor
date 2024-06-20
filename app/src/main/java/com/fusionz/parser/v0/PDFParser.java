package com.fusionz.parser.v0;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFParser implements Parser {

    private final ITesseract tesseract;
    private final Tokenizer tokenizer;

    public PDFParser(String tessDataPath, Tokenizer tokenizer) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(tessDataPath);
        this.tokenizer = tokenizer;
    }

    @Override
    public String parseFullText(String filePath)  {
        List<TextSegment> content = new ArrayList<>();
        try(PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper textStripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
                int pageNumber = getCurrentPageNo();
                long timestamp = System.currentTimeMillis();
                Metadata metadata = new Metadata();
                metadata.put("pageNumber", String.valueOf(pageNumber));
                metadata.put("timestamp", String.valueOf(timestamp));
                content.add(new TextSegment(string, metadata));
                super.writeString(string, textPositions);
            }
        };

        // Extract text content
        textStripper.getText(document);
        // Extract images
        int imageIndex = 0;
        for (PDPage page : document.getPages()) {
            PDResources resources = page.getResources();
            for (COSName xObjectName : resources.getXObjectNames()) {
                if (resources.isImageXObject(xObjectName)) {
                    PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);
                    String imagePath = "temp/image_" + imageIndex + ".png";
                    BufferedImage bufferedImage = image.getImage();
                    ImageIO.write(bufferedImage, "png", new File(imagePath));
                    String imgText = tesseract.doOCR(bufferedImage);
                    int pageNumber = document.getPages().indexOf(page) + 1;
                    long timestamp = System.currentTimeMillis();
                    Metadata metadata = new Metadata();
                    metadata.put("pageNumber", String.valueOf(pageNumber));
                    metadata.put("imageIndex", String.valueOf(imageIndex));
                    metadata.put("timestamp", String.valueOf(timestamp));
                    content.add(new TextSegment(imgText, metadata));
                    imageIndex++;
                }
            }
        }
        document.close();
        }catch (IOException | TesseractException e) {
            e.printStackTrace();
        }
        // Combine the content into a single string (for demonstration purposes)
        StringBuilder combinedContent = new StringBuilder();
        for (TextSegment text : content) {
            combinedContent.append(text.text()).append("\n");
        }
        // Create and return the Document object
        return combinedContent.toString();
    }

    public List<TextSegment> splitIntoSegments(Document document, int chunkSize, int overlap) {
        DocumentSplitter splitter = DocumentSplitters.recursive( chunkSize, overlap, tokenizer);
        List<TextSegment> segments = splitter.split(document);
        List<TextSegment> enhancedSegments = new ArrayList<>();
        for (TextSegment segment : segments) {
            // Assuming some default values for metadata
            Metadata metadata = new Metadata();
            enhancedSegments.add(new TextSegment(segment.text(), metadata));
        }
        return enhancedSegments;
    }
}
