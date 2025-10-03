package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IndexControllerTest {

    private final IndexController indexController = new IndexController();

    @Test
    public void whenRequestRootPathThenGetIndexPage() {
        var view = indexController.getIndex();
        assertThat(view).isEqualTo("index");
    }

    @Test
    public void whenRequestIndexPathThenGetIndexPage() {
        var view = indexController.getIndex();
        assertThat(view).isEqualTo("index");
    }
}