import java.util.*;

public interface TaskManager {
    //Получение списка задач
    ArrayList<Task> showAllTasks();

    //Получение списка epics
    ArrayList<Epic> showAllEpics();

    //Получение списка subtasks
    ArrayList<Subtask> showAllSubtasks();

    //Получение Task по ID
    Task getTaskById(int id);

    //Получение Epic по ID
    Epic getEpicById(int id);

    //Получение Subtask по ID
    Subtask getSubtaskById(int id);

    //Создание Task
    void addTask(Task task);

    //Создание Epic
    void addTask(Epic epic);

    //Создание Subtask
    void addTask(Subtask subtask);

    //Обновление Таска
    void updateTask(Task task);

    //Обновление Epic
    void updateTask(Epic epic);

    //Обновление Subtask
    void updateTask(Subtask subtask);

    //Получение всех подзадач Epic
    ArrayList<Subtask> getEpicsSubtasks(int epidId);

    //Удаление всех задач
    void deleteAllTasks();

    //Удаление всех Epic
    void deleteAllEpics();

    //Удаление всех Subtasks
    void deleteAllSubtasks();

    //Удаление Task по ID
    void deleteTaskById(int id);

    //Удаление Epic по ID
    void deleteEpicById(int id);

    //Удаление Subtasks по ID
    void deleteSubtasksById(int id);

    List<Task> getHistory();

    int getCurrentId();
}
