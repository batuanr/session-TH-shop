package com.service;

import com.model.Product;

import java.util.Optional;

public interface IProductService {
    Iterable<Product> findAll();
    Optional<Product> findById(Long id);
    void save(Product product);
    void remove(Long id);
}
