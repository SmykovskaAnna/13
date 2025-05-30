package com.marketplace.service;

import com.marketplace.model.User;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public String processPayment(User user, double amount) {
        if (amount <= 0) return "Invalid";
        return "Success";
    }
}