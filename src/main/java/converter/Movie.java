package converter;

import com.opencsv.bean.CsvBindByName;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"title", "director", "genre", "releaseYear", "country", "kinopoiskScore", "duration"})
public class Movie {
    @CsvBindByName(column = "title", locale = "ru-RU")
    private String title;

    @CsvBindByName(column = "director", locale = "ru-RU")
    private String director;

    @CsvBindByName(column = "genre", locale = "ru-RU")
    private String genre;

    @CsvBindByName(column = "release_year")
    private int releaseYear;

    @CsvBindByName(column = "country", locale = "ru-RU")
    private String country;

    @CsvBindByName(column = "kinopoisk_score", locale = "ru-RU")
    private double kinopoiskScore;

    @CsvBindByName(column = "duration")
    private int duration;

    public Movie() {}

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
    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
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
    public double getKinopoiskScore() {
        return kinopoiskScore;
    }

    public void setKinopoiskScore(double kinopoiskScore) {
        this.kinopoiskScore = kinopoiskScore;
    }

    @XmlElement(name = "duration")
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
