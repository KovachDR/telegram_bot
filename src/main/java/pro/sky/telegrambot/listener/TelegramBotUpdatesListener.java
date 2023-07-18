package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(messageText);
            if (messageText.equals("/start")) {
                answerStartCommand(chatId, update.message().chat().firstName());
            } else if (matcher.matches()) {
                String time = matcher.group(1);
                String task = matcher.group(3);
                LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                NotificationTask notificationTask = new NotificationTask(chatId, task, localDateTime);
                sendMessage(chatId, "Задание добавлено. Вы получите уведомление в указанное вами время.");
                createTask(notificationTask);
            } else {
                sendMessage(chatId, "Некорректный ввод. Введите сообщение в формате дд.мм.гггг чч:мм 'текст задачи'");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void answerStartCommand(long chatId, String name) {
        String answer = "Гамарджоба " + name + ", добро пожаловать";
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String messageToSend) {
        SendMessage message = new SendMessage(chatId, messageToSend);
        SendResponse response = telegramBot.execute(message);
    }

    private void createTask(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }

    @Scheduled(cron = "0 * * * * *")
    public void findAndSendNotificationByTime() {
        LocalDateTime checkTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notificationTasks = notificationTaskRepository.findByTime(checkTime);
        for (NotificationTask notificationTask : notificationTasks) {
            sendMessage(notificationTask.getChatId(), notificationTask.getTask());
        }

    }
}

