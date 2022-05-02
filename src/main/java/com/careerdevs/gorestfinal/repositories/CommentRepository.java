package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
}