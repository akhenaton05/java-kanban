package tasks;

import managing.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, StatusPriority status, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String result = String.format("Tasks.Subtask{description='%s', id='%d', epicId='%d', status='%s', startTime='%s'}", this.getDescription(), this.getId(), epicId, this.getStatus(), this.getStartTime().format(FORMATTER));
        return result;
    }
}
