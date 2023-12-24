package com.netology.diploma.loikokate.diplomabackend.controller;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileDTO;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("file")
@AllArgsConstructor
@Slf4j
public class FileController {

    private FileService fileService;

    @PostMapping
    public void file(FileRequest fileRequest) {
        log.debug("file upload request " + fileRequest);
        fileService.saveFile(fileRequest);
    }

    @DeleteMapping
    public void deleteFile(FileRequest fileRequest) {
        log.debug("file delete request " + fileRequest);
        fileService.deleteFile(fileRequest);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody Resource downloadFile(FileRequest fileRequest) {
        log.debug("downloading file " + fileRequest.getFilename());
        return fileService.downloadFile(fileRequest);
    }

    @PutMapping
    public FileDTO editFile(@RequestParam String filename, @RequestBody FileRequest fileRequest) {
        log.debug("editing file " + filename + " to " + fileRequest.getFilename());
        FileEntity fileEntity = fileService.editFile(filename, fileRequest);
        return new FileDTO(fileEntity.getFilename(), fileEntity.getSize());
    }
}
