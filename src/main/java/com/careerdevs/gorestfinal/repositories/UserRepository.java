package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}