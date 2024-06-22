import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    private int id = 1;

    //Получение списка задач
    public ArrayList<Task> showAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка epics
    public ArrayList<Epic> showAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    //Получение списка subtasks
    public ArrayList<Subtask> showAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    //Получение Task по ID
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    //Получение Epic по ID
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    //Получение Subtask по ID
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    //Создание Task
    public void addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        task.setId(id);
        id++;
        tasks.put(task.getId(), task);
    }

    //Создание Epic
    public void addTask(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        epic.setId(id);
        id++;
        //Проверка на наличие subtasks
        if (!epic.isHaveSubtasks()) {
            epic.setStatus(StatusPriority.NEW);
        }
        epics.put(epic.getId(), epic);
    }

    //Создание Subtask
    public void addTask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }
        subtask.setId(id);
        id++;
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.setHaveSubtasks(true);
        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        subtaskIds.add(subtask.getId());
        checkEpicStatusPriority();
    }

    //Проверка StatusPriority для Epic
    private void checkEpicStatusPriority() {
        for (Epic epic : epics.values()) {
            if (epic.isHaveSubtasks()) {
                ArrayList<Integer> subtasksIds = epic.getSubtasks();
                ArrayList<Subtask> subtaskList = new ArrayList<>();
                for (Integer id : subtasksIds) {
                    subtaskList.add(subtasks.get(id));
                }
                //Проверка по статусу NEW
                boolean allStatusNew = true;
                for (Subtask tempSubtask : subtaskList) {
                    if (!(tempSubtask.getStatus().equals(StatusPriority.NEW))) {
                        allStatusNew = false;
                    }
                }
                if (allStatusNew) {
                    epic.setStatus(StatusPriority.NEW);
                }
                //Проверка по статусу DONE
                boolean allStatusDone = true;
                for (Subtask tempSubtask : subtaskList) {
                    if (!(tempSubtask.getStatus().equals(StatusPriority.DONE))) {
                        allStatusDone = false;
                    }
                }
                if (allStatusDone) {
                    epic.setStatus(StatusPriority.DONE);
                }
                //В любом другом слйчае - IN_PROGRESS
                if (!allStatusNew && !allStatusDone) {
                    epic.setStatus(StatusPriority.IN_PROGRESS);
                }
            }
        }
    }

    //Обновление Таска
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //Обновление Epic
    public void updateTask(Epic epic) {
        //Проверка на наличие сабтасков для обновляемого эпика
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic.isHaveSubtasks()) {
            ArrayList<Integer> subList = oldEpic.getSubtasks();
            epics.put(epic.getId(), epic);
            epics.get(epic.getId()).setSubtasks(subList);
        } else {
            epics.put(epic.getId(), epic);
        }
        checkEpicStatusPriority();
    }

    //Обновление Subtask
    public void updateTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        //Проверка на наличие сабтасков у эпика
        //Для списка со значением null
        if (epic.getSubtasks() == null) {
            ArrayList<Integer> subList = new ArrayList<>();
            subList.add(subtask.getId());
            epic.setSubtasks(subList);
        }
        //Для пустого списка
        if (epic.getSubtasks().isEmpty()) {
            ArrayList<Integer> subList = new ArrayList<>();
            subList.add(subtask.getId());
            epic.setSubtasks(subList);
        }
        checkEpicStatusPriority();
    }

    //Получение всех подзадач Epic
    public ArrayList<Subtask> getEpicsSubtasks(int epidId) {
        Epic epic = epics.get(epidId);
        ArrayList<Subtask> subList = new ArrayList<>();
        if (epic.getSubtasks() == null) {
            return null;
        }
        for (int id : epic.getSubtasks()) {
            subList.add(subtasks.get(id));
        }
        return subList;
    }

    //Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Удаление всех Epic
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear(); //Также решил удалить все сабтаски, т.к. без эпиков в них нету смысла
    }

    //Удаление всех Subtasks
    public void deleteAllSubtasks() {
        subtasks.clear();
        //Замена статусов Epic
        for (Epic epic : epics.values()) {
            epic.setSubtasks(null);
            epic.setStatus(StatusPriority.NEW);
            epic.setHaveSubtasks(false);
        }
    }

    //Удаление Task по ID
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //Удаление Epic по ID
    public void deleteEpicById(int id) {
        Epic target = epics.get(id);
        ArrayList<Integer> subtaskList = target.getSubtasks();
        //Проверка листов на null / empty
        if (subtaskList == null || subtaskList.isEmpty()) {
            epics.remove(id);
            return;
        }
        //Не нашел простого способа удалить сабтаски эпика из хэшмапы без ConcurrentError, сделал через итератор, но его еще не проходили по курсу
        Iterator<Map.Entry<Integer, Subtask>> iterator = subtasks.entrySet().iterator();
        while (iterator.hasNext()) {
            int iterId = iterator.next().getKey();
            for (int targetId : subtaskList) {
                if (targetId == iterId) {
                    iterator.remove();
                }
            }
        }
        epics.remove(id);
    }

    //Удаление Subtasks по ID
    public void deleteSubtasksById(int id) {
        int subId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        //Удаление subtask из списка эпика
        Epic epic = epics.get(subId);
        if (epic.isHaveSubtasks()) {
            ArrayList<Integer> subList = epic.getSubtasks();
            subList.remove(subList.indexOf(id));
            epic.setSubtasks(subList);
        }
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(StatusPriority.NEW);
            epic.setHaveSubtasks(false);
        }
        checkEpicStatusPriority();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashMap<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
