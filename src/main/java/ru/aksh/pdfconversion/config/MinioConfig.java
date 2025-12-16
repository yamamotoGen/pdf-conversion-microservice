package ru.aksh.pdfconversion.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${storage.minio.url}")
    private String minioUrl;

    @Value("${storage.minio.root-user}")
    private String user;

    @Value("${storage.minio.root-password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(user, password)
                .build();
    }
}
