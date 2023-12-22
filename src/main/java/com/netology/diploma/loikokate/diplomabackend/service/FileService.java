package com.netology.diploma.loikokate.diplomabackend.service;

import com.netology.diploma.loikokate.diplomabackend.dto.file.FileDTO;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileService {


    void saveFile(FileRequest fileRequest);

    List<FileDTO> getFiles(Integer limit);

    void deleteFile(FileRequest fileRequest);

    Resource downloadFile(FileRequest fileRequest);

    FileDTO editFile(String originalFilename, FileRequest fileRequest);
}
