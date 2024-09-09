package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managing.InMemoryTaskManager;
import tasks.Epic;
import tasks.StatusPriority;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static server.HttpTaskServer.DEFAULT_CHARSET;

class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(InMemoryTaskManager tManager) {
        super(tManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента.");
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
            case DELETE, EPICS_SUBTASKS:
                deleteSublistHandler(exchange, endpoint);
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
                String response = gson.toJson(tManager.showAllEpics());
                sendText(exchange, response);
            } else if (isNumeric(pathParts[2]) && pathParts.length == 3) {
                if (!(tManager.getEpics().containsKey(Integer.parseInt(pathParts[2])))) {
                    sendNotFound(exchange,"Эпика с таким ID нету");
                    return;
                }
                Epic epic = tManager.getEpicById(Integer.parseInt(pathParts[2]));
                String singleTask = gson.toJson(epic);
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
            Optional<Epic> opEpicToAdd = epicCreator(jsonBody);
            if (opEpicToAdd.isEmpty()) {
                sendError(exchange, "Ошибка во время выполнения запроса");
                return;
            }
            Epic epicToAdd = opEpicToAdd.get();
            if (pathParts.length == 3 && isNumeric(pathParts[2])) {
                if (!(tManager.getEpics().containsKey(Integer.parseInt(path.split("/")[2])))) {
                    sendError(exchange, "Эпик с ID " + Integer.parseInt(path.split("/")[2]) + " отсутствует");
                    return;
                }
                epicToAdd.setId(Integer.parseInt(path.split("/")[2]));
                if (epicToAdd.getStatus() == null) {
                    epicToAdd.setStatus(StatusPriority.NEW);
                }
                tManager.updateTask(epicToAdd);
                writeResponse(exchange, "Эпик обновлен");
                return;
            }
            if (tManager.showAllEpics().contains(epicToAdd)) {
                sendHasInteractions(exchange, "Такой эпик уже существует");
                return;
            }
            tManager.addTask(epicToAdd);
            writeResponse(exchange, "Эпик добавлен");
        } catch (IOException e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private void deleteSublistHandler(HttpExchange exchange, Endpoint endpoint) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            int epicId = Integer.parseInt(path.split("/")[2]);
            if (!(tManager.getEpics().containsKey(epicId))) {
                sendNotFound(exchange,"Эпика с ID " + epicId + " нету");
                return;
            }
            if (endpoint == Endpoint.DELETE) {
                tManager.deleteEpicById(epicId);
                sendText(exchange, "Эпик с ID " + epicId + " был удален.");
            } else {
                if (!tManager.getEpicById(epicId).isHaveSubtasks()) {
                    sendNotFound(exchange,"У эпика с ID " + epicId + " нету подзадач");
                    return;
                }
                String subList = gson.toJson(tManager.getEpicsSubtasks(epicId));
                writeResponse(exchange, subList);
            }
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }

    private static Optional<Epic> epicCreator(String jsonTask) {
        try {
            Epic epic = gson.fromJson(jsonTask, Epic.class);
            return Optional.ofNullable(epic);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
