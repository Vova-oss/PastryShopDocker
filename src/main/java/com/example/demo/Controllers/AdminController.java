package com.example.demo.Controllers;

import com.example.demo.Email.EmailService;
import com.example.demo.Entity.Basket;
import com.example.demo.Entity.User;
import com.example.demo.Services.BasketService;
import com.example.demo.Services.ProductService;
import com.example.demo.Services.UserService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
                model.addAttribute("PictureIsNull","???????????????? ???? ???????????? ???????? ??????????");
            if(productService.findByTypeProduct(nameOfNewProduct)!=null)
                model.addAttribute("NameExists","?????????? ?? ?????????? ?????????????????? ?????? ????????????????????");
            if(priceOfNewProduct.equals(""))
                model.addAttribute("PriceIsNull","?????????????? ????????");
            if(amountOfNewProduct.equals(""))
                model.addAttribute("AmountIsNull","?????????????? ???????????????????? ????????????");
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


        if(operation.equals("????????????????")) {
            if(amount.equals("")){
                model.addAttribute("nullAmount","??????. ???????????????????? = 1");
                model.addAttribute("nullPrice","?????????? ????????");
                model.addAttribute("nameOfProduct", nameOfProduct);
                return pageForAdmin(model, request);
            }
            productService.addSomeAmountOfProductByName(nameOfProduct, amount);
        }
        else if(operation.equals("??????????????")) {
            if(amount.equals("")){
                model.addAttribute("nullAmount","??????. ???????????????????? = 1");
                model.addAttribute("nullPrice","?????????? ????????");
                model.addAttribute("nameOfProduct", nameOfProduct);
                return pageForAdmin(model, request);
            }
            productService.deleteSomeAmountOfProductByName(nameOfProduct, amount);
        }
        else if(operation.equals("?????????????? ??????????")) {
            productService.eraseProductByName(nameOfProduct);

//            ?????? ?????? ????????????????
            basketService.eraseProductFromBasketByName(nameOfProduct);
        }else if(operation.equals("???????????????? ????????")){
            if(newPrice.equals("")){
                model.addAttribute("nullPrice","??????. ???????? = 1");
                model.addAttribute("nullAmount","N-???? ????????????????????");
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
                         @RequestParam(name = "type") String type) throws IOException {

        if(type.equals("archive")) {
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + file.getOriginalFilename();
            String wayOfFile = System.getProperty("user.dir").replace('\\', '/') + "static/files/" + fileName;


            try {
                System.out.println(file.getSize());
                String path =  System.getProperty("user.dir").replace('\\','/') + "static/files/";
//                String path = System.getProperty("user.dir").replace('\\', '/') + "/src/main/resources/static/files/";
                System.out.println(path);
                file.transferTo(new File(path + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "workWithPDF";
        }

        if(type.equals("redis")){
            byte[] bytes = file.getBytes();
            Jedis jedis = new Jedis("redis");
            String amount = jedis.get("amount");
            if(amount == null){
                jedis.set("amount", "1");
                amount = "1";
            }
            else{
                amount = String.valueOf(Integer.valueOf(amount) + 1);
                jedis.set("amount", amount);
            }
            String key = "pdf"+amount;
            byte[] keyB = key.getBytes();
            jedis.set(keyB, bytes);
            jedis.close();

            return "workWithPDF";
        }
        return null;
    }

    @GetMapping("/getPDF")
    public String getPDF(@RequestParam("type") String type, Model model) throws IOException {
        if(type.equals("archive")) {
            String uuid = UUID.randomUUID().toString();
//            String fileName = uuid + file.getOriginalFilename();
//            String wayOfFile = System.getProperty("user.dir").replace('\\', '/') + "static/files/" + fileName;



            List<File> list = new LinkedList<>();
            File dir = new File(System.getProperty("user.dir").replace('\\', '/') + "static/files/");
            if(dir.isDirectory()){
                Collections.addAll(list, Objects.requireNonNull(dir.listFiles()));
            }
            model.addAttribute("files", list);


            return "workWithPDF";
        }

        if(type.equals("redis")){
            Jedis jedis = new Jedis("redis");
            String amount = jedis.get("amount");
            System.out.println(amount);
            if(amount != null) {
                List<File> list = new LinkedList<>();

                System.out.println(Integer.valueOf(amount)+1);
                System.out.println(Integer.parseInt(amount)+1);

                for(int i = 1; i < Integer.valueOf(amount)+1; i++){
                    String key = "pdf"+i;
                    byte[] keyB = key.getBytes();
                    byte[] bytes = jedis.get(keyB);
                    File file = new File(System.getProperty("user.dir").replace('\\', '/') + "static/deleteME/" + key + ".pdf");

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.add(file);
                }

                jedis.close();

                model.addAttribute("files", list);
                return "workWithPDF";


            }
            return "workWithPDF";

        }
        return null;
    }


    @GetMapping("/checkFile")
    public ResponseEntity<ByteArrayResource> checkFile(
            @RequestParam("file") String file,
            Model model) throws IOException {
        System.out.println("=-----------------------------------");
        System.out.println(file);
        byte[] bytes = Files.readAllBytes(Path.of(file));
        System.out.println(bytes);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        System.out.println(byteArrayResource);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(byteArrayResource);

    }

}
