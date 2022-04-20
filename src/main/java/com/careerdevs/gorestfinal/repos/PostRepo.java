package com.careerdevs.gorestfinal.repos;

import com.careerdevs.gorestfinal.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepo extends CrudRepository<Post, Long> {
}
