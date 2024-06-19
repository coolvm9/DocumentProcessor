package com.fusionz.parser_v1;

import com.fusionz.data.DocumentStructure;

import java.io.IOException;

public interface Parser_v1 {
    String parseFullText(String filePath) throws IOException;
    DocumentStructure parse(String filePath) throws IOException;
}
