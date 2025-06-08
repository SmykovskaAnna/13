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

    public UserController(UserRepository repo, PasswordEncoder encoder) {
        this.userRepo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute @Valid User user,
                                  BindingResult result,
                                  Model model) {

        // Перевірка на дублікати імені користувача
        if (userRepo.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // Перевірка email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (user.getEmail() == null || !Pattern.matches(emailRegex, user.getEmail())) {
            result.rejectValue("email", "error.user", "Invalid email format");
        }

        // Перевірка на порожні поля
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            result.rejectValue("username", "error.user", "Username is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            result.rejectValue("password", "error.user", "Password is required");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepo.save(user);
        return "redirect:/login";
    }
}
