package com.example.demo.Entity;


import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;

@Entity
@Table(schema = "public",name = "basketPS")
public class Basket {

    @Id
    @GeneratedValue
    private long id;

    private long dateCreate;

    private int amount;

    private long price;

    private int available;

    private String nameOfProduct;

    private String pathOfPicture;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(long dateCreate) {
        this.dateCreate = dateCreate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getNameOfProduct() {
        return nameOfProduct;
    }

    public void setNameOfProduct(String nameOfProduct) {
        this.nameOfProduct = nameOfProduct;
    }

    public String getPathOfPicture() {
        return pathOfPicture;
    }

    public void setPathOfPicture(String pathOfPicture) {
        this.pathOfPicture = pathOfPicture;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
