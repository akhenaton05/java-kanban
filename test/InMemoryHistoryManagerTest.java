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
        for (int i = 1; i < 15; i++) {
            tManager.addTask(new Task("T", "D", StatusPriority.DONE));
        }

        for (int i = 1; i < 15; i++) {
            tManager.getTaskById(i);
        }

        Assertions.assertTrue(tManager.getHistory().size() == 14, "В списке меньше 14 задач");
        Assertions.assertEquals(tManager.getHistory().get(0).getId(), 1, "Задачи хранятся в неправильном порядке");
    }

    @Test
    public void duplicatedTasksOverwritten() {
        for (int i = 1; i < 6; i++) {
            tManager.addTask(new Task("T", "D", StatusPriority.DONE));
        }

        for (int i = 1; i < 6; i++) {
            tManager.getTaskById(i);
        }

        //Добавляем таск с 4 id заного
        tManager.getTaskById(4);
        List<Task> testList = tManager.getHistory();

        //Проверяем, что теперь последний элемент в истории - элемент с айди 4(перезаписанный)
        Assertions.assertTrue(testList.get(testList.size() - 1).getId() == 4, "Таск не был перезаписан");

        //Добавляем голову узла
        tManager.getTaskById(1);
        List<Task> newTestList = tManager.getHistory();

        //Проверяем, что теперь последний элемент в истории - элемент с айди 1(перезаписанный)
        Assertions.assertTrue(newTestList.get(testList.size() - 1).getId() == 1, "Таск не был перезаписан");
    }

    @Test
    public void taskDeletedFromHistoryList() {
        //Добавление в список
        for (int i = 1; i < 6; i++) {
            tManager.addTask(new Task("T", "D", StatusPriority.DONE));
        }

        //Добавление в историю просмотров
        for (int i = 1; i < 6; i++) {
            tManager.getTaskById(i);
        }

        Task target = tManager.getTaskById(3);
        tManager.deleteTaskById(3);
        List<Task> historyList = tManager.getHistory();

        //Проверяем, что размер списка изменился после удаления таска
        Assertions.assertTrue(historyList.size() == 4, "Размер списка истории не изменился");
        //Проверяем, что удаленный таск отсутствует в списке с историей
        Assertions.assertFalse(historyList.contains(target), "Удаленный таск в списке с историей");
    }

    @Test
    public void addDeleteOperationsKeepingRightOrder() {
        //Добавление в список
        for (int i = 1; i < 6; i++) {
            tManager.addTask(new Task("T", "D", StatusPriority.DONE));
        }

        tManager.getTaskById(4);
        tManager.getTaskById(2);
        tManager.deleteTaskById(4);
        tManager.getTaskById(3);
        tManager.getTaskById(2);
        tManager.getTaskById(1);
        tManager.deleteTaskById(2);
        tManager.getTaskById(5);

        List<Task> historyList = tManager.getHistory();

        //Проверяем порядок элементов после операций удаления\перезаписи
        Assertions.assertTrue(historyList.get(historyList.size() - 1).getId() == 5, "Порядом не соблюден. Последний элемент не совпадает");
        Assertions.assertTrue(historyList.get(0).getId() == 3, "Порядом не соблюден. Первый элемент не совпадает");
        Assertions.assertTrue(historyList.get(1).getId() == 1, "Порядом не соблюден. 2й элемент не совпадает");

    }

    @Test
    public void epicAndHisSubtasksDeletedFromHistoryList() {
        tManager.addTask(new Epic("sdass", "s"));
        tManager.addTask(new Subtask("a", "s", StatusPriority.DONE, 1));
        tManager.addTask(new Subtask("s", "e", StatusPriority.DONE, 1));

        tManager.getEpicById(1);
        tManager.getSubtaskById(2);
        tManager.getSubtaskById(3);

        List<Task> beforeList = tManager.getHistory();

        //Проверяем что лист с историей записал эпик и 2 сабтаска
        Assertions.assertTrue(beforeList.size() == 3, "Запись истории не осуществлена");

        tManager.deleteEpicById(1);

        //Проверяем, что после удаления эпика весь лист пустой
        Assertions.assertTrue(tManager.getHistory().isEmpty(), "Сабтаски эпика не были удалены");
    }

}