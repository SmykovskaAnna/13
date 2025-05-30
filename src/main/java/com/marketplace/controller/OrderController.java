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
public class OrderController {
    @Autowired private CartItemRepository cartRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private UserRepository userRepo;

    @PostMapping("/order")
    public String placeOrder(Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        List<CartItem> items = cartRepo.findByUser(user);
        double total = items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus("Placed");
        orderRepo.save(order);

        cartRepo.deleteAll(items);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        model.addAttribute("orders", orderRepo.findByUser(user));
        return "orders";
    }

    @PostMapping("/order/pay/{id}")
    public String payOrder(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByUsername(principal.getName());
        Order order = orderRepo.findById(id).orElse(null);

        if (order != null && order.getUser().getId().equals(user.getId())) {
            order.setStatus("Paid");
            orderRepo.save(order);
        }

        return "redirect:/dashboard";
    }
}