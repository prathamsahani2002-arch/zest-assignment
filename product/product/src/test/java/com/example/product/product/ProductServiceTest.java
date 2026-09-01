package com.example.product.product;

import com.example.product.product.ProductDtos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock ProductRepository repository;
    @InjectMocks ProductService service;

    @Test
    void createPersistsProductAndItems() {
        ProductRequest request = new ProductRequest("Widget", List.of(new ItemRequest(3)));
        when(repository.saveAndFlush(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProductDtos.ProductResponse response = service.create(request, "alice");
        assertEquals("Widget", response.productName());
        assertEquals("alice", response.createdBy());
        assertEquals(1, response.items().size());
        verify(repository).saveAndFlush(any(Product.class));
    }

    @Test
    void updateChangesNameAndReplacesItems() {
        Product product = new Product("Old", "alice");
        when(repository.findById(7L)).thenReturn(Optional.of(product));
        ProductRequest request = new ProductRequest("New", List.of(new ItemRequest(9), new ItemRequest(2)));
        ProductDtos.ProductResponse response = service.update(7L, request, "bob");
        assertEquals("New", response.productName());
        assertEquals("bob", response.modifiedBy());
        assertEquals(List.of(9, 2), response.items().stream().map(ItemResponse::quantity).toList());
    }

    @Test
    void missingProductIsRejected() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.find(99L));
    }
}