package managing;

import historytracking.HistoryManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    private int id = 1;

    //Получение списка задач
    @Override
    public ArrayList<Task> showAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка epics
    @Override
    public ArrayList<Epic> showAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    //Получение списка subtasks
    @Override
    public ArrayList<Subtask> showAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    //Получение Tasks.Task по ID
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    //Получение Tasks.Epic по ID
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    //Получение Tasks.Subtask по ID
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    //Создание Tasks.Task
    @Override
    public void addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        task.setId(id);
        id++;
        tasks.put(task.getId(), task);
    }

    //Создание Tasks.Epic
    @Override
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

    //Создание Tasks.Subtask
    @Override
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

    //Проверка Tasks.StatusPriority для Tasks.Epic
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
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //Обновление Tasks.Epic
    @Override
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

    //Обновление Tasks.Subtask
    @Override
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

    //Получение всех подзадач Tasks.Epic
    @Override
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
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Удаление всех Tasks.Epic
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Удаление всех Subtasks
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        //Замена статусов Tasks.Epic
        for (Epic epic : epics.values()) {
            epic.setSubtasks(null);
            epic.setStatus(StatusPriority.NEW);
            epic.setHaveSubtasks(false);
        }
    }

    //Удаление Tasks.Task по ID
    @Override
    public void deleteTaskById(int id) {
        Task temp = tasks.get(id);
        tasks.remove(id);
        temp.setId(-1);
        //Удаление из истории просмотра
        historyManager.remove(id);
    }

    //Удаление Tasks.Epic по ID
    @Override
    public void deleteEpicById(int id) {
        Epic target = epics.get(id);
        ArrayList<Integer> subtaskList = target.getSubtasks();
        //Проверка листов на null / empty
        if (subtaskList == null || subtaskList.isEmpty()) {
            epics.remove(id);
            target.setId(-1);
            //Удаление из истории просмотра
            historyManager.remove(id);
            return;
        }
        //Удаления сабтасков через итератор
        Iterator<Map.Entry<Integer, Subtask>> iterator = subtasks.entrySet().iterator();
        while (iterator.hasNext()) {
            int iterId = iterator.next().getKey();
            for (int targetId : subtaskList) {
                if (targetId == iterId) {
                    //Поменять айди удаляемых сабтасков
                    subtasks.get(targetId).setId(-1);
                    iterator.remove();
                    //Удаление из истории просмотра
                    historyManager.remove(targetId);
                }
            }
        }
        target.setId(-1);
        epics.remove(id);
        //Удаление из истории просмотра
        historyManager.remove(id);
    }

    //Удаление Subtasks по ID
    @Override
    public void deleteSubtasksById(int id) {
        int subId = subtasks.get(id).getEpicId();
        Subtask sub = subtasks.get(id);
        subtasks.remove(id);
        sub.setId(-1);
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
        //Удаление из истории просмотра
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

    @Override
    public int getCurrentId() {
        return this.id;
    }
}
