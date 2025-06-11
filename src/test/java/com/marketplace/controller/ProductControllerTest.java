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
        // Initialize Mockito annotations before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_ValidProduct_ShouldSaveAndRedirect() {
        // Prepare a valid product with name and price
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(10.0);

        BindingResult bindingResult = mock(BindingResult.class);
        Model model = mock(Model.class);

        // No validation errors expected
        when(bindingResult.hasErrors()).thenReturn(false);

        // Call the method under test
        String view = productController.addProduct(product, bindingResult, model);

        // Verify that product is saved to repository
        verify(productRepo, times(1)).save(product);
        // Verify redirect to products list page
        assertEquals("redirect:/products", view);
    }

    @Test
    void addProduct_DuplicateProductName_ShouldNotSaveAndReturnError() {
        // Product with a name that already exists in repo
        Product product = new Product();
        product.setName("Existing Product");

        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        Model model = mock(Model.class);

        // Simulate duplicate product found in repository
        when(productRepo.findByName("Existing Product")).thenReturn(product);

        // Call the method under test
        String view = productController.addProduct(product, bindingResult, model);

        // Expect to stay on the add-product page due to error
        assertEquals("add-product", view);
        // Validation errors should be present (duplicate name)
        assertTrue(bindingResult.hasErrors());
        // Ensure save is NOT called for duplicate
        verify(productRepo, never()).save(any());
    }

    @Test
    void addProduct_MissingRequiredFields_ShouldNotSaveAndReturnError() {
        // Product with missing required fields (e.g. name)
        Product product = new Product();
        product.setName(null);

        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        Model model = mock(Model.class);

        // No duplicate found for null name (usually validation fails before this)
        when(productRepo.findByName(null)).thenReturn(null);

        // Call the method under test
        String view = productController.addProduct(product, bindingResult, model);

        // Expect to stay on add-product page due to validation errors
        assertEquals("add-product", view);
        // Validation errors should be present for missing fields
        assertTrue(bindingResult.hasErrors());
        // Ensure no product is saved when data invalid
        verify(productRepo, never()).save(any());
    }
}