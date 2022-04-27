package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repositories.PostRepository;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import com.careerdevs.gorestfinal.utils.BasicUtils;
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
@RequestMapping ("/api/posts")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @GetMapping ("/all")
    public ResponseEntity<?> getAllPosts () {

        try {

            Iterable<Post> allPosts = postRepository.findAll();

            return new ResponseEntity<>(allPosts, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping ("/{id}")
    public ResponseEntity<?> getPostById (@PathVariable ("id") String id) {

        try {

            if (BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int pID = Integer.parseInt(id);

            Optional<Post> foundPost = postRepository.findById((long)pID);

            if (foundPost.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            return new ResponseEntity<>(foundPost, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/deleteall")
    public ResponseEntity<?> deleteAllPosts () {

        try {

            Iterable<Post> allPosts = postRepository.findAll();

            postRepository.deleteAll(allPosts);

            return new ResponseEntity<>(allPosts, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deletePostById (@PathVariable ("id") String id) {

        try {

            if(BasicUtils.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int pID = Integer.parseInt(id);

            Optional<Post> foundUser = postRepository.findById((long)pID);

            if (foundUser.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            postRepository.deleteById((long)pID);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/")
    public ResponseEntity<?> createPost (@RequestBody Post newPost) {

        try {

            ValidationError errors = PostValidation.validatePost(newPost, postRepository, false);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            Post createdPost = postRepository.save(newPost);

            return new ResponseEntity<> (createdPost, HttpStatus.CREATED);

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

            String url = "https://gorest.co.in/public/v2/posts";

            ResponseEntity<Post[]> response = restTemplate.getForEntity(url, Post[].class);

            Post[] firstPagePosts = response.getBody();

            if (firstPagePosts == null) {

                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get first page of posts");

            }

            ArrayList<Post> allPosts = new ArrayList<>(Arrays.asList(firstPagePosts));

            HttpHeaders responseHeaders = response.getHeaders();

            String totalPages = Objects.requireNonNull(responseHeaders.get("X-Pagination-Pages")).get(0);
            int totalPgNum = Integer.parseInt(totalPages);

            System.out.println(totalPgNum);

            for (int i = 2; i <= totalPgNum; i++) {

                String pageUrl = url + "?page=" + i;
                Post[] pagePosts = restTemplate.getForObject(pageUrl, Post[].class);

                if (pagePosts == null) {

                    throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get page " + i + " of posts");

                }

                allPosts.addAll(Arrays.asList(firstPagePosts));

            }

            postRepository.saveAll(allPosts);

            return new ResponseEntity<>("Posts Created: " + allPosts.size(), HttpStatus.OK);

        } catch(HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping
    public ResponseEntity<?> uploadPostById (
            @PathVariable ("id") String postId,
            RestTemplate restTemplate
    ) {

        try {

            if(BasicUtils.isStrNaN(postId)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, postId + " is not a valid Post ID");

            }

            int pID = Integer.parseInt(postId);

            String url = "https://gorest.co.in/public/v2/users/" + pID;

            Post foundPost = restTemplate.getForObject(url, Post.class);

            if (foundPost == null) {

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Post data was null");

            }

            Post savedPost = postRepository.save(foundPost);

            return new ResponseEntity<>(savedPost, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (NumberFormatException e) {

            return new ResponseEntity<>("Post ID must be a number", HttpStatus.NOT_FOUND);

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PutMapping("/")
    public ResponseEntity<?> updatePost (@RequestBody Post newPost) {

        try {

            ValidationError errors = PostValidation.validatePost(newPost, postRepository, true);

            if (errors.hasError()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errors.toJSONString());

            }

            Post createdPost = postRepository.save(newPost);

            return new ResponseEntity<> (createdPost, HttpStatus.CREATED);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}
