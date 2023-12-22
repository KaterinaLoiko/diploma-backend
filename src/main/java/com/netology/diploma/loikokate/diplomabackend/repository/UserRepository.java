package com.netology.diploma.loikokate.diplomabackend.repository;


import com.netology.diploma.loikokate.diplomabackend.dao.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findByLoginAndPassword(String login, String password);

    UserEntity findByAuthToken(String token);
}
