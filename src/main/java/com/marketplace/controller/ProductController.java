package com.marketplace.controller;

import com.marketplace.repository.ProductRepository;
import com.marketplace.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    /**
     * Displays a list of all products.
     * If a keyword is provided, filters products by name containing that keyword (case-insensitive).
     */
    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;

        // If a search keyword is provided, filter products by name
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepo.findByNameContainingIgnoreCase(keyword);
        } else {
            // Otherwise, retrieve all products
            products = productRepo.findAll();
        }

        // Add products and keyword to the model for view rendering
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "products";
    }

    /**
     * Displays a form for adding a new product.
     */
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    /**
     * Handles form submission for adding a new product.
     * Performs basic validation and checks for duplicate product names.
     */
    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, BindingResult result, Model model) {
        // Validate that the product name is not empty
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            result.rejectValue("name", "error.product", "Product name is required");
        }

        // Check if a product with the same name already exists
        if (productRepo.findByName(product.getName()) != null) {
            result.rejectValue("name", "error.product", "Product with this name already exists");
        }

        // If there are validation errors, return to the form
        if (result.hasErrors()) {
            return "add-product";
        }

        // Save the new product to the database
        productRepo.save(product);

        // Redirect to the product list page
        return "redirect:/products";
    }

}