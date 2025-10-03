package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenGetExistingFileThenReturnFileContent() {
        var fileContent = new byte[]{1, 2, 3, 4, 5};
        var fileDto = new FileDto("test.jpg", fileContent);
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> response = fileController.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fileContent);
    }

    @Test
    public void whenGetNonExistingFileThenReturnNotFound() {
        when(fileService.getFileById(999)).thenReturn(Optional.empty());

        ResponseEntity<?> response = fileController.getById(999);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}