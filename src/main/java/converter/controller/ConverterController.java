package converter.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import converter.model.Form;
import converter.model.Movie;
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
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Контроллер конвертации файлов
 */
@Controller
public class ConverterController {

    /**
     * Форма загрузки файла и настроек
     * @param form параметры формы
     * @return шаблон формы
     */
    @RequestMapping(value="/", method= RequestMethod.GET)
    public String fileForm(@ModelAttribute Form form) {
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        return "form";
    }

    /**
     * Конвертация файла
     * @param form параметры формы
     * @param file файл
     * @param httpResponse HTTP ответ
     * @throws IOException
     */
    @RequestMapping(value="/", method= RequestMethod.POST)
    public @ResponseBody ResponseEntity<Object> convertFile(@ModelAttribute Form form,
                                           @RequestParam("file") MultipartFile file,
                                           HttpServletResponse httpResponse)
            throws IOException, HttpClientErrorException.BadRequest, JAXBException {
        Logger logger = LoggerFactory.getLogger(ConverterController.class);
        if (file.isEmpty())
            throw new FileNotFoundException();
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(".csv"))
            fileName = fileName.substring(0, fileName.length() - 4);
        List<Movie> mvs = parseMoviesFromCsvFile(file, fileName);
        logger.info("CSV файл {} считан.", fileName);
        mvs = filterMoviesByYear(mvs, form.getFirstYearFilter(), form.getLastYearFilter());
        logger.info("Список {} отфильтрован с {} по {} гг.", fileName, form.getFirstYearFilter(), form.getLastYearFilter());
        sortMoviesWithParams(mvs, form.getFirstSorting(), form.getSecondSorting());
        logger.info("Список {} отсортирован по первому полю {} и второму полю {}", fileName, form.getFirstSorting(), form.getSecondSorting());
        MovieList movies = new MovieList(mvs);
        String result = getXmlString(movies);
        logger.info("XML файл {} создан", fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName + ".xml"));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/xml")).body(result);
    }

    private List<Movie> parseMoviesFromCsvFile(MultipartFile file, String fileName) throws IOException {
        try(InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream())){
            return new CsvToBeanBuilder<Movie>(inputStreamReader).withType(Movie.class).build().parse();
        }
    }

    private List<Movie> filterMoviesByYear(List<Movie> list, Integer firstYear, Integer lastYear) {
        return list.stream().filter(movie -> movie.getReleaseYear() >= firstYear && movie.getReleaseYear() <= lastYear)
                .collect(Collectors.toList());
    }

    private void sortMoviesWithParams(List<Movie> list, String firstSorting, String secondSorting) {
        Comparator<Movie> firstComparator = getMovieComparatorOrNullByValue(firstSorting),
                secondComparator = getMovieComparatorOrNullByValue(secondSorting);
        if (firstComparator != null && secondComparator != null) {
            list.sort(firstComparator.thenComparing(secondComparator));
        }
        else if (firstComparator != null) {
            list.sort(firstComparator);
        }
        else if (secondComparator != null) {
            list.sort(secondComparator);
        }
    }

    private Comparator<Movie> getMovieComparatorOrNullByValue(String value) throws NoSuchElementException {
        if (value == null)
            return null;
        switch (value) {
            case "title":
                return Comparator.comparing(Movie::getTitle);
            case "director":
                return Comparator.comparing(Movie::getDirector);
            case "genre":
                return Comparator.comparing(Movie::getGenre);
            case "releaseYear":
                return Comparator.comparing(Movie::getReleaseYear);
            case "country":
                return Comparator.comparing(Movie::getCountry);
            case "kinopoiskScore":
                return Comparator.comparing(Movie::getKinopoiskScore);
            case "duration":
                return Comparator.comparing(Movie::getDuration);
            default:
                throw new NoSuchElementException();
        }
    }

    private String getXmlString(MovieList movies) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(MovieList.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        try(Writer writer = new StringWriter()) {
            marshaller.marshal(movies, writer);
            return writer.toString();
        }
    }
}
