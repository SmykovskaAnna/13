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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_ShouldCreateOrderAndClearCart() {
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

        String view = orderController.placeOrder(principal);

        // Expect saving the order
        verify(orderRepo, times(1)).save(any(Order.class));
        // Expect clearing the cart
        verify(cartRepo, times(1)).deleteAll(items);
        // Expect redirect
        assertEquals("redirect:/orders", view);
    }

    @Test
    void viewOrders_ShouldReturnOrdersPageWithModel() {
        User user = new User();
        user.setId(1L);

        List<Order> orders = List.of(new Order());

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userRepo.findByUsername("user")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

        Model model = mock(Model.class);

        String view = orderController.viewOrders(model, principal);

        verify(model).addAttribute("orders", orders);
        assertEquals("orders", view);
    }

    @Test
    void payOrder_ValidOrder_ShouldSetStatusPaid() {
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

        String view = orderController.payOrder(100L, principal);

        assertEquals("Paid", order.getStatus());
        verify(orderRepo).save(order);
        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void payOrder_InvalidOrderOrDifferentUser_ShouldNotUpdateStatus() {
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

        String view = orderController.payOrder(200L, principal);

        assertNotEquals("Paid", order.getStatus());
        verify(orderRepo, never()).save(order);
        assertEquals("redirect:/dashboard", view);
    }
}