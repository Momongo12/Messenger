package com.example.messenger.controller;

import com.example.messenger.model.User;
import com.example.messenger.service.UserService;
import com.example.messenger.validator.UserValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This is the RegistrationController class that handles user registration and login functionality.
 *
 * @version 1.0
 * @author Denis Moskvin
 */
@Controller
@Log4j2
public class RegistrationController {

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserService userService;

    /**
     * Handles the GET request for the login page.
     *
     * @return the view name for rendering the login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Handles the POST request for user registration.
     *
     * @param userForm       the user registration form data
     * @param bindingResult  the binding result for form validation
     * @param model          the model to be populated with data
     * @return the view name for rendering the login page or redirecting to the chats page
     */
    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "login";
        }
        if (!userService.createUser(userForm)) {
            log.error("User service error");
            return "login";
        }

        return "redirect:/chats";
    }

}