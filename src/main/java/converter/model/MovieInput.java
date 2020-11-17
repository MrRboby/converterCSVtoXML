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

    public MovieInput() {}

    public MovieInput(String title, String director, String genre, Integer releaseYear,
                      String country, Double kinopoiskScore, Integer duration, int id) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.country = country;
        this.kinopoiskScore = kinopoiskScore;
        this.duration = duration;
        this.id = id;
    }

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

    @Override
    public boolean equals(Object obj) {
        MovieInput other = (MovieInput)obj;
        return this.title.equals(other.title) &&
                this.director.equals(other.director) &&
                this.genre.equals(other.genre) &&
                this.releaseYear.equals(other.releaseYear) &&
                this.country.equals(other.country) &&
                this.kinopoiskScore.equals(other.kinopoiskScore) &&
                this.id == other.id;
    }
}
