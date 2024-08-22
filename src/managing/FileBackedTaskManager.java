package managing;

import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String HEADER = "id,type,name,status,description,epicid";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        List<String> saveList = new ArrayList<>();
        saveList.add(HEADER);

        Stream.concat(showAllTasks().stream(), Stream.concat(showAllEpics().stream(), showAllSubtasks().stream()))
                .forEach(task -> saveList.add(toString(task)));

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
        sb.append(task.getStartTime() + ",");
        sb.append(task.getDuration().toMinutes() + ",");

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
        LocalDateTime startTime = LocalDateTime.parse(array[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(array[6]));

        if (typeName == TaskType.TASK) {
            Task tempTask = new Task(title, description, status, startTime, duration);
            tempTask.setId(id);
            return tempTask;
        } else if (typeName == TaskType.EPIC) {
            Epic tempEpic = new Epic(title, description);
            tempEpic.setId(id);
            tempEpic.setHaveSubtasks(true);
            tempEpic.setStatus(status);
            return tempEpic;
        } else if (typeName == TaskType.SUBTASK) {
            Subtask tempSubtask = new Subtask(title, description, status, Integer.parseInt(array[7]), startTime, duration);
            tempSubtask.setId(id);
            tempSubtask.setStartTime(startTime);
            tempSubtask.setDuration(duration);
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
        stringList.stream()
                .forEach(loadedTask -> loadedTaskList.add(fManager.fromString(loadedTask)));
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
                Epic epic = fManager.getEpics().get(((Subtask) task).getEpicId());
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
}
