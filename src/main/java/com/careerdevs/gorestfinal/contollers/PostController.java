package com.careerdevs.gorestfinal.contollers;

import com.careerdevs.gorestfinal.models.Post;
import com.careerdevs.gorestfinal.repos.PostRepo;
import com.careerdevs.gorestfinal.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/posts")
public class PostController {

    @Autowired
    PostRepo postRepo;

//    @GetMapping ("/test")
//    public String testRoute () {
//        return "Test";
//    }

    @GetMapping ("/all")
    public ResponseEntity<?> getAllPosts () {

        try {

            Iterable<Post> allPosts = postRepo.findAll();

            return new ResponseEntity<>(allPosts, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/")
    public ResponseEntity<?> createPost (@RequestBody Post newPost) {

        try {

            Post createdPost = postRepo.save(newPost);

            return new ResponseEntity<> (createdPost, HttpStatus.CREATED);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}
