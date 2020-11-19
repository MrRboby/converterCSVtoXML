package converter.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.lang.reflect.Field;

/**
 * Класс-хранилище фильма для выходного файла
 */
@XmlType(propOrder = {"title", "director", "genre", "releaseYear", "country", "kinopoiskScore", "duration"})
public class MovieOutput {
    private int id;
    private String title;
    private String director;
    private String genre;
    private Integer releaseYear;
    private String country;
    private Double kinopoiskScore;
    private Integer duration;

    public MovieOutput(MovieInput movieInput) {}

    @XmlAttribute(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "director")
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @XmlElement(name = "genre")
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @XmlElement(name = "release_year")
    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    @XmlElement(name = "country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement(name = "kinopoisk_score")
    public Double getKinopoiskScore() {
        return kinopoiskScore;
    }

    public void setKinopoiskScore(Double kinopoiskScore) {
        this.kinopoiskScore = kinopoiskScore;
    }

    @XmlElement(name = "duration")
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
