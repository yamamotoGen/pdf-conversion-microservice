package ru.aksh.pdfconversion.service;

import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aksh.pdfconversion.converter.PdfConverter;
import ru.aksh.pdfconversion.exception.IORuntimeException;
import ru.aksh.pdfconversion.exception.UnsupportedFileFormatException;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PdfConversionServiceImpl implements PdfConversionService {

    private final List<PdfConverter> converters;

    @Override
    public void convertToPdf(String inputFile, String outputFile) {
        log.info("Идет конвертация файла: {}", inputFile);

        converters.stream()
                .filter(converter -> converter.isSupportedFormat(inputFile))
                .findFirst()
                .flatMap(converter -> {
                    try {
                        return converter.convert(inputFile, outputFile);
                    } catch (DocumentException | IOException e) {
                        log.error("Ошибка конвертации файла: {}", e.getMessage());
                        throw new IORuntimeException(e);
                    }
                })
                .orElseThrow(() -> new UnsupportedFileFormatException("Не поддерживаемый формат файла: " + inputFile));

        log.info("Файл успешно конвертирован: {}", outputFile);
    }
}