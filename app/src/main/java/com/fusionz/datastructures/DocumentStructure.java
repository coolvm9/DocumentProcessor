package com.fusionz.datastructures;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
public class DocumentStructure {
    private Map<Integer, Page> pages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page {
        private int pageNumber;
        private List<Paragraph> paragraphs;
        private List<Table> tables;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paragraph {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Table {
        private List<String> header;
        private List<Row> rows;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Row {
        private List<String> cells;
    }
}