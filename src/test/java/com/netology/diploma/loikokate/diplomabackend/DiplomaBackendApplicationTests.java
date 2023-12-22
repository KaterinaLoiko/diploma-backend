package com.netology.diploma.loikokate.diplomabackend;

import com.netology.diploma.loikokate.diplomabackend.controller.FileController;
import com.netology.diploma.loikokate.diplomabackend.controller.ListController;
import com.netology.diploma.loikokate.diplomabackend.controller.LoginController;
import com.netology.diploma.loikokate.diplomabackend.controller.LogoutController;
import com.netology.diploma.loikokate.diplomabackend.dao.FileEntity;
import com.netology.diploma.loikokate.diplomabackend.dao.UserEntity;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileDTO;
import com.netology.diploma.loikokate.diplomabackend.dto.file.FileRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.list.ListRequest;
import com.netology.diploma.loikokate.diplomabackend.dto.login.LoginRequest;
import com.netology.diploma.loikokate.diplomabackend.exception.StorageException;
import com.netology.diploma.loikokate.diplomabackend.exception.UserNotFoundException;
import com.netology.diploma.loikokate.diplomabackend.repository.FileRepository;
import com.netology.diploma.loikokate.diplomabackend.repository.UserRepository;
import com.netology.diploma.loikokate.diplomabackend.service.FileService;
import com.netology.diploma.loikokate.diplomabackend.service.LoginService;
import com.netology.diploma.loikokate.diplomabackend.service.impl.FileServiceImpl;
import com.netology.diploma.loikokate.diplomabackend.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;
import static org.testcontainers.shaded.org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest
@Testcontainers
//@ContextConfiguration(initializers = {DiplomaBackendApplicationTests.Initializer.class})
class DiplomaBackendApplicationTests {

    private final static MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes());
    private final static MockMultipartFile wrongFile
            = new MockMultipartFile(
            "file",
            "",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes());
    @Container
    MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("diploma")
            .withUsername("root")
            .withPassword("mysql");
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileRepository fileRepository;
    @Value("${my-storage-directory}")
    private String storageDir;
    private LoginController loginController;
    private FileController fileController;
    private ListController listFileController;
    private LoginRequest loginRequest;
    private String fileName;
    private FileEntity fileEntity;
    private Pageable firstPageWithLimit;
    private FileRequest fileRequest;

    @BeforeEach
    void setUp() {
        LoginService loginService = new LoginServiceImpl(userRepository);
        FileService fileService = new FileServiceImpl(fileRepository);
        fileController = new FileController(fileService);
        loginController = new LoginController(loginService);
        listFileController = new ListController(fileService);
        ReflectionTestUtils.setField(fileService, "storageDir", storageDir);
        loginRequest = new LoginRequest("user", "qwerty");
        fileName = file.getOriginalFilename();
        fileRequest = new FileRequest(fileName, file);
        fileEntity = new FileEntity(1, fileName, 234L);
        firstPageWithLimit = PageRequest.of(0, 1);
    }

    @DisplayName("Login successful")
    @Test
    void testLoginAuthSuccessful() {
        when(userRepository.findByLoginAndPassword(loginRequest.getLogin(), loginRequest.getPassword()))
                .thenReturn(new UserEntity(1L, "test", "test", "5bf82eab207b54ffdf3a980ddf668aeb"));
        final var result = loginController.login(loginRequest).getAuthToken();
        assertThat(result, is(equalTo("5bf82eab207b54ffdf3a980ddf668aeb")));
    }

    @DisplayName("Login unsuccessful")
    @Test
    void testLoginAuthUnSuccessful() {
        Exception exception = assertThrows(UserNotFoundException.class, () -> loginController.login(loginRequest));

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }

    @DisplayName("Logout")
    @Test
    void testLogoutSuccessful() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new LogoutController()).build();
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("JSESSIONID"));
    }

    @DisplayName("Save and list file successful")
    @Test
    void testSaveFileSuccessful() {
        when(fileRepository.findByOrderByIdDesc(firstPageWithLimit))
                .thenReturn(List.of(fileEntity));
        when(fileRepository.save(fileEntity))
                .thenReturn(fileEntity);
        fileController.file(fileRequest);
        List<FileDTO> files = listFileController.list(new ListRequest(1));
        assertThat(files.size(), is(equalTo(1)));
        assertThat(files.get(0), is(equalTo(new FileDTO(fileName, 234L))));
    }

    @DisplayName("Delete file successful")
    @Test
    void testDeleteFileSuccessful() {
        when(fileRepository.findByOrderByIdDesc(firstPageWithLimit))
                .thenReturn(new ArrayList<>());
        when(fileRepository.save(fileEntity))
                .thenReturn(fileEntity);
        fileController.file(fileRequest);
        fileController.deleteFile(fileRequest);
        List<FileDTO> files = listFileController.list(new ListRequest(1));
        assertThat(files.size(), is(equalTo(0)));
    }

    @DisplayName("Edit file successful")
    @Test
    void testEditFileSuccessful() {
        String newFileName = fileName + "New";
        FileEntity newFileEntity = new FileEntity(1, newFileName, 234L);
        when(fileRepository.findByFilename(fileName))
                .thenReturn(fileEntity);
        when(fileRepository.findByOrderByIdDesc(firstPageWithLimit))
                .thenReturn(List.of(newFileEntity));
        when(fileRepository.save(fileEntity))
                .thenReturn(fileEntity);
        fileController.file(fileRequest);
        fileController.editFile(fileName, new FileRequest(newFileName, file));
        List<FileDTO> files = listFileController.list(new ListRequest(1));
        assertThat(files.size(), is(equalTo(1)));
        assertThat(files.get(0), is(equalTo(new FileDTO(newFileName, 234L))));
    }

    @DisplayName("Save file unsuccessful")
    @Test
    void testSaveUnSuccessful() {
        Exception exception = assertThrows(StorageException.class, () -> fileController.file(new FileRequest(fileName, wrongFile)));

        String expectedMessage = "Failed to store file.";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }

    @DisplayName("Delete file unsuccessful")
    @Test
    void testDeleteUnSuccessful() {
        Exception exception = assertThrows(StorageException.class, () -> fileController.deleteFile(new FileRequest(fileName, wrongFile)));

        String expectedMessage = "Failed to delete file.";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }

    @DisplayName("Edit file unsuccessful")
    @Test
    void testEditUnSuccessful() {
        Exception exception = assertThrows(StorageException.class, () -> fileController.editFile("newFile", new FileRequest(fileName, wrongFile)));

        String expectedMessage = "Failed to rename file.";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }

    @DisplayName("List file unsuccessful")
    @Test
    void testListUnSuccessful() {
        assertThat(listFileController.list(new ListRequest()).size(), is(equalTo(0)));
    }
}
