package ru.aksh.pdfconversion.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.aksh.pdfconversion.converter.ImageToPdfConverter;
import ru.aksh.pdfconversion.converter.PdfConverter;
import ru.aksh.pdfconversion.converter.TxtToPdfConverter;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class PdfConverterConfig {

    @Value("${storage.local.input-dir}")
    private String inputDir;

    @Value("${storage.local.output-dir}")
    private String outputDir;

    private final TxtToPdfConverter txtToPdfConverter;
    private final ImageToPdfConverter imageToPdfConverter;

    @Bean
    public Map<String, String> getDirectories() {
        Map<String, String> directories = new HashMap<>();
        directories.put("input", inputDir);
        directories.put("output", outputDir);
        return directories;
    }

    @Bean
    public List<PdfConverter> getPdfConverters() {
        return new ArrayList<>(Arrays.asList(txtToPdfConverter, imageToPdfConverter));
    }
}
