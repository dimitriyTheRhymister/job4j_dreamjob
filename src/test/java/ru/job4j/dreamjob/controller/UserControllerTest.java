package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
    }

    @Test
    public void whenRequestRegistrationPageThenGetPage() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterNewUserThenRedirectToIndex() {
        var user = new User(1, "user@example.com", "John Doe", "password");
        when(userService.save(user)).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);

        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    public void whenRegisterExistingUserThenGetErrorPage() {
        var user = new User(1, "existing@example.com", "John Doe", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualMessage).isEqualTo("Пользователь с таким email уже существует.");
    }

    @Test
    public void whenRequestLoginPageThenGetPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginSuccessThenRedirectToVacancies() {
        var user = new User(1, "user@example.com", "John Doe", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);

        verify(session).setAttribute("user", user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginWithWrongCredentialsThenGetErrorPage() {
        var user = new User(1, "wrong@example.com", "John Doe", "wrongpassword");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var actualError = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualError).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLogoutThenInvalidateSessionAndRedirectToLogin() {
        var view = userController.logout(session);

        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}