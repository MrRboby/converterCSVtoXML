package converter.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import converter.model.Form;
import converter.model.MovieInput;
import converter.model.MovieOutput;
import converter.model.MovieList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        form.setFirstSorting("noSorting");
        form.setSecondSorting("noSorting");
        return "form";
    }

    /**
     * Конвертация файла
     *
     * @param form     параметры формы
     * @param file     файл
     * @param response данные о выходном файле
     * @return выходные данные
     * @throws IOException                  нарушение работы потока данных
     * @throws JAXBException                некорректная обработка XML файла
     * @throws IllegalAccessException       отсутствие доступа к полям хранилища выходного файла
     * @throws CsvDataTypeMismatchException некорректные значения полей входного файла
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String convertFile(@ModelAttribute final Form form,
                       @RequestParam("file") final MultipartFile file, HttpServletResponse response)
            throws IOException, JAXBException, IllegalAccessException, CsvDataTypeMismatchException {
        if (file.isEmpty() || file.getOriginalFilename() == null)
            throw new FileNotFoundException("Файл не найден или пустой");
        final String fileName = file.getOriginalFilename();

        List<MovieInput> parsedMovieList = parseMoviesFromCsvFile(file);
        logger.info("CSV файл {} считан.", fileName);

        parsedMovieList = filterMoviesByYear(parsedMovieList, form.getFirstYearFilter(), form.getLastYearFilter());
        logger.info("Список {} отфильтрован с {} по {} гг.",
                fileName, form.getFirstYearFilter(), form.getLastYearFilter());

        sortMoviesWithParams(parsedMovieList, form.getFirstSorting(), form.getSecondSorting());
        if (form.getFirstSorting() != null && form.getSecondSorting() != null)
            logger.info("Список {} отсортирован по первому полю {} и второму полю {}",
                    fileName, form.getFirstSorting(), form.getSecondSorting());
        else if (form.getFirstSorting() != null)
            logger.info("Список {} отсортирован по полю {}", fileName, form.getFirstSorting());
        else if (form.getSecondSorting() != null)
            logger.info("Список {} отсортирован по полю {}", fileName, form.getSecondSorting());

        List<MovieOutput> convertedMovieList = new ArrayList<>();
        for (int index = 0; index < parsedMovieList.size(); index++)
            convertedMovieList.add(new MovieOutput(parsedMovieList.get(index)));
        final MovieList movies = new MovieList(convertedMovieList);
        String result = getXmlString(movies);
        logger.info("XML файл {} создан", fileName);

        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"",
                (fileName.endsWith(".csv") ? fileName.substring(0, fileName.length() - 4) : fileName).concat(".xml")));
        response.setCharacterEncoding("utf-8");
        return result;
    }

    private List<MovieInput> parseMoviesFromCsvFile(final MultipartFile file) throws IOException, CsvDataTypeMismatchException {
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

    private List<MovieInput> filterMoviesByYear(final List<MovieInput> list, final Integer firstYear, final Integer lastYear) {
        return list.stream().filter(movie -> movie.getReleaseYear() != null
                && movie.getReleaseYear() >= firstYear && movie.getReleaseYear() <= lastYear)
                .collect(Collectors.toList());
    }

    private void sortMoviesWithParams(final List<MovieInput> list, final String firstSorting, final String secondSorting) {
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

    private Comparator<MovieInput> getMovieComparatorOrNullByValue(final String value) throws NoSuchElementException {
        switch (value) {
            case "title":
                return Comparator.comparing(MovieInput::getTitle,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "director":
                return Comparator.comparing(MovieInput::getDirector,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "genre":
                return Comparator.comparing(MovieInput::getGenre,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "releaseYear":
                return Comparator.comparing(MovieInput::getReleaseYear,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "country":
                return Comparator.comparing(MovieInput::getCountry,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "kinopoiskScore":
                return Comparator.comparing(MovieInput::getKinopoiskScore,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "duration":
                return Comparator.comparing(MovieInput::getDuration,
                        Comparator.nullsLast(Comparator.naturalOrder()));
            case "noSorting":
                return null;
            default:
                throw new NoSuchElementException("Некорректный вариант сортировки");
        }
    }

    private String getXmlString(final MovieList movies) throws JAXBException, IOException {
        Marshaller marshaller = JAXBContext.newInstance(MovieList.class)
                .createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try (Writer writer = new StringWriter()) {
            marshaller.marshal(movies, writer);
            return writer.toString();
        }
    }
}
