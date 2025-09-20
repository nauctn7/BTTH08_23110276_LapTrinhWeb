package vn.iotstar.service.impl;

import java.io.InputStream;
import java.nio.file.*;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.config.StorageProperties;
import vn.iotstar.Exception.StorageException;
import vn.iotstar.service.IStorageService;

@Service
public class FileSystemStorageServiceImpl implements IStorageService {

    private final Path rootLocation;

    public FileSystemStorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public String getStorageFilename(MultipartFile file, String id) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        return "p" + id + (ext != null && !ext.isEmpty() ? "." + ext.toLowerCase() : "");
    }

    @Override
    public void store(MultipartFile file, String storedName) {
        try {
            if (file.isEmpty()) throw new StorageException("Failed to store empty file");
            Path destinationFile = this.rootLocation.resolve(Paths.get(storedName))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new StorageException("Failed to store file: " + storedName, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new StorageException("Cannot read file: " + filename);
        } catch (Exception e) {
            throw new StorageException("Could not read file: " + filename);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void delete(String storedName) throws Exception {
        Path destinationFile = rootLocation.resolve(Paths.get(storedName))
                .normalize().toAbsolutePath();
        Files.deleteIfExists(destinationFile);
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            System.out.println("Storage root: " + rootLocation.toString());
        } catch (Exception e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
