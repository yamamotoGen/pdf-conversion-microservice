package ru.aksh.pdfconversion.exception;

public class UnsupportedFileFormatException extends IllegalArgumentException {
    public UnsupportedFileFormatException(String message) {
        super(message);
    }
}
