package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.Comment;
import com.careerdevs.gorestfinal.repositories.CommentRepository;

public class CommentValidation {

    public static ValidationError validateComment (Comment comment, CommentRepository commentRepo, boolean isUpdating) {

        ValidationError errors = new ValidationError();

        return errors;

    }

}