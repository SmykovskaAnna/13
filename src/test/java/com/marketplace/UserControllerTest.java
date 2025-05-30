package com.marketplace;

import com.marketplace.controller.UserController;
import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Test
    public void testProcessRegister_SavesUserWithEncodedPasswordAndRole() {
        // Мокаем зависимости
        UserRepository userRepo = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Настраиваем поведение моков
        when(passwordEncoder.encode("pass1234")).thenReturn("encodedPass1234");

        // Создаем контроллер с моками
        UserController userController = new UserController(userRepo, passwordEncoder);

        // Создаем пользователя для регистрации
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("pass1234");

        // Вызов тестируемого метода
        String view = userController.processRegister(user);

        // Проверяем, что вернулся редирект на /login
        assertThat(view).isEqualTo("redirect:/login");

        // Проверяем, что пароль был зашифрован и роль установлена
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("john_doe");
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPass1234");
        assertThat(savedUser.getRole()).isEqualTo("ROLE_USER");
    }
}
