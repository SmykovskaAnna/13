package com.marketplace.controller;

import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    // Constructor injection for UserRepository and PasswordEncoder
    public UserController(UserRepository repo, PasswordEncoder encoder) {
        this.userRepo = repo;
        this.encoder = encoder;
    }

    /**
     * Displays the login form.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Displays the registration form.
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Processes the registration form submission.
     * Performs validation, encodes the password, and saves the new user.
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute @Valid User user,
                                  BindingResult result,
                                  Model model) {

        // Check if the username already exists
        if (userRepo.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // Validate the email format
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (user.getEmail() == null || !Pattern.matches(emailRegex, user.getEmail())) {
            result.rejectValue("email", "error.user", "Invalid email format");
        }

        // Check if username is empty
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            result.rejectValue("username", "error.user", "Username is required");
        }

        // Check if password is empty
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            result.rejectValue("password", "error.user", "Password is required");
        }

        // If there are any validation errors, return to the registration form
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        // Encode the password before saving to the database
        user.setPassword(encoder.encode(user.getPassword()));

        // Set default role
        user.setRole("ROLE_USER");

        // Save user to the repository
        userRepo.save(user);

        // Redirect to login page after successful registration
        return "redirect:/login";
    }
}