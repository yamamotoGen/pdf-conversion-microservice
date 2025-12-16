package ru.aksh.pdfconversion.service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.exception.KafkaRuntimeException;
import ru.aksh.pdfconversion.factory.PdfConversionManagerFactory;
import ru.aksh.pdfconversion.service.PdfConversionManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PdfConversionManagerFactory pdfConversionManagerFactory;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.dead-letter-queue}")
    private String dlqTopic;

    @KafkaListener(topics = "${app.kafka.topics.create-pdf}")
    @KafkaHandler
    public void listen(FileEventDto fileEventDto) {
        try {
            log.info("Получено сообщение для обработки: {}", fileEventDto);

            PdfConversionManager manager = pdfConversionManagerFactory.getManager(fileEventDto);
            manager.run(fileEventDto);

            log.info("Сообщение обработано: {}", fileEventDto);
        } catch (KafkaRuntimeException e) {
            log.error("Ошибка при обработке сообщения: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка чтения сообщения: {}", e.getMessage());
            sendToDlq(fileEventDto, e.getMessage());
        }
    }

    private void sendToDlq(FileEventDto fileEventDto, String error) {
        try {
            kafkaTemplate.send(dlqTopic, fileEventDto);
            log.info("Сообщение {} отправлено в DLQ топик {} с ошибкой {}", fileEventDto, dlqTopic, error);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения {} в DLQ топик {}", fileEventDto, dlqTopic);
            throw new KafkaRuntimeException("Ошибка отправки сообщения: " + e.getMessage());
        }
    }
}