package com.example.demo.ForRCSCH.DTO;

import com.example.demo.Entity.Product;
import lombok.Data;
import org.springframework.security.web.header.writers.HstsHeaderWriter;

import java.util.LinkedList;
import java.util.List;

@Data
public class ProductDTO {

    private long id;
    private long price;
    private String typeProduct;
    private int amount;


    public static ProductDTO createProductDTO(Product product){
        if(product == null)
            return null;

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setPrice(product.getPrice());
        productDTO.setTypeProduct(product.getTypeProduct());
        productDTO.setAmount(product.getAmount());
        return productDTO;
    }

    public static List<ProductDTO> createListProductDTO(List<Product> list){
        List<ProductDTO> newList = new LinkedList<>();
        for(Product product: list)
            newList.add(createProductDTO(product));
        return newList;
    }
}
