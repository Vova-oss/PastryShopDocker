package com.example.demo.Controllers;

import com.example.demo.Email.EmailService;
import com.example.demo.Entity.User;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
public class StartController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

//    @GetMapping("/ppss")
//    public String ppss(HttpServletResponse response){
//        response.addCookie(new Cookie("ppppp","vvvvv"));
//        return "redirect:/person/profile";
//    }

    @GetMapping("/postCookie")
    public String setCooliePost(HttpServletResponse response){
        User user = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

//        Cookie pCookie = new Cookie("password",user.getPassword());
//        Cookie lCookie = new Cookie("login", user.getLogin());
//        pCookie.setMaxAge(60*60*24*365);
//        lCookie.setMaxAge(60*60*24*365);
//        response.addCookie(pCookie);
//        response.addCookie(lCookie);

        if(user.getRole().equals("ROLE_ADMIN"))
            return "redirect:/admin/pageForAdmin";
        return "redirect:/personalAccount";

    }

    @GetMapping("/login")
    public String authorization(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String afterR,
                                Model model,
                                HttpServletRequest request){

        if(userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())!=null){
            return "redirect:/postCookie";
        }

        if(error != null){
            model.addAttribute("IncorrectData", "Неправильный email или пароль");
        }
        else{
            Cookie[] cookies = request.getCookies();
            if(cookies!=null)
                for (Cookie c: cookies){
                    if(c.getName().equals("password"))
                        model.addAttribute("password", c.getValue());
                    if(c.getName().equals("login"))
                        model.addAttribute("login", c.getValue());
                }
        }

        if(afterR != null){
            model.addAttribute("activateCompleted", 2);
        }

        return "login";
    }

    @GetMapping("/registration")
    public String registration(){

        if(userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())!=null){
            return "redirect:/postCookie";
        }

        return "registration";
    }

    @GetMapping("/")
    public String main(){
        if(userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())!=null){
            return "redirect:/postCookie";
        }
        return "main";
    }


    @GetMapping("/registrationAction")
    public String registrationAction(@ModelAttribute @Valid User user, Errors errors, Model model){

        if(userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())!=null){
            return "redirect:/postCookie";
        }

        if(errors.hasErrors() || userService.checkingExistingUsers(user)) {
//            model.addAttribute("currentLogin",user.getLogin());
            model.addAttribute("currentPassword",user.getPassword());
            model.addAttribute("currentEmail",user.getEmail());
            model.addAttribute("currentName",user.getName());
            model.addAttribute("currentSurname",user.getSurname());
            model.addAttribute("currentTelephoneNumber",user.getTelephone_number());
            model.addAttribute("currentAddress",user.getTelephone_number());

            List<FieldError> list = errors.getFieldErrors();
            for (FieldError f : list) {
                model.addAttribute(f.getField(), f.getDefaultMessage());
            }

            if (userService.checkingExistingUsers(user)) {
                model.addAttribute("login", "Пользователь с таким email'ом уже существует");
            }
            return "/registration";
        }
        userService.saveOneUser(user);
        emailService.mailSenderAfterRegistration(user.getActivationCode(),user.getEmail(),user.getName());

        return "redirect:/login?afterR";
    }

    @GetMapping("/activate/{code}")
    public String activateCode(@PathVariable() String code, Model model){

        boolean activateCompleted = userService.checkActivate(code);
        if(activateCompleted)
            model.addAttribute("activateCompleted", 1);
        else model.addAttribute("activateCompleted", 3);

        return "login";
    }

    @GetMapping("/getCookie")
    public String getCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie c: cookies){
            System.out.println(c.getName());
            System.out.println(c.getValue());
        }
        return "main";
    }



}
