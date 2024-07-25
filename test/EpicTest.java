import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private TaskManager tManager = Managers.getDefault();
    private Epic epic = new Epic("Name", "Description");
    private Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 1);

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
        //Получаем список сабтасков для epic
        ArrayList<Subtask> subtasks = tManager.getEpicsSubtasks(1);
        //Проверка на отсутствите savedEpic в сабтасках epic (2й элемент savedEpic - должен быть null)
        Assertions.assertNull(subtasks.get(1));
    }

    @Test
    public void addNewEpic() {
        tManager.addTask(epic); // id = 1
        final int epicId = epic.getId();
        final Epic savedEpic = tManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден."); // Метод add доабвил epic в список
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = tManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    //Тесты с удалением Tasks.Epic из списка
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

        Subtask subtask1 = new Subtask("T1", "D1", StatusPriority.DONE, 1);
        tManager.addTask(subtask1);
        //Проверка изменения статуса на IN_PROGRESS (2 сабтаска, 1 из которых не завершенный)
        Assertions.assertEquals(StatusPriority.IN_PROGRESS, epic.getStatus(), "Статус не обновился");

        subtask.setStatus(StatusPriority.DONE);
        tManager.updateTask(subtask);
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
    public void deletedEpicsNotHavingOldIds() {
        tManager.addTask(epic);
        Epic temp = tManager.getEpicById(1);

        tManager.deleteEpicById(1);

        Assertions.assertTrue(temp.getId() == -1, "ID эпика не поменялся");

        //Проверка ID после удаления всех эпиков сразу
        tManager.addTask(epic);
        tManager.addTask(epic);
        tManager.addTask(epic);

        List<Epic> epicList = tManager.showAllEpics();

        tManager.deleteAllEpics();

        for (Epic tempEpic : epicList) {
            Assertions.assertTrue(tempEpic.getId() == -1, "ID эпика не поменялся после полной очистки списка");
        }
    }
}