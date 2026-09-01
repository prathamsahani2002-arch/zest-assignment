package com.example.product.product;

import com.example.product.product.ProductDtos.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) { return repository.findAll(pageable).map(ProductResponse::from); }
    @Transactional(readOnly = true)
    public ProductResponse find(Long id) { return ProductResponse.from(get(id)); }
    @Transactional
    public ProductResponse create(ProductRequest request, String user) {
        Product product = new Product(request.productName(), user);
        replaceItems(product, request);
        return ProductResponse.from(repository.saveAndFlush(product));
    }
    @Transactional
    public ProductResponse update(Long id, ProductRequest request, String user) {
        Product product = get(id); product.modify(request.productName(), user); product.getItems().clear(); replaceItems(product, request);
        return ProductResponse.from(product);
    }
    @Transactional
    public void delete(Long id) { repository.delete(get(id)); }
    private void replaceItems(Product product, ProductRequest request) { request.items().forEach(item -> product.getItems().add(new Item(product, item.quantity()))); }
    private Product get(Long id) { return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id)); }
}