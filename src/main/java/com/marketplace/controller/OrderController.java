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

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * Places a new order using all items in the user's cart.
     * Calculates the total price, saves the order, and clears the cart.
     */
    @PostMapping("/order")
    public String placeOrder(Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Fetch all cart items for the user
        List<CartItem> items = cartRepo.findByUser(user);

        // Calculate the total price of the order
        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        // Create a new order and set its properties
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus("Placed");

        // Save the order to the database
        orderRepo.save(order);

        // Clear the user's cart after placing the order
        cartRepo.deleteAll(items);

        // Redirect to the orders page
        return "redirect:/orders";
    }

    /**
     * Displays a list of the current user's orders.
     */
    @GetMapping("/orders")
    public String viewOrders(Model model, Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Fetch all orders for the user and add to model
        model.addAttribute("orders", orderRepo.findByUser(user));

        // Return the orders view
        return "orders";
    }

    /**
     * Marks a specific order as paid.
     * Ensures that the order belongs to the authenticated user before updating.
     */
    @PostMapping("/order/pay/{id}")
    public String payOrder(@PathVariable Long id, Principal principal) {
        // Get the currently authenticated user
        User user = userRepo.findByUsername(principal.getName());

        // Retrieve the order by ID
        Order order = orderRepo.findById(id).orElse(null);

        // Check ownership and update status to "Paid"
        if (order != null && order.getUser().getId().equals(user.getId())) {
            order.setStatus("Paid");
            orderRepo.save(order);
        }

        // Redirect to the user's dashboard
        return "redirect:/dashboard";
    }
}