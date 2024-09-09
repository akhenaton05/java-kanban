package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managing.InMemoryTaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static server.HttpTaskServer.DEFAULT_CHARSET;

class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(InMemoryTaskManager tManager) {
        super(tManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента.");
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
                String response = gson.toJson(tManager.showAllSubtasks());
                sendText(exchange, response);
            } else if (isNumeric(pathParts[2]) && pathParts.length == 3) {
                Subtask subtask = tManager.getSubtaskById(Integer.parseInt(pathParts[2]));
                if (!(tManager.showAllSubtasks().contains(subtask))) {
                    sendNotFound(exchange,"Подзадачи с ID " + pathParts[2] + " нету");
                    return;
                }
                String singleSubtask = gson.toJson(subtask);
                sendText(exchange, singleSubtask);
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
            Optional<Subtask> opSubtask = subtaskCreator(jsonBody);
            if (opSubtask.isEmpty()) {
                sendError(exchange, "Ошибка во время выполнения запроса");
                return;
            }
            Subtask subtaskToAdd = opSubtask.get();
            //Для обновления сабтаска
            if (pathParts.length == 3 && isNumeric(pathParts[2])) {
                subtaskToAdd.setId(Integer.parseInt(pathParts[2]));
                if (!dateCrossesChecker(subtaskToAdd)) {
                    sendHasInteractions(exchange, "Подзадача пересекается с существующими");
                    return;
                }
                if (!(tManager.getSubtasks().containsKey(subtaskToAdd.getId()))) {
                    sendHasInteractions(exchange, "Подзадача с айди " + pathParts[2] + " нету");
                    return;
                }
                tManager.updateTask(subtaskToAdd);
                writeResponse(exchange, "Подзадача обновлена");
                return;
            }
            if (!(tManager.getEpics().containsKey(subtaskToAdd.getEpicId()))) {
                sendHasInteractions(exchange, "epicID подзадачи указан неверно");
                return;
            }
            if (tManager.showAllSubtasks().contains(subtaskToAdd) || !dateCrossesChecker(subtaskToAdd)) {
                sendHasInteractions(exchange, "Подзадача пересекается с существующими");
                return;
            }
            tManager.addTask(subtaskToAdd);
            writeResponse(exchange, "Подзадача добавлена");
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        int subtaskId = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);

        try {
            if (!(tManager.getSubtasks().containsKey(subtaskId))) {
                sendNotFound(exchange,"Подзадачи с таким ID нету");
                return;
            }
            tManager.deleteSubtasksById(subtaskId);
            sendText(exchange, "Подзадача с ID " + subtaskId + " была удалена.");
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private static Optional<Subtask> subtaskCreator(String jsonTask) {
        try {
            Subtask subtask = gson.fromJson(jsonTask, Subtask.class);
            if (subtask.getStatus() == null) {
                return Optional.empty();
            }
            return Optional.of(subtask);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}