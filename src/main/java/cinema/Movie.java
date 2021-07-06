package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private long id;
    private String title;
    private LocalDateTime date;
    private int maxSpaces;
    private int freeSpaces;

    public void requestedSeats(int requestedSeats) {
        if (requestedSeats <= freeSpaces) {
            freeSpaces = freeSpaces - requestedSeats;
        } else {
            throw new IllegalStateException("No enough space");
        }
    }
}
