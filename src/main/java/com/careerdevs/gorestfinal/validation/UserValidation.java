package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.UserRepository;

public class UserValidation {

    public static ValidationError validateUser (User user, UserRepository userRepo, boolean isUpdating) {

        ValidationError errors = new ValidationError();

        return errors;

    }

}