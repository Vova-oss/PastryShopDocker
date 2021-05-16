package com.example.demo.Controllers;

import com.example.demo.Entity.Basket;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Services.BasketService;
import com.example.demo.Services.ProductService;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;


@Controller
//@RequestMapping("/person")
public class PersonController {

    @Autowired
    ProductService productService;
    @Autowired
    BasketService basketService;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Model model){
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("name",user.getName());
        model.addAttribute("surname",user.getSurname());
//        model.addAttribute("login",user.getLogin());
        model.addAttribute("address",user.getAddress());
        model.addAttribute("email",user.getEmail());
        model.addAttribute("telephoneNumber",user.getTelephone_number());


        return "profile";
    }

    @GetMapping("/placeAnOrder")
    public String placeAnOrder(Model model){
        List<Basket> baskets = basketService.getAllBasketCurrentUser();
        List<Product> products = productService.findAll();

        model.addAttribute("basketIsEmpty",basketService.chekBasketIsEmpty());

        for(Basket basket:baskets) {
            for (Product product : products) {
                if (basket.getNameOfProduct().equals(product.getTypeProduct())) {
                    if (basket.getAmount() > product.getAmount()) {
                        basketService.editingAmount();
                        List<Basket> newList = basketService.getAllBasketCurrentUser();
                        model.addAttribute("allPrice",basketService.getAllPriceCurrentUser());
                        model.addAttribute("products", newList);
                        model.addAttribute("result", 1);
                        return "basket";

                    }
                }
            }
        }

        model.addAttribute("allPrice", basketService.getAllPriceCurrentUser());
        model.addAttribute("result", 2);
        model.addAttribute("products", baskets);



        return "basket";
    }

    @GetMapping("/clarificationOfTheDesign")
    public String clarificationOfTheDesing(Model model){
        List<Basket> baskets = basketService.getAllBasketCurrentUser();
        List<Product> products = productService.findAll();



        for(Basket basket:baskets) {
            for (Product product : products) {
                if (basket.getNameOfProduct().equals(product.getTypeProduct())) {
                    if (basket.getAmount() > product.getAmount()) {
                        basketService.editingAmount();
                        List<Basket> newList = basketService.getAllBasketCurrentUser();
                        model.addAttribute("basketIsEmpty",basketService.chekBasketIsEmpty());
                        model.addAttribute("products", newList);
                        model.addAttribute("result", 1);
                        return "basket";

                    }
                }
            }
        }

        basketService.deleteAllBasketsOfPerson();

        model.addAttribute("basketIsEmpty",basketService.chekBasketIsEmpty());
        model.addAttribute("address", userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getAddress());
        model.addAttribute("result", 3);


        return "basket";
    }


    @GetMapping("/basket")
    public String basket(Model model){
        // Проверка на количество (в корзине < в бд)
        basketService.editingAmount();
        List<Basket> baskets = basketService.getAllBasketCurrentUser();

        model.addAttribute("basketIsEmpty",basketService.chekBasketIsEmpty());
        model.addAttribute("products", baskets);
        model.addAttribute("allPrice", basketService.getAllPriceCurrentUser());
        return "basket";
    }

    @GetMapping("/personalAccount")
    public String personalAccount(Model model){
        // Проверка на количество (в корзине < в бд)
        basketService.editingAmount();

        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        model.addAttribute("product",productService.findAllByUserIdWithNull(user.getId()));
        return "personalAccount";
    }

    @PostMapping("/check")
    public String trying(@RequestParam(name = "product_id") String id, @RequestParam(name = "searchByName",required = false) String name, Model model){
        System.out.println(name);
        if(name != null)
            model.addAttribute("searchByName",name.toLowerCase());
        Product product = productService.findById(id);

        basketService.saveOrUpdateOneAmountOfProduct(product);
        return personalAccount(model);
//        return "redirect:/personalAccount";
    }

    @PostMapping("/actionInBasket")
    public String actionInBasket(@RequestParam(name="nameOfProduct")String nameOfProduct,
                                 @RequestParam(name="operation") String operation){

        Product product = productService.findByTypeProduct(nameOfProduct);
        if(operation.equals("+")){
            basketService.addByNameOfProduct(nameOfProduct);

        }else{
            basketService.deleteByNameOfProduct(nameOfProduct);
        }

        return "redirect:/basket";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model, HttpServletRequest request){
        String psw = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie c: cookies){
            if(c.getName().equals("password"))
                 psw = c.getValue();
        }

        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
//        model.addAttribute("currentLogin",user.getLogin());
        model.addAttribute("currentPassword",psw);
        model.addAttribute("currentAddress",user.getAddress());
        model.addAttribute("currentEmail",user.getEmail());
        model.addAttribute("currentName",user.getName());
        model.addAttribute("currentSurname",user.getSurname());
        model.addAttribute("currentTelephoneNumber",user.getTelephone_number());

        return "editProfile";
    }

    @GetMapping("/editProfileAction")
    public String editProfile(@ModelAttribute @Valid User user,
                              Errors errors,
                              Model model,
                              @RequestParam("realPassword") String realPassword,
                              HttpServletResponse response){

        User currentUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(errors.hasErrors()
//                || userService.checkingExistingUsers(user)
                || !passwordEncoder.matches(realPassword, currentUser.getPassword())) {
//            model.addAttribute("currentLogin",user.getLogin());
            model.addAttribute("currentAddress",user.getAddress());
            model.addAttribute("currentPassword",user.getPassword());
            model.addAttribute("currentEmail",user.getEmail());
            model.addAttribute("currentName",user.getName());
            model.addAttribute("currentSurname",user.getSurname());
            model.addAttribute("currentTelephoneNumber",user.getTelephone_number());

            List<FieldError> list = errors.getFieldErrors();
            for (FieldError f : list) {
                model.addAttribute(f.getField(), f.getDefaultMessage());
            }

//            if (userService.checkingExistingUsers(user)) {
//                model.addAttribute("login", "Пользователь с таким логином уже существует");
//            }

            if(!passwordEncoder.matches(realPassword, currentUser.getPassword())){
                model.addAttribute("uncorrectedPassword","Неверный пароль");
            }
            return "/editProfile";
        }

        userService.editProfile(user, currentUser.getEmail());

        Cookie pCookie = new Cookie("password", user.getPassword());
        pCookie.setMaxAge(60*60*24*365);
        response.addCookie(pCookie);

        return "redirect:/profile";
    }




}
