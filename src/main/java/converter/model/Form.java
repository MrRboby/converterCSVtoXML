package converter.model;

/**
 * Класс-хранилище данных формы
 */
public class Form {
    private String firstSorting;
    private String secondSorting;
    private Integer firstYearFilter;
    private Integer lastYearFilter;

    /**
     * Возвращает тип сортировки
     * @return тип сортировки
     */
    public String getFirstSorting() {
        return firstSorting;
    }

    /**
     * Устанавливает значение сортировки
     * @param firstSorting тип сортировки (title, director, genre, release_year, country, kinopoisk_score, duration)
     */
    public void setFirstSorting(String firstSorting) {
        this.firstSorting = firstSorting;
    }

    /**
     * Возвращает тип дополнительной сортировки
     * @return тип дополнительной сортировки
     */
    public String getSecondSorting() {
        return secondSorting;
    }

    /**
     * Устанавливает значение дополнительной сортировки
     * @param secondSorting тип дополнительной сортировки (title, director, genre, release_year, country, kinopoisk_score, duration)
     */
    public void setSecondSorting(String secondSorting) {
        this.secondSorting = secondSorting;
    }

    /**
     * Возвращает значение первого года фильтрации
     * @return первый год фильтрации
     */
    public Integer getFirstYearFilter() {
        return firstYearFilter;
    }

    /**
     * Устанавливает значение первого года фильтрации
     * @param firstYearFilter первый год фильтрации
     */
    public void setFirstYearFilter(Integer firstYearFilter) {
        this.firstYearFilter = firstYearFilter;
    }

    /**
     * Возвращает значение последнего года фильтрации
     * @return последний год фильтрации
     */
    public Integer getLastYearFilter() {
        return lastYearFilter;
    }

    /**
     * Устанавливает значение последнего года фильтрации
     * @param lastYearFilter последний год фильтрации
     */
    public void setLastYearFilter(Integer lastYearFilter) {
        this.lastYearFilter = lastYearFilter;
    }
}
