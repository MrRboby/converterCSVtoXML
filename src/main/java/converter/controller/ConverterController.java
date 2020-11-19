package converter.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import converter.MovieMapper;
import converter.model.Form;
import converter.model.MovieInput;
import converter.model.MovieOutput;
import converter.model.MovieList;
import org.apache.logging.log4j.util.PropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер конвертации файлов
 */
@Controller
public final class ConverterController {

    @Autowired
    private MovieMapper movieMapper;

    private final Logger logger = LoggerFactory.getLogger(ConverterController.class);

    /**
     * Форма загрузки файла и настроек
     *
     * @param form параметры формы
     * @return шаблон формы
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String fileForm(@ModelAttribute Form form) {
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        form.setFirstSorting("");
        form.setSecondSorting("");
        return "form";
    }

    /**
     * Конвертация файла
     *
     * @param form     параметры формы
     * @param file     файл
     * @param response данные о выходном файле
     * @throws IOException                  нарушение работы потока данных
     * @throws JAXBException                некорректная обработка XML файла
     * @throws CsvDataTypeMismatchException некорректные значения полей входного файла
     * @throws NoSuchMethodException некорректное значение сортировки
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    void convertFile(@ModelAttribute final Form form,
                       @RequestParam("file") final MultipartFile file, HttpServletResponse response)
            throws IOException, JAXBException, CsvDataTypeMismatchException, NoSuchMethodException {
        if (file.isEmpty() || file.getOriginalFilename() == null)
            throw new FileNotFoundException("Файл не найден или пустой");
        final String fileName = file.getOriginalFilename();

        List<MovieInput> parsedMovieList = parseMoviesFromCsvFile(file);
        logger.info("CSV файл {} считан.", fileName);

        parsedMovieList = filterMoviesByYear(parsedMovieList, form.getFirstYearFilter(), form.getLastYearFilter());
        logger.info("Список {} отфильтрован с {} по {} гг.",
                fileName, form.getFirstYearFilter(), form.getLastYearFilter());

        sortMoviesWithParams(parsedMovieList, form.getFirstSorting(), form.getSecondSorting());
        if (!form.getFirstSorting().equals("") && !form.getSecondSorting().equals(""))
            logger.info("Список {} отсортирован по первому полю {} и второму полю {}",
                    fileName, form.getFirstSorting(), form.getSecondSorting());
        else if (!form.getFirstSorting().equals(""))
            logger.info("Список {} отсортирован по полю {}", fileName, form.getFirstSorting());
        else if (!form.getSecondSorting().equals(""))
            logger.info("Список {} отсортирован по полю {}", fileName, form.getSecondSorting());

        List<MovieOutput> convertedMovieList = new ArrayList<>();
        for (int index = 0; index < parsedMovieList.size(); index++)
            convertedMovieList.add(movieMapper.movieInputToOutput(parsedMovieList.get(index)));
        final MovieList movies = new MovieList(convertedMovieList);

        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
                (fileName.endsWith(".csv") ? fileName.substring(0, fileName.length() - 4) : fileName).concat(".xml")));
        response.setCharacterEncoding("utf-8");

        writeXmlStringToStream(movies, response.getOutputStream());
        logger.info("XML файл {} создан", fileName);
        response.getOutputStream().close();

    }

    /**
     * Парсинг списка фильмов из CSV-файла
     *
     * @param file CSV-файл
     * @return список фильмов
     * @throws IOException                  нарушение работы потока данных файла
     * @throws CsvDataTypeMismatchException некорректные значения полей входного файла
     */
    public static List<MovieInput> parseMoviesFromCsvFile(final MultipartFile file) throws IOException, CsvDataTypeMismatchException {
        List<String> errorsList = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            CsvToBean<MovieInput> csvToBean = new CsvToBeanBuilder<MovieInput>(inputStreamReader)
                    .withType(MovieInput.class)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withFilter(strings -> {
                        String parsedString = "";
                        for (String string : strings) {
                            if (string != null) {
                                parsedString = parsedString.concat(String.format("\"%s\",", string));
                            }
                        }
                        try {
                            if (strings[3] != null) {
                                Integer.parseInt(strings[3]); //release_year
                            }
                        } catch (Exception exception) {
                            errorsList.add(String.format("В строке '%s' ошибка. Значение года выхода должно быть целым числом.",
                                    parsedString));
                            return false;
                        }
                        try {
                            if (strings[5] != null) {
                                Double.parseDouble(strings[5].replace(',', '.')); //kinopoisk_score
                            }
                        } catch (Exception exception) {
                            errorsList.add(
                                    String.format("В строке '%s' ошибка. Значение года выхода должно " +
                                                    "быть вещественным числом с запятой в качестве разделителя.",
                                            parsedString));
                            return false;
                        }
                        try {
                            if (strings[6] != null) {
                                Integer.parseInt(strings[6]); //duration
                            }
                        } catch (Exception exception) {
                            errorsList.add(
                                    String.format("В строке '%s' ошибка. Значение длительности должно быть целым числом.",
                                            parsedString));
                            return false;
                        }
                        return true;
                    })
                    .build();
            List<MovieInput> movieList = csvToBean.parse();
            if (errorsList.size() > 0) {
                String errorLines = "";
                for (String string : errorsList)
                    errorLines = errorLines.concat(string).concat("\n");
                throw new CsvDataTypeMismatchException("CSV файл содержит некорректные значения:\n".concat(errorLines));
            }
            for (int index = 0; index < movieList.size(); index++) {
                movieList.get(index).setId(index);
            }

            return movieList;
        }
    }

    /**
     * Фильтрация списка фильмов по году
     *
     * @param list      список фильмов
     * @param firstYear начальный год
     * @param lastYear  конечный год
     * @return отфильтрованный список фильмов
     */
    public static List<MovieInput> filterMoviesByYear(final List<MovieInput> list, final Integer firstYear, final Integer lastYear) {
        return list.stream().filter(movie -> movie.getReleaseYear() != null
                && movie.getReleaseYear() >= firstYear && movie.getReleaseYear() <= lastYear)
                .collect(Collectors.toList());
    }

    /**
     * Сортировка списка фильмов
     *
     * @param list          список фильмов
     * @param firstSorting  основное поле для сортировки
     * @param secondSorting дополнительное поле для сортировки
     */
    public static void sortMoviesWithParams(final List<MovieInput> list, final String firstSorting, final String secondSorting) {
        Comparator<MovieInput> firstComparator = getMovieComparatorOrNullByValue(firstSorting),
                secondComparator = getMovieComparatorOrNullByValue(secondSorting);
        if (firstComparator != null && secondComparator != null) {
            list.sort(firstComparator.thenComparing(secondComparator));
        } else if (firstComparator != null) {
            list.sort(firstComparator);
        } else if (secondComparator != null) {
            list.sort(secondComparator);
        }
    }

    private static Comparator<MovieInput> getMovieComparatorOrNullByValue(final String value) {
        if (value.equals(""))
            return null;
        String comparingMethod = "get".concat(value.substring(0, 1).toUpperCase()).concat(value.substring(1));
        return (movieInput1, movieInput2) -> {
            Comparable comparable1 = null, comparable2 = null;
            try {
                comparable1 = (Comparable) MovieInput.class.getMethod(comparingMethod).invoke(movieInput1);
            } catch (Exception ignored) {
            }
            try {
                comparable2 = (Comparable) MovieInput.class.getMethod(comparingMethod).invoke(movieInput2);
            } catch (Exception ignored) {
            }
            if (comparable1 != null && comparable2 != null)
                return comparable1.compareTo(comparable2);
            if (comparable1 != null)
                return 1;
            if (comparable2 != null)
                return -1;
            return 0;
        };
    }

    /**
     * Маршалинг XML файла
     *
     * @param movies       список фильмов
     * @param outputStream поток, принимающий значение XML
     * @throws JAXBException некорректная работа при преобразовании
     * @throws IOException   нарушение работы записи в строку
     */
    public static void writeXmlStringToStream(final MovieList movies, final OutputStream outputStream) throws JAXBException, IOException {
        Marshaller marshaller = JAXBContext.newInstance(MovieList.class)
                .createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            marshaller.marshal(movies, writer);
        }
    }
}
