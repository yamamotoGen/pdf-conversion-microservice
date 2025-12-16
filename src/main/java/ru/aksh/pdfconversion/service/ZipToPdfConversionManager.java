package ru.aksh.pdfconversion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.aksh.pdfconversion.dto.FileEventDto;
import ru.aksh.pdfconversion.exception.IORuntimeException;
import ru.aksh.pdfconversion.io.minio.MinioManager;
import ru.aksh.pdfconversion.io.utils.DirectoryUtils;
import ru.aksh.pdfconversion.io.utils.FileUtils;
import ru.aksh.pdfconversion.io.utils.ZipUtils;
import ru.aksh.pdfconversion.service.producer.KafkaProducer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZipToPdfConversionManager implements PdfConversionManager {

    private final PdfConversionService pdfConversionService;
    private final MinioManager minioManager;
    private final KafkaProducer kafkaProducer;
    private final DirectoryUtils directoryUtils;
    private final FileUtils fileUtils;
    private final ZipUtils zipUtils;

    @Value("${storage.minio.upload-bucket.name}")
    private String uploadBucketName;

    @Override
    public void run(FileEventDto fileEventDto) {
        String inputZipPath = Paths.get(directoryUtils.getInputDirectory(), fileEventDto.fileName()).toString();
        String unzipPath = fileUtils.getBaseFileName(inputZipPath);
        String baseFileName = fileUtils.getBaseFileName(fileEventDto.fileName());
        Path outputDirectory = Paths.get(directoryUtils.getOutputDirectory(), baseFileName);

        minioManager.download(fileEventDto.bucketName(), fileEventDto.fileName(), inputZipPath);
        zipUtils.unzipArchive(inputZipPath);
        directoryUtils.createDirectory(outputDirectory);

        try (Stream<Path> paths = Files.list(Paths.get(unzipPath))) {
            paths.forEach(filePath -> {
                String finalFileName = fileUtils.changeFileFormat(filePath.getFileName().toString(), ".pdf");
                String outputPath = Paths.get(outputDirectory.toString(), finalFileName).toString();

                pdfConversionService.convertToPdf(filePath.toString(), outputPath);
            });
        } catch (IOException e) {
            log.error("Ошибка чтения из директории {}: {}", unzipPath, e.getMessage());
            throw new IORuntimeException(e);
        }

        String outputZipName = baseFileName.concat("-" + uploadBucketName).concat(".zip");
        String outputZipPath = Paths.get(directoryUtils.getOutputDirectory(), outputZipName).toString();

        zipUtils.createZipArchive(outputDirectory.toString(), outputZipPath);
        minioManager.upload(uploadBucketName, outputZipName, outputZipPath);
        kafkaProducer.sendEvent(new FileEventDto(uploadBucketName, outputZipName));
    }
}
