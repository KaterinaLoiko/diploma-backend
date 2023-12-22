package com.netology.diploma.loikokate.diplomabackend.dao;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String filename;

    private Long size;
}
