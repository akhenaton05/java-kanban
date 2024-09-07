package server;

import com.sun.net.httpserver.HttpServer;
import managing.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private final int Port = 8080;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static InMemoryTaskManager tManager;
    private HttpServer server;

    public HttpTaskServer(InMemoryTaskManager tManager) throws IOException {
        HttpTaskServer.tManager = tManager;
    }

    public static void main(String[] args) throws IOException {
    }

    public void createServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Port), 0);
        server.createContext("/tasks", new TasksHandler(tManager));
        server.createContext("/epics", new EpicsHandler(tManager));
        server.createContext("/subtasks", new SubtasksHandler(tManager));
        server.createContext("/history", new HistoryHandler(tManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(tManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + Port + " порту!");
    }

    public void stop() {
        server.stop(0);
    }

    public static InMemoryTaskManager getManager() {
        return tManager;
    }

    public void setManager(InMemoryTaskManager tManager) {
        HttpTaskServer.tManager = tManager;
    }
}

