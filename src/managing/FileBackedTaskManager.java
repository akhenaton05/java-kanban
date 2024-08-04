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
    private static final String HEADER = "id,type,name,status,description,epicid";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        List<String> saveList = new ArrayList<>();
        saveList.add(HEADER);
        List<Task> taskList = super.showAllTasks();
        for (Task task : taskList) {
            saveList.add(toString(task));
        }
        List<Epic> epicList = super.showAllEpics();
        for (Epic epic : epicList) {
            saveList.add(toString(epic));
        }
        List<Subtask> subtaskList = super.showAllSubtasks();
        for (Subtask subtask : subtaskList) {
            saveList.add(toString(subtask));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String s : saveList) {
                bw.write(s + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("IOException caught", e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId() + ",");
        sb.append(task.getType() + ",");
        sb.append(task.getTitle() + ",");
        sb.append(task.getStatus() + ",");
        sb.append(task.getDescription() + ",");

        if (task.getType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpicId() + ",");
        }

        return sb.toString();
    }

    private Task fromString(String value) {
        String[] array = value.split(",");
        int id = Integer.parseInt(array[0]);
        TaskType typeName = TaskType.valueOf(array[1]);
        String title = array[2];
        StatusPriority status = StatusPriority.valueOf(array[3]);
        String description = array[4];

        if (typeName == TaskType.TASK) {
            Task tempTask = new Task(title, description, status);
            tempTask.setId(id);
            return tempTask;
        } else if (typeName == TaskType.EPIC) {
            Epic tempEpic = new Epic(title, description);
            tempEpic.setId(id);
            tempEpic.setHaveSubtasks(true);
            tempEpic.setStatus(status);
            return tempEpic;
        } else if (typeName == TaskType.SUBTASK) {
            Subtask tempSubtask = new Subtask(title, description, status, Integer.parseInt(array[5]));
            tempSubtask.setId(id);
            return tempSubtask;
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
            throw new ManagerSaveException("IOException caught", e);
        }
        stringList.remove(0);
        List<Task> loadedTaskList = new ArrayList<>();
        for (String s : stringList) {
            loadedTaskList.add(fManager.fromString(s));
        }
        int maxId = 0;
        for (Task task : loadedTaskList) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
            if (task.getType() == TaskType.TASK) {
                HashMap<Integer, Task> map = fManager.getTasks();
                map.put(task.getId(), task);
            } else if (task.getType() == TaskType.EPIC) {
                HashMap<Integer, Epic> map = fManager.getEpics();
                map.put(task.getId(), (Epic) task);
            } else {
                HashMap<Integer, Subtask> map = fManager.getSubtasks();
                Epic epic = fManager.getEpics().get(((Subtask)task).getEpicId());
                ArrayList<Integer> subIds = epic.getSubtasks();
                subIds.add(task.getId());
                map.put(task.getId(), (Subtask) task);
            }
        }
        fManager.setCurrentId(maxId + 1);
        return fManager;
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

        System.out.println("Проверка идентификатора экземпляров Manager: " + (newManager.getCurrentId() == fManager.getCurrentId()));

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
        System.out.println("Проверка тасков экземпляров Manager: " + taskResult);

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
        System.out.println("Проверка эпиков экземпляров Manager: " + epicResult);

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
        System.out.println("Проверка сабтасков экземпляров Manager: " + subtaskResult);
    }
}
