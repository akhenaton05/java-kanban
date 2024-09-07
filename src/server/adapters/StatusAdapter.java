package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.StatusPriority;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<StatusPriority> {

    @Override
    public void write(final JsonWriter jsonWriter, final StatusPriority status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public StatusPriority read(final JsonReader jsonReader) throws IOException {
        return StatusPriority.valueOf(jsonReader.nextString());
    }
}
