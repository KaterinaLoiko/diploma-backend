package com.netology.diploma.loikokate.diplomabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
//@EntityScan(basePackages = "com.netology.diploma.loikokate.diplomabackend.dao" )
//@EnableJpaRepositories(basePackages = "com.netology.diploma.loikokate.diplomabackend.repository")
public class DiplomaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiplomaBackendApplication.class, args);
    }
}
