package ru.aksh.pdfconversion.io.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.aksh.pdfconversion.exception.IORuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectoryUtils {

    private final Map<String, String> directories;

    public String getInputDirectory() {
        Path input = Paths.get(directories.get("input"));
        createDirectory(input);
        return input.toString();
    }

    public String getOutputDirectory() {
        Path output = Paths.get(directories.get("output"));
        createDirectory(output);
        return output.toString();
    }

    public void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
                log.info("Создана директория: {}", path);
            } catch (IOException e) {
                log.error("Ошибка создания директории: {}", path);
                throw new IORuntimeException(e);
            }
        }
    }

    public void createZipDirectories(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Создана директория архива: {}", path);
            } catch (IOException e) {
                log.error("Ошибка создания директории для архива: {}", path, e);
                throw new IORuntimeException(e);
            }
        }
    }
}
