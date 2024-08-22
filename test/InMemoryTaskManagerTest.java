import managing.InMemoryTaskManager;
import managing.Managers;
import managing.TaskManager;
import org.junit.Before;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    private final InMemoryTaskManager tManager = (InMemoryTaskManager) Managers.getDefault();
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);


    @Override
    TaskManager createManager() {
        return new InMemoryTaskManager();
    }
}