package com.example.lms.faculty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootDir;

    public FileStorageService(@Value("${lms.upload-dir:uploads}") String uploadDir) throws IOException {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.rootDir);
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String storedName = UUID.randomUUID() + "_" + originalFilename;
        Path target = rootDir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }
}

