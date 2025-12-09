package ru.aksh.pdfconversion.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.io.utils.ZipUtils;
import ru.aksh.pdfconversion.service.PdfConversionManager;
import ru.aksh.pdfconversion.service.SimplePdfConversionManager;
import ru.aksh.pdfconversion.service.ZipToPdfConversionManager;

@Component
@RequiredArgsConstructor
public class PdfConversionManagerFactory {

    private final SimplePdfConversionManager simplePdfConversionManager;
    private final ZipToPdfConversionManager zipToPdfConversionManager;
    private final ZipUtils zipUtils;

    public PdfConversionManager getManager(FileEventDto fileEventDto) {
        if (zipUtils.isZipArchive(fileEventDto.fileName())) {
            return zipToPdfConversionManager;
        }
        return simplePdfConversionManager;
    }
}
