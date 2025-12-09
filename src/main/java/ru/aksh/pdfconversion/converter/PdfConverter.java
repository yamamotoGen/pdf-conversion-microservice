package ru.aksh.pdfconversion.converter;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface PdfConverter {
    boolean isSupportedFormat(String fileName);

    Optional<File> convert(String inputFile, String outputFile) throws IOException, DocumentException;
}
