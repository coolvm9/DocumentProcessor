package com.fusionz.parser.v0;

import java.io.IOException;

public interface Parser {
    String parseFullText(String filePath) throws IOException;
}
