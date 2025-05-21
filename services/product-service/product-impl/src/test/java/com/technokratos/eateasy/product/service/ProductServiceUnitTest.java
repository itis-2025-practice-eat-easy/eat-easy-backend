package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.exception.ProductAlreadyExistsException;
import com.technokratos.eateasy.product.exception.ProductNotFoundException;
import com.technokratos.eateasy.product.mapper.ProductMapper;
import com.technokratos.eateasy.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        product = Product.builder()
                .id(productId)
                .title("Burger")
                .description("Tasty burger")
                .photoUrl("http://image.com")
                .price(new BigDecimal("5.99"))
                .quantity(10)
                .build();

        productRequest = ProductRequest.builder()
                .title("Burger")
                .description("Tasty burger")
                .photoUrl("http://image.com")
                .price(new BigDecimal("5.99"))
                .quantity(10)
                .build();

        productResponse = ProductResponse.builder()
                .id(productId)
                .title("Burger")
                .description("Tasty burger")
                .photoUrl("http://image.com")
                .price(new BigDecimal("5.99"))
                .quantity(10)
                .build();
    }

    @Test
    void getByIdSuccess() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getById(productId);

        assertNotNull(result);
        assertEquals(productResponse, result);
    }

    @Test
    void getByIdNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getById(productId));
    }

    @Test
    void createSuccess() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.create(productRequest);

        assertNotNull(result);
        assertEquals(productResponse, result);
    }

    @Test
    void createDuplicateThrowsException() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.create(productRequest));
    }

    @Test
    void updateQuantitySuccess() {
        when(productRepository.updateQuantityIfNotNegative(productId, 5)).thenReturn(1);

        assertDoesNotThrow(() -> productService.updateQuantity(productId, 5));
    }

    @Test
    void updateQuantityNotFound() {
        when(productRepository.updateQuantityIfNotNegative(productId, 5)).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () -> productService.updateQuantity(productId, 5));
    }

    @Test
    void updateQuantityDataViolation() {
        when(productRepository.updateQuantityIfNotNegative(productId, 5))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> productService.updateQuantity(productId, 5));
    }

    @Test
    void updateSuccess() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.update(eq(productId), anyMap())).thenReturn(1);

        assertDoesNotThrow(() -> productService.update(productId, productRequest));
    }

    @Test
    void updateNotFound() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.update(eq(productId), anyMap())).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () -> productService.update(productId, productRequest));
    }

    @Test
    void updateDataViolation() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.update(eq(productId), anyMap()))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> productService.update(productId, productRequest));
    }

    @Test
    void deleteSuccess() {
        when(productRepository.deleteById(productId)).thenReturn(1);

        assertDoesNotThrow(() -> productService.delete(productId));
    }

    @Test
    void deleteNotFound() {
        when(productRepository.deleteById(productId)).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () -> productService.delete(productId));
    }

    @Test
    void getByCategoryIdReturnsProducts() {
        UUID categoryId = UUID.randomUUID();
        when(productRepository.getByCategoryId(eq(categoryId), any(), any(), any(), any(), any()))
                .thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> result = productService.getByCategoryId(categoryId, "title", 0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponse, result.get(0));
    }
}
