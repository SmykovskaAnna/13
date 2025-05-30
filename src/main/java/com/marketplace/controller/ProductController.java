package com.marketplace.controller;

import com.marketplace.repository.ProductRepository;
import com.marketplace.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
    @Autowired private ProductRepository productRepo;

    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;

        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepo.findByNameContainingIgnoreCase(keyword);
        } else {
            products = productRepo.findAll();
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "products";
    }
}