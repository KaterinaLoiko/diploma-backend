package com.netology.diploma.loikokate.diplomabackend.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String filename;
    private Long size;
}
