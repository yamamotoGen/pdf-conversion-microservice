package ru.aksh.pdfconversion.io.minio;

import io.minio.*;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@AllArgsConstructor
public class MinioManagerImpl implements MinioManager {

    private final MinioClient minioClient;

    @Override
    public void download(String bucketName, String fileName, String localPath) {
        Path path = Paths.get(localPath);

        log.info("Загрузка файла {} из MinIO по локальному пути: {}", fileName, localPath);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                log.warn("Существующий файл с аналогичным именем {} будет заменен на загружаемый", fileName);
            }

            minioClient.downloadObject(DownloadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .filename(localPath)
                    .build());

            log.info("Файл успешно загружен в {}", localPath);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Ошибка при загрузке файла {}: {}",fileName, e.getMessage());
        }
    }

    @Override
    public void upload(String bucketName, String fileName, String localPath) {
        log.info("Выгрузка файла {} в MinIO, bucketName={}", fileName, bucketName);

        createBucket(bucketName);

        try {
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .filename(localPath)
                    .build());

            log.info("Файл {} успешно выгружен в MinIO, bucketName={}", fileName, bucketName);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Ошибка при выгрузке файла {} в MinIO: {}", fileName, e.getMessage());
        }
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            log.info("Попытка создания bucket {} в хранилище MinIO", bucketName);

            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Создан bucket: {}", bucketName);
            } else {
                log.warn("Bucket {} уже существует", bucketName);
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Ошибка создания bucket {}: {}", bucketName, e.getMessage());
        }
    }
}
