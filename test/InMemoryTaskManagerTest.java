import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest {
    private final TaskManager tManager = Managers.getDefault();
    private Task task = new Task("Title", "Description", StatusPriority.NEW);
    private Epic epic = new Epic("Title", "Descrip");

    //Очитска перед каждым тестом
    @BeforeEach
    public void beforeEach() {
        tManager.deleteAllSubtasks();
        tManager.deleteAllEpics();
        tManager.deleteAllTasks();
    }

    @Test
    public void taskFieldsNotChangedWhenAdded() {
        tManager.addTask(task);
        Task savedTask = tManager.getTaskById(1);

        Assertions.assertTrue(savedTask.getTitle().equals("Title"));
        Assertions.assertTrue(savedTask.getDescription().equals("Description"));
        Assertions.assertTrue(savedTask.getStatus().equals(StatusPriority.NEW));
    }

    @Test
    public void addingDifferentTypesOfTasksAndGettingThemById() {
        Task task = new Task("Title", "Descr", StatusPriority.NEW);
        Epic epic = new Epic("Title", "Descrip");
        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 2);
        tManager.addTask(task);
        tManager.addTask(epic);
        tManager.addTask(subtask);

        Assertions.assertTrue(tManager.getTaskById(1) instanceof Task);
        Assertions.assertTrue(tManager.getEpicById(2) instanceof Epic);
        Assertions.assertTrue(tManager.getSubtaskById(3) instanceof Subtask);
    }

    @Test
    public void counterIncreasingAfterAdding() {
        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 2);

        Assertions.assertTrue(tManager.getCurrentId() == 1);
        tManager.addTask(task);
        Assertions.assertTrue(tManager.getCurrentId() == 2);
        tManager.addTask(epic);
        Assertions.assertTrue(tManager.getCurrentId() == 3);
        tManager.addTask(subtask);
        Assertions.assertTrue(tManager.getCurrentId() == 4);
    }

    @Test
    public void addingTaskWithExistingIdResultsNothing() {
        tManager.addTask(task);

        Task newTask = new Task("New Title", "New Descr", StatusPriority.DONE);
        newTask.setId(1);
        tManager.addTask(newTask);

        Assertions.assertEquals(task.getTitle(), tManager.getTaskById(1).getTitle(), "Название залачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(task.getDescription(), tManager.getTaskById(1).getDescription(), "Описание задачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(task.getStatus(), tManager.getTaskById(1).getStatus(), "Статус задачи с сущетвующим ID был перезаписан");
    }

    @Test
    public void updatingTaskWithExistingIdResultsReplaced() {
        tManager.addTask(task);

        Task newTask = new Task("New Title", "New Descr", StatusPriority.DONE);
        newTask.setId(1);
        tManager.updateTask(newTask);

        Assertions.assertEquals(newTask.getTitle(), tManager.getTaskById(1).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newTask.getDescription(), tManager.getTaskById(1).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newTask.getStatus(), tManager.getTaskById(1).getStatus(), "Статус задачи с сущетвующим ID не был перезаписан");
    }

    @Test
    public void addingEpicWithExistingIdResultsNothing() {
        tManager.addTask(epic);

        Epic newEpic = new Epic("New Title", "New Descr");
        newEpic.setId(1);
        tManager.addTask(newEpic);

        Assertions.assertEquals(epic.getTitle(), tManager.getEpicById(1).getTitle(), "Название залачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(epic.getDescription(), tManager.getEpicById(1).getDescription(), "Описание задачи с сущетвующим ID было перезаписано");
    }

    @Test
    public void updatingEpicWithExistingIdResultsReplaced() {
        tManager.addTask(epic);

        Epic newEpic = new Epic("New Title", "New Descr");
        newEpic.setId(1);
        tManager.updateTask(newEpic);

        Assertions.assertEquals(newEpic.getTitle(), tManager.getEpicById(1).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newEpic.getDescription(), tManager.getEpicById(1).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
    }

    @Test
    public void addingSubtaskWithExistingIdResultsNothing() {
        tManager.addTask(epic);

        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 1);
        tManager.addTask(subtask);

        Subtask newSubtask = new Subtask("New Title", "New Descr", StatusPriority.IN_PROGRESS, 1);
        newSubtask.setId(2);
        tManager.addTask(newSubtask);

        Assertions.assertEquals(subtask.getTitle(), tManager.getSubtaskById(2).getTitle(), "Название подзалачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(subtask.getDescription(), tManager.getSubtaskById(2).getDescription(), "Описание подзадачи с сущетвующим ID было перезаписано");
        Assertions.assertEquals(subtask.getEpicId(), tManager.getSubtaskById(2).getEpicId(), "Эпик ID подзадачи с сущетвующим ID был перезаписан");
        Assertions.assertEquals(subtask.getStatus(), tManager.getSubtaskById(2).getStatus(), "Статус подзадачи с сущетвующим ID был перезаписан");
    }

    @Test
    public void updatingSubtaskWithExistingIdResultsReplaced() {
        tManager.addTask(epic);

        Subtask subtask = new Subtask("Title", "Descr", StatusPriority.DONE, 1);
        tManager.addTask(subtask);

        Subtask newSubtask = new Subtask("New Title", "New Descr", StatusPriority.IN_PROGRESS, 1);
        newSubtask.setId(2);
        tManager.updateTask(newSubtask);

        Assertions.assertEquals(newSubtask.getTitle(), tManager.getSubtaskById(2).getTitle(), "Название залачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newSubtask.getDescription(), tManager.getSubtaskById(2).getDescription(), "Описание задачи с сущетвующим ID не было перезаписано");
        Assertions.assertEquals(newSubtask.getEpicId(), tManager.getSubtaskById(2).getEpicId(), "Эпик ID подзадачи с сущетвующим ID не был перезаписан");
        Assertions.assertEquals(newSubtask.getStatus(), tManager.getSubtaskById(2).getStatus(), "Статус подзадачи с сущетвующим ID не был перезаписан");
    }
}