package managing;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public interface TaskManager {
    //Получение списка задач
    ArrayList<Task> showAllTasks();

    //Получение списка epics
    ArrayList<Epic> showAllEpics();

    //Получение списка subtasks
    ArrayList<Subtask> showAllSubtasks();

    //Получение Tasks.Task по ID
    Task getTaskById(int id);

    //Получение Tasks.Epic по ID
    Epic getEpicById(int id);

    //Получение Tasks.Subtask по ID
    Subtask getSubtaskById(int id);

    //Создание Tasks.Task
    void addTask(Task task);

    //Создание Tasks.Epic
    void addTask(Epic epic);

    //Создание Tasks.Subtask
    void addTask(Subtask subtask);

    //Обновление Таска
    void updateTask(Task task);

    //Обновление Tasks.Epic
    void updateTask(Epic epic);

    //Обновление Tasks.Subtask
    void updateTask(Subtask subtask);

    //Получение всех подзадач Tasks.Epic
    ArrayList<Subtask> getEpicsSubtasks(int epidId);

    //Удаление всех задач
    void deleteAllTasks();

    //Удаление всех Tasks.Epic
    void deleteAllEpics();

    //Удаление всех Subtasks
    void deleteAllSubtasks();

    //Удаление Tasks.Task по ID
    void deleteTaskById(int id);

    //Удаление Tasks.Epic по ID
    void deleteEpicById(int id);

    //Удаление Subtasks по ID
    void deleteSubtasksById(int id);

    //Сортировка по дате начала задачи
    TreeSet<Task> getPrioritizedTasks();

    List<Task> getHistory();

    int getCurrentId();
}
