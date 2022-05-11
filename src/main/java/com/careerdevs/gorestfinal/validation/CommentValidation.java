package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.CommentRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class CommentValidation {

    public static ValidationError validateComment(Comment comment, CommentRepository commentRepo, UserRepository userRepo,
                                               boolean isUpdate) {

        ValidationError errors = new ValidationError();

        if (isUpdate) {
            if (comment.getId() == 0) {
                errors.addError("id", "ID can not be left blank");
            } else {
                Optional<Comment> foundUser = commentRepo.findById(comment.getId());
                if (foundUser.isEmpty()) {
                    errors.addError("id", "No user found with the ID: " + comment.getId());
                }
            }
        }

        String commentName = comment.getName();
        String commentEmail = comment.getEmail();
        String commentBody = comment.getBody();
        Long commentPostId = comment.getPostId();

        if (commentName == null || commentName.trim().equals("")) {
            errors.addError("name", "Name can not be left blank");
        }

        if (commentEmail == null || commentEmail.trim().equals("")) {
            errors.addError("email", "Email can not be left blank");
        }

        if (commentBody == null || commentBody.trim().equals("")) {
            errors.addError("body", "Body can not be left blank");
        }

        if (commentPostId == 0) {
            errors.addError("user_id", "User_ID can not be left blank");
        } else {
            Optional<User> foundUser = userRepo.findById(commentPostId);

            if (foundUser.isEmpty()) {

                errors.addError("user_id", "User_ID is invalid because there is no user found with the id: " + commentPostId);

            }

        }

        return errors;

    }

}