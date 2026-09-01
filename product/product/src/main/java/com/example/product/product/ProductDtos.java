package com.example.product.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import java.time.Instant;
import java.util.List;

public final class ProductDtos {
    private ProductDtos() { }
    public record ItemRequest(@NotNull @Positive Integer quantity) { }
    public record ProductRequest(@NotBlank @Size(max = 255) String productName,
                                 @NotNull @Size(max = 100) List<@Valid ItemRequest> items) { }
    public record ItemResponse(Long id, Integer quantity) { }
    public record ProductResponse(Long id, String productName, String createdBy, Instant createdOn,
                                  String modifiedBy, Instant modifiedOn, List<ItemResponse> items) {
        static ProductResponse from(Product product) {
            return new ProductResponse(product.getId(), product.getProductName(), product.getCreatedBy(),
                product.getCreatedOn(), product.getModifiedBy(), product.getModifiedOn(),
                product.getItems().stream().map(item -> new ItemResponse(item.getId(), item.getQuantity())).toList());
        }
    }
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
        static <T> PageResponse<T> from(Page<T> page) { return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()); }
    }
}