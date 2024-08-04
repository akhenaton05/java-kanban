package managing;

import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        List<String> saveList = new ArrayList<>(List.of("id,type,name,status,description,epicid"));
        List<Task> taskList = super.showAllTasks();
        if (!taskList.isEmpty()) {
            for (Task task : taskList) {
                saveList.add(toString(task));
            }
        }
        List<Epic> epicList = super.showAllEpics();
        if (!epicList.isEmpty()) {
            for (Epic epic : epicList) {
                saveList.add(toString(epic));
            }
        }
        List<Subtask> subtaskList = super.showAllSubtasks();
        if (!subtaskList.isEmpty()) {
            for (Subtask subtask : subtaskList) {
                saveList.add(toString(subtask));
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String s : saveList) {
                bw.write(s + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("IOException caught");
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId() + ",");
        sb.append(TaskType.TASK + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getStatus() + ",");
        sb.append(task.getDescription() + ",");
        return sb.toString();
    }

    private String toString(Epic task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId() + ",");
        sb.append(TaskType.EPIC + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getStatus() + ",");
        sb.append(task.getDescription() + ",");
        return sb.toString();
    }

    private String toString(Subtask task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId() + ",");
        sb.append(TaskType.SUBTASK + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getStatus() + ",");
        sb.append(task.getDescription() + ",");
        sb.append(task.getEpicId() + ",");
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] array = value.split(",");
        if (TaskType.valueOf(array[1]) == TaskType.TASK) {
            if (StatusPriority.valueOf(array[3]) == StatusPriority.NEW) {
                Task tempTask = new Task(array[2], array[4], StatusPriority.NEW);
                tempTask.setId(Integer.parseInt(array[0]));
                return tempTask;
            } else if (StatusPriority.valueOf(array[3]) == StatusPriority.IN_PROGRESS) {
                Task tempTask = new Task(array[2], array[4], StatusPriority.IN_PROGRESS);
                tempTask.setId(Integer.parseInt(array[0]));
                return tempTask;
            } else {
                Task tempTask = new Task(array[2], array[4], StatusPriority.DONE);
                tempTask.setId(Integer.parseInt(array[0]));
                return tempTask;
            }
        } else if (TaskType.valueOf(array[1]) == TaskType.EPIC) {
            if (StatusPriority.valueOf(array[3]) == StatusPriority.NEW) {
                Epic tempEpic = new Epic(array[2], array[4]);
                tempEpic.setStatus(StatusPriority.NEW);
                tempEpic.setId(Integer.parseInt(array[0]));
                tempEpic.setHaveSubtasks(true);
                return tempEpic;
            } else if (StatusPriority.valueOf(array[3]) == StatusPriority.IN_PROGRESS) {
                Epic tempEpic = new Epic(array[2], array[4]);
                tempEpic.setStatus(StatusPriority.IN_PROGRESS);
                tempEpic.setId(Integer.parseInt(array[0]));
                tempEpic.setHaveSubtasks(true);
                return tempEpic;
            } else {
                Epic tempEpic = new Epic(array[2], array[4]);
                tempEpic.setStatus(StatusPriority.DONE);
                tempEpic.setId(Integer.parseInt(array[0]));
                tempEpic.setHaveSubtasks(true);
                return tempEpic;
            }
        } else {
            if (StatusPriority.valueOf(array[3]) == StatusPriority.NEW) {
                Subtask tempSubtask = new Subtask(array[2], array[4], StatusPriority.NEW, Integer.parseInt(array[5]));
                tempSubtask.setId(Integer.parseInt(array[0]));
                return tempSubtask;
            } else if (StatusPriority.valueOf(array[3]) == StatusPriority.IN_PROGRESS) {
                Subtask tempSubtask = new Subtask(array[2], array[4], StatusPriority.IN_PROGRESS, Integer.parseInt(array[5]));
                tempSubtask.setId(Integer.parseInt(array[0]));
                return tempSubtask;
            } else if (StatusPriority.valueOf(array[3]) == StatusPriority.DONE) {
                Subtask tempSubtask = new Subtask(array[2], array[4], StatusPriority.DONE, Integer.parseInt(array[5]));
                tempSubtask.setId(Integer.parseInt(array[0]));
                return tempSubtask;
            }
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fManager = new FileBackedTaskManager(file);
        List<String> stringList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                stringList.add(br.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("IOException caught");
        }
        stringList.remove(0);
        List<Task> loadedTaskList = new ArrayList<>();
        for (String s : stringList) {
            loadedTaskList.add(fManager.fromString(s));
        }
        for (Task task : loadedTaskList) {
            if (task.getClass() == Task.class) {
                HashMap<Integer, Task> map = fManager.getTasks();
                map.put(task.getId(), task);
            }
            if (task.getClass() == Epic.class) {
                HashMap<Integer, Epic> map = fManager.getEpics();
                map.put(task.getId(), (Epic) task);
            }
            if (task.getClass() == Subtask.class) {
                HashMap<Integer, Subtask> map = fManager.getSubtasks();
                map.put(task.getId(), (Subtask) task);
            }
        }
        fManager.setEpicsWithSubtasks();
        return fManager;
    }

    private void setEpicsWithSubtasks() {
        HashMap<Integer, Epic> epicMap = this.getEpics();
        HashMap<Integer, Subtask> subtaskMap = this.getSubtasks();
        for (Epic epic : epicMap.values()) {
            ArrayList<Integer> epicsSubs = epic.getSubtasks();
            for (Subtask subtask : subtaskMap.values()) {
                if (epic.getId() == subtask.getEpicId()) {
                    epicsSubs.add(subtask.getId());
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addTask(Epic epic) {
        super.addTask(epic);
        save();
    }

    @Override
    public void addTask(Subtask subtask) {
        super.addTask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateTask(Epic epic) {
        super.updateTask(epic);
        save();
    }

    @Override
    public void updateTask(Subtask subtask) {
        super.updateTask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtasksById(int id) {
        super.deleteSubtasksById(id);
        save();
    }

    public static void main(String[] args) throws IOException {
        Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS);
        Task task2 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");
        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");
        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 3);
        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 3);
        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.DONE, 4);

        File tempFile = File.createTempFile("test", ".txt");
        FileBackedTaskManager fManager = new FileBackedTaskManager(tempFile);

        fManager.addTask(task1);
        fManager.addTask(task2);
        fManager.addTask(epic1);
        fManager.addTask(epic2);
        fManager.addTask(subtask1);
        fManager.addTask(subtask2);
        fManager.addTask(subtask3);

        System.out.println(Files.readString(tempFile.toPath()));

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println(newManager.showAllTasks());
        System.out.println(newManager.showAllEpics());
        System.out.println(newManager.showAllSubtasks());
    }
}
