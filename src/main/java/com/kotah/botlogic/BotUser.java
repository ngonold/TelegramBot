package com.kotah.botlogic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotUser {
    private String userName;
    private Long chatId;
    private String nickName;
    private UserRole userRole;
    private Long telegramId;

    private List<LocalDateTime> tasksFinishTime;
    private Boolean isRegistered;

    //TODO: enum?
    // -1 not in game, 0 in game, 1..n - m-task is done.

    private Integer currentTask;

    BotUser(String userName, Long chatId, String nickName, Long telegramId) {
        this.userName = userName;
        this.chatId = chatId;
        this.nickName = nickName;
        this.telegramId = telegramId;
        this.isRegistered = false;
        this.currentTask = -1;
        final String KOTAH = "nickych";
        final String DIMKAS = "Dimkas2003";
        tasksFinishTime = new ArrayList<>();
        if (userName.equals(DIMKAS) || userName.equals(KOTAH)) {
            userRole = UserRole.ADMIN;
        } else {
            userRole = UserRole.GAMER;
        }
    }

    public String getUserName() {
        return userName;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public String getNickName() {
        return nickName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Boolean getRegistered() {
        return isRegistered;
    }

    public Integer getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Integer currentTask) {
        this.currentTask = currentTask;
    }

    public void registerUser(Boolean register) {
        isRegistered = register;
    }

    private void updateNickname(String nickName) {
        this.nickName = nickName;
    }

    public List<LocalDateTime> getTasksFinishTime() {
        return tasksFinishTime;
    }

    public void setTasksFinishTime(List<LocalDateTime> tasksFinishTime) {
        this.tasksFinishTime = tasksFinishTime;
    }

    @Override
    public String toString() {
        return "BotUser(" +
                "userName='" + userName + '\'' +
                ", chatId='" + chatId + '\'' +
                ", telegram id= '" + telegramId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", userRole=" + userRole +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BotUser botUser))
            return false;
        return (Objects.equals(getUserName(), botUser.getUserName())
                && (Objects.equals(getTelegramId(), botUser.getTelegramId())));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getTelegramId());
    }
}

enum UserRole {
    ADMIN,
    GAMER
}