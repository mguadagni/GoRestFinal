package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    private Integer user_id;

    private String title;

    @Column(length = 512)
    private String body;

    public Long getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}
