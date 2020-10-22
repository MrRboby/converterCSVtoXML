package converter;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.Comparator;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

@Controller
public class ConverterController {
    MovieList movies;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String fileForm(@ModelAttribute Form form) {
        form.setFirstSorting("title");
        form.setSecondSorting("director");
        return "form";
    }

    @RequestMapping(value="/", method= RequestMethod.POST)
    public @ResponseBody String fileUpload(@ModelAttribute Form form,
                                           @RequestParam("file") MultipartFile file,
                                           HttpServletResponse httpResponse)
            throws IOException {
        String name = file.getName();
        if (file.isEmpty())
            throw new FileNotFoundException();
        byte[] bytes = file.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File( name + ".csv")));
        stream.write(bytes);
        stream.close();
        List<Movie> mvs = new CsvToBeanBuilder(new FileReader(name + ".csv")).withType(Movie.class).build().parse();
        Comparator<Movie> firstComparator= Comparator.comparing(Movie::getTitle),
                secondComparator = Comparator.comparing(Movie::getDirector);
        switch (form.getFirstSorting()) {
            case "title":
                firstComparator = Comparator.comparing(Movie::getTitle);
                break;
            case "director":
                firstComparator = Comparator.comparing(Movie::getDirector);
                break;
            case "genre":
                firstComparator = Comparator.comparing(Movie::getGenre);
                break;
            case "releaseYear":
                firstComparator = Comparator.comparing(Movie::getReleaseYear);
                break;
            case "country":
                firstComparator = Comparator.comparing(Movie::getCountry);
                break;
            case "kinopoiskScore":
                firstComparator = Comparator.comparing(Movie::getKinopoiskScore);
                break;
            case "duration":
                firstComparator = Comparator.comparing(Movie::getDuration);
                break;
        }
        switch (form.getSecondSorting()) {
            case "title":
                secondComparator = Comparator.comparing(Movie::getTitle);
                break;
            case "director":
                secondComparator = Comparator.comparing(Movie::getDirector);
                break;
            case "genre":
                secondComparator = Comparator.comparing(Movie::getGenre);
                break;
            case "releaseYear":
                secondComparator = Comparator.comparing(Movie::getReleaseYear);
                break;
            case "country":
                secondComparator = Comparator.comparing(Movie::getCountry);
                break;
            case "kinopoiskScore":
                secondComparator = Comparator.comparing(Movie::getKinopoiskScore);
                break;
            case "duration":
                secondComparator = Comparator.comparing(Movie::getDuration);
                break;
        }
        mvs.sort(firstComparator.thenComparing(secondComparator));
        movies = new MovieList(mvs);
        httpResponse.sendRedirect("download");
        return "form";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<Object> fileDownload() throws IOException, JAXBException {
        if (movies == null)
            throw new FileNotFoundException();
        String filename = "file.xml";
        JAXBContext contextObj = JAXBContext.newInstance(MovieList.class);
        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshallerObj.marshal(movies, new FileOutputStream(filename));
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/xml")).body(resource);
        return responseEntity;

    }

}
