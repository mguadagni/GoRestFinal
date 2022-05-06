package com.careerdevs.gorestfinal.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonProperty ("user_id")
    private int userId;

    private String title;

    @JsonProperty ("due_on")
    private String dueOn;

    private String status;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDueOn() {
        return dueOn;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", dueOn='" + dueOn + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}