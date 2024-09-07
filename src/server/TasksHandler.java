package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managing.InMemoryTaskManager;
import tasks.Task;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static server.HttpTaskServer.DEFAULT_CHARSET;

class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(InMemoryTaskManager tManager) {
        super(tManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET:
                getHandler(exchange);
                break;
            case POST:
                postHandler(exchange);
                break;
            case DELETE:
                deleteHandler(exchange);
                break;
            case UNKNOWN:
                sendError(exchange, "Неизвестный запрос, проверьте правильность написания");
                break;
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (pathParts.length == 2) {
                String response = gson.toJson(tManager.showAllTasks());
                sendText(exchange, response);
            } else if (isNumeric(pathParts[2]) && pathParts.length == 3) {
                if (!(tManager.getTasks().containsKey(Integer.parseInt(pathParts[2])))) {
                    sendNotFound(exchange,"Задачи с таким ID нету");
                    return;
                }
                Task task = tManager.getTaskById(Integer.parseInt(pathParts[2]));
                String singleTask = gson.toJson(task);
                sendText(exchange, singleTask);
            }
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            InputStream bodyStream = exchange.getRequestBody();
            String jsonBody = new String(bodyStream.readAllBytes(), DEFAULT_CHARSET);
            Optional<Task> opTaskToAdd = taskCreator(jsonBody);
            if (opTaskToAdd.isEmpty()) {
                sendError(exchange, "Ошибка во время выполнения запроса");
                return;
            }
            Task taskToAdd = opTaskToAdd.get();
            //Для обновления таска
            if (pathParts.length == 3 && isNumeric(pathParts[2])) {
                taskToAdd.setId(Integer.parseInt(pathParts[2]));
                if (!dateCrossesChecker(taskToAdd)) {
                    sendHasInteractions(exchange, "Задача пересекается с существующими");
                    return;
                }
                if (!(tManager.getTasks().containsKey(taskToAdd.getId()))) {
                    sendHasInteractions(exchange, "Задачи с таким айди нету");
                    return;
                }
                tManager.updateTask(taskToAdd);
                sendText(exchange, "Задача обновлена");
                return;
            }
            if (tManager.showAllTasks().contains(taskToAdd) || !dateCrossesChecker(taskToAdd)) {
                sendHasInteractions(exchange, "Задача пересекается с существующими");
                return;
            }
            tManager.addTask(taskToAdd);
            sendText(exchange, "Задача добавлена");
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        int taskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);

        try {
            if (!(tManager.getTasks().containsKey(taskId))) {
                sendNotFound(exchange, "Задачи с таким ID нету");
                return;
            }
            tManager.deleteTaskById(taskId);
            sendText(exchange, "Задача с ID " + taskId + " бьыла удалена.");
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private static Optional<Task> taskCreator(String jsonTask) {
        Task task = gson.fromJson(jsonTask, Task.class);
        //Без StatusPriority сериализации не будет
        if (task == null || task.getStatus() == null) {
            return Optional.empty();
        }
        return Optional.of(task);
    }
}
