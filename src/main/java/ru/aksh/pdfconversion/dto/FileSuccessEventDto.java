package ru.aksh.pdfconversion.dto;

import jakarta.validation.constraints.NotEmpty;

public record FileSuccessEventDto(
        @NotEmpty
        String fileName,

        @NotEmpty
        String newFilePath) {
}
