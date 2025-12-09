package ru.aksh.pdfconversion.io.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.exception.IORuntimeException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class ZipUtils {

    private final DirectoryUtils directoryUtils;
    private final FileUtils fileUtils;

    public boolean isZipArchive(String fileName) {
        return fileName.toLowerCase().endsWith(".zip");
    }

    public void unzipArchive(String inputZipPath) {
        log.info("Распаковка архива: {}", inputZipPath);

        Path destPath = Paths.get(fileUtils.getBaseFileName(inputZipPath));
        try (ZipFile zipFile = new ZipFile(inputZipPath)) {
            zipFile.stream().forEach(entry -> {
                Path entryPath = destPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    directoryUtils.createZipDirectories(entryPath);
                } else {
                    directoryUtils.createZipDirectories(entryPath.getParent());
                    try {
                        Files.copy(zipFile.getInputStream(entry), entryPath, StandardCopyOption.REPLACE_EXISTING);

                        log.info("Файл {} распакован в: {}", entry.getName(), entryPath);
                    } catch (IOException e) {
                        log.error("Ошибка копирования файла {} при распаковке архива: {}", entryPath, e.getMessage());
                        throw new IORuntimeException(e);
                    }
                }
            });

            log.info("Распаковка архива завершена: {}", inputZipPath);
        } catch (IOException e) {
            log.error("Ошибка при распаковке архива {}: {}", inputZipPath, e.getMessage());
            throw new IORuntimeException(e);
        }
    }

    public void createZipArchive(String sourceDirPath, String outputZipPath) {
        log.info("Создание ZIP-архива: {}", outputZipPath);
        Path sourcePath = Paths.get(sourceDirPath);

        try (ZipArchiveOutputStream archiveOutputStream = new ZipArchiveOutputStream(new FileOutputStream(outputZipPath));
             Stream<Path> paths = Files.list(sourcePath)) {

            paths.forEach(path -> {
                try {
                    ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), sourcePath.toString());
                    archiveOutputStream.putArchiveEntry(entry);

                    try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                        IOUtils.copy(fileInputStream, archiveOutputStream);
                    }
                    archiveOutputStream.closeArchiveEntry();

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });

            log.info("ZIP-архив создан: {}", outputZipPath);
        } catch (IOException e) {
            log.error("ошибка создание ZIP-архива {}: {}", outputZipPath, e.getMessage());
            throw new IORuntimeException(e);
        }
    }
}

