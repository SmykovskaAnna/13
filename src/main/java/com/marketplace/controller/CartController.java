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
    @Autowired private CartItemRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UserRepository userRepo;

    @PostMapping("/cart/add-multiple")
    public String addMultipleToCart(@RequestParam List<Long> productIds,
                                    @RequestParam List<Integer> quantities,
                                    Principal principal) {
        User user = userRepo.findByUsername(principal.getName());

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            int qty = quantities.get(i);
            if (qty <= 0) continue;

            Product product = productRepo.findById(productId).orElse(null);
            if (product != null) {
                CartItem item = new CartItem();
                item.setUser(user);
                item.setProduct(product);
                item.setQuantity(qty);
                cartRepo.save(item);
            }
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        model.addAttribute("items", cartRepo.findByUser(user));
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam Long itemId, Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        CartItem item = cartRepo.findById(itemId).orElse(null);
        if (item != null && item.getUser().getId().equals(user.getId())) {
            cartRepo.delete(item);
        }
        return "redirect:/cart";
    }
}