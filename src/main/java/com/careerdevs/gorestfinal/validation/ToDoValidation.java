package com.careerdevs.gorestfinal.validation;

import com.careerdevs.gorestfinal.models.ToDo;
import com.careerdevs.gorestfinal.models.User;
import com.careerdevs.gorestfinal.repositories.ToDoRepository;
import com.careerdevs.gorestfinal.repositories.UserRepository;

import java.util.Optional;

public class ToDoValidation {

    public static ValidationError validateToDo(ToDo toDo, ToDoRepository toDoRepo, UserRepository userRepo,
                                                  boolean isUpdate) {

        ValidationError errors = new ValidationError();

        if (isUpdate) {
            if (toDo.getId() == 0) {
                errors.addError("id", "ID can not be left blank");
            } else {
                Optional<ToDo> foundUser = toDoRepo.findById(toDo.getId());
                if (foundUser.isEmpty()) {
                    errors.addError("id", "No user found with the ID: " + toDo.getId());
                }
            }
        }

        String toDoTitle = toDo.getTitle();
        String toDoDueOn = toDo.getDueOn();
        String toDoStatus = toDo.getStatus();
        Long toDoUserID = toDo.getUserId();

        if (toDoTitle == null || toDoTitle.trim().equals("")) {
            errors.addError("title", "Title can not be left blank");
        }

        if (toDoDueOn == null || toDoDueOn.trim().equals("")) {
            errors.addError("dueOn", "dueOn can not be left blank");
        }

        if (toDoStatus == null || toDoStatus.trim().equals("")) {
            errors.addError("status", "Status can not be left blank");
        }

        if (toDoUserID == 0) {
            errors.addError("user_id", "User_ID can not be left blank");
        } else {
            Optional<User> foundUser = userRepo.findById(toDoUserID);

            if (foundUser.isEmpty()) {

                errors.addError("user_id", "User_ID is invalid because there is no user found with the id: " + toDoUserID);

            }

        }

        return errors;

    }

}