package ru.aksh.pdfconversion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.aksh.pdfconversion.service.PdfConversionManager;
import ru.aksh.pdfconversion.service.SimplePdfConversionManager;
import ru.aksh.pdfconversion.service.ZipToPdfConversionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PdfConversionManagerConfig {

    @Bean
    public Map<String, PdfConversionManager> pdfConversionManagerMap(
            SimplePdfConversionManager simplePdfConversionManager,
            ZipToPdfConversionManager zipToPdfConversionManager) {

        Map<String, PdfConversionManager> map = new HashMap<>();
        map.put("zip", zipToPdfConversionManager);
        map.put("txt", simplePdfConversionManager);
        map.put("png", simplePdfConversionManager);
        map.put("jpg", simplePdfConversionManager);
        map.put("jpeg", simplePdfConversionManager);
        return map;
    }
}
