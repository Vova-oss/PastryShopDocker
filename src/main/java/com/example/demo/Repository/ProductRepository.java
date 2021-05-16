package com.example.demo.Repository;

import com.example.demo.Entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findById(long id);
    Product findByTypeProduct(String typeProduct);
}
