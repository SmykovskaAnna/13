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
public class DashboardController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepo;

    /**
     * Displays the user's dashboard with order statistics.
     * Shows all orders, count of total and paid orders, and total amount spent.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Retrieve the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Get all orders placed by the user
        List<Order> orders = orderRepo.findByUser(user);

        // Calculate the total number of orders
        long totalOrders = orders.size();

        // Count the number of paid orders
        long paidOrders = orders.stream()
                .filter(o -> "Paid".equalsIgnoreCase(o.getStatus()))
                .count();

        // Sum the total amount spent on paid orders
        double totalSpent = orders.stream()
                .filter(o -> "Paid".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotal)
                .sum();

        // Add data to the model for rendering in the view
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("paidOrders", paidOrders);
        model.addAttribute("totalSpent", totalSpent);

        // Return the dashboard view
        return "dashboard";
    }
}