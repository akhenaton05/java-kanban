package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managing.InMemoryTaskManager;

import java.io.IOException;

class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedTasksHandler(InMemoryTaskManager tManager) {
        super(tManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /prioritized запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET:
                getHandler(exchange);
                break;
            case UNKNOWN:
                sendError(exchange, "Неизвестный запрос, проверьте правильность ввода");
                break;
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(tManager.getPrioritizedTasks());
            sendText(exchange, response);
        } catch (Exception e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }
}
