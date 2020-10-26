package converter.model;

import com.opencsv.bean.CsvBindByName;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Класс-хранилище фильма
 */
@XmlType(propOrder={"title", "director", "genre", "releaseYear", "country", "kinopoiskScore", "duration"})
public class Movie {
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

    /**
     * Создание пустого объекта филмьа
     */
    public Movie() {}

    /**
     * Возвращает название фильма
     * @return название фильма
     */
    @XmlElement(name = "title")
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название фильма
     * @param title название фильма
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Возвращает режиссера фильма
     * @return режиссер фильма
     */
    @XmlElement(name = "director")
    public String getDirector() {
        return director;
    }

    /**
     * Устанавливает режиссера фильма
     * @param director режиссер фильма
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Возвращает жанр фильма
     * @return жанр фильма
     */
    @XmlElement(name = "genre")
    public String getGenre() {
        return genre;
    }

    /**
     * Устанавливает жанр фильма
     * @param genre жанр фильма
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Возвращает год выхода фильма
     * @return год выхода фильма
     */
    @XmlElement(name = "release_year")
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Устанавливает год выхода фильма
     * @param releaseYear год выхода фильма
     */
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Возвращает страну-производителя фильма
     * @return страна-производитель фильма
     */
    @XmlElement(name = "country")
    public String getCountry() {
        return country;
    }

    /**
     * Устанавливает страну-производителя фильма
     * @param country страна-производитель фильма
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Возвращает оценку фильма на сервисе Кинопоиск
     * @return оценка фальма на сервисе Кинопоиск
     */
    @XmlElement(name = "kinopoisk_score")
    public Double getKinopoiskScore() {
        return kinopoiskScore;
    }

    /**
     * Устанавливает оценку фильма на сервисе Кинопоиск
     * @param kinopoiskScore оценка фильма на сервисе Кинопоиск
     */
    public void setKinopoiskScore(Double kinopoiskScore) {
        this.kinopoiskScore = kinopoiskScore;
    }

    /**
     * Возвращает длительность фильма
     * @return длительность фильма
     */
    @XmlElement(name = "duration")
    public Integer getDuration() {
        return duration;
    }

    /**
     * Устанавливает длительность фильма
     * @param duration длительность фильма
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}