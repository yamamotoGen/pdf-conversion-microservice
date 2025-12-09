package ru.aksh.pdfconversion.service;

import ru.aksh.pdfconversion.dto.FileEventDto;

import java.io.IOException;

public interface PdfConversionManager {
    void run(FileEventDto fileEventDto);
}
