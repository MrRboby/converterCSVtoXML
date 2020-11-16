package converter.model;

import com.opencsv.bean.CsvBindByName;

/**
 * Класс-хранилище фильма из входного файла
 */
public class MovieInput {
    @CsvBindByName(column = "title", locale = "ru-RU")
    private String title;

    @CsvBindByName(column = "director", locale = "ru-RU")
    private String director;

    @CsvBindByName(column = "genre", locale = "ru-RU")
    private String genre;

    @CsvBindByName(column = "release_year")
    private Integer releaseYear;

    @CsvBindByName(column = "country", locale = "ru-RU")
    private String country;

    @CsvBindByName(column = "kinopoisk_score", locale = "ru-RU")
    private Double kinopoiskScore;

    @CsvBindByName(column = "duration")
    private Integer duration;

    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getKinopoiskScore() {
        return kinopoiskScore;
    }

    public void setKinopoiskScore(Double kinopoiskScore) {
        this.kinopoiskScore = kinopoiskScore;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
