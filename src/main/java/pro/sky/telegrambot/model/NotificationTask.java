package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification_task")
public class NotificationTask {

    @Id
    private Long chatId;
    private String task;
    private LocalDateTime time;

    public NotificationTask(Long chatId, String task, LocalDateTime time) {
        this.chatId = chatId;
        this.task = task;
        this.time = time;
    }

    public NotificationTask() {

    }

    public Long getChatId() {
        return chatId;
    }

    public String getTask() {
        return task;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(task, that.task) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, task, time);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "chatId=" + chatId +
                ", task='" + task + '\'' +
                ", time=" + time +
                '}';
    }
}
