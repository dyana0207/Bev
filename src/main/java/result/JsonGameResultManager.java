package result;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import util.JacksonHelper;

/**
 * Manages game results by storing them in a JSON file.
 */
public class JsonGameResultManager implements GameResultManager {
    private final Path filePath;

    /**
     * Creates a {@code JsonGameResultManager} object with the specified file path.
     *
     * @param filePath the path to the JSON file where game results are stored
     */
    public JsonGameResultManager(@NonNull Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Adds a new game result to the JSON file.
     *
     * @param result the game result to add
     * @return a list of all game results including the newly added result
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<GameResult> add(@NonNull GameResult result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            JacksonHelper.writeList(out, results);
        }
        return results;
    }

    /**
     * Retrieves all game results from the JSON file.
     *
     * @return a list of all game results
     * @throws IOException if an I/O error occurs
     */
    public List<GameResult> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (var in = Files.newInputStream(filePath)) {

            return JacksonHelper.readList(in, GameResult.class);
        }
    }
}
