package com.marketplace.controller;

import com.marketplace.model.*;
import com.marketplace.model.Order;
import com.marketplace.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MarketplaceIntegrationTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartItemRepository cartRepo;

    @BeforeEach
    public void cleanUp() {
        cartRepo.deleteAll();
        orderRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void testUserCanRegisterAddToCartAndPlaceOrder() {
        // Step 1: Register user
        User user = new User();
        user.setUsername("integrationUser");
        user.setPassword("password");
        user.setEmail("integration@example.com");
        userRepo.save(user);

        // Step 2: Add product
        Product product = new Product();
        product.setName("Integration Product");
        product.setPrice(50.0);
        productRepo.save(product);

        // Step 3: Add to cart
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartRepo.save(cartItem);

        // Step 4: Simulate placing order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("Placed");
        order.setTotal(100.0);
        orderRepo.save(order);

        // Assertions
        assertNotNull(userRepo.findByUsername("integrationUser"));
        assertEquals(1, productRepo.findAll().size());
        assertEquals(1, cartRepo.findByUser(user).size());
        assertEquals(1, orderRepo.findByUser(user).size());
    }
}