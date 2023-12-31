package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            if (messageText.equals("/start")) {
                answerStartCommand(chatId, update.message().chat().firstName());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void answerStartCommand(long chatId, String name) {
        String answer = "Гамарджоба " + name + ", добро пожаловать";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String messageToSend) {
        SendMessage message = new SendMessage(chatId, messageToSend);
        SendResponse response = telegramBot.execute(message);
    }

}
