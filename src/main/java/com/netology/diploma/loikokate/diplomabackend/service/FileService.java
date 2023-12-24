package com.netology.diploma.loikokate.diplomabackend.service;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileService {


    void saveFile(FileRequest fileRequest);

//    List<FileDTO> getFiles(Integer limit);
    List<FileEntity> getFiles(Integer limit);

    void deleteFile(FileRequest fileRequest);

    Resource downloadFile(FileRequest fileRequest);

    FileEntity editFile(String originalFilename, FileRequest fileRequest);
}
