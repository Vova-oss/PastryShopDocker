package com.example.demo.Services;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Transactional
    public boolean checkingExistingUsers(User user){
//        return userRepository.findByLogin(user.getLogin()) != null;
        return userRepository.findByEmail(user.getEmail()) != null;
    }

    @PostConstruct
    public void init(){
        User user = new User();
        user.setAddress("sdfsdf");
        user.setEmail("admin@admin");
        user.setName("lksdfj");
        user.setPassword("admin");
        user.setSurname("skdjf");
        user.setActivationCode(null);
        user.setTelephone_number("lksjdfskdjf");
        user.setRole("ROLE_ADMIN");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void saveOneUser(User user){
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findUserByActivationCode(String code){
        return userRepository.findUserByActivationCode(code);
    }

    @Transactional
    //На самом деле он находит по логину!!!
    public User findUserByEmail(String name) {
        return userRepository.findByEmail(name);
    }

    @Transactional
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public void editProfile(User user, String email){
        User fromDb = findUserByEmail(email);
//        fromDb.setLogin(user.getLogin());
        fromDb.setEmail(user.getEmail());
        fromDb.setPassword(passwordEncoder.encode(user.getPassword()));
        fromDb.setName(user.getName());
        fromDb.setSurname(user.getSurname());
        fromDb.setTelephone_number(user.getTelephone_number());
        fromDb.setAddress(user.getAddress());
        userRepository.save(fromDb);
    }

    public boolean checkActivate(String code) {

        User user = findUserByActivationCode(code);
        if (user == null)
            return false;

        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }
}
