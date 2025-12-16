package ru.aksh.pdfconversion.io.minio;

public interface MinioManager {
    void createBucket(String bucketName);

    void download(String bucketName, String fileName, String localPath);

    void upload(String bucketName, String fileName, String localPath);
}
