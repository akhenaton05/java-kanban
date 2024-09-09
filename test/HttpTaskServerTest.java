import com.google.gson.Gson;
import managing.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.BaseHttpHandler;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    InMemoryTaskManager tManager = (InMemoryTaskManager) Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(tManager);
    Gson gson = BaseHttpHandler.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void BeforeEach() throws IOException {
        tManager.deleteAllTasks();
        tManager.deleteAllEpics();
        tManager.deleteAllSubtasks();
        server.createServer();
        server.start();
    }

    @AfterEach
    public void AfterEach() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = tManager.showAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = tManager.showAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");


        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Task> newTasksFromManager = tManager.showAllTasks();

        assertNotNull(newTasksFromManager, "Задачи не возвращаются");
        assertEquals(0, newTasksFromManager.size(), "Задача не была удалена");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskToUpdate = new Task("New task", "Testing New task",
                StatusPriority.NEW, LocalDateTime.now().plus(Duration.ofDays(4)),  Duration.ofMinutes(5));
        String updatedJson = gson.toJson(taskToUpdate);

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(updatedJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Task> tasksFromManager = tManager.showAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Задача не была обновлена");
        assertEquals("New task", tasksFromManager.getFirst().getTitle(), "Некорректное имя обновленной задачи");
    }

    @Test
    public void testAddingTaskErrors() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //Пытаемся добавить таск с пересекающийся датой
        Task taskToAdd = new Task("New task", "Testing New task", StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String updatedJson = gson.toJson(taskToAdd);

        URI url2 = URI.create("http://localhost:8080/tasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(updatedJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        //Пытаемся получить таск с несуществующим ID
        URI url3 = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());

        //Empty Task from Json
        String taskJson2 = "";
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response4.statusCode());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = tManager.showAllEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", epicsFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        Subtask subtask = new Subtask("Name", "Description", StatusPriority.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(1));
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        //Добавляем эпик
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        //Добавляем сабтаск для эпика
        URI url2 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Subtask> subtasksFromManager = tManager.showAllSubtasks();

        assertNotNull(subtasksFromManager, "Сабтаски не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Name", subtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testPrioritizedList() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                StatusPriority.NEW, LocalDateTime.now().plus(Duration.ofDays(111)),  Duration.ofMinutes(5));
        Task task2 = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now().minus(Duration.ofDays(1)),  Duration.ofMinutes(5));
        Task task3 = new Task("Test 3", "Testing task 3",
                StatusPriority.NEW, LocalDateTime.now().plus(Duration.ofDays(3)),  Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);
        String taskJson3 = gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();
        //Добавляем 3 таска
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response3.statusCode());

        URI url2 = URI.create("http://localhost:8080/prioritized");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2)
                .GET()
                .build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response4.statusCode());

        TreeSet<Task> pList = tManager.getPrioritizedTasks();

        assertEquals(3, pList.size());
        assertEquals("Test 2", pList.getFirst().getTitle());
        assertEquals("Test 1", pList.getLast().getTitle());
    }

    @Test
    public void testHistoryList() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                StatusPriority.NEW, LocalDateTime.now(),  Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task2 = new Task("Test 2", "Testing task 2",
                StatusPriority.NEW, LocalDateTime.now().plus(Duration.ofDays(1)),  Duration.ofMinutes(5));
        String taskJson2 = gson.toJson(task2);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        client.send(request3, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();
        client.send(request4, HttpResponse.BodyHandlers.ofString());

        List<Task> history = tManager.getHistory();

        assertEquals(2, history.size());
        assertEquals("Test 2", history.getFirst().getTitle());
        assertEquals("Test 1", history.getLast().getTitle());
    }
}
