package result;

import lombok.*;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GameResult {

    @NonNull
    private String playerName;
    private boolean solved;
    private int steps;
    @NonNull private Duration duration;
    @NonNull private ZonedDateTime created;
}
