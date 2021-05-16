package com.example.demo.Email;

import com.example.demo.Entity.User;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Component
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    UserService userService;

    @Async
    public void mailSenderAfterRegistration(String code, String emailAddress, String userName ){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(emailAddress);
        message.setFrom("pastryshopps@gmail.com");
        message.setSubject("Подтверждение авторизации");
        message.setText(String.format("Доброго времени суток, %s! \n"
                            + "Для подтвержения вашей почты на сервисе PastryShop перейдите по сслыке: " +
                "http://localhost:7777/activate/%s ", userName, code));

        javaMailSender.send(message);
    }

    @Async
    public void mailSenderWithNewProduct(String wayOfFile, String nameOfProduct, String priceOfProduct){

        MimeMessage message;
        MimeMessageHelper helper;

        try {

            List<User> users = userService.findAllUsers();
            for(User user:users){
                message = javaMailSender.createMimeMessage();
                helper = new MimeMessageHelper(message,true);
                helper.setTo(user.getEmail());
                helper.setFrom("pastryshopps@gmail.com");
                helper.setSubject("В PastryShop появилась новая продукция!");
                helper.setText(String.format("Доброго времени суток, %s! \nВ PastryShop появилась новая продукция!"
                        + "Спешите заказать %s. Цена за одну штуку равна %s " ,
                        user.getName(), nameOfProduct, priceOfProduct));
                FileSystemResource file = new FileSystemResource(new File(wayOfFile));
                helper.addInline(nameOfProduct,file);

                javaMailSender.send(message);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
