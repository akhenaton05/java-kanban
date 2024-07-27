import managing.Managers;
import managing.TaskManager;
import tasks.StatusPriority;
import tasks.Task;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private TaskManager tManager = Managers.getDefault();
    private Task task = new Task("Name", "Description", StatusPriority.NEW);

    //Очистка списка после каждого теста
    @AfterEach
    public void afterEach() {
        tManager.deleteAllTasks();
    }

    //Тест на равенство задач
    @Test
    public void equalTasks() {
        tManager.addTask(task);
        Task savedTask = new Task("Name!", "Description!", StatusPriority.DONE);
        savedTask.setId(1);
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    //Тесты на добавление задач
    @Test
    public void addNewTask() {
        tManager.addTask(task);
        final int taskId = task.getId(); // 1
        final Task savedTask = tManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = tManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //Тесты с удалением задач из списка
    @Test
    public void deleteTask() {
        //Удаление по ID
        tManager.addTask(task);
        tManager.deleteTaskById(1);
        Assertions.assertTrue(tManager.showAllTasks().isEmpty());

        //Удаление через очистку всего списка
        Task savedTask = new Task("Name!", "Description!", StatusPriority.DONE);

        tManager.addTask(task);
        tManager.addTask(savedTask);
        tManager.deleteAllTasks();

        Assertions.assertTrue(tManager.showAllTasks().isEmpty());
    }

    @Test
    public void equalsByGettingById() {
        tManager.addTask(task);
        Task t = tManager.getTaskById(1);
        Task savedTask = new Task("Name", "Description", StatusPriority.DONE);
        savedTask.setId(1);

        Assertions.assertEquals(t, savedTask, "Не равны по Id");
    }

    @Test
    public void equalsByUpdating() {
        tManager.addTask(task);
        Task savedTask = new Task("New Name", "New Description", StatusPriority.NEW);
        savedTask.setId(1);
        tManager.updateTask(savedTask);

        //Проверка по сравнению названий, описаний и статусов(из за метода equals())
        Assertions.assertTrue((savedTask.getDescription()).equals(tManager.getTaskById(1).getDescription()));
        Assertions.assertTrue((savedTask.getStatus()).equals(tManager.getTaskById(1).getStatus()));
        Assertions.assertTrue((savedTask.getTitle()).equals(tManager.getTaskById(1).getTitle()));
    }

    @Test
    public void settingIdAfterAddingTask() {
        tManager.addTask(task);
        Task temp = tManager.getTaskById(1);
        temp.setId(2);
        List<Task> taskList = tManager.showAllTasks();

        //ID таска поменяется с 1 на 2
        Assertions.assertTrue(taskList.get(0).getId() == 2, "ID таска не изменилось после изменения сеттером");
    }
}