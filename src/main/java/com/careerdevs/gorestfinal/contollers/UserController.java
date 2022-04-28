package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.utils.BasicUtils;
import com.careerdevs.gorestfinal.validation.UserValidation;
import com.careerdevs.gorestfinal.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping ("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers () {

        try {

            Iterable<User> allUsers = userRepository.findAll();

            return new ResponseEntity<>(allUsers, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getUserById (@PathVariable("id") String id) {

        try {

            if (BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int uID = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(uID);

            if (foundUser.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllUsers () {

        try {

            Iterable<User> allUsers = userRepository.findAll();

            userRepository.deleteAll(allUsers);

            return new ResponseEntity<>(allUsers, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteUserById (@PathVariable ("id") String id) {

        try {

            if(BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int uID = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(uID);

            if (foundUser.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            userRepository.deleteById(uID);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/")
    public ResponseEntity<?> createUser (@RequestBody User newUser) {

        try {

            ValidationError errors = UserValidation.validateUser(newUser, userRepository, false);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            User createdUser = userRepository.save(newUser);

            return new ResponseEntity<> (createdUser, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping ("/uploadall")
    public ResponseEntity<?> uploadAll (
            RestTemplate restTemplate
    ){

        try {

            String url = "https://gorest.co.in/public/v2/users";

            ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);

            User[] firstPageUsers = response.getBody();

            if (firstPageUsers == null) {

                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page of users");

            }

            ArrayList<User> allUsers = new ArrayList<>(Arrays.asList(firstPageUsers));

            HttpHeaders responseHeaders = response.getHeaders();

            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            System.out.println(totalPgNum);

            for (int i = 2; i <= totalPgNum; i++) {

                String pageUrl = url + "?page=" + i;
                User[] pageUsers = restTemplate.getForObject(pageUrl, User[].class);

                if (pageUsers == null) {

                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page " + i + " of users");

                }

                allUsers.addAll(Arrays.asList(firstPageUsers));

            }

            userRepository.saveAll(allUsers);

            return new ResponseEntity<>("Users Created: " + allUsers.size(), HttpStatus.OK);

        } catch(HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping ("/{id}")
    public ResponseEntity<?> uploadUserById (
            @PathVariable ("id") String userId,
            RestTemplate restTemplate
    ) {

        try {

            if(BasicUtils.isStrNaN(userId)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + " is not a valid ID");

            }

            int uID = Integer.parseInt(userId);

            String url = "https://gorest.co.in/public/v2/users/" + uID;

            User foundUser = restTemplate.getForObject(url, User.class);

            if (foundUser == null) {

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User data was null");

            }

            User savedUser = userRepository.save(foundUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (NumberFormatException e) {

            return new ResponseEntity<>("User ID must be a number", HttpStatus.NOT_FOUND);

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser (@RequestBody User newUser) {

        try {

            ValidationError errors = UserValidation.validateUser(newUser, userRepository, true);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            User createdUser = userRepository.save(newUser);

            return new ResponseEntity<> (createdUser, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}
