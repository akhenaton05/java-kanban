import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private TaskManager tManager = Managers.getDefault();
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);
    private Epic epic = new Epic("Name", "SD");
    private Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 1, now, duration);

    @AfterEach
    public void afterEach() {
        tManager.deleteAllEpics();
        tManager.deleteAllSubtasks();
    }

    @Test
    public void equalSubtasksBySetId() {
        tManager.addTask(epic);
        tManager.addTask(subtask);
        Subtask savedSubtask = new Subtask("Name!", "Description!", StatusPriority.NEW, 1, now.plus(Duration.ofDays(2)), duration);
        savedSubtask.setId(2);
        Assertions.assertEquals(subtask, savedSubtask, "Эпики не совпадают.");
    }

    @Test
    public void notAbleToAddSubtaskAsAnEpic() {
        tManager.addTask(epic);
        Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 1, now, duration);
        //Добавляем сабтаск, его id = 2
        tManager.addTask(subtask);
        //Проверяем на null список эпиков с id = 2, если null - сабтаск с epicId = 2 не добавить
        assertNull(tManager.getEpicById(2));
    }

    @Test
    public void addNewSubtask() {
        tManager.addTask(epic); // id = 1
        tManager.addTask(subtask);
        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = tManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не найден."); // Метод add доабвил epic в список
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = tManager.showAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void deleteSubtasks() {
        //Удаление по ID
        tManager.addTask(epic);
        tManager.addTask(subtask);
        tManager.deleteSubtasksById(2);
        Assertions.assertTrue(tManager.showAllSubtasks().isEmpty());

        //Удаление через очистку всего списка
        Subtask subtask1 = new Subtask("Description!", "SDA", StatusPriority.DONE, 1, now, duration);

        tManager.addTask(subtask);
        tManager.addTask(subtask1);
        tManager.deleteAllSubtasks();

        Assertions.assertTrue(tManager.showAllTasks().isEmpty());
    }

    @Test
    public void equalsByGettingById() {
        tManager.addTask(epic);
        tManager.addTask(subtask);
        Subtask s = tManager.getSubtaskById(2);
        Subtask savedSubtask = new Subtask("Name", "Description", StatusPriority.DONE, 1, now, duration);
        savedSubtask.setId(2);

        Assertions.assertEquals(s, savedSubtask, "Не равны по Id");
    }

    @Test
    public void equalsByUpdating() {
        tManager.addTask(epic);
        tManager.addTask(subtask);
        Subtask savedSubtask = new Subtask("New Name", "New Description", StatusPriority.DONE, 1, now.plus(Duration.ofDays(1)), duration);
        savedSubtask.setId(2);
        tManager.updateTask(savedSubtask);

        //Проверка по сравнению названий, описаний и статусов(из за метода equals())
        Assertions.assertTrue((savedSubtask.getDescription()).equals(tManager.getSubtaskById(2).getDescription()));
        Assertions.assertTrue((savedSubtask.getStatus()).equals(tManager.getSubtaskById(2).getStatus()));
        Assertions.assertTrue((savedSubtask.getTitle()).equals(tManager.getSubtaskById(2).getTitle()));
        Assertions.assertTrue((savedSubtask.getEpicId()) == (tManager.getSubtaskById(2).getEpicId()));
    }
}