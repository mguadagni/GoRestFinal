package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class UserValidation {

    public static ValidationError validateUser(User user, UserRepository userRepo, boolean isUpdate) {
        ValidationError errors = new ValidationError();

        if (isUpdate) {
            if (user.getId() == 0) {
                errors.addError("id", "ID can not be left blank");
            } else {
                Optional<User> foundUser = userRepo.findById(user.getId());
                if (foundUser.isEmpty()) {
                    errors.addError("id", "No user found with the ID: " + user.getId());
                }
            }
        }

        String userName = user.getName();
        String userEmail = user.getEmail();
        String userGender = user.getGender();
        String userStatus = user.getStatus();

        if (userName == null || userName.trim().equals("")) {
            errors.addError("name", "Name can not be left blank");
        }

        if (userEmail == null || userEmail.trim().equals("")) {
            errors.addError("email", "Email can not be left blank");
        }

        if (userGender == null || userGender.trim().equals("")) {
            errors.addError("gender", "Gender can not be left blank");
        } else if (!(userGender.equals("male") || userGender.equals("female") || userGender.equals("other") )) {
            errors.addError("gender", "Gender must be: male, female, or other");
        }

        if (userStatus == null || userStatus.trim().equals("")) {
            errors.addError("status", "Status can not be left blank");
        }  else if (!(userStatus.equals("active") || userStatus.equals("inactive") )) {
            errors.addError("status", "Status must be: active or inactive");
        }

        return errors;

    }

}