package tasks;

import managing.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;
    private boolean haveSubtasks;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        this.haveSubtasks = false;
        this.startTime = LocalDateTime.MAX;
        this.duration = Duration.ZERO;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
        haveSubtasks = true;
    }

    public boolean isHaveSubtasks() {
        return haveSubtasks;
    }

    public void setHaveSubtasks(boolean haveSubtasks) {
        this.haveSubtasks = haveSubtasks;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        String result = String.format("Tasks.Epic{title='%s', description='%s', id='%d', status='%s', haveSubtasks='%b', startTime='%s', duration='%d':%d'}", this.getTitle(), this.getDescription(), this.getId(), this.getStatus(), haveSubtasks, (this.getStartTime() != null) ? this.getStartTime().format(FORMATTER) : null, duration.toHours(), duration.toMinutesPart());
        return result;
    }
}
