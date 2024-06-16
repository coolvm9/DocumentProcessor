package com.fusionz.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static List<File> getFilesRecursively(String directoryPath, List<String> mimeTypes) throws IOException {
        Collection<File> allFiles = FileUtils.listFiles(new File(directoryPath), null, true);
        return allFiles.stream()
                .filter(file -> {
                    try {
                        String mimeType = Files.probeContentType(file.toPath());
                        return mimeTypes.contains(mimeType);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
