package com.example.demo.ForRCSCH.DTO;

import com.example.demo.Entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;


@NoArgsConstructor
@Data
public class UserDTO {

    private long id;
    private String surname;
    private String name;
    private String email;
    private String role;
    private String address;
    private String telephone_number;

    public static UserDTO createUserDTO(User user){
        if(user == null)
            return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setSurname(user.getSurname());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setAddress(user.getAddress());
        userDTO.setTelephone_number(user.getTelephone_number());
        return userDTO;
    }

    public static List<UserDTO> createListUserDTO(List<User> list){
        List<UserDTO> newList = new LinkedList<>();
        for(User user: list)
            newList.add(createUserDTO(user));
        return newList;
    }


}
