package com.example.demo.Entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;



@Entity
@Table(schema = "public",name = "userPS")
public class User {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank(message = "Некорректные данные")
    @Size(min = 1, message = "Минимальная длина фамилии = 1")
    private String surname;

    @NotBlank(message = "Некорректные данные")
    @Size(min = 1, message = "Минимальная длина имени = 1")
    private String name;

    @NotBlank(message = "Некорректные данные")
    @Size(min = 1, message = "Минимальная длина пароля = 1")
    private String password;

    @NotBlank(message = "Некорректный email")
    @Email(message = "Некорректный email")
    private String email;

    private String role = "ROLE_USER";

    @Size(min = 1, message = "Впишите свой адрес")
    private String address;

    @NotBlank(message = "Некорректные данные")
    private String telephone_number;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Basket> baskets;

    private String activationCode;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone_number() {
        return telephone_number;
    }

    public void setTelephone_number(String telephone_number) {
        this.telephone_number = telephone_number;
    }

    public List<Basket> getBaskets() {
        return baskets;
    }

    public void setBaskets(List<Basket> baskets) {
        this.baskets = baskets;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        User user = (User) o;
//        return Objects.equals(login, user.login) && Objects.equals(password, user.password) && Objects.equals(email, user.email);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(login, password, email);
//    }
//}
