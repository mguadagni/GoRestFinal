package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;



}
