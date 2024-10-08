import managing.InMemoryTaskManager;
import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private InMemoryTaskManager tManager = (InMemoryTaskManager) Managers.getDefault();
    private LocalDateTime now = LocalDateTime.now();
    private Duration duration = Duration.ofMinutes(30);
    private Epic epic = new Epic("Name", "Description");
    private Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 1, now, duration);

    @AfterEach
    public void afterEach() {
        tManager.deleteAllEpics();
        tManager.deleteAllSubtasks();
    }

    @Test
    public void equalEpics() {
        tManager.addTask(epic);
        Epic savedEpic = new Epic("Name!", "Description!");
        savedEpic.setId(1);
        Assertions.assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    public void epicIsNullWhenAddingAsASubtask() {
        Epic savedEpic = new Epic("Name!", "Description!");
        tManager.addTask(epic);
        tManager.addTask(subtask);
        tManager.addTask(savedEpic);
        //Добавляем savedEpic ID в список сабтаска epic
        ArrayList<Integer> epicIds = epic.getSubtasks();
        epicIds.add(savedEpic.getId());
        ArrayList<Subtask> subtasks = tManager.getEpicsSubtasks(epic.getId());
        //Проверка на отсутствие savedEpic в сабтасках epic
        Assertions.assertTrue(subtasks.size() == 1);
    }

    @Test
    public void addNewEpic() {
        tManager.addTask(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = tManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = tManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void deleteEpic() {
        //Удаление по ID
        tManager.addTask(epic);
        tManager.addTask(subtask);
        tManager.deleteEpicById(1);
        Assertions.assertTrue(tManager.showAllEpics().isEmpty());
        Assertions.assertTrue(tManager.showAllSubtasks().isEmpty());

        //Удаление через очистку всего списка
        Epic savedEpic = new Epic("Name!", "Description!");
        subtask.setEpicId(3);

        tManager.addTask(savedEpic);
        tManager.addTask(subtask);
        tManager.addTask(epic);
        tManager.deleteAllEpics();

        Assertions.assertTrue(tManager.showAllEpics().isEmpty());
        Assertions.assertTrue(tManager.showAllSubtasks().isEmpty());
    }

    @Test
    public void epicsStatusCheck() {
        tManager.addTask(epic);
        tManager.addTask(subtask);

        //Проверка статуса
        Assertions.assertEquals(StatusPriority.NEW, epic.getStatus(), "Статус не обновился");

        Subtask subtask1 = new Subtask("T1", "D1", StatusPriority.DONE, 1, now.plus(Duration.ofDays(1)), duration);
        tManager.addTask(subtask1);
        //Проверка изменения статуса на IN_PROGRESS (2 сабтаска, 1 из которых не завершенный)
        Assertions.assertEquals(StatusPriority.IN_PROGRESS, epic.getStatus(), "Статус не обновился");

        Subtask subtask2 = new Subtask("T1", "D1", StatusPriority.DONE, 1, now.plus(Duration.ofDays(2)), duration);
        subtask2.setId(subtask.getId());
        tManager.updateTask(subtask2);
        //Проверка изменения статуса на DONE (2 сабтаска, 2 из которых завершенные)
        Assertions.assertEquals(StatusPriority.DONE, epic.getStatus(), "Статус не обновился");

        tManager.deleteAllSubtasks();
        //Сабтаски удалены, статус должен быть NEW
        Assertions.assertEquals(StatusPriority.NEW, epic.getStatus(), "Статус не обновился");
    }

    @Test
    public void equalsByGettingById() {
        tManager.addTask(epic);
        Epic e = tManager.getEpicById(1);
        Epic savedEpic = new Epic("Name!", "Description!");
        savedEpic.setId(1);

        Assertions.assertEquals(e, savedEpic, "Не равны по Id");
    }

    @Test
    public void equalsByUpdating() {
        tManager.addTask(epic);
        tManager.addTask(subtask);
        Epic savedEpic = new Epic("Name!", "Description!");
        savedEpic.setId(1);

        tManager.updateTask(savedEpic);

        //Проверка по сравнению названий, описаний и статусов(из за метода equals())
        Assertions.assertTrue((savedEpic.getDescription()).equals(tManager.getEpicById(1).getDescription()));
        Assertions.assertTrue((savedEpic.getStatus()).equals(tManager.getEpicById(1).getStatus()));
        Assertions.assertTrue((savedEpic.getTitle()).equals(tManager.getEpicById(1).getTitle()));
    }

    @Test
    public void epicNotHavingDeletedSubtasksIds() {
        tManager.addTask(epic);
        tManager.addTask(subtask);

        ArrayList<Integer> epicLists = epic.getSubtasks();

        Assertions.assertTrue(epicLists.size() == 1, "Эпик не содежит все ID сабтасков");

        tManager.deleteSubtasksById(2);

        Assertions.assertTrue(epicLists.size() == 0, "Удаленный ID сабтаска все еще остался в списке сабтасков эпика");
    }
}