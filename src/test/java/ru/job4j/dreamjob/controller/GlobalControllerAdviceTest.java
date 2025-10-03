package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GlobalControllerAdviceTest {

    private final GlobalControllerAdvice globalControllerAdvice = new GlobalControllerAdvice();

    @Test
    public void whenRootPathThenReturnHome() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("home");
    }

    @Test
    public void whenIndexPathThenReturnHome() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/index");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("home");
    }

    @Test
    public void whenVacanciesCreatePathThenReturnVacanciesCreate() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/vacancies/create");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("vacancies-create");
    }

    @Test
    public void whenCandidatesCreatePathThenReturnCandidatesCreate() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/candidates/create");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("candidates-create");
    }

    @Test
    public void whenVacanciesPathThenReturnVacancies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/vacancies");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("vacancies");
    }

    @Test
    public void whenVacanciesWithIdPathThenReturnVacancies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/vacancies/123");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("vacancies");
    }

    @Test
    public void whenCandidatesPathThenReturnCandidates() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/candidates");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("candidates");
    }

    @Test
    public void whenUnknownPathThenReturnEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/unknown");
        String currentPage = globalControllerAdvice.getCurrentPage(request);
        assertThat(currentPage).isEqualTo("");
    }
}