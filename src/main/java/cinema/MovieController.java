package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cinema")
public class MovieController {

    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {
        return movieService.getMovies(title);
    }

    @GetMapping("/{id}")
    public MovieDTO getMovieById(@Valid @PathVariable("id") long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO addNewMovie(@Valid @RequestBody CreateMovieCommand command) {
       return movieService.addNewMovie(command);
    }

    @PostMapping("/{id}/reserve")
    public MovieDTO reserveSeat(@PathVariable("id") long id, @RequestBody CreateReservationCommand command) {
        return movieService.reserveSeat(id, command);
    }

    @PutMapping("/{id}")
    public MovieDTO updateMovieDate(@PathVariable("id") long id, @RequestBody UpdateDateCommand command) {
        return movieService.updateMovieDate(id, command);
    }

    @DeleteMapping
    public void deleteAllMovies() {
        movieService.deleteAllMovies();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("instruments/not-found"))
                .withTitle("instruments/not-found")
                .withStatus(Status.BAD_REQUEST)
                .withDetail("instruments/not-found")
                .with("violation", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }

    //Mikor URI-t vár   és ebből adhatok meg tonnaszám a projektembe/hez
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleNotFound(IllegalArgumentException ex) {
        Problem problem =
                Problem.builder()
                        .withType(URI.create("cinema/not-found"))              //ITT VÁRJA AZ URIT
                        .withTitle("Not Found")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);


    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleNotFound(IllegalStateException ex) {
        Problem problem =
                Problem.builder()
                        .withType(URI.create("cinema/bad-reservation"))              //ITT VÁRJA AZ URIT
                        .withTitle("Not Found")
                        .withStatus(Status.BAD_REQUEST)
                        .withDetail(ex.getMessage())
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);


    }


}
