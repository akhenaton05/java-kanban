package server.adapters;

import com.google.gson.*;
import tasks.Epic;

import java.lang.reflect.Type;

import static tasks.Task.FORMATTER;

public class EpicConverter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {

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
