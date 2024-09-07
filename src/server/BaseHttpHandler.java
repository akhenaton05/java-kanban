package server;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import managing.InMemoryTaskManager;
import tasks.Epic;
import tasks.StatusPriority;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static server.HttpTaskServer.DEFAULT_CHARSET;
import static tasks.Task.FORMATTER;

public class BaseHttpHandler {
    protected InMemoryTaskManager tManager;
    protected static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Epic.class, new EpicConverter())
            .registerTypeAdapter(StatusPriority.class, new StatusAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public BaseHttpHandler(InMemoryTaskManager tManager) {
        this.tManager = tManager;
    }

    protected static void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void sendError(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void writeResponse(HttpExchange exchange,
                                        String responseString) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(201, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    protected static Endpoint getEndpoint(String path, String method) {
        List<String> endpointList = List.of("tasks", "epics", "subtasks", "history", "prioritized");
        String[] pathParts = path.split("/");

        if (!(endpointList.contains(pathParts[1]))) {
            return Endpoint.UNKNOWN;
        }
        switch (method) {
            case "GET" -> {
                if (pathParts.length == 4 && isNumeric(pathParts[2]) && pathParts[3].equals("subtasks")) {
                    return Endpoint.EPICS_SUBTASKS;
                }
                return Endpoint.GET;
            }
            case "POST" -> {
                return Endpoint.POST;
            }
            case "DELETE" -> {
                return Endpoint.DELETE;
            }
            default -> {
                return Endpoint.UNKNOWN;
            }
        }
    }

    protected static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean dateCrossesChecker(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        return tManager.getPrioritizedTasks().stream()
                .allMatch(t -> (taskStart.isAfter(t.getEndTime())) || taskEnd.isBefore(t.getStartTime()));
    }

    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        BaseHttpHandler.gson = gson;
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

class EpicConverter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {

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
