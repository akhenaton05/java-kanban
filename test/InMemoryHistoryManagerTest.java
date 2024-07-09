import historytracking.HistoryManager;
import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private TaskManager tManager = Managers.getDefault();
    private HistoryManager hManager = Managers.getDefaultHistory();

    @AfterEach
    public void afterEach() {
        tManager.deleteAllTasks();
        tManager.deleteAllSubtasks();
        tManager.deleteAllSubtasks();
    }

    @Test
    public void add() {
        Task task = new Task("Name", "Description", StatusPriority.NEW);
        hManager.add(task);
        final List<Task> history = hManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void addingToHistoryCorrectly() {
        Task task = new Task("Name", "Description", StatusPriority.NEW);
        Epic epic = new Epic("Name", "Description");
        Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 2);
        tManager.addTask(task);
        tManager.addTask(epic);
        tManager.addTask(subtask);

        tManager.getTaskById(1);
        tManager.getEpicById(2);
        tManager.getSubtaskById(3);

        List<Task> history = tManager.getHistory();

        //Проверка кол-ва элементов в списке
        Assertions.assertTrue(history.size() == 3, "Неправильное кол-во элементов в списке");
        //Проверка соответствия элементов
        Assertions.assertEquals(task, history.get(0), "Добавлен неправильный элемент в историю");
        Assertions.assertEquals(epic, history.get(1), "Добавлен неправильный элемент в историю");
        Assertions.assertEquals(subtask, history.get(2), "Добавлен неправильный элемент в историю");
    }

    @Test
    public void addingMoreThan10TasksToHistory() {
        //Добавление в список
        for (int i = 1; i < 15; i++) {
            tManager.addTask(new Task("T", "D", StatusPriority.DONE));
        }

        //Добавление в историю просмотров
        for (int i = 1; i < 15; i++) {
            tManager.getTaskById(i);
        }

        Assertions.assertTrue(tManager.getHistory().size() == 10, "В списке больше 10 задач");
        //Проверка перезаписи истории при tasks > 10
        Assertions.assertEquals(tManager.getHistory().get(0).getId(), 5, "Перезапись работает неправильно");
    }

}