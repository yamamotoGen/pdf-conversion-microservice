package ru.aksh.pdfconversion.service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.factory.PdfConversionManagerFactory;
import ru.aksh.pdfconversion.service.PdfConversionManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PdfConversionManagerFactory pdfConversionManagerFactory;

    @KafkaListener(topics = "${app.kafka.topics.create-pdf}")
    @KafkaHandler
    public void listen(FileEventDto fileEventDto) {
        try {
            log.info("Получено сообщение для обработки: {}", fileEventDto);

            PdfConversionManager manager = pdfConversionManagerFactory.getManager(fileEventDto);
            manager.run(fileEventDto);

            log.info("Сообщение обработано: {}", fileEventDto);
        } catch (RuntimeException e) {
            log.error("Ошибка чтения сообщения: {}", e.getMessage());
        }
    }
}