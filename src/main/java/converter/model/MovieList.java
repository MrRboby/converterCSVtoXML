package converter.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс-хранилище списка фильмов
 */
@XmlRootElement(name = "movies")
public class MovieList {
    private List<MovieOutput> movies;

    /**
     * Создание пустого хранилища фильмов
     */
    public MovieList() {
        movies = new ArrayList<MovieOutput>();
    }

    /**
     * Создание хранилища фильмов с исходным списком
     *
     * @param movies список фильмов
     */
    public MovieList(List<MovieOutput> movies) {
        this.movies = movies;
    }

    /**
     * Установить список фильмов
     *
     * @param movies список фильмов
     */
    public void setMovies(List<MovieOutput> movies) {
        this.movies = movies;
    }

    /**
     * Получить список фильмов
     *
     * @return список фильмов
     */
    @XmlElement(name = "movie")
    public List<MovieOutput> getMovies() {
        return movies;
    }
}
