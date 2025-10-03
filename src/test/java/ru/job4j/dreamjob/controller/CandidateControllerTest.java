package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CandidateControllerTest {

    private CandidateService candidateService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        candidateController = new CandidateController(candidateService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "John Doe", "Java Developer", now(), 1);
        var candidate2 = new Candidate(2, "Jane Smith", "Frontend Developer", now(), 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPage() {
        var view = candidateController.getCreationPage();

        assertThat(view).isEqualTo("candidates/create");
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "John Doe", "Java Developer", now(), 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        doNothing().when(candidateService).save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture());

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenPostCandidateWithExceptionThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to save file");
        doThrow(expectedException).when(candidateService).save(any(), any());

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestCandidateByIdThenGetPageWithCandidate() {
        var candidate = new Candidate(1, "John Doe", "Java Developer", now(), 1);
        when(candidateService.findById(1)).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        var actualCandidate = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate);
    }

    @Test
    public void whenRequestNonExistentCandidateByIdThenGetErrorPage() {
        when(candidateService.findById(999)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 999);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenUpdateCandidateThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "John Doe", "Java Developer", now(), 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenUpdateNonExistentCandidateThenGetErrorPage() {
        var candidate = new Candidate(999, "John Doe", "Java Developer", now(), 1);
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найдена");
    }

    @Test
    public void whenUpdateCandidateWithExceptionThenGetErrorPage() {
        var candidate = new Candidate(1, "John Doe", "Java Developer", now(), 1);
        var expectedException = new RuntimeException("Update failed");
        doThrow(expectedException).when(candidateService).update(any(Candidate.class), any(FileDto.class));

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Update failed");
    }

    @Test
    public void whenDeleteCandidateThenRedirectToCandidatesPage() {
        when(candidateService.deleteById(1)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteNonExistentCandidateThenGetErrorPage() {
        when(candidateService.deleteById(999)).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 999);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
    }
}