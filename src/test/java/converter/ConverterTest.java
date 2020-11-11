package converter;

import converter.model.Form;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
public class ConverterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void correctFull() throws Exception {
        MockMultipartFile file;
        try (FileInputStream inputStream = new FileInputStream(new File("src/test/resources/correct_full.csv"))) {
            file = new MockMultipartFile("file", inputStream);
        }
        String output = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_full.xml")),
                StandardCharsets.UTF_8);
        Form form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(file).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, output);
    }

    @Test
    public void correctWithEmptyFields() throws Exception {
        MockMultipartFile file;
        try (FileInputStream inputStream = new FileInputStream(new File("src/test/resources/correct_with_empty_fields.csv"))) {
            file = new MockMultipartFile("file", inputStream);
        }
        String output = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_with_empty_fields.xml")),
                StandardCharsets.UTF_8);
        Form form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(file).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, output);
    }

    @Test
    public void correctSorted() throws Exception {
        MockMultipartFile file;
        try (FileInputStream inputStream = new FileInputStream(new File("src/test/resources/correct_full.csv"))) {
            file = new MockMultipartFile("file", inputStream);
        }
        String output = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_sorted.xml")),
                StandardCharsets.UTF_8);
        Form form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        form.setFirstSorting("genre");
        form.setSecondSorting("kinopoiskScore");
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(file).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, output);
    }

    @Test
    public void correctFiltered() throws Exception {
        MockMultipartFile file;
        try (FileInputStream inputStream = new FileInputStream(new File("src/test/resources/correct_full.csv"))) {
            file = new MockMultipartFile("file", inputStream);
        }
        String output = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_filtered.xml")),
                StandardCharsets.UTF_8);
        Form form = new Form();
        form.setFirstYearFilter(2000);
        form.setLastYearFilter(2010);
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(file).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, output);
    }

    //@Test(expected = CsvDataTypeMismatchException.class)
    @Test
    public void incorrectFields() throws Exception {
        MockMultipartFile file;
        try (FileInputStream inputStream = new FileInputStream(new File("src/test/resources/incorrect_fields.csv"))) {
            file = new MockMultipartFile("file", inputStream);
        }
        Form form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(file).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.view().name("error"));

    }

}
