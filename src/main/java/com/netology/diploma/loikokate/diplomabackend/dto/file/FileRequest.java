package com.netology.diploma.loikokate.diplomabackend.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {

    private String filename;
    private MultipartFile file;
}
