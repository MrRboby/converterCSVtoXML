package converter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "movies")
public class MovieList {
    private List<Movie> movies;

    public MovieList() {
        movies = new ArrayList<Movie>();
    }

    public MovieList(List<Movie> movies) {
        this.movies = movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @XmlElement(name = "movie")
    public List<Movie> getMovies() {
        return movies;
    }
}
