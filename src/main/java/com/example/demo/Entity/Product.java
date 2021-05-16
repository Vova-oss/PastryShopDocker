package com.example.demo.Entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(schema = "public",name = "productPS")
public class Product {

    @Id
    @GeneratedValue
    private long id;

    private long price;

    private String typeProduct;

    private int amount;

    private String pathOfPicture;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPathOfPicture() {
        return pathOfPicture;
    }

    public void setPathOfPicture(String pathOfPicture) {
        this.pathOfPicture = pathOfPicture;
    }
}
