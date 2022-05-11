package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.utils.BasicUtils;
import com.careerdevs.gorestfinal.validation.CommentValidation;
import com.careerdevs.gorestfinal.validation.PostValidation;
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
@RequestMapping ("/api/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping ("/all")
    public ResponseEntity<?> getAllComments () {

        try {

            Iterable<Comment> allComments = commentRepository.findAll();

            return new ResponseEntity<>(allComments, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getCommentById (@PathVariable ("id") String id) {

        try {

            if (BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            Long cID = Long.parseLong(id);

            Optional<Comment> foundComment = commentRepository.findById(cID);

            if (foundComment.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            return new ResponseEntity<>(foundComment, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/deleteall")
    public ResponseEntity<?> deleteAllComments () {

        try {

            Iterable<Comment> allComments = commentRepository.findAll();

            commentRepository.deleteAll(allComments);

            return new ResponseEntity<>(allComments, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteCommentById (@PathVariable ("id") String id) {

        try {

            if(BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            long cID = Long.parseLong(id);

            Optional<Comment> foundComment = commentRepository.findById(cID);

            if (foundComment.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            commentRepository.deleteById(cID);

            return new ResponseEntity<>(foundComment, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/")
    public ResponseEntity<?> createComment (@RequestBody Comment newComment) {

        try {

            ValidationError errors = CommentValidation.validateComment(newComment, commentRepository, userRepository, false);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            Comment createdComment = commentRepository.save(newComment);

            return new ResponseEntity<> (createdComment, HttpStatus.CREATED);

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

            String url = "https://gorest.co.in/public/v2/comments";

            ResponseEntity<Comment[]> response = restTemplate.getForEntity(url, Comment[].class);

            Comment[] firstPageComments = response.getBody();

            if (firstPageComments == null) {

                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page of comments");

            }

            ArrayList<Comment> allComments = new ArrayList<>(Arrays.asList(firstPageComments));

            HttpHeaders responseHeaders = response.getHeaders();

            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            System.out.println(totalPgNum);

            for (int i = 2; i <= totalPgNum; i++) {

                String pageUrl = url + "?page=" + i;
                Comment[] pageComments = restTemplate.getForObject(pageUrl, Comment[].class);

                if (pageComments == null) {

                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page " + i + " of comments");

                }

                allComments.addAll(Arrays.asList(firstPageComments));

            }

            commentRepository.saveAll(allComments);

            return new ResponseEntity<>("Comments Created: " + allComments.size(), HttpStatus.OK);

        } catch(HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping ("/{id}")
    public ResponseEntity<?> uploadCommentById (
            @PathVariable ("id") String commentId,
            RestTemplate restTemplate
    ) {

        try {

            if(BasicUtils.isStrNaN(commentId)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, commentId + " is not a valid ID");

            }

            int cID = Integer.parseInt(commentId);

            String url = "https://gorest.co.in/public/v2/comments/" + cID;

            Comment foundComment = restTemplate.getForObject(url, Comment.class);

            if (foundComment == null) {

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Post data was null");

            }

            Comment savedComment = commentRepository.save(foundComment);

            return new ResponseEntity<>(savedComment, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (NumberFormatException e) {

            return new ResponseEntity<>("ID must be a number", HttpStatus.NOT_FOUND);

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PutMapping("/")
    public ResponseEntity<?> updateComment (@RequestBody Comment newComment) {

        try {

            ValidationError errors = CommentValidation.validateComment(newComment, commentRepository, userRepository, true);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            Comment createdComment = commentRepository.save(newComment);

            return new ResponseEntity<> (createdComment, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}