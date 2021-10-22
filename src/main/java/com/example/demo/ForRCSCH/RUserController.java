package com.example.demo.ForRCSCH;

import com.example.demo.Entity.User;
import com.example.demo.ForRCSCH.AuxiliaryClasses.StaticMethods;
import com.example.demo.ForRCSCH.DTO.UserDTO;
import com.example.demo.Services.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/RU")
@Api(tags = "User")
public class RUserController {

    @Autowired
    UserService userService;


    @ApiOperation(value = "Получение информации обо всех пользователях")
    @GetMapping("/getUsers")
    public List<UserDTO> getUsers(){
        return UserDTO.createListUserDTO(userService.findAllUsers());
    }

    @ApiOperation(value = "Получение информации об одном пользователе по id")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User with this id doesn't exist")
    })
    @GetMapping("/getUser/{id}")
    public UserDTO getUserById(
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        UserDTO userDTO = UserDTO.createUserDTO(userService.findUserById(id));
        if(userDTO == null)
            StaticMethods.createResponse(request, response, 400, "User with this id doesn't exist");
        return userDTO;
    }


    @ApiOperation(value = "Добавление пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created user with id = :id"),
            @ApiResponse(code = 400, message = "Incorrect JSON\n")
    })
    @PostMapping("/addUser")
    public void addUser(
            @ApiParam(type = "String",
                    value = "Отчество, Имя, Пароль, Email, Адрес, Телефонный номер",
                    example = "{\n\"surname\":\"Викторович\", " +
                            "\n\"name\":\"Владимир\", " +
                            "\n\"password\":\"password\", " +
                            "\n\"email\":\"email@mail.ru\", " +
                            "\n\"address\":\"34F\", " +
                            "\n\"telephone_number\": \"+443534\"}",
                    required = true)
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response){

        String surname = StaticMethods.parsingJson(body, "surname", request, response);
        String name = StaticMethods.parsingJson(body, "name", request, response);
        String password = StaticMethods.parsingJson(body, "password", request, response);
        String email = StaticMethods.parsingJson(body, "email", request, response);
        String address = StaticMethods.parsingJson(body, "address", request, response);
        String telephone_number = StaticMethods.parsingJson(body, "telephone_number", request, response);
        if(surname == null || name == null || password == null || email == null || address == null || telephone_number == null)
            return;
        User user = new User();
        user.setActivationCode(null);
        user.setSurname(surname);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setAddress(address);
        user.setTelephone_number(telephone_number);
        user = userService.saveOneUser(user);

        StaticMethods.createResponse(request, response, 201, "Created user with id = "+user.getId());
    }

    @ApiOperation(value = "Редактирование данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Updated user with id = :id"),
            @ApiResponse(code = 400, message = "Incorrect JSON\n" +
                    "User with this id doesn't exist")
    })
    @PutMapping("/editUser/{id}")
    public void editUser(
            @ApiParam(type = "String",
                    value = "Отчество, Имя, Пароль, Email, Адрес, Телефонный номер",
                    example = "{\n\"surname\":\"Викторович\", " +
                            "\n\"name\":\"Владимир\", " +
                            "\n\"password\":\"password\", " +
                            "\n\"email\":\"email@mail.ru\", " +
                            "\n\"address\":\"34F\", " +
                            "\n\"telephone_number\": \"+443534\"}",
                    required = true)
            @RequestBody String body,
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        User user = userService.findUserById(id);
        if(user == null) {
            StaticMethods.createResponse(request, response, 400, "User with this id doesn't exist");
            return;
        }
        String surname = StaticMethods.parsingJson(body, "surname", request, response);
        String name = StaticMethods.parsingJson(body, "name", request, response);
        String password = StaticMethods.parsingJson(body, "password", request, response);
        String email = StaticMethods.parsingJson(body, "email", request, response);
        String address = StaticMethods.parsingJson(body, "address", request, response);
        String telephone_number = StaticMethods.parsingJson(body, "telephone_number", request, response);
        if(surname == null || name == null || password == null || email == null || address == null || telephone_number == null)
            return;

        user.setActivationCode(null);
        user.setSurname(surname);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setAddress(address);
        user.setTelephone_number(telephone_number);
        userService.saveOneUser(user);

        StaticMethods.createResponse(request, response, 201, "Updated user with id = "+user.getId());
    }

    @ApiOperation(value = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 204, message = "User have deleted"),
            @ApiResponse(code = 400, message = "User with this id doesn't exist")
    })
    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        User user = userService.findUserById(id);
        if(user == null) {
            StaticMethods.createResponse(request, response, 400, "User with this id doesn't exist");
            return;
        }
        userService.delete(user);
        StaticMethods.createResponse(request, response, 204, "User have deleted");
    }

}
