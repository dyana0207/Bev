package result;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import util.JacksonHelper;


public class JsonGameResultManager implements GameResultManager {
    private final Path filePath;

    public JsonGameResultManager(@NonNull Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<GameResult> add(@NonNull GameResult result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            JacksonHelper.writeList(out, results);
        }
        return results;
    }

    public List<GameResult> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (var in = Files.newInputStream(filePath)) {

            return JacksonHelper.readList(in, GameResult.class);
        }
    }
}
