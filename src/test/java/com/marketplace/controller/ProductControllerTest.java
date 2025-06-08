package com.marketplace.controller;

import com.marketplace.model.Product;
import com.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_ValidProduct_ShouldSaveAndRedirect() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(10.0);

        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = productController.addProduct(product, bindingResult, model);

        verify(productRepo, times(1)).save(product);
        assertEquals("redirect:/products", view);
    }

    @Test
    void addProduct_DuplicateProductName_ShouldNotSaveAndReturnError() {
        Product product = new Product();
        product.setName("Existing Product");

        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        Model model = mock(Model.class);

        when(productRepo.findByName("Existing Product")).thenReturn(product);

        String view = productController.addProduct(product, bindingResult, model);

        assertEquals("add-product", view);
        assertTrue(bindingResult.hasErrors());
        verify(productRepo, never()).save(any());
    }


    @Test
    void addProduct_MissingRequiredFields_ShouldNotSaveAndReturnError() {
        Product product = new Product();
        product.setName(null);

        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        Model model = mock(Model.class);

        when(productRepo.findByName(null)).thenReturn(null);

        String view = productController.addProduct(product, bindingResult, model);

        assertEquals("add-product", view);
        assertTrue(bindingResult.hasErrors());
        verify(productRepo, never()).save(any());
    }

}
