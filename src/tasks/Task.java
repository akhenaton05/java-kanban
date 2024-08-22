package tasks;

import managing.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String title;
    private String description;
    private int id;
    private StatusPriority status;
    private LocalDateTime startTime;
    private Duration duration;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public Task(String title, String description, StatusPriority status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusPriority getStatus() {
        return status;
    }

    public void setStatus(StatusPriority status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        String result = String.format("Tasks.Task{title='%s', description='%s', id='%d', status='%s', startTime='%s'}", title, description, id, status, (this.getStartTime() != null) ? this.getStartTime().format(FORMATTER) : null);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
