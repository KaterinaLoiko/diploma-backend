package com.netology.diploma.loikokate.diplomabackend;

import com.netology.diploma.loikokate.diplomabackend.controller.FileController;
import com.netology.diploma.loikokate.diplomabackend.controller.LoginController;
import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginRequest;
import com.netology.diploma.loikokate.diplomabackend.exception.UserNotFoundException;
import com.netology.diploma.loikokate.diplomabackend.repository.FileRepository;
import com.netology.diploma.loikokate.diplomabackend.repository.UserRepository;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import com.netology.diploma.loikokate.diplomabackend.service.LoginService;
import com.netology.diploma.loikokate.diplomabackend.service.impl.FileServiceImpl;
import com.netology.diploma.loikokate.diplomabackend.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.hamcrest.core.IsEqual;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;

@Testcontainers
@SpringBootTest
@Sql(scripts = {"classpath:/init_mysql.sql"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationApplicationTest {

    private final static MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes());

    @Container
    MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("diploma")
            .withUsername("root")
            .withPassword("mysql");

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Value("${my-storage-directory}")
    private String storageDir;
    private LoginController loginController;
    private String fileName;
    private FileRequest fileRequest;
    private FileController fileController;
    private Pageable firstPageWithLimit;

    @BeforeEach
    void setUp() {
        LoginService loginService = new LoginServiceImpl(userRepository);
        FileService fileService = new FileServiceImpl(fileRepository);
        ReflectionTestUtils.setField(fileService, "storageDir", storageDir);
        fileController = new FileController(fileService);
        fileName = file.getOriginalFilename();
        fileRequest = new FileRequest(fileName, file);
        loginController = new LoginController(loginService);
        firstPageWithLimit = PageRequest.of(0, 1);
    }

    @DisplayName("Login successful")
    @Test
    @Order(1)
    void testLoginAuthSuccessful() {
        final var result = loginController.login(new LoginRequest("user", "qwerty1")).getAuthToken();
        assertThat(result, is(equalTo("5bf82eab207b54ffdf3a980ddf668aeb")));
    }

    @DisplayName("Login unsuccessful")
    @Test
    @Order(2)
    void testLoginAuthUnSuccessful() {
        Exception exception = assertThrows(UserNotFoundException.class,
                () -> loginController.login(new LoginRequest("user", "qw")));
        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(IsEqual.equalTo(expectedMessage)));
    }

    @DisplayName("Save file in db successful")
    @Test
    @Order(3)
    void testSaveFileSuccessful() {
        fileController.file(new FileRequest(fileName, file));
        FileEntity fileEntity = fileRepository.findByFilename(fileName);
        assertThat(fileEntity.getFilename(), is(IsEqual.equalTo(fileName)));
        assertThat(fileEntity.getSize(), is(IsEqual.equalTo(13L)));
    }

    @DisplayName("Delete file successful")
    @Test
    @Order(4)
    void testDeleteFileSuccessful() {
        fileController.deleteFile(fileRequest);
        List<FileEntity> fileEntityList = fileRepository.findByOrderByIdDesc(firstPageWithLimit);
        assertThat(fileEntityList.size(), is(IsEqual.equalTo(0)));
    }

    @DisplayName("Download file successful")
    @Test
    @Order(5)
    void testDownloadFileSuccessful() {
        fileController.file(new FileRequest(fileName, file));
        Resource resource = fileController.downloadFile(fileRequest);
        assertThat(resource.exists(), is(IsEqual.equalTo(true)));
    }

    @DisplayName("Edit file successful")
    @Test
    @Order(6)
    void testEditFileSuccessful() {
        String newFileName = fileName + "New";
        fileController.editFile(fileName, new FileRequest(newFileName, file));
        List<FileEntity> fileEntityList = fileRepository.findByOrderByIdDesc(firstPageWithLimit);
        assertThat(fileEntityList.size(), is(IsEqual.equalTo(1)));
        assertThat(fileEntityList.get(0).getFilename(), is(IsEqual.equalTo(newFileName)));
        assertThat(fileEntityList.get(0).getSize(), is(IsEqual.equalTo(13L)));
    }
}
