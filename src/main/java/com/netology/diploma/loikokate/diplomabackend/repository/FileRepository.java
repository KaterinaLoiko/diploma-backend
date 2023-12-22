package com.netology.diploma.loikokate.diplomabackend.repository;

import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface FileRepository extends CrudRepository<FileEntity, Long> {
    List<FileEntity> findByOrderByIdDesc(Pageable pageable);

    @Transactional
    void deleteByFilename(String filename);

    FileEntity save(FileEntity file);

    FileEntity findByFilename(String filename);
}
