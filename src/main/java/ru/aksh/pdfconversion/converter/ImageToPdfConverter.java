package ru.aksh.pdfconversion.converter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ImageToPdfConverter implements PdfConverter {
    @Override
    public boolean isSupportedFormat(String fileName) {
        List<String> formats = new ArrayList<>(Arrays.asList(".png", ".jpeg", ".jpg"));
        return formats.stream()
                .anyMatch(format -> fileName.toLowerCase().endsWith(format));
    }

    @Override
    public Optional<File> convert(String inputFile, String outputFile) throws IOException, DocumentException {
        Image image = Image.getInstance(inputFile);
        Rectangle pageSize = new Rectangle(image.getWidth(), image.getHeight());
        Document pdfDocument = new Document(pageSize, 0, 0, 0, 0);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            PdfWriter.getInstance(pdfDocument, fileOutputStream);
            try {
                pdfDocument.open();
                pdfDocument.add(image);
            } finally {
                pdfDocument.close();
            }
        }
        return Optional.of(new File(outputFile));
    }
}
