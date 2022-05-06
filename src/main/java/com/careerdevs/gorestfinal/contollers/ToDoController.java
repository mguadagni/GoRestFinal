package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.models.ToDo;
import com.careerdevs.gorestfinal.repositories.ToDoRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.utils.BasicUtils;
import com.careerdevs.gorestfinal.validation.ToDoValidation;
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
@RequestMapping ("/api/todos")
public class ToDoController {

    @Autowired
    ToDoRepository toDoRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllToDos () {

        try {

            Iterable<ToDo> allToDos = toDoRepository.findAll();

            return new ResponseEntity<>(allToDos, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getToDoById (@PathVariable("id") String id) {

        try {

            if (BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int tID = Integer.parseInt(id);

            Optional<ToDo> foundToDo = toDoRepository.findById(tID);

            if (foundToDo.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            return new ResponseEntity<>(foundToDo, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<?> deleteAllToDos () {

        try {

            Iterable<ToDo> allToDos = toDoRepository.findAll();

            toDoRepository.deleteAll(allToDos);

            return new ResponseEntity<>(allToDos, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteToDoById (@PathVariable ("id") String id) {

        try {

            if(BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int tID = Integer.parseInt(id);

            Optional<ToDo> foundToDo = toDoRepository.findById(tID);

            if (foundToDo.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            toDoRepository.deleteById(tID);

            return new ResponseEntity<>(foundToDo, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/")
    public ResponseEntity<?> createToDo (@RequestBody ToDo newToDo) {

        try {

            ValidationError errors = ToDoValidation.validateToDo(newToDo, toDoRepository, userRepository, false);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            ToDo createdToDo = toDoRepository.save(newToDo);

            return new ResponseEntity<> (createdToDo, HttpStatus.CREATED);

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

            String url = "https://gorest.co.in/public/v2/todos";

            ResponseEntity<ToDo[]> response = restTemplate.getForEntity(url, ToDo[].class);

            ToDo[] firstPageToDos = response.getBody();

            if (firstPageToDos == null) {

                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page of users");

            }

            ArrayList<ToDo> allToDos = new ArrayList<>(Arrays.asList(firstPageToDos));

            HttpHeaders responseHeaders = response.getHeaders();

            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            System.out.println(totalPgNum);

            for (int i = 2; i <= totalPgNum; i++) {

                String pageUrl = url + "?page=" + i;
                ToDo[] pageToDos = restTemplate.getForObject(pageUrl, ToDo[].class);

                if (pageToDos == null) {

                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page " + i + " of To-Dos");

                }

                allToDos.addAll(Arrays.asList(firstPageToDos));

            }

            toDoRepository.saveAll(allToDos);

            return new ResponseEntity<>("To-Dos Created: " + allToDos.size(), HttpStatus.OK);

        } catch(HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping ("/{id}")
    public ResponseEntity<?> uploadToDoById (
            @PathVariable ("id") String userId,
            RestTemplate restTemplate
    ) {

        try {

            if(BasicUtils.isStrNaN(userId)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + " is not a valid ID");

            }

            int tID = Integer.parseInt(userId);

            String url = "https://gorest.co.in/public/v2/todos/" + tID;

            ToDo foundToDo = restTemplate.getForObject(url, ToDo.class);

            if (foundToDo == null) {

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User data was null");

            }

            ToDo savedToDo = toDoRepository.save(foundToDo);

            return new ResponseEntity<>(savedToDo, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (NumberFormatException e) {

            return new ResponseEntity<>("User ID must be a number", HttpStatus.NOT_FOUND);

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PutMapping("/")
    public ResponseEntity<?> updateToDo (@RequestBody ToDo newToDo) {

        try {

            ValidationError errors = ToDoValidation.validateToDo(newToDo, toDoRepository, userRepository, true);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            ToDo createdToDo = toDoRepository.save(newToDo);

            return new ResponseEntity<> (createdToDo, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}