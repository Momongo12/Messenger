package com.example.messenger.controller;

import com.example.messenger.model.User;
import com.example.messenger.service.UserService;
import com.example.messenger.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model){
        userValidator.validate(userForm, bindingResult);
        System.out.println(userForm.getUsername());
        System.out.println(userForm.getEmail());
        System.out.println(userForm.getPassword());

        if (bindingResult.hasErrors()){
            return "login";
        }
        if (!userService.saveUser(userForm)){
            System.out.println("User service error");
            return "login";
        }

        return "redirect:/chat";
    }

}
