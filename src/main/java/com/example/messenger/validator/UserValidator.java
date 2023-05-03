package com.example.messenger.validator;


import com.example.messenger.model.User;
import com.example.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private UserService userService;

    @Autowired
    public UserValidator(@Qualifier("UserServiceImpl") UserService userService){
        this.userService = userService;
    }

    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    public void validate(Object o, Errors errors) {
//        User user = (User) o;
//
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Required");
//        if (user.getUsername().length() < 8 || user.getUsername().length() > 32) {
//            errors.rejectValue("username", "Size.userForm.username");
//        }
//
//        if (userService.findByEmail(user.getEmail()) != null) {
//            errors.rejectValue("email", "Duplicate.userForm.email");
//        }
//
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Required");
//        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
//            errors.rejectValue("password", "Size.userForm.password");
//        }

//        if (!user.getConfirmPassword().equals(user.getPassword())) {
//            errors.rejectValue("confirmPassword", "Different.userForm.password");
//        }
    }
}