package converter;

import converter.controller.ConverterController;
import converter.model.Form;
import converter.model.MovieInput;
import converter.model.MovieList;
import converter.model.MovieOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
@AutoConfigureMockMvc
public class ConverterTest {

    @Autowired
    private MockMvc mockMvc;

    private static Form form;

    @BeforeEach
    public void setForm() {
        form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        form.setFirstSorting("noSorting");
        form.setSecondSorting("noSorting");
    }

    @Test
    public void correctFull() throws Exception {
        MockMultipartFile inputFile;
        String output;
        try(InputStream inputStream = getClass().getResourceAsStream("/correct_full.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }
        try (InputStream inputStream = getClass().getResourceAsStream("/correct_full.xml");
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        Assertions.assertEquals(output, mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(inputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void correctWithEmptyFields() throws Exception {
        MockMultipartFile inputFile;
        String output;
        try(InputStream inputStream = getClass().getResourceAsStream("/correct_with_empty_fields.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }
        try (InputStream inputStream = getClass().getResourceAsStream("/correct_with_empty_fields.xml");
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        Assertions.assertEquals(output, mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(inputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void correctSorted() throws Exception {
        MockMultipartFile inputFile;
        String output;
        try(InputStream inputStream = getClass().getResourceAsStream("/correct_full.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }
        try (InputStream inputStream = getClass().getResourceAsStream("/correct_sorted.xml");
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        form.setFirstSorting("genre");
        form.setSecondSorting("kinopoiskScore");
        Assertions.assertEquals(output, mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(inputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void correctFiltered() throws Exception {
        MockMultipartFile inputFile;
        String output;
        try(InputStream inputStream = getClass().getResourceAsStream("/correct_full.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }
        try (InputStream inputStream = getClass().getResourceAsStream("/correct_filtered.xml");
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            output = scanner.useDelimiter("\\A").next();
        }
        form.setFirstYearFilter(2000);
        form.setLastYearFilter(2010);
        Assertions.assertEquals(output, mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(inputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void incorrectFields() throws Exception {
        MockMultipartFile inputFile;
        try(InputStream inputStream = getClass().getResourceAsStream("/incorrect_fields.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(inputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    public void parseMoviesFromCsvFileToList() throws Exception {
        MockMultipartFile inputFile;
        try(InputStream inputStream = getClass().getResourceAsStream("/correct_short.csv")) {
            inputFile = new MockMultipartFile("file", inputStream);
        }

        List<MovieInput> movieList = new ArrayList<>();
        movieList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        movieList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        movieList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));
        movieList.add(new MovieInput("Амели", "Жан-Пьер Жёне", "мелодрама",
                2001, "Франция", 7.994, 122, 3));

        List<MovieInput> output = ConverterController.parseMoviesFromCsvFile(inputFile);

        Assertions.assertEquals(movieList.size(), output.size());
        for(int index = 0; index < movieList.size(); index++) {
            Assertions.assertEquals(movieList.get(index), output.get(index));
        }
    }

    @Test
    public void filterMoviesByYear() throws Exception {
        List<MovieInput> movieList = new ArrayList<>();
        movieList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        movieList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        movieList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));
        movieList.add(new MovieInput("Амели", "Жан-Пьер Жёне", "мелодрама",
                2001, "Франция", 7.994, 122, 3));

        List<MovieInput> filteredMovieList = new ArrayList<>();
        filteredMovieList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        filteredMovieList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        filteredMovieList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));

        List<MovieInput> output = ConverterController.filterMoviesByYear(movieList, 1980, 2000);

        Assertions.assertEquals(filteredMovieList.size(), output.size());
        for(int index = 0; index < filteredMovieList.size(); index++) {
            Assertions.assertEquals(filteredMovieList.get(index), output.get(index));
        }
    }

    @Test
    public void sortMoviesWithParams() throws Exception {
        List<MovieInput> movieList = new ArrayList<>();
        movieList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        movieList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        movieList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));
        movieList.add(new MovieInput("Амели", "Жан-Пьер Жёне", "мелодрама",
                2001, "Франция", 7.994, 122, 3));

        List<MovieInput> sortedMovieList = new ArrayList<>();
        sortedMovieList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        sortedMovieList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));
        sortedMovieList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        sortedMovieList.add(new MovieInput("Амели", "Жан-Пьер Жёне", "мелодрама",
                2001, "Франция", 7.994, 122, 3));

        ConverterController.sortMoviesWithParams(movieList, "releaseYear", "kinopoiskScore");

        Assertions.assertEquals(sortedMovieList.size(), movieList.size());
        for(int index = 0; index < sortedMovieList.size(); index++) {
            Assertions.assertEquals(sortedMovieList.get(index), movieList.get(index));
        }
    }

    @Test
    public void getXmlString() throws Exception {
        List<MovieInput> movieInputList = new ArrayList<>();
        movieInputList.add(new MovieInput("Инопланетянин", "Стивен Спилберг","фантастика",
                1982, "США", 7.778, 115, 0));
        movieInputList.add(new MovieInput("Парк Юрского периода", "Стивен Спилберг","приключения",
                1993, "США", 7.763, 127, 1));
        movieInputList.add(new MovieInput("Чародеи", "Константин Бромберг", "мюзикл",
                1982, "СССР", 7.949, 147, 2));
        movieInputList.add(new MovieInput("Амели", "Жан-Пьер Жёне", "мелодрама",
                2001, "Франция", 7.994, 122, 3));

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<movies>\n" +
                "    <movie id=\"0\">\n" +
                "        <title>Инопланетянин</title>\n" +
                "        <director>Стивен Спилберг</director>\n" +
                "        <genre>фантастика</genre>\n" +
                "        <release_year>1982</release_year>\n" +
                "        <country>США</country>\n" +
                "        <kinopoisk_score>7.778</kinopoisk_score>\n" +
                "        <duration>115</duration>\n" +
                "    </movie>\n" +
                "    <movie id=\"1\">\n" +
                "        <title>Парк Юрского периода</title>\n" +
                "        <director>Стивен Спилберг</director>\n" +
                "        <genre>приключения</genre>\n" +
                "        <release_year>1993</release_year>\n" +
                "        <country>США</country>\n" +
                "        <kinopoisk_score>7.763</kinopoisk_score>\n" +
                "        <duration>127</duration>\n" +
                "    </movie>\n" +
                "    <movie id=\"2\">\n" +
                "        <title>Чародеи</title>\n" +
                "        <director>Константин Бромберг</director>\n" +
                "        <genre>мюзикл</genre>\n" +
                "        <release_year>1982</release_year>\n" +
                "        <country>СССР</country>\n" +
                "        <kinopoisk_score>7.949</kinopoisk_score>\n" +
                "        <duration>147</duration>\n" +
                "    </movie>\n" +
                "    <movie id=\"3\">\n" +
                "        <title>Амели</title>\n" +
                "        <director>Жан-Пьер Жёне</director>\n" +
                "        <genre>мелодрама</genre>\n" +
                "        <release_year>2001</release_year>\n" +
                "        <country>Франция</country>\n" +
                "        <kinopoisk_score>7.994</kinopoisk_score>\n" +
                "        <duration>122</duration>\n" +
                "    </movie>\n" +
                "</movies>\n";

        List<MovieOutput> movieOutputList = new ArrayList<>();
        for (int index = 0; index < movieInputList.size(); index++)
            movieOutputList.add(new MovieOutput(movieInputList.get(index)));
        MovieList movies = new MovieList(movieOutputList);

        String output = ConverterController.getXmlString(movies);

        Assertions.assertEquals(xml, output);
    }

}
