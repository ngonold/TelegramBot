package com.kotah.gamelogic;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Task {
    private String title;
    private String description;
    private Integer id;
    private String answer;

    public Task(String title, String description, String answer) {
        this.title = title;
        this.description = description;
        this.answer = answer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTaskText() {
        return title + "\n" + description;
    }

    @Override
    public String toString() {
        return "[TASK=" + id + "]\n [TITLE]\n" + title + "\n[DESCR]\n" + description + "\n[ANSWER]\n" + answer + "\n" ;
    }
}
