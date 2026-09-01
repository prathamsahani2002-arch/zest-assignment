package com.example.product.product;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product", indexes = @Index(name = "idx_product_created_on", columnList = "created_on"))
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    @Column(name = "created_on", nullable = false)
    private Instant createdOn;
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;
    @Column(name = "modified_on")
    private Instant modifiedOn;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    protected Product() { }
    public Product(String productName, String createdBy) {
        this.productName = productName; this.createdBy = createdBy; this.createdOn = Instant.now();
    }
    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedOn() { return createdOn; }
    public String getModifiedBy() { return modifiedBy; }
    public Instant getModifiedOn() { return modifiedOn; }
    public List<Item> getItems() { return items; }
    public void modify(String name, String user) { productName = name; modifiedBy = user; modifiedOn = Instant.now(); }
}