package com.example.demo.Controllers;

import com.example.demo.Email.EmailService;
import com.example.demo.Entity.Basket;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.Services.BasketService;
import com.example.demo.Services.ProductService;
import com.example.demo.Services.UserService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Value("${upload.path}")
    String uploadPath;
    @Autowired
    EntityManager entityManager;
    @Autowired
    javax.persistence.EntityManagerFactory entityManagerFactory;
    @Autowired
    ProductService productService;
    @Autowired
    BasketService basketService;
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;

    @GetMapping("/pageForAdmin")
    public String pageForAdmin(Model model, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if(cookies!=null)
            for (Cookie c: cookies){
                if(c.getName().equals("background")) {
                    model.addAttribute("background", c.getValue());
                }
            }

//        model.addAttribute("background", true);
        model.addAttribute("Products",productService.findAllSorted());



        return "pageForAdmin";
    }

    @PostMapping("/setBackground")
    public String setBackground(@RequestParam(name = "background") String background,
                                HttpServletResponse response,
                                Model model){

        Cookie cookie = new Cookie("background", background);
        cookie.setMaxAge(60*60*24*365);
        response.addCookie(cookie);

        model.addAttribute("background", background);
        model.addAttribute("Products",productService.findAllSorted());
        return "pageForAdmin";
    }

    @PostMapping("/addProduct")
    public String addProduct(@RequestParam(name="nameOfNewProduct")String nameOfNewProduct,
                             @RequestParam(name="priceOfNewProduct")String priceOfNewProduct,
                             @RequestParam(name="amountOfNewProduct")String amountOfNewProduct,
                             @RequestParam(name = "pictureOfProduct") MultipartFile file,
                             Model model, HttpServletRequest request){

        if(productService.findByTypeProduct(nameOfNewProduct)!=null ||
                file.getOriginalFilename().equals("") ||
                priceOfNewProduct.equals("") ||
                amountOfNewProduct.equals("")){
            if(file.getOriginalFilename().equals(""))
                model.addAttribute("PictureIsNull","Картинка не должна быть пуста");
            if(productService.findByTypeProduct(nameOfNewProduct)!=null)
                model.addAttribute("NameExists","Товар с таким названием уже существует");
            if(priceOfNewProduct.equals(""))
                model.addAttribute("PriceIsNull","Введите цену");
            if(amountOfNewProduct.equals(""))
                model.addAttribute("AmountIsNull","Введите количество товара");
            return pageForAdmin(model, request);
        }



        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + file.getOriginalFilename();
        String wayOfFile = System.getProperty("user.dir").replace('\\','/') + "static/images/" + fileName;



        try {
            System.out.println(file.getSize());
            String path =  System.getProperty("user.dir").replace('\\','/') + "static/images/";
            System.out.println(path);
            file.transferTo(new File(path + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        productService.addNewProduct(nameOfNewProduct,priceOfNewProduct,amountOfNewProduct,fileName);
        emailService.mailSenderWithNewProduct(wayOfFile, nameOfNewProduct, priceOfNewProduct);

        return "redirect:/admin/pageForAdmin";
    }

    @PostMapping("/check")
    public String check(@RequestParam(name="nameOfProduct")String nameOfProduct,
                        @RequestParam(name="operation") String operation,
                        @RequestParam(name="amount_of_product",required = false) String amount,
                        @RequestParam(name="new_price", required = false) String newPrice,
                        Model model, HttpServletRequest request, HttpServletResponse response){


        if(operation.equals("Добавить")) {
            if(amount.equals("")){
                model.addAttribute("nullAmount","Мин. количество = 1");
                model.addAttribute("nullPrice","Новая цена");
                model.addAttribute("nameOfProduct", nameOfProduct);
                return pageForAdmin(model, request);
            }
            productService.addSomeAmountOfProductByName(nameOfProduct, amount);
        }
        else if(operation.equals("Удалить")) {
            if(amount.equals("")){
                model.addAttribute("nullAmount","Мин. количество = 1");
                model.addAttribute("nullPrice","Новая цена");
                model.addAttribute("nameOfProduct", nameOfProduct);
                return pageForAdmin(model, request);
            }
            productService.deleteSomeAmountOfProductByName(nameOfProduct, amount);
        }
        else if(operation.equals("Стереть товар")) {
            productService.eraseProductByName(nameOfProduct);

//            ето под вопросом
            basketService.eraseProductFromBasketByName(nameOfProduct);
        }else if(operation.equals("Изменить цену")){
            if(newPrice.equals("")){
                model.addAttribute("nullPrice","Мин. цена = 1");
                model.addAttribute("nullAmount","N-ое количество");
                model.addAttribute("nameOfProduct", nameOfProduct);
                return pageForAdmin(model, request);
            }
            productService.changePriceOfProduct(nameOfProduct, newPrice);
        }



        return "redirect:/admin/pageForAdmin";
    }

    @GetMapping("/monitorsUsers")
    public String monitorsUsers(Model model){
        List<User> users = userService.findAllUsers();
        List<Basket> baskets = basketService.findAll();

        for(Basket b:baskets)
            b.getUser().getId();

        model.addAttribute("users",users);
        model.addAttribute("baskets", baskets);


//        model.addAttribute("productsInBasket",basketService.findAllSorted());
        return "adminMonitorsUsers";
    }

    @PreDestroy()
    public void preDestroy(){
        System.out.println("ksdfjksjfOK!!!!!!!!!!!!!!!!!!!");
        Session session = entityManager.unwrap(Session.class);
        session.close();
        entityManager.close();
        entityManagerFactory.close();
    }





    @GetMapping("/workWithPDF")
    public String workWithPDF(){
        return "workWithPDF";
    }

    @PostMapping("/addPDF")
    public String addPDF(@RequestParam(name = "pdfFile") MultipartFile file,
                             Model model, HttpServletRequest request){

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + file.getOriginalFilename();
        String wayOfFile = System.getProperty("user.dir").replace('\\','/') + "static/images/" + fileName;



        try {
            System.out.println(file.getSize());
//            String path =  System.getProperty("user.dir").replace('\\','/') + "static/images/";
            String path =  System.getProperty("user.dir").replace('\\','/') + "/src/main/resources/static/images/";
            System.out.println(path);
            file.transferTo(new File(path + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "workWithPDF";
    }


}
