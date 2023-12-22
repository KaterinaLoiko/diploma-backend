package com.netology.diploma.loikokate.diplomabackend.service.impl;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileDTO;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import com.netology.diploma.loikokate.diplomabackend.exception.StorageException;
import com.netology.diploma.loikokate.diplomabackend.repository.FileRepository;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Value("${my-storage-directory}")
    private String storageDir;


    @Override
    public void saveFile(FileRequest fileRequest) {
        storeFileOnDisk(fileRequest.getFile());

        FileEntity fileEntity = FileEntity.builder()
                .filename(fileRequest.getFilename())
                .size(fileRequest.getFile().getSize())
                .build();
        fileRepository.save(fileEntity);
    }

    @Override
    public List<FileDTO> getFiles(Integer limit) {
        Pageable firstPageWithLimit = PageRequest.of(0, limit);
        List<FileEntity> entities = fileRepository.findByOrderByIdDesc(firstPageWithLimit);

        List<FileDTO> result = new ArrayList<>();
        entities.stream().forEach(e -> result.add(new FileDTO(e.getFilename(), e.getSize())));

        return result;
    }

    @Override
    @Transactional
    public void deleteFile(FileRequest fileRequest) {
        removeFromStorage(fileRequest.getFilename());

        fileRepository.deleteByFilename(fileRequest.getFilename());
    }

    @Override
    public FileDTO editFile(String originalFilename, FileRequest fileRequest) {
        renameInStorage(originalFilename, fileRequest.getFilename());

        FileEntity fileEntity = fileRepository.findByFilename(originalFilename);
        fileEntity.setFilename(fileRequest.getFilename());
        fileRepository.save(fileEntity);

        return new FileDTO(fileEntity.getFilename(), fileEntity.getSize());
    }

    private void renameInStorage(String originalFilename, String newFilename) {
        Path originalPath = Path.of(storageDir, originalFilename);
        Path newPath = Path.of(storageDir, newFilename);

        try {
            Files.move(originalPath, newPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new StorageException("Failed to rename file.", e);
        }
    }

    private void removeFromStorage(String filename) {
        Path target = Path.of(storageDir, filename);
        try {
            Files.delete(target);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file.", e);
        }
    }

    private void storeFileOnDisk(MultipartFile file) {
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
