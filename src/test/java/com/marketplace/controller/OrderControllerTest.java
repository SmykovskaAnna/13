package com.marketplace.controller;

import com.marketplace.model.*;
import com.marketplace.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock private CartItemRepository cartRepo;
    @Mock private OrderRepository orderRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks private OrderController orderController;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_ShouldCreateOrderAndClearCart() {
        // Setup mock user and cart item
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setPrice(20.0);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);

        List<CartItem> items = List.of(item);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userRepo.findByUsername("user")).thenReturn(user);
        when(cartRepo.findByUser(user)).thenReturn(items);

        // Call the method under test
        String view = orderController.placeOrder(principal);

        // Verify that a new order was saved
        verify(orderRepo, times(1)).save(any(Order.class));
        // Verify that the cart was cleared after placing the order
        verify(cartRepo, times(1)).deleteAll(items);
        // Verify redirection to orders page
        assertEquals("redirect:/orders", view);
    }

    @Test
    void viewOrders_ShouldReturnOrdersPageWithModel() {
        // Setup mock user and orders
        User user = new User();
        user.setId(1L);

        List<Order> orders = List.of(new Order());

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userRepo.findByUsername("user")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

        Model model = mock(Model.class);

        // Call the method under test
        String view = orderController.viewOrders(model, principal);

        // Verify that orders are added to the model
        verify(model).addAttribute("orders", orders);
        // Verify that correct view name is returned
        assertEquals("orders", view);
    }

    @Test
    void payOrder_ValidOrder_ShouldSetStatusPaid() {
        // Setup valid user and order
        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus("Placed");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userRepo.findByUsername("user")).thenReturn(user);
        when(orderRepo.findById(100L)).thenReturn(Optional.of(order));

        // Call the method under test
        String view = orderController.payOrder(100L, principal);

        // Verify that order status is updated to Paid
        assertEquals("Paid", order.getStatus());
        // Verify that the updated order was saved
        verify(orderRepo).save(order);
        // Verify redirection to dashboard
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void payOrder_InvalidOrderOrDifferentUser_ShouldNotUpdateStatus() {
        // Setup current user and an order that belongs to a different user
        User user = new User();
        user.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Order order = new Order();
        order.setId(200L);
        order.setUser(otherUser);
        order.setStatus("Placed");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userRepo.findByUsername("user")).thenReturn(user);
        when(orderRepo.findById(200L)).thenReturn(Optional.of(order));

        // Call the method under test
        String view = orderController.payOrder(200L, principal);

        // Ensure order status is not changed by unauthorized user
        assertNotEquals("Paid", order.getStatus());
        // Ensure the order is not saved
        verify(orderRepo, never()).save(order);
        // Verify redirection still happens
        assertEquals("redirect:/dashboard", view);
    }
}