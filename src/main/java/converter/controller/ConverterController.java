package converter.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import converter.model.Form;
import converter.model.MovieCsv;
import converter.model.MovieXml;
import converter.model.MovieList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Контроллер конвертации файлов
 */
@Controller
public class ConverterController {

    private final Logger logger = LoggerFactory.getLogger(ConverterController.class);

    /**
     * Форма загрузки файла и настроек
     *
     * @param form параметры формы
     * @return шаблон формы
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String fileForm(@ModelAttribute final Form form) {
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        return "form";
    }

    /**
     * Конвертация файла
     *
     * @param form параметры формы
     * @param file файл
     * @throws IOException нарушение работы потока данных
     * @throws HttpClientErrorException.BadRequest некорректный запрос
     * @throws JAXBException некорректная обработка XML файла
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Object> convertFile(@ModelAttribute final Form form,
                                       @RequestParam("file") final MultipartFile file)
            throws IOException, JAXBException {
        if (file.isEmpty() || file.getOriginalFilename() == null)
            throw new FileNotFoundException();
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(".csv"))
            fileName = fileName.substring(0, fileName.length() - 4);

        List<MovieCsv> parsedMovieList = parseMoviesFromCsvFile(file);
        logger.info("CSV файл {} считан.", fileName);

        parsedMovieList = filterMoviesByYear(parsedMovieList, form.getFirstYearFilter(), form.getLastYearFilter());
        logger.info("Список {} отфильтрован с {} по {} гг.", fileName, form.getFirstYearFilter(), form.getLastYearFilter());

        sortMoviesWithParams(parsedMovieList, form.getFirstSorting(), form.getSecondSorting());
        if (form.getFirstSorting() != null && form.getSecondSorting() != null)
            logger.info("Список {} отсортирован по первому полю {} и второму полю {}", fileName, form.getFirstSorting(), form.getSecondSorting());
        else if (form.getFirstSorting() != null)
            logger.info("Список {} отсортирован по полю {}", fileName, form.getFirstSorting());
        else if (form.getSecondSorting() != null)
            logger.info("Список {} отсортирован по полю {}", fileName, form.getSecondSorting());

        List<MovieXml> convertedMovieList = new ArrayList<>();
        for(int i = 0; i < parsedMovieList.size(); i++)
            convertedMovieList.add(new MovieXml(parsedMovieList.get(i), i));
        MovieList movies = new MovieList(convertedMovieList);
        String result = getXmlString(movies);
        logger.info("XML файл {} создан", fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName + ".xml"));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/xml")).body(result);
    }

    private List<MovieCsv> parseMoviesFromCsvFile(final MultipartFile file) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream())) {
            return new CsvToBeanBuilder<MovieCsv>(inputStreamReader)
                    .withType(MovieCsv.class)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build()
                    .parse();
        }
    }

    private List<MovieCsv> filterMoviesByYear(final List<MovieCsv> list, final Integer firstYear, final Integer lastYear) {
        return list.stream().filter(movie -> movie.getReleaseYear() != null
                && movie.getReleaseYear() >= firstYear && movie.getReleaseYear() <= lastYear)
                .collect(Collectors.toList());
    }

    private void sortMoviesWithParams(final List<MovieCsv> list, final String firstSorting, final String secondSorting) {
        Comparator<MovieCsv> firstComparator = getMovieComparatorOrNullByValue(firstSorting),
                secondComparator = getMovieComparatorOrNullByValue(secondSorting);
        if (firstComparator != null && secondComparator != null) {
            list.sort(firstComparator.thenComparing(secondComparator));
        } else if (firstComparator != null) {
            list.sort(firstComparator);
        } else if (secondComparator != null) {
            list.sort(secondComparator);
        }
    }

    private Comparator<MovieCsv> getMovieComparatorOrNullByValue(final String value) throws NoSuchElementException {
        if (value == null)
            return null;
        switch (value) {
            case "title":
                return (o1, o2) -> getSortingValue(o1.getTitle(), o2.getTitle());
            case "director":
                return (o1, o2) -> getSortingValue(o1.getDirector(), o2.getDirector());
            case "genre":
                return (o1, o2) -> getSortingValue(o1.getGenre(), o2.getGenre());
            case "releaseYear":
                return (o1, o2) -> getSortingValue(o1.getReleaseYear(), o2.getReleaseYear());
            case "country":
                return (o1, o2) -> getSortingValue(o1.getCountry(), o2.getCountry());
            case "kinopoiskScore":
                return (o1, o2) -> getSortingValue(o1.getKinopoiskScore(), o2.getKinopoiskScore());
            case "duration":
                return (o1, o2) -> getSortingValue(o1.getDuration(), o2.getDuration());
            default:
                throw new NoSuchElementException();
        }
    }

    private <T extends Comparable<T>> Integer getSortingValue(final T o1, final T o2) {
        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return 1;
        else if (o2 == null)
            return -1;
        else
            return o1.compareTo(o2);
    }

    private String getXmlString(final MovieList movies) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(MovieList.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try (Writer writer = new StringWriter()) {
            marshaller.marshal(movies, writer);
            return writer.toString();
        }
    }
}
