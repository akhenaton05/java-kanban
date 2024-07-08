import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //InMemoryTaskManager tManager = new InMemoryTaskManager();
        TaskManager tManager = Managers.getDefault();

        Task task1 = new Task("Сделать уроки", "Math", StatusPriority.IN_PROGRESS);

        Task task2 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);

        Task task3 = new Task("DADWADWADAWDAWD", "DETECTADAWDAWDAWDIVE", StatusPriority.DONE);
        //task3.setId(2);
        Task task4 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task5 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task6 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task7 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task8 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task9 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task10 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);
        Task task11 = new Task("Read the book", "DETECTIVE", StatusPriority.NEW);

        Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");

        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");

        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 12);

        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 12);

        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.NEW, 13);


        tManager.addTask(task1);
        tManager.addTask(task2);
        tManager.addTask(task3);
        tManager.addTask(task4);
        tManager.addTask(task5);
        tManager.addTask(task6);
        tManager.addTask(task7);
        tManager.addTask(task8);
        tManager.addTask(task9);
        tManager.addTask(task10);
        tManager.addTask(task11);
        tManager.addTask(epic1);
        tManager.addTask(epic2);
        tManager.addTask(subtask1);
        tManager.addTask(subtask2);
        tManager.addTask(subtask3);
        System.out.println(tManager.showAllTasks());
        System.out.println(tManager.showAllEpics());
        System.out.println(tManager.showAllSubtasks());
        tManager.getTaskById(1);
        tManager.getTaskById(2);
        tManager.getTaskById(3);
        tManager.getTaskById(4);
        tManager.getTaskById(5);
        tManager.getTaskById(6);
        tManager.getTaskById(7);
        tManager.getTaskById(8);
        tManager.getTaskById(9);
        tManager.getTaskById(10);
        tManager.getTaskById(11);
        tManager.getEpicById(12);
        tManager.getEpicById(13);
        tManager.getSubtaskById(14);
        tManager.getSubtaskById(15);
        tManager.getSubtaskById(16);
        System.out.println(tManager.getHistory());
        System.out.println(tManager.getHistory().size());




        /*Epic epic1 = new Epic("Сдача проекта", "Доделать проект по английскому");

        Epic epic2 = new Epic("Проверка снаряжения", "Просмотреть все снаряжение");

        Subtask subtask1 = new Subtask("", "Доделать проект по английскому", StatusPriority.DONE, 3);

        Subtask subtask2 = new Subtask("", "Выучить пи", StatusPriority.IN_PROGRESS, 3);

        Subtask subtask3 = new Subtask("", "Погулять", StatusPriority.NEW, 4);*/

        /*Subtask subtask4 = new Subtask("", "Пи выучено", StatusPriority.DONE, 3);
        subtask4.setId(6);
        subtask4.setEpicId(3);
        subtask4.setStatus(StatusPriority.DONE);

        Subtask subtask5 = new Subtask("", "Погуляно", StatusPriority.DONE, 4);
        subtask5.setId(7);
        subtask5.setDescription("Погуляно");
        subtask5.setEpicId(4);
        subtask5.setStatus(StatusPriority.DONE);*/

        //тест
        /*tManager.addTask(task1);
        tManager.addTask(task2);

        tManager.addTask(epic1);
        tManager.addTask(epic2);

        tManager.addTask(subtask1);
        tManager.addTask(subtask2);
        tManager.addTask(subtask3);

        System.out.println(tManager.showAllTasks());
        System.out.println(tManager.showAllEpics());
        System.out.println(tManager.showAllSubtasks());

        tManager.updateTask(subtask4);
        tManager.updateTask(subtask5);
        tManager.updateTask(task3);

        System.out.println("/////////////////////////////////////////////////////");
        System.out.println(tManager.showAllTasks());
        System.out.println(tManager.showAllEpics());
        System.out.println(tManager.showAllSubtasks());

        tManager.deleteTaskById(2);
        //tManager.deleteEpicById(4);
        //tManager.deleteSubtasksById(7);

        System.out.println("/////////////////////////////////////////////////////");
        System.out.println(tManager.showAllTasks());
        System.out.println(tManager.showAllEpics());
        System.out.println(tManager.showAllSubtasks());*/




        /*int cmd;

        while (true) {
            printMenu();
            cmd = scanner.nextInt();
            if (cmd == 1) {
                System.out.println(tManager.showAllTasks());
                System.out.println(tManager.showAllEpics());
                System.out.println(tManager.showAllSubtasks());
            } else if (cmd == 2) {
                tManager.deleteAllSubtasks();
            } else if (cmd == 3) {
                System.out.println("Введите ID задачи");
                int id = scanner.nextInt();
                System.out.println(tManager.getTaskById(id));
            } else if (cmd == 4) {
                tManager.addTask(task1);
                tManager.addTask(task2);
                tManager.addTask(epic1);
                tManager.addTask(epic2);
                tManager.addTask(subtask1);
                tManager.addTask(subtask2);
                tManager.addTask(subtask3);
            } else if (cmd == 5) {
                tManager.updateTask(subtask4);
                tManager.updateTask(subtask5);
                //tManager.updateTask(epic2);
                //tManager.updateTask(task2);
            } else if (cmd == 6) {
                System.out.println("Введите ID Epic");
                int id = scanner.nextInt();
                System.out.println(tManager.getEpicsSubtasks(id));
            } else if (cmd == 7) {
                System.out.println("Введите ID Epic");
                int id = scanner.nextInt();
                tManager.deleteSubtasksById(id);
            } else if (cmd == 0) {
                System.out.println("Quitting...");
                break;
            }
        }*/


    }


    public static void printMenu() {
        System.out.println("Выберите пункт меню");
        System.out.println("1. Получение списка всех задач.");
        System.out.println("2. Удаление всех задач.");
        System.out.println("3. Получение по идентификатору.");
        System.out.println("4. Создание задач.");
        System.out.println("5. Обновление задач.");
        System.out.println("6. Показать эпик и его сабтаск.");
        System.out.println("7. Удаление по ID");
        System.out.println("0. Выход.");
    }
}
