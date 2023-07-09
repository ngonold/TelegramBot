package com.kotah.gamelogic;

import com.kotah.botlogic.BotUser;
import com.kotah.parser.TaskFileParser;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Game {
    private List<Task> taskList;
    private List<BotUser> participants;
    private List<BotUser> winners;
    private String title;
    private Boolean isStarted;
    private int gameId;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String description;

    // Map of usernames and list of tasks finish times
    private Map<String, List<LocalDateTime>> userStats;

    public Game(String title) {
        this.title = title;
        this.isStarted = false;
        this.description = "";
        userStats = new HashMap<>();
    }

    private Boolean addTask(Task task) {
        if(taskList.isEmpty()) {
            taskList = new ArrayList<>();
            taskList.add(task);
            return true;
        }
        return false;
    }

    public Boolean startGame(List<BotUser> users) {
        startTime = LocalDateTime.now();
        participants = updateParticipants(users);
        //set task number to 0 for each participant
        participants.forEach(p->p.setCurrentTask(0));

        //TODO: implement task source detection
        // for now it starts from file
        TaskFileParser parser = new TaskFileParser();
        taskList = parser.readTasksFile("tasks.txt");
        this.description = parser.getGameDescription();
        if(!taskList.isEmpty() && !participants.isEmpty()) {
            isStarted = true;
            //participants.forEach();
        }
        return isStarted;
    }
    public void finishGame() {
        finishTime = LocalDateTime.now();
        isStarted = false;
        participants.clear();
    }

    public void getGameResults() {

    }

    public String getDescription() {
        return this.description;
    }
    public Boolean validateAnswer(String answer, BotUser participant) {
         if (answer.equalsIgnoreCase(taskList.get(participant.getCurrentTask()).getAnswer())) {
             participant.getTasksFinishTime().add(LocalDateTime.now());
             userStats.put(participant.getNickName(),participant.getTasksFinishTime());
             // if we got a correct answer figure out if there are participants with uncompleted tasks
             int activePlayers = 0;
             for (BotUser user : participants) {
                 if ((user.getCurrentTask() < taskList.size()) && (user.getCurrentTask() > -1)) {
                     activePlayers++;
                 }
             }
             if (activePlayers == 0) {
                 finishGame();
             }
             return true;
         } else {
             return false;
         }
    }

    List<BotUser> updateParticipants(List<BotUser> botUserList) {
        return botUserList.stream().filter(BotUser::getRegistered).collect(Collectors.toList());
    }

    public String getCurrentTaskText(BotUser user) {
        return taskList.get(user.getCurrentTask()).getTaskText();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void getNextTask(BotUser participant) {
        if(participant.getRegistered() && (participant.getCurrentTask() > -1)) {
            int currTask = participant.getCurrentTask();
            if(++currTask <= taskList.size()) {
                participant.setCurrentTask(currTask);
            }
            else {
                // all tasks are done
                //TODO: fix this patch
                participant.setCurrentTask(-1);
                //new Task("Congratulations!", "Ты закончил игру!", "none");
            }
        }
        else {
            participant.registerUser(false);
            participant.setCurrentTask(-1);
            System.out.println("Something wrong with this user. Set to default");
            //new Task("404. Error ", "Что-то сломалось. Свяжитесь с @Dimkas2003", "none");
        }
    }
}
