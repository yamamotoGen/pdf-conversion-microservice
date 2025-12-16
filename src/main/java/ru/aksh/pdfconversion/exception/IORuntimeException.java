package ru.aksh.pdfconversion.exception;

public class IORuntimeException extends RuntimeException {
    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(Throwable cause) {
        super(cause);
    }
}
