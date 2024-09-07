package managing;

import historytracking.HistoryManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

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
        historyManager.add(task);
        return task;
    }

    //Получение Tasks.Epic по ID
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    //Получение Tasks.Subtask по ID
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //Создание Tasks.Task
    @Override
    public void addTask(Task task) {
        if (tasks.containsKey(task.getId()) || !dateCrossesChecker(task)) {
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
        if (subtasks.containsKey(subtask.getId()) || !dateCrossesChecker(subtask)) {
            return;
        }
        subtask.setId(id);
        id++;
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.setHaveSubtasks(true);
        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        subtaskIds.add(subtask.getId());
        epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
        checkEpicStatusPriority();
    }

    //Проверка Tasks.StatusPriority для Tasks.Epic
    private void checkEpicStatusPriority() {
        for (Epic epic : epics.values()) {
            if (epic.isHaveSubtasks()) {
                ArrayList<Integer> subtasksIds = epic.getSubtasks();
                ArrayList<Subtask> subtaskList = new ArrayList<>();
                subtasksIds.stream()
                        .forEach(id -> subtaskList.add(subtasks.get(id)));
                //Рассчет стартовой даты эпика
                LocalDateTime maxTime = LocalDateTime.MAX;
                for (Subtask subtask : subtaskList) {
                    if (subtask.getStartTime().isBefore(maxTime)) {
                        maxTime = subtask.getStartTime();
                    }
                }
                epic.setStartTime(maxTime);
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
        if (dateCrossesChecker(task)) {
            tasks.put(task.getId(), task);
        }
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
        if (dateCrossesChecker(subtask)) {
            Epic epic = epics.get(subtask.getEpicId());
            //Смена продолжительности эпика
            epic.setDuration(epic.getDuration().minus(subtasks.get(subtask.getId()).getDuration()).plus(subtask.getDuration()));
            subtasks.put(subtask.getId(), subtask);
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
    }

    //Получение всех подзадач Tasks.Epic
    @Override
    public ArrayList<Subtask> getEpicsSubtasks(int epidId) {
        ArrayList<Subtask> subList = new ArrayList<>();
        if (epics.get(epidId).getSubtasks() == null) {
            return null;
        }
        subtasks.values().stream()
                .filter(sub -> sub.getEpicId() == epidId)
                .forEach(sub -> subList.add(sub));
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
        epics.values().stream()
                .forEach(epic -> {
                    epic.setSubtasks(null);
                    epic.setStatus(StatusPriority.NEW);
                    epic.setHaveSubtasks(false);
                    epic.setDuration(Duration.ZERO);
                    epic.setStartTime(null);
                    epic.setEndTime(null);
                });
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
            epic.setStartTime(LocalDateTime.MAX);
            epic.setDuration(Duration.ZERO);
        }
        checkEpicStatusPriority();
        epic.setDuration(epic.getDuration().minus(sub.getDuration()));
        //Удаление из истории просмотра
        historyManager.remove(id);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> sortedList = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return -1;
            } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        });
        Stream.concat(showAllTasks().stream(), showAllSubtasks().stream())
                .filter(task -> (task.getStartTime() != null))
                .forEach(task -> sortedList.add(task));
        return sortedList;
    }

    private boolean dateCrossesChecker(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        boolean isNotCrossing = getPrioritizedTasks().stream()
                .allMatch(t -> (taskStart.isAfter(t.getEndTime())) || taskEnd.isBefore(t.getStartTime()));
        return isNotCrossing;
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

    public void setCurrentId(int id) {
        this.id = id;
    }
}
