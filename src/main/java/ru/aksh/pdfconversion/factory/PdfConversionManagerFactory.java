package ru.aksh.pdfconversion.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.io.utils.FileUtils;
import ru.aksh.pdfconversion.service.PdfConversionManager;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfConversionManagerFactory {

    private final Map<String, PdfConversionManager> pdfConversionManagerMap;
    private final FileUtils fileUtils;

    public PdfConversionManager getManager(FileEventDto fileEventDto) {
        String extension = fileUtils.getFileExtension(fileEventDto.fileName());
        return pdfConversionManagerMap.get(extension);
    }
}
