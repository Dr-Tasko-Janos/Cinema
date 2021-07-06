package cinema;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private ModelMapper modelMapper;

    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    List<Movie> movies = Collections.synchronizedList(new ArrayList<>());

    AtomicLong idGenerator = new AtomicLong();

    public Movie findMovieById(long id) {
        return movies.stream().filter(e -> e.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("Not found movie with id " + id));
    }

    public List<MovieDTO> getMovies(Optional<String> title) {
        Type returnListType = new TypeToken<List<MovieDTO>>(){}.getType();

        List<Movie> filteredList = movies.stream().filter(e -> title.isEmpty() || e.getTitle().equalsIgnoreCase(title.get())).collect(Collectors.toList());

        return modelMapper.map(filteredList, returnListType);
    }

    public MovieDTO getMovieById(long id) {
        return modelMapper.map(findMovieById(id), MovieDTO.class);
    }

    public MovieDTO addNewMovie(CreateMovieCommand command) {
        Movie newMovie = new Movie(idGenerator.incrementAndGet(), command.getTitle(), command.getDate(), command.getMaxSpaces(), command.getMaxSpaces());
       //newMovie.setFreeSpaces(command.getMaxSpaces());
        movies.add(newMovie);
        return modelMapper.map(newMovie, MovieDTO.class);
    }

    public MovieDTO reserveSeat(long id, CreateReservationCommand command) {
        Movie filmForReserve = findMovieById(id);
        if (command.getReserveNumber() > filmForReserve.getFreeSpaces()) {
            throw new IllegalStateException("There isn't enough space");
        }
        filmForReserve.setFreeSpaces(filmForReserve.getFreeSpaces() - command.getReserveNumber());
        return modelMapper.map(filmForReserve, MovieDTO.class);
    }

    public MovieDTO updateMovieDate(long id, UpdateDateCommand command) {
        Movie movieForUpdateDate = findMovieById(id);
        movieForUpdateDate.setDate(command.getDate());
        return modelMapper.map(movieForUpdateDate, MovieDTO.class);
    }

    public void deleteAllMovies() {
        movies.clear();
        idGenerator = new AtomicLong();
    }

}
