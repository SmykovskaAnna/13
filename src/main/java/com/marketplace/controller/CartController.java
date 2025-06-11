package com.marketplace.controller;

import com.marketplace.model.*;
import com.marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * Adds multiple products to the user's cart.
     * Accepts a list of product IDs and corresponding quantities.
     */
    @PostMapping("/cart/add-multiple")
    public String addMultipleToCart(@RequestParam List<Long> productIds,
                                    @RequestParam List<Integer> quantities,
                                    Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Iterate over the list of products and quantities
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int qty = quantities.get(i);

            // Skip invalid quantity
            if (qty <= 0) continue;

            // Find the product by ID
            Product product = productRepo.findById(productId).orElse(null);
            if (product != null) {
                // Create a new cart item and associate it with the user and product
                CartItem item = new CartItem();
                item.setUser(user);
                item.setProduct(product);
                item.setQuantity(qty);

                // Save the cart item
                cartRepo.save(item);
            }
        }

        // Redirect to cart view
        return "redirect:/cart";
    }

    /**
     * Displays the current user's cart.
     */
    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Fetch all cart items for the user and pass to the view
        model.addAttribute("items", cartRepo.findByUser(user));
        return "cart";
    }

    /**
     * Removes a specific item from the user's cart.
     * Only allows removal if the item belongs to the current user.
     */
    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam Long itemId, Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Find the cart item by ID
        CartItem item = cartRepo.findById(itemId).orElse(null);

        // Ensure the item belongs to the user before deleting
        if (item != null && item.getUser().getId().equals(user.getId())) {
            cartRepo.delete(item);
        }

        // Redirect to cart view
        return "redirect:/cart";
    }
}