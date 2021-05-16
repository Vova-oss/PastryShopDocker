package com.example.demo.Services;

import com.example.demo.Entity.Basket;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BasketService {

    @Autowired
    BasketRepository basketRepository;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;

    @Transactional
    public void saveOrUpdateOneAmountOfProduct(Product product) {
        List<Basket> baskets = basketRepository.findByNameOfProduct(product.getTypeProduct());
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        boolean flag = false;
        for (Basket basket : baskets) {
            if (basket.getUser().getId() == user.getId()) {
                basket.setAmount(basket.getAmount() + 1);
                basketRepository.save(basket);
                flag = true;
            }

        }
        if (!flag) {
            Basket basket = new Basket();
            basket.setAmount(1);
            basket.setPrice(product.getPrice());
            basket.setDateCreate(new Date().getTime());
            basket.setNameOfProduct(product.getTypeProduct());
            basket.setUser(user);
            basket.setPathOfPicture(product.getPathOfPicture());
            basketRepository.save(basket);
        }
        upgradeAllTimeOfProduct(user);
    }

    public List<Basket> getAllByUserId(long id) {
        List<Basket> list = basketRepository.findAllByUserId(id);
        return list;
    }

    public void editingAmount() {
        List<Basket> baskets = getAllBasketCurrentUser();
        List<Product> products = productService.findAll();
        for (Basket basket : baskets) {
            for (Product product : products) {
                if (basket.getNameOfProduct().equals(product.getTypeProduct())) {
                    if (basket.getAmount() > product.getAmount()) {
                        basket.setAmount(product.getAmount());
                        basketRepository.save(basket);
                    }
                }
            }
        }

    }

    public void deleteAllBasketsOfPerson() {

        List<Basket> list = basketRepository.findAllByUserId(
                userService.findUserByEmail(
                        SecurityContextHolder.
                                getContext().
                                getAuthentication().
                                getName()).
                        getId());



        for (Basket basket : list) {
            // Удаление из основной бд
            productService.deleteSomeAmountOfProductByName(basket.getNameOfProduct(), String.valueOf(basket.getAmount()));

            basketRepository.delete(basket);
        }

    }

    @Transactional
    public List<Basket> getAllBasketCurrentUser() {
        List<Basket> listForDelete = new ArrayList<>();
        List<Basket> list = basketRepository.findAllByUserId(
                userService.findUserByEmail(
                        SecurityContextHolder.
                                getContext().
                                getAuthentication().
                                getName()).
                        getId());
        for (Basket basket : list) {
            if (basket.getAmount() == 0) {
                listForDelete.add(basket);
                basketRepository.delete(basket);
            }
        }
        for (Basket basket : listForDelete) {
            list.remove(basket);
        }

        for (Basket basket : list) {
            Product product = productService.findByTypeProduct(basket.getNameOfProduct());
            basket.setAvailable(product.getAmount() - basket.getAmount());
        }
        List<Basket> newList = new ArrayList<>();
        Stream<Basket> stream = list.stream();
        stream.sorted((o1, o2) -> o1.getNameOfProduct().compareTo(o2.getNameOfProduct()))
                .forEach(newList::add);

        return newList;
    }

    @Transactional
    public void addByNameOfProduct(String nameOfProduct) {
        List<Basket> baskets = basketRepository.findByNameOfProduct(nameOfProduct);
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        for (Basket basket : baskets) {
            if (basket.getUser().getId() == user.getId()) {
                basket.setAmount(basket.getAmount() + 1);
                basketRepository.save(basket);
            }
        }
        upgradeAllTimeOfProduct(user);
    }

    @Transactional
    public void deleteByNameOfProduct(String nameOfProduct) {
        List<Basket> baskets = basketRepository.findByNameOfProduct(nameOfProduct);
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        for (Basket basket : baskets) {
            if (basket.getUser().getId() == user.getId()) {
                basket.setAmount(basket.getAmount() - 1);
                basketRepository.save(basket);
            }
        }
        upgradeAllTimeOfProduct(user);
    }

    @Transactional
    public long deleteAllAmountByBasket(Basket basket) {
        long amount = basket.getAmount();
        basketRepository.delete(basket);
        return amount;
    }

    @Transactional
    public List<Basket> findAll() {
        return (List<Basket>) basketRepository.findAll();
    }

    @Transactional
    public List<Basket> findAllSorted() {
        List<Basket> baskets = findAll();
        List<Basket> newList = new ArrayList<>();
        Stream<Basket> stream = baskets.stream();
        stream.sorted((o1, o2) -> o1.getNameOfProduct().compareTo(o2.getNameOfProduct()))
                .forEach(newList::add);
        return newList;
    }

    @Transactional
    public void eraseProductFromBasketByName(String nameOfProduct) {
        List<Basket> baskets = basketRepository.findByNameOfProduct(nameOfProduct);
        for (Basket basket : baskets)
            basketRepository.delete(basket);
    }

    @Transactional
    public void upgradeAllTimeOfProduct(User user) {
        // Обновляю время продукции данного пользователя
        List<Basket> allBaskets = findAll();
        for (Basket basket : allBaskets) {
            if (basket.getUser().getId() == user.getId()) {
                basket.setDateCreate(new Date().getTime());
                basketRepository.save(basket);
                System.out.println(basket.getDateCreate());
            }
        }
    }

    public void changePriceOfProduct(String nameOfProduct, String newPrice) {

        List<Basket> basketList = findAll();
        for (Basket basket : basketList) {
            if (basket.getNameOfProduct().equals(nameOfProduct)) {
                basket.setPrice(Long.parseLong(newPrice));
                basketRepository.save(basket);
            }
        }

    }

    public int getAllPriceCurrentUser() {
        List<Basket> baskets = getAllBasketCurrentUser();
        int allPrice = 0;
        for (Basket basket : baskets) {
            allPrice += basket.getPrice() * basket.getAmount();
        }
        return allPrice;
    }

    public boolean chekBasketIsEmpty() {
        List<Basket> baskets = getAllBasketCurrentUser();
        return baskets.isEmpty();
    }

}
