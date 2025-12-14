package ru.aksh.pdfconversion.converter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

@Component
public class TxtToPdfConverter implements PdfConverter {

    @Override
    public boolean isSupportedFormat(String fileName) {
        return fileName.toLowerCase().endsWith(".txt");
    }

    @Override
    public Optional<File> convert(String inputFile, String outputFile) throws IOException, DocumentException {
        File file = new File(inputFile);
        Document pdfDocument = new Document(PageSize.A4);

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            PdfWriter.getInstance(pdfDocument, fileOutputStream);
            try {
                pdfDocument.open();

                Font font = new Font();
                font.setStyle(Font.NORMAL);
                font.setSize(11);
                pdfDocument.add(new Paragraph("\n"));

                if (file.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String strLine;
                        while ((strLine = br.readLine()) != null) {
                            Paragraph paragraph = new Paragraph(strLine + "\n", font);
                            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                            pdfDocument.add(paragraph);
                        }
                    }
                }
            } finally {
                pdfDocument.close();
            }
        }
        return Optional.of(new File(outputFile));
    }
}