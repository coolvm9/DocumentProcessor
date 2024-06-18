public void extractTables(PDDocument document) {
        BasicExtractionAlgorithm algorithm = new BasicExtractionAlgorithm();
        for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
            try {
                technology.tabula.Page page = new technology.tabula.Page(document.getPage(pageNumber - 1), pageNumber, 0, 0, 0, 0);
                List<Table> tables = algorithm.extract(page);
                for (Table table : tables) {
                    for (List<RectangularTextContainer<?>> row : table.getRows()) {
                        StringBuilder rowText = new StringBuilder("Table Row: ");
                        for (RectangularTextContainer<?> cell : row) {
                            rowText.append(cell.getText()).append("\t");
                        }
                        elements.add(rowText.toString().trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
