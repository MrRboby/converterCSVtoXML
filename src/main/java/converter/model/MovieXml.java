package converter.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Класс-хранилище фильма для выходного файла
 */
@XmlType(propOrder = {"title", "director", "genre", "releaseYear", "country", "kinopoiskScore", "duration"})
public class MovieXml {
    private Integer id;
    private String title;
    private String director;
    private String genre;
    private Integer releaseYear;
    private String country;
    private Double kinopoiskScore;
    private Integer duration;

    /**
     * Создание пустого объекта филмьа
     */
    public MovieXml() {
    }

    /**
     * Создание объекта фильма он основе объекта фильма входного файла и id
     *
     * @param movieCsv объект фильма входного файла
     * @param id       id
     */
    public MovieXml(MovieCsv movieCsv, Integer id) {
        this.id = id;
        this.title = movieCsv.getTitle();
        this.director = movieCsv.getDirector();
        this.genre = movieCsv.getGenre();
        this.releaseYear = movieCsv.getReleaseYear();
        this.country = movieCsv.getCountry();
        this.kinopoiskScore = movieCsv.getKinopoiskScore();
        this.duration = movieCsv.getDuration();
    }

    /**
     * Возвращает идентификатор фильма
     *
     * @return идентификатор фильма
     */
    @XmlAttribute(name = "id")
    public Integer getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор фильма
     *
     * @param id идентификатор фильма
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Возвращает название фильма
     *
     * @return название фильма
     */
    @XmlElement(name = "title")
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название фильма
     *
     * @param title название фильма
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Возвращает режиссера фильма
     *
     * @return режиссер фильма
     */
    @XmlElement(name = "director")
    public String getDirector() {
        return director;
    }

    /**
     * Устанавливает режиссера фильма
     *
     * @param director режиссер фильма
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Возвращает жанр фильма
     *
     * @return жанр фильма
     */
    @XmlElement(name = "genre")
    public String getGenre() {
        return genre;
    }

    /**
     * Устанавливает жанр фильма
     *
     * @param genre жанр фильма
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Возвращает год выхода фильма
     *
     * @return год выхода фильма
     */
    @XmlElement(name = "release_year")
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Устанавливает год выхода фильма
     *
     * @param releaseYear год выхода фильма
     */
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Возвращает страну-производителя фильма
     *
     * @return страна-производитель фильма
     */
    @XmlElement(name = "country")
    public String getCountry() {
        return country;
    }

    /**
     * Устанавливает страну-производителя фильма
     *
     * @param country страна-производитель фильма
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Возвращает оценку фильма на сервисе Кинопоиск
     *
     * @return оценка фальма на сервисе Кинопоиск
     */
    @XmlElement(name = "kinopoisk_score")
    public Double getKinopoiskScore() {
        return kinopoiskScore;
    }

    /**
     * Устанавливает оценку фильма на сервисе Кинопоиск
     *
     * @param kinopoiskScore оценка фильма на сервисе Кинопоиск
     */
    public void setKinopoiskScore(Double kinopoiskScore) {
        this.kinopoiskScore = kinopoiskScore;
    }

    /**
     * Возвращает длительность фильма
     *
     * @return длительность фильма
     */
    @XmlElement(name = "duration")
    public Integer getDuration() {
        return duration;
    }

    /**
     * Устанавливает длительность фильма
     *
     * @param duration длительность фильма
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
