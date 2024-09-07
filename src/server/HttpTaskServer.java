package server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import managing.InMemoryTaskManager;
import tasks.Epic;
import tasks.StatusPriority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static tasks.Task.FORMATTER;

public class HttpTaskServer {
    private final static int PORT = 8080;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static InMemoryTaskManager tManager;
    private HttpServer server;
    protected static Gson gson;

    public HttpTaskServer(InMemoryTaskManager tManager) throws IOException {
        HttpTaskServer.tManager = tManager;
    }

    public static void main(String[] args) throws IOException {
    }

    public void createServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(tManager));
        server.createContext("/epics", new EpicsHandler(tManager));
        server.createContext("/subtasks", new SubtasksHandler(tManager));
        server.createContext("/history", new HistoryHandler(tManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(tManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
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

class StatusAdapter extends TypeAdapter<StatusPriority> {

    @Override
    public void write(final JsonWriter jsonWriter, final StatusPriority status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public StatusPriority read(final JsonReader jsonReader) throws IOException {
        return StatusPriority.valueOf(jsonReader.nextString());
    }
}

class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.parse(jsonReader.nextString());
    }
}

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        jsonWriter.value(localDate.format(FORMATTER));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), FORMATTER);
    }
}

class EpicConverter implements JsonSerializer<Epic>, JsonDeserializer<Epic>  {

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("title", epic.getTitle());
        object.addProperty("description", epic.getDescription());
        object.addProperty("id", epic.getId());
        object.addProperty("status", "NEW");
        object.addProperty("haveSubtasks", epic.isHaveSubtasks());
        object.addProperty("startTime", epic.getStartTime().format(FORMATTER));
        object.addProperty("duration", epic.getDuration().toString());
        return object;
    }

    @Override
    public Epic deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String title = object.get("title").getAsString();
        String description = object.get("description").getAsString();
        return new Epic(title, description);
    }
}

