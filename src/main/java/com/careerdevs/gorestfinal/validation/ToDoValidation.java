package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.ToDo;
import com.careerdevs.gorestfinal.repositories.ToDoRepository;

public class ToDoValidation {

    public static ValidationError validateToDo (ToDo todo, ToDoRepository todoRepo, boolean isUpdating) {

        ValidationError errors = new ValidationError();

        return errors;

    }

}