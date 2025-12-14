package ru.aksh.pdfconversion.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.exception.KafkaRuntimeException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.success-pdf}")
    private String successPdfTopic;

    public void sendEvent(FileEventDto fileEventDto) {
        try {
            kafkaTemplate.send(successPdfTopic, fileEventDto);
            log.info("Продюсер отправил event в топик {}: value={}", successPdfTopic, fileEventDto);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения: {}", fileEventDto);
            throw new KafkaRuntimeException("Ошибка отправки сообщения", e);
        }
    }
}
