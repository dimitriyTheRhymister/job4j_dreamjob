package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentPage")
    public String getCurrentPage(HttpServletRequest request) {
        String path = request.getServletPath();

        if (path.equals("/") || path.equals("/index")) {
            return "home";
        } else if (path.startsWith("/vacancies/create")) {
            return "vacancies-create";
        } else if (path.startsWith("/candidates")) {
            return "candidates";
        } else if (path.startsWith("/vacancies")) {
            return "vacancies";
        }

        return "";
    }
}