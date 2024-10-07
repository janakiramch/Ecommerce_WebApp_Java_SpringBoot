package com.dev.Ecommerce.repository;

import com.dev.Ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameOrDescriptionContaining(String name, String description);

}
