package ru.aksh.pdfconversion.io.utils;

import org.springframework.stereotype.Component;

@Component
public class FileUtils {

    public String getBaseFileName(String file) {
        return file.substring(0, file.lastIndexOf("."));
    }

    public String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public String changeFileFormat(String fileName, String newFormat) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + newFormat;
    }
}
