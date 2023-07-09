package com.kotah.parser;

import com.kotah.gamelogic.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskFileParser {
    private List<Task> taskList;
    private String gameDescription;
    enum TaskPart {
        TITLE,
        DESCRIPTION,
        ANSWER,
        DONE,
        SKIP,
        GAME
    }

    public TaskFileParser() {
        this.taskList = new ArrayList<>();
        this.gameDescription = "";
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public List<Task> readTasksFile(String fileName) {
        Task task;
        String title = "";
        String gameDescription = "";
        String taskDescription = "";
        String answer = "";
        TaskPart whereToAdd = TaskPart.SKIP;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    this.getClass().getResourceAsStream("/" + fileName)), StandardCharsets.UTF_8));
            String line;
            while (!Objects.isNull(line = br.readLine())) {
                if (line.contains("[TITLE]")) {
                    whereToAdd = TaskPart.TITLE;
                } else if (line.contains("[DESCRIPTION]")) {
                    whereToAdd = TaskPart.DESCRIPTION;
                } else if (line.contains("[ANSWER]")) {
                    whereToAdd = TaskPart.ANSWER;
                    //} else if (line.contains("[TASK]") && (whereToAdd != TaskPart.TITLE)) {
                    //    whereToAdd = TaskPart.DONE;
                } else if (line.contains("[GAME]")) {
                    whereToAdd = TaskPart.GAME;
                } else if (line.equals("")) {
                   whereToAdd = TaskPart.DONE;
                } else {
                    switch (whereToAdd) {
                        case TITLE -> title = title.concat(line);
                        case DESCRIPTION -> taskDescription = taskDescription.concat(line);
                        case ANSWER -> answer = answer.concat(line);
                        case GAME -> {
                            gameDescription = gameDescription.concat(line);
                            whereToAdd = TaskPart.SKIP;
                        }
                        case DONE -> {
                            task = new Task(title, taskDescription, answer);
                            taskList.add(task);
                            this.gameDescription = gameDescription;
                            title = "";
                            taskDescription = "";
                            answer = "";
                        }
                    }
                }
            }
            // add the last task
            taskList.add(new Task(title, taskDescription, answer));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return taskList;
    }
}
