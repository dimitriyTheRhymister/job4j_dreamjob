package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;

import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveThenFindByEmailAndPassword() {
        var user = new ru.job4j.dreamjob.model.User(0, "test@example.com", "Test User", "password123");
        var savedUser = sql2oUserRepository.save(user).get();

        var foundUser = sql2oUserRepository.findByEmailAndPassword("test@example.com", "password123").get();

        assertThat(foundUser).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void whenFindByWrongPasswordThenGetEmpty() {
        var user = new ru.job4j.dreamjob.model.User(0, "login@example.com", "Login User", "pass456");
        sql2oUserRepository.save(user);

        var foundUser = sql2oUserRepository.findByEmailAndPassword("login@example.com", "wrongpass");

        assertThat(foundUser).isEqualTo(empty());
    }

    @Test
    public void whenFindByWrongEmailThenGetEmpty() {
        var user = new ru.job4j.dreamjob.model.User(0, "login@example.com", "Login User", "pass456");
        sql2oUserRepository.save(user);

        var foundUser = sql2oUserRepository.findByEmailAndPassword("wrong@example.com", "pass456");

        assertThat(foundUser).isEqualTo(empty());
    }

    @Test
    public void whenFindByNonExistentUserThenGetEmpty() {
        var foundUser = sql2oUserRepository.findByEmailAndPassword("nonexistent@example.com", "nopass");

        assertThat(foundUser).isEqualTo(empty());
    }

    @Test
    public void whenSaveSameEmailThenReturnEmptyOptional() {
        var user1 = new ru.job4j.dreamjob.model.User(0, "duplicate@example.com", "User One", "password123");
        var user2 = new ru.job4j.dreamjob.model.User(0, "duplicate@example.com", "User Two", "password456");

        sql2oUserRepository.save(user1);

        var result = sql2oUserRepository.save(user2);

        assertThat(result.isEmpty()).isTrue();
    }
}