package com.netology.diploma.loikokate.diplomabackend.service.impl;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import com.netology.diploma.loikokate.diplomabackend.exception.StorageException;
import com.netology.diploma.loikokate.diplomabackend.repository.FileRepository;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Value("${my-storage-directory}")
    private String storageDir;


    @Override
    public void saveFile(FileRequest fileRequest) {
        log.debug("Save file with name " + fileRequest.getFilename());
        storeFileOnDisk(fileRequest.getFile());

        FileEntity fileEntity = FileEntity.builder()
                .filename(fileRequest.getFilename())
                .size(fileRequest.getFile().getSize())
                .build();
        fileRepository.save(fileEntity);
    }

    @Override
    public List<FileEntity> getFiles(Integer limit) {
        log.debug("Get list of files with limit " + limit);
        Pageable firstPageWithLimit = PageRequest.of(0, limit);
        return fileRepository.findByOrderByIdDesc(firstPageWithLimit);
    }

    @Override
    @Transactional
    public void deleteFile(FileRequest fileRequest) {
        log.debug("Delete file with name " + fileRequest.getFilename());
        removeFromStorage(fileRequest.getFilename());

        fileRepository.deleteByFilename(fileRequest.getFilename());
    }

    @Override
    public FileEntity editFile(String originalFilename, FileRequest fileRequest) {
        log.debug(String.format("Edit file with name %s to %s in storage", originalFilename, fileRequest.getFilename()));
        renameInStorage(originalFilename, fileRequest.getFilename());

        FileEntity fileEntity = fileRepository.findByFilename(originalFilename);
        fileEntity.setFilename(fileRequest.getFilename());
        fileRepository.save(fileEntity);
        return fileEntity;
    }

    private void renameInStorage(String originalFilename, String newFilename) {
        log.debug(String.format("Rename file with name %s to %s in storage", originalFilename, newFilename));
        Path originalPath = Path.of(storageDir, originalFilename);
        Path newPath = Path.of(storageDir, newFilename);

        try {
            Files.move(originalPath, newPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new StorageException("Failed to rename file.", e);
        }
    }

    private void removeFromStorage(String filename) {
        log.debug("Delete file from storage with name " + filename);
        Path target = Path.of(storageDir, filename);
        try {
            Files.delete(target);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file.", e);
        }
    }

    private void storeFileOnDisk(MultipartFile file) {
        log.debug("Save file to storage with name " + file.getOriginalFilename());
        try {
            Path target = Path.of(storageDir, file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }

    }

    @Override
    public Resource downloadFile(FileRequest fileRequest) {
        log.debug("Download file with name " + fileRequest.getFilename());
        Path target = Path.of(storageDir, fileRequest.getFilename());

        ByteArrayResource resource;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(target));
        } catch (IOException e) {
            throw new StorageException("Failed to locate file.", e);
        }
        return resource;
    }
}
