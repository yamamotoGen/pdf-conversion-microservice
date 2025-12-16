package ru.aksh.pdfconversion.dto;


import jakarta.validation.constraints.NotEmpty;

public record FileEventDto(
        @NotEmpty
        String bucketName,

        @NotEmpty
        String fileName) {
}