package com.kotah.botlogic;

import com.kotah.gamelogic.Game;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameBot extends TelegramLongPollingBot {
    //private final String BOT_TOKEN = "6028684083:AAsif7SLzSAV7CL7";
    private final String BOT_USERNAME = "@DimkasGameBot";
    private final String CMD_ABOUT = "/about";
    private final String CMD_REGISTER = "/register";
    private final String CMD_LEAVE = "/leave";
    private final String CMD_DESCRIPTION = "/description";
    private final String CMD_START = "/start";

    // admin commands
    private final String CMD_NEW_GAME = "/new_game";
    private final String CMD_NEW_TASK = "/new_task";
    private final String CMD_START_GAME = "/start_game";
    private final String CMD_END_GAME = "/end_game";
    private List<BotUser> botUsers;
    private List<BotUser> participants;

    private Game game;

    private Boolean addUser(BotUser user) {
        if (!botUsers.contains(user)) {
            botUsers.add(user);
            System.out.println("user added:\n" + user);
            return true;
        } else {
            System.out.println("This user already exists!");
            return false;
        }
    }

    public GameBot(DefaultBotOptions options) {
        super(options, "6028684083:AAFsif7SLzSAV7CL7m6_Ni05ldHFr0QKPmU");
        game = new Game("Unknown");
        botUsers = new ArrayList<>();
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message msg = update.getMessage();
            if (msg.isCommand()) {
                handleCommand(msg);
            } else if (msg.isUserMessage()) {
                handleUserText(msg);
            }
        }
    }

    private void handleCommand(@org.jetbrains.annotations.NotNull Message msg) {
        if (msg.hasText() && msg.hasEntities()) {
            Optional<MessageEntity> commandEntity = msg.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                // cut the msg to find command
                String command = msg.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case (CMD_ABOUT) -> tellAboutBot(msg);
                    case (CMD_REGISTER) -> addToGame(msg);
                    case (CMD_START) -> welcomeUser(msg);
                    case (CMD_START_GAME) -> gameStart(msg);
                    case (CMD_LEAVE) -> excludeFromGame(msg);
                    case (CMD_DESCRIPTION) -> tellGameDescription(msg);
                }
            }
        }
    }

    void handleUserText(Message msg) {
        // options: 1 - answer to question, 2 - nickname
        BotUser user = getSender(msg);
        if (game.getIsStarted() && user.getRegistered()) {
            // here may be answer
            if (game.validateAnswer(msg.getText(), user)) {
                game.getNextTask(user);
                if (user.getCurrentTask() < game.getTaskList().size()) {
                    notifyBotUser(user, "Верный ответ! Жди следующее задание");
                    notifyBotUser(user, game.getCurrentTaskText(botUsers.get(botUsers.indexOf(user))));
                } else {
//                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//                    LocalDateTime userGameDuration = LocalDateTime.now().minus(game.getStartTime().adjustInto());
                    Duration userGameDuration = Duration.between(game.getStartTime(), LocalDateTime.now());
                    notifyBotUser(user, "Ура! Ты прошёл игру! \nТвоё время: " + userGameDuration.getSeconds());
                    // check if this user was the last one
//                    if (BotUser usr : botUsers) {
//                        usr.getCurrentTask()
//                    }
                }
            }
            else {
                notifyBotUser(user, "Упс! Ошибочка вышла.\nПопробуй ещё разик.");
            }
        }
    }

    @SneakyThrows
    private void tellAboutBot(Message msg) {
        execute(SendMessage.builder().chatId(msg.getChatId()).text("This is a very exciting game, invented by @Dimkas2003").build());
    }

    @SneakyThrows
    private void welcomeUser(Message msg) {
        // get users info and add him to users base
        BotUser botUser = getSender(msg);
        if (addUser(botUser)) {
            //TODO: ask and save his/her nickname

            // send welcome message
//            execute(SendMessage.builder()
//                    .chatId(botUser.getChatId())
//                    .text("Добро пожаловать, " + botUser.getNickName() + "! Рад приветствовать тебя в нашей игре. Посмотри мой список команд, чтобы зарегистрироваться на следующую игру.")
//                    .build());
            notifyBotUser(botUser, "Добро пожаловать, " + botUser.getNickName() +
                    "! Рад приветствовать тебя в нашей игре. Посмотри мой список команд, чтобы зарегистрироваться на следующую игру.");
        }
        else {
            notifyBotUser(botUser, "О! А я тебя знаю!");
            execute(SendSticker.builder()
                    .chatId(botUser.getChatId())
                    .sticker(new InputFile("AgADSw4AAlOx9wM"))
                    .build());
        }

    }

    private void tellGameDescription(Message msg) {
        notifyBotUser(getSender(msg),game.getDescription());
    }
    @SneakyThrows
    private void addToGame(Message msg) {
        BotUser botUser = getSender(msg);
        if (botUsers.stream().anyMatch(user -> user.equals(botUser))) {
            botUsers.get(botUsers.indexOf(botUser)).registerUser(true);
            execute(SendMessage.builder()
                    .chatId(botUser.getChatId())
                    .text("Всё получилось!\nЯ тебя зарегал. Ты можешь ливнуть, если передумаешь, командой /leave.")
                    .build());
        } else {
            execute(SendMessage.builder()
                    .chatId(botUser.getChatId())
                    .text("Ты кто такой?\nТебя почему-то нет в базе О_о. Выполни команду /start.")
                    .build());
        }
    }

    @SneakyThrows
    private void excludeFromGame(Message msg) {
        BotUser botUser = getSender(msg);
        if (botUsers.stream().anyMatch(user -> user.equals(botUser))) {
            botUsers.get(botUsers.indexOf(botUser)).registerUser(false);
            execute(SendMessage.builder()
                    .chatId(botUser.getChatId())
                    .text("Надеюсь, ты хорошо подумал(а)! Ты можешь ещё можешь исправить ситуацию и не обижать Димкаса. Регайся командой /register!")
                    .build());
        } else {
            execute(SendMessage.builder()
                    .chatId(botUser.getChatId())
                    .text("Тебя почему-то нет в базе О_о. Выполни команду /start")
                    .build());
        }
    }

    @SneakyThrows
    private void gameStart(Message msg) {
        BotUser user = getSender(msg);
        if (user.getUserRole() == UserRole.ADMIN) {
            game = new Game("Dimkas Game");
            participants = getGameParticipants(botUsers);
            // reset previous results
            participants.forEach(participant -> participant.setTasksFinishTime(new ArrayList<>()));
            if (game.startGame(participants)) {
                notifyAllBotUsers(participants, "Игра <<" + game.getTitle() + ">> началась!\n" +
                        "Описание игры:\n" + game.getDescription() +
                        "\nСейчас ты получишь первое задание. Напиши правильный ответ на него, чтобы перейти к следующему.");
                notifyAllBotUsers(participants, game.getCurrentTaskText(participants.get(0)));
            }
            else {
                notifyBotUser(user, "С игрой что-то не так. Посмотри, добавлены ли задания и есть ли участники.");
            }
        }
        else {
            notifyBotUser(user, "Команда-то есть... Но ты не админ и не можешь стартовать игру О_о. Попроси @Dimkas2003, ему можно начать");
        }
    }

    private BotUser getSender(Message msg) {
        String userName = msg.getFrom().getUserName();//msg.getChat().getActiveUsernames().stream().findFirst().orElse("unknown");
        Long chatId = msg.getChatId();
        String firstName = msg.getFrom().getFirstName();
        String lastName = msg.getFrom().getLastName();
        if(Objects.isNull(lastName)) {
            lastName = "";
        }
        String nickName = firstName.concat(" ").concat(lastName);
        Long telegramId = msg.getFrom().getId();
        BotUser user = new BotUser(userName, chatId, nickName, telegramId);
        if(botUsers.contains(user)) {
            return botUsers.get(botUsers.indexOf(user));
        }
        else {
            return user;
        }
    }

    List<BotUser> getGameParticipants(List<BotUser> users) {
        return users.stream().filter(BotUser::getRegistered).collect(Collectors.toList());
    }

    private void notifyBotUser(BotUser user, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(user.getChatId())
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyAllBotUsers(List<BotUser> users, String text) {
        if (Objects.nonNull(text)) {
            users.forEach(user -> {
                try {
                    execute(SendMessage.builder()
                            .chatId(user.getChatId())
                            .text(text)
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
