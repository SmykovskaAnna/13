package com.marketplace.controller;

import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidUserRegistration() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userRepo.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        String view = userController.processRegister(user, bindingResult, model);

        verify(userRepo, times(1)).save(any(User.class));
        assertEquals("redirect:/login", view);
    }

    @Test
    public void testDuplicateUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userRepo.findByUsername("testuser")).thenReturn(new User());
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.processRegister(user, bindingResult, model);

        verify(userRepo, never()).save(any(User.class));
        assertEquals("register", view);
    }

    @Test
    public void testInvalidEmail() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("invalid-email");

        when(userRepo.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.processRegister(user, bindingResult, model);

        verify(userRepo, never()).save(any(User.class));
        assertEquals("register", view);
    }

    @Test
    public void testMissingFields() {
        User user = new User(); // no username, no password, no email

        when(bindingResult.hasErrors()).thenReturn(true);

        String view = userController.processRegister(user, bindingResult, model);

        verify(userRepo, never()).save(any(User.class));
        assertEquals("register", view);
    }
}
