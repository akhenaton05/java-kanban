import managing.Managers;
import managing.TaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager tManager = Managers.getDefault();

        Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS);
        Task task2 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);

        Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");
        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");

        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 3);
        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 3);
        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.NEW, 3);

        tManager.addTask(task1);
        tManager.addTask(task2);

        tManager.addTask(epic1);
        tManager.addTask(epic2);

        tManager.addTask(subtask1);
        tManager.addTask(subtask2);
        tManager.addTask(subtask3);

        //Добавление и вывод истории
        tManager.getEpicById(3);
        System.out.println(tManager.getHistory());

        tManager.getTaskById(1);
        System.out.println(tManager.getHistory());

        tManager.getTaskById(2);
        System.out.println(tManager.getHistory());

        tManager.getEpicById(4);
        System.out.println(tManager.getHistory());

        tManager.getTaskById(2);
        System.out.println(tManager.getHistory());

        tManager.getSubtaskById(6);
        System.out.println(tManager.getHistory());

        tManager.getSubtaskById(5);
        System.out.println(tManager.getHistory());

        tManager.getSubtaskById(7);
        System.out.println(tManager.getHistory());

        tManager.getTaskById(1);
        System.out.println(tManager.getHistory());

        //Удаление подзадачи
        tManager.deleteSubtasksById(5);
        System.out.println(tManager.getHistory());

        //Удаление эпика с 2 подзадачами
        tManager.deleteEpicById(3);
        System.out.println(tManager.getHistory());
    }
}

