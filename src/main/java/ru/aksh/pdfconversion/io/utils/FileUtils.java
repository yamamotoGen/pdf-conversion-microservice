package ru.aksh.pdfconversion.io.utils;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {

    public String getBaseFileName(String file) {
        return file.substring(0, file.lastIndexOf("."));
    }

    public String changeFileFormat(String fileName, String newFormat) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + newFormat;
    }
}
