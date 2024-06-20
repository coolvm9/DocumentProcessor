package com.fusionz.datastructures;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

public class EnhancedTextSegment extends TextSegment {
    public EnhancedTextSegment(String text, Metadata metadata) {
        super(text, metadata);
    }
}