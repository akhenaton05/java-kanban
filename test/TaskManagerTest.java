import managing.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

abstract class TaskManagerTest<T extends TaskManager> {
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);
    private Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(55));
    private Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");
    private Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 2, LocalDateTime.of(2022, 10, 22, 10, 15, 3), Duration.ofMinutes(33));

    abstract T createManager();

    //Очистка списка до каждого теста
    @BeforeEach
    public void beforeEach() {
        T manager = createManager();
        manager.deleteAllSubtasks();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
    }

    @Test
    public void addingTest() {
        T manager = createManager();
        manager.addTask(task1);

        Assertions.assertTrue(manager.showAllTasks().size() == 1);
    }

    @Test
    public void taskFieldsNotChangedWhenAdded() {
        T manager = createManager();
        manager.addTask(task1);
        Task savedTask = manager.getTaskById(1);

        Assertions.assertTrue(savedTask.getTitle().equals(task1.getTitle()));
        Assertions.assertTrue(savedTask.getDescription().equals(task1.getDescription()));
        Assertions.assertTrue(savedTask.getStatus().equals(task1.getStatus()));
    }

    @Test
    public void addingDifferentTypesOfTasksAndGettingThemById() {
        T manager = createManager();
        manager.addTask(task1);
        manager.addTask(epic1);
        manager.addTask(subtask1);

        Assertions.assertTrue(manager.getTaskById(1) instanceof Task);
        Assertions.assertTrue(manager.getEpicById(2) instanceof Epic);
        Assertions.assertTrue(manager.getSubtaskById(3) instanceof Subtask);
    }

    @Test
    public void counterIncreasingAfterAdding() {
        T manager = createManager();

        Assertions.assertTrue(manager.getCurrentId() == 1);
        manager.addTask(task1);
        Assertions.assertTrue(manager.getCurrentId() == 2);
        manager.addTask(epic1);
        Assertions.assertTrue(manager.getCurrentId() == 3);
        manager.addTask(subtask1);
        Assertions.assertTrue(manager.getCurrentId() == 4);
    }

    @Test
    public void addingTaskWithExistingIdResultsNothing() {
        T manager = createManager();
        manager.addTask(task1);

        Task newTask = new Task("New Title", "New Descr", StatusPriority.DONE, now.plus(Duration.ofDays(1)), duration);
        newTask.setId(1);
        manager.addTask(newTask);

        Assertions.assertEquals(task1.getTitle(), manager.getTaskById(1).getTitle(), "Название залачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(task1.getDescription(), manager.getTaskById(1).getDescription(), "Описание задачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(task1.getStatus(), manager.getTaskById(1).getStatus(), "Статус задачи с сущетвующим ID был перезаписан");
    }

    @Test
    public void updatingTaskWithExistingIdResultsReplaced() {
        T manager = createManager();
        manager.addTask(task1);

        Task newTask = new Task("New Title", "New Descr", StatusPriority.DONE, now.plus(Duration.ofDays(2)), duration);
        newTask.setId(1);
        manager.updateTask(newTask);

        Assertions.assertEquals(newTask.getTitle(), manager.getTaskById(1).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newTask.getDescription(), manager.getTaskById(1).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newTask.getStatus(), manager.getTaskById(1).getStatus(), "Статус задачи с сущетвующим ID не был перезаписан");
    }

    @Test
    public void addingEpicWithExistingIdResultsNothing() {
        T manager = createManager();
        manager.addTask(epic1);

        Epic newEpic = new Epic("New Title", "New Descr");
        newEpic.setId(1);
        manager.addTask(newEpic);

        Assertions.assertEquals(epic1.getTitle(), manager.getEpicById(1).getTitle(), "Название залачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(epic1.getDescription(), manager.getEpicById(1).getDescription(), "Описание задачи с сущетвующим ID было перезаписано");
    }


    @Test
    public void updatingEpicWithExistingIdResultsReplaced() {
        T manager = createManager();
        manager.addTask(epic1);

        Epic newEpic = new Epic("New Title", "New Descr");
        newEpic.setId(1);
        manager.updateTask(newEpic);

        Assertions.assertEquals(newEpic.getTitle(), manager.getEpicById(1).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newEpic.getDescription(), manager.getEpicById(1).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
    }

    @Test
    public void addingSubtaskWithExistingIdResultsNothing() {
        T manager = createManager();
        manager.addTask(epic1);

        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 1, now, duration);
        manager.addTask(subtask);

        Subtask newSubtask = new Subtask("New Title", "New Descr", StatusPriority.IN_PROGRESS, 1, now.plus(Duration.ofDays(2)), duration);
        newSubtask.setId(2);
        manager.addTask(newSubtask);

        Assertions.assertEquals(subtask.getTitle(), manager.getSubtaskById(2).getTitle(), "Название подзалачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(subtask.getDescription(), manager.getSubtaskById(2).getDescription(), "Описание подзадачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(subtask.getEpicId(), manager.getSubtaskById(2).getEpicId(), "Эпик ID подзадачи с сущетвующим ID был перезаписан");
        Assertions.assertEquals(subtask.getStatus(), manager.getSubtaskById(2).getStatus(), "Статус подзадачи с сущетвующим ID был перезаписан");
    }

    @Test
    public void updatingSubtaskWithExistingIdResultsReplaced() {
        T manager = createManager();
        manager.addTask(epic1);

        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 1, now, duration);
        manager.addTask(subtask);

        Subtask newSubtask = new Subtask("New Title", "New Descr", StatusPriority.IN_PROGRESS, 1, now.plus(Duration.ofDays(2)), duration);
        newSubtask.setId(2);
        manager.updateTask(newSubtask);

        Assertions.assertEquals(newSubtask.getTitle(), manager.getSubtaskById(2).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newSubtask.getDescription(), manager.getSubtaskById(2).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newSubtask.getEpicId(), manager.getSubtaskById(2).getEpicId(), "Эпик ID подзадачи с сущетвующим ID не был перезаписан");
        Assertions.assertEquals(newSubtask.getStatus(), manager.getSubtaskById(2).getStatus(), "Статус подзадачи с сущетвующим ID не был перезаписан");
    }

    @Test
    public void crossingDataCheckingTest() {
        T manager = createManager();
        manager.addTask(task1);
        manager.addTask(epic1);
        subtask1.setStartTime(now);
        manager.addTask(subtask1);

        Assertions.assertTrue(manager.showAllSubtasks().isEmpty(), "Сабтаск с пересекающийся датой добавился в список");

        Subtask subtask = subtask1;
        subtask.setStartTime(now.plus(Duration.ofDays(1)));
        manager.addTask(subtask);

        Assertions.assertTrue(manager.showAllSubtasks().size() == 1, "Сабтаск с непересекающийся датой не добавился в список");

    }

    @Test
    public void endTimeAndDurationCalculationTest() {
        LocalDateTime date = LocalDateTime.of(2022, 10, 10, 10, 0, 0);
        Task task = new Task("A", "B", StatusPriority.NEW, date, Duration.ofMinutes(30));

        Assertions.assertEquals(task.getStartTime().plus(Duration.ofMinutes(30)).format(Task.FORMATTER), task.getEndTime().format(Task.FORMATTER), "endTime рассчиталось неверно");
    }

    @Test
    public void epicStartTimeChangesBySubtasks() {
        T manager = createManager();
        Subtask subtask2 = new Subtask("S", "s", StatusPriority.DONE, 1, now.plus(Duration.ofDays(1)), duration);
        manager.addTask(epic1);
        subtask1.setEpicId(1);
        manager.addTask(subtask1);
        manager.addTask(subtask2);

        Assertions.assertTrue(epic1.getStartTime().equals(subtask1.getStartTime()));

        manager.deleteSubtasksById(2);
        Assertions.assertTrue(epic1.getStartTime().equals(subtask2.getStartTime()));
    }


    @Test
    public void prioritizedTasksTest() {
        T manager = createManager();
        Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(55));
        Task task2 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW, LocalDateTime.of(2025, 11, 21, 10, 15, 3), Duration.ofMinutes(55));
        Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");
        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");
        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 3, LocalDateTime.of(2022, 10, 22, 10, 15, 3), Duration.ofMinutes(33));
        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 3, LocalDateTime.of(2023, 11, 21, 10, 15, 3), Duration.ofMinutes(55));
        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.DONE, 4, LocalDateTime.of(2024, 6, 4, 10, 15, 3), Duration.ofMinutes(3));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(epic1);
        manager.addTask(epic2);
        manager.addTask(subtask1);
        manager.addTask(subtask2);
        manager.addTask(subtask3);

        TreeSet<Task> sortedList = manager.getPrioritizedTasks();

        Assertions.assertTrue(sortedList.first().equals(subtask1));
        Assertions.assertTrue(sortedList.last().equals(task2));

    }
}
