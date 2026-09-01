package com.example.product.product;

import com.example.product.product.ProductDtos.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }
    @GetMapping public PageResponse<ProductResponse> all(@PageableDefault(size = 20, sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable) { return PageResponse.from(service.findAll(pageable)); }
    @GetMapping("/{id}") public ProductResponse one(@PathVariable Long id) { return service.find(id); }
    @GetMapping("/{id}/items") public java.util.List<ItemResponse> items(@PathVariable Long id) { return service.find(id).items(); }
    @PostMapping public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request, Authentication auth) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName())); }
    @PutMapping("/{id}") public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request, Authentication auth) { return service.update(id, request, auth.getName()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}