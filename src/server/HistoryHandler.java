package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managing.InMemoryTaskManager;

import java.io.IOException;

class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(InMemoryTaskManager tManager) {
        super(tManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /history запроса от клиента.");
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET:
                getHistoryHandle(exchange);
                break;
            case UNKNOWN:
                sendError(exchange, "Неизвестный запрос, проверьте правильность ввода");
                break;
        }
    }

    public void getHistoryHandle(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(tManager.getHistory());
            sendText(exchange, response);
        } catch (IOException e) {
            sendError(exchange, "Ошибка выполнения запроса, проверьте правильность введеных данных");
        }
    }
}
