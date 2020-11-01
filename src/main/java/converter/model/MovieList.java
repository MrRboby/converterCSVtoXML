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
    private List<MovieXml> movies;

    /**
     * Создание пустого хранилища фильмов
     */
    public MovieList() {
        movies = new ArrayList<MovieXml>();
    }

    /**
     * Создание хранилища фильмов с исходным списком
     *
     * @param movies список фильмов
     */
    public MovieList(List<MovieXml> movies) {
        this.movies = movies;
    }

    /**
     * Установить список фильмов
     *
     * @param movies список фильмов
     */
    public void setMovies(List<MovieXml> movies) {
        this.movies = movies;
    }

    /**
     * Получить список фильмов
     *
     * @return список фильмов
     */
    @XmlElement(name = "movie")
    public List<MovieXml> getMovies() {
        return movies;
    }
}
