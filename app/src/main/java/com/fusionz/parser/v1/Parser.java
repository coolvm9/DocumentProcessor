package com.fusionz.parser.v1;

import com.fusionz.datastructures.DocumentStructure;

import java.io.IOException;

public interface Parser {
    String parseFullText(String filePath) throws IOException;
    DocumentStructure parse(String filePath) throws IOException;
}
