import managing.FileBackedTaskManager;
import managing.ManagerSaveException;
import managing.TaskManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private File tempFile = File.createTempFile("test", ".txt");
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);

    public FileBackedTaskManagerTest() throws IOException {
    }

    @Test
    public void serializationAndDeserializationEqualityTest() throws IOException {
        Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(55));
        Task task2 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW, LocalDateTime.of(2025, 11, 21, 10, 15, 3), Duration.ofMinutes(55));
        Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");
        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");
        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 3, LocalDateTime.of(2022, 10, 22, 10, 15, 3), Duration.ofMinutes(33));
        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 3, LocalDateTime.of(2023, 11, 21, 10, 15, 3), Duration.ofMinutes(55));
        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.DONE, 4, LocalDateTime.of(2024, 6, 4, 10, 15, 3), Duration.ofMinutes(3));

        FileBackedTaskManager fManager = new FileBackedTaskManager(tempFile);

        fManager.addTask(task1);
        fManager.addTask(task2);
        fManager.addTask(epic1);
        fManager.addTask(epic2);
        fManager.addTask(subtask1);
        fManager.addTask(subtask2);
        fManager.addTask(subtask3);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        //Проверка равенства счетчика ID менеджеров до и после
        Assertions.assertEquals(newManager.getCurrentId(), fManager.getCurrentId(), "ID тасков до и после сериализации не совпадают");

        //Проверка равества каждого поля тасков
        boolean taskResult = true;
        if (fManager.showAllTasks().size() != newManager.showAllTasks().size()) {
            taskResult = false;
        }
        int size = fManager.showAllTasks().size();
        for (int i = 0; i < size - 1; i++) {
            Task t1 = fManager.showAllTasks().get(i);
            Task t2 = newManager.showAllTasks().get(i);
            int id1 = t1.getId();
            int id2 = t2.getId();
            String title1 = t1.getTitle();
            String title2 = t2.getTitle();
            String description1 = t1.getDescription();
            String description2 = t2.getDescription();
            StatusPriority status1 = t1.getStatus();
            StatusPriority status2 = t2.getStatus();

            if (!(id1 == id2 && title1.equals(title2) && description1.equals(description2)
                    && status1 == status2)) {
                taskResult = false;
                break;
            }
        }
        Assertions.assertTrue(taskResult, "Поля тасков до и после сериализации не совпадают");

        boolean epicResult = true;
        if (fManager.showAllEpics().size() != newManager.showAllEpics().size()) {
            epicResult = false;
        }
        int epicSize = fManager.showAllEpics().size();
        for (int i = 0; i < epicSize - 1; i++) {
            Epic t1 = fManager.showAllEpics().get(i);
            Epic t2 = newManager.showAllEpics().get(i);
            int id1 = t1.getId();
            int id2 = t2.getId();
            String title1 = t1.getTitle();
            String title2 = t2.getTitle();
            String description1 = t1.getDescription();
            String description2 = t2.getDescription();
            StatusPriority status1 = t1.getStatus();
            StatusPriority status2 = t2.getStatus();

            if (!(id1 == id2 && title1.equals(title2) && description1.equals(description2)
                    && status1 == status2)) {
                epicResult = false;
                break;
            }
        }
        Assertions.assertTrue(epicResult, "Поля эпиков до и после сериализации не совпадают");

        boolean subtaskResult = true;
        if (fManager.showAllSubtasks().size() != newManager.showAllSubtasks().size()) {
            subtaskResult = false;
        }
        int subtaskSize = fManager.showAllSubtasks().size();
        for (int i = 0; i < subtaskSize - 1; i++) {
            Subtask t1 = fManager.showAllSubtasks().get(i);
            Subtask t2 = newManager.showAllSubtasks().get(i);
            int id1 = t1.getId();
            int id2 = t2.getId();
            String title1 = t1.getTitle();
            String title2 = t2.getTitle();
            String description1 = t1.getDescription();
            String description2 = t2.getDescription();
            StatusPriority status1 = t1.getStatus();
            StatusPriority status2 = t2.getStatus();
            int eId1 = t1.getEpicId();
            int eId2 = t2.getEpicId();

            if (!(id1 == id2 && title1.equals(title2) && description1.equals(description2)
                    && status1 == status2 && eId1 == eId2)) {
                subtaskResult = false;
                break;
            }
        }
        Assertions.assertTrue(subtaskResult, "Поля сабтасков до и после сериализации не совпадают");

    }

    @Test
    public void exceptionsThrowsTest() throws IOException {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            Task task = new Task("A", "B", StatusPriority.NEW, now, duration);
            FileBackedTaskManager fManager = new FileBackedTaskManager(tempFile);
            fManager.addTask(task);

            File tempFile1 = new File("test2.txt");

            FileBackedTaskManager.loadFromFile(tempFile1);
        }, "Чтение из несуществующего файла должно выбросить ошибку");
    }

    @Override
    TaskManager createManager() {
        return new FileBackedTaskManager(tempFile);
    }
}
