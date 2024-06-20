package com.fusionz.parser.v0;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PPTXParser implements Parser {

    private final ITesseract tesseract;

    public PPTXParser(String tessDataPath) {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(tessDataPath);
    }

    @Override
    public String parseFullText(String filePath)  {
        List<String> content = new ArrayList<>();
        File file = new File(filePath);
        try(XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file))){
        int imageIndex = 0;
        for (XSLFSlide slide : ppt.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    content.add(textShape.getText());
                } else if (shape instanceof org.apache.poi.xslf.usermodel.XSLFPictureShape) {
                    org.apache.poi.xslf.usermodel.XSLFPictureShape pictureShape = (org.apache.poi.xslf.usermodel.XSLFPictureShape) shape;
                    PictureData pictureData = pictureShape.getPictureData();
                    String extension = pictureData.getType().extension;
                    String imagePath = "temp/image_" + imageIndex + "." + extension;
                    try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                        fos.write(pictureData.getData());
                    }
                    BufferedImage img = ImageIO.read(new File(imagePath));
                    String imgText = tesseract.doOCR(img);
                    content.add(imgText);
                    imageIndex++;
                }
            }
        }
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
        }
        // Combine the content into a single string
        StringBuilder combinedContent = new StringBuilder();
        for (String text : content) {
            combinedContent.append(text).append("\n");
        }
        return combinedContent.toString();
    }

    public List<String> chunkContent(String content, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] tokens = content.split("\\s+");
        StringBuilder sb = new StringBuilder();
        int tokenCount = 0;

        for (String token : tokens) {
            sb.append(token).append(" ");
            tokenCount++;
            if (tokenCount >= chunkSize) {
                chunks.add(sb.toString().trim());
                sb.setLength(0);
                tokenCount = Math.max(0, tokenCount - overlap);
            }
        }

        if (sb.length() > 0) {
            chunks.add(sb.toString().trim());
        }

        return chunks;
    }
}
