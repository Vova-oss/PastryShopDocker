package com.example.demo.Repository;

import com.example.demo.Entity.Basket;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BasketRepository extends CrudRepository<Basket,Long> {
    List<Basket> findByNameOfProduct(String nameOfProduct);
    List<Basket> findAllByUserId(long id);
//    List<Basket> findAll();
}
