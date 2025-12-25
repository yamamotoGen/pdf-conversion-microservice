package ru.aksh.pdfconversion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.dto.FileSuccessEventDto;
import ru.aksh.pdfconversion.io.minio.MinioManager;
import ru.aksh.pdfconversion.io.utils.DirectoryUtils;
import ru.aksh.pdfconversion.io.utils.FileUtils;
import ru.aksh.pdfconversion.service.producer.KafkaProducer;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SimplePdfConversionManager implements PdfConversionManager {

    private final PdfConversionService pdfConversionService;
    private final MinioManager minioManager;
    private final KafkaProducer kafkaProducer;
    private final DirectoryUtils directoryUtils;
    private final FileUtils fileUtils;

    @Value("${storage.minio.upload-bucket.name}")
    private String uploadBucketName;

    @Override
    public void run(FileEventDto fileEventDto) {
        String inputDirectory = directoryUtils.getInputDirectory();
        String outputDirectory = directoryUtils.getOutputDirectory();
        String inputPath = Paths.get(inputDirectory, fileEventDto.fileName()).toString();
        String finalFileName = fileUtils.changeFileFormat(fileEventDto.fileName(), ".pdf");
        String outputPath = Paths.get(outputDirectory, finalFileName).toString();
        String uploadBucketPath = String.join("/", uploadBucketName, finalFileName);

        minioManager.download(fileEventDto.bucketName(), fileEventDto.fileName(), inputPath);
        pdfConversionService.convertToPdf(inputPath, outputPath);
        minioManager.upload(uploadBucketName, finalFileName, outputPath);
        kafkaProducer.sendEvent(new FileSuccessEventDto(fileEventDto.fileName(), uploadBucketPath));
    }
}
