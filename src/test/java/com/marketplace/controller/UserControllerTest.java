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
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidUserRegistration() {
        // Prepare a valid user with username, password, email
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        // Simulate no existing user with the same username
        when(userRepo.findByUsername("testuser")).thenReturn(null);
        // Simulate no validation errors
        when(bindingResult.hasErrors()).thenReturn(false);
        // Simulate password encoding
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        // Call method under test
        String view = userController.processRegister(user, bindingResult, model);

        // Verify user is saved once
        verify(userRepo, times(1)).save(any(User.class));
        // Expect redirect to login page on successful registration
        assertEquals("redirect:/login", view);
    }

    @Test
    public void testDuplicateUsername() {
        // Prepare user with username that already exists
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        // Simulate duplicate username found in repository
        when(userRepo.findByUsername("testuser")).thenReturn(new User());
        // Simulate validation errors exist
        when(bindingResult.hasErrors()).thenReturn(true);

        // Call method under test
        String view = userController.processRegister(user, bindingResult, model);

        // Verify user is NOT saved due to duplicate username
        verify(userRepo, never()).save(any(User.class));
        // Expect to return to registration page with errors
        assertEquals("register", view);
    }

    @Test
    public void testInvalidEmail() {
        // Prepare user with invalid email format
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("invalid-email");

        // Simulate no duplicate username found
        when(userRepo.findByUsername("testuser")).thenReturn(null);
        // Simulate validation errors due to invalid email
        when(bindingResult.hasErrors()).thenReturn(true);

        // Call method under test
        String view = userController.processRegister(user, bindingResult, model);

        // Verify user is NOT saved due to validation errors
        verify(userRepo, never()).save(any(User.class));
        // Expect to return to registration page with errors
        assertEquals("register", view);
    }

    @Test
    public void testMissingFields() {
        // Prepare user missing all required fields
        User user = new User(); // username, password, email all null

        // Simulate validation errors for missing fields
        when(bindingResult.hasErrors()).thenReturn(true);

        // Call method under test
        String view = userController.processRegister(user, bindingResult, model);

        // Verify user is NOT saved due to validation errors
        verify(userRepo, never()).save(any(User.class));
        // Expect to return to registration page with errors
        assertEquals("register", view);
    }
}