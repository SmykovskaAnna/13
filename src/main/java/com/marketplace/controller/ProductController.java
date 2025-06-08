package com.marketplace.controller;

import com.marketplace.repository.ProductRepository;
import com.marketplace.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    // Показать форму для добавления нового продукта
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    // Обработать форму добавления продукта
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, BindingResult result, Model model) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            result.rejectValue("name", "error.product", "Product name is required");
        }
        if (productRepo.findByName(product.getName()) != null) {
            result.rejectValue("name", "error.product", "Product with this name already exists");
        }
        if (result.hasErrors()) {
            return "add-product";
        }
        productRepo.save(product);
        return "redirect:/products";
    }

}