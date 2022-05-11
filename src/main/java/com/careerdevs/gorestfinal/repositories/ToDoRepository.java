package com.careerdevs.gorestfinal.repositories;

import com.careerdevs.gorestfinal.models.ToDo;
import org.springframework.data.repository.CrudRepository;

public interface ToDoRepository extends CrudRepository<ToDo, Long> {
}