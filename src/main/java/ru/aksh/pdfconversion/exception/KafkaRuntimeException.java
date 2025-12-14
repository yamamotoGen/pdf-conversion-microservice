package ru.aksh.pdfconversion.exception;

import org.springframework.kafka.KafkaException;

public class KafkaRuntimeException extends KafkaException {
    public KafkaRuntimeException(String message) {
        super(message);
    }

    public KafkaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
