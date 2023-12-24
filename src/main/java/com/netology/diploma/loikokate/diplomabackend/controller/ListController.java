package com.netology.diploma.loikokate.diplomabackend.controller;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileDTO;
import com.netology.diploma.loikokate.diplomabackend.dto.list.ListRequest;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("list")
@Slf4j
@AllArgsConstructor
public class ListController {

    private FileService fileService;

    @GetMapping
    @ResponseBody
    public List<FileDTO> list(ListRequest listRequest) {
        log.debug("listRequest " + listRequest);
        if (listRequest.getLimit() != null) {
            List<FileEntity> entities = fileService.getFiles(listRequest.getLimit());
            List<FileDTO> result = new ArrayList<>();
            entities.stream().forEach(e -> result.add(new FileDTO(e.getFilename(), e.getSize())));
            return result;
        } else {
            return new ArrayList<>();
        }
    }
}
