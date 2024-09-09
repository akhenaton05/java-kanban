import managing.InMemoryTaskManager;
import managing.Managers;
import managing.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    private final InMemoryTaskManager tManager = (InMemoryTaskManager) Managers.getDefault();
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);


    @Override
    TaskManager createManager() {
        return new InMemoryTaskManager();
    }
}