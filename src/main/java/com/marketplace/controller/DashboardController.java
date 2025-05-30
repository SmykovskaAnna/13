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
    @Autowired private UserRepository userRepo;
    @Autowired private OrderRepository orderRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        List<Order> orders = orderRepo.findByUser(user);

        long totalOrders = orders.size();
        long paidOrders = orders.stream().filter(o -> "Paid".equalsIgnoreCase(o.getStatus())).count();
        double totalSpent = orders.stream()
                .filter(o -> "Paid".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotal).sum();

        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("paidOrders", paidOrders);
        model.addAttribute("totalSpent", totalSpent);
        return "dashboard";
    }
}