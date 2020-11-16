package converter;

import converter.model.Form;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@AutoConfigureMockMvc
public class ConverterTest {

    @Autowired
    private MockMvc mockMvc;

    private static MockMultipartFile correctInputFile, correctInputFileWithEmptyFields, incorrectInputFile;

    private static String correctFullOutputFile, correctOutputFileWithEmptyFields, correctSortedOutputFile, correctFilteredOutputFile;

    private static Form form;

    @BeforeAll
    private static void getFiles() throws IOException {
        try(FileInputStream correctFileInputStream =
                    new FileInputStream(new File("src/test/resources/correct_full.csv"))) {
            correctInputFile = new MockMultipartFile("file", correctFileInputStream);
        }
        try (FileInputStream correctFileWithEmptyFieldsInputStream =
                     new FileInputStream(new File("src/test/resources/correct_with_empty_fields.csv"))) {
            correctInputFileWithEmptyFields = new MockMultipartFile("file", correctFileWithEmptyFieldsInputStream);
        }
        try (FileInputStream incorrectFileInputStream =
                     new FileInputStream(new File("src/test/resources/incorrect_fields.csv"))) {
            incorrectInputFile = new MockMultipartFile("file", incorrectFileInputStream);
        }
        correctFullOutputFile = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_full.xml")),
                StandardCharsets.UTF_8);
        correctOutputFileWithEmptyFields = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_with_empty_fields.xml")),
                StandardCharsets.UTF_8);
        correctSortedOutputFile = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_sorted.xml")),
                StandardCharsets.UTF_8);
        correctFilteredOutputFile = new String(Files.readAllBytes(Paths.get("src/test/resources/correct_filtered.xml")),
                StandardCharsets.UTF_8);
    }

    @BeforeEach
    private void setForm() {
        form = new Form();
        form.setFirstYearFilter(1970);
        form.setLastYearFilter(2020);
        form.setFirstSorting("noSorting");
        form.setSecondSorting("noSorting");
    }

    @Test
    public void correctFull() throws Exception {
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(correctInputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, correctFullOutputFile);
    }

    @Test
    public void correctWithEmptyFields() throws Exception {
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(correctInputFileWithEmptyFields).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, correctOutputFileWithEmptyFields);
    }

    @Test
    public void correctSorted() throws Exception {
        form.setFirstSorting("genre");
        form.setSecondSorting("kinopoiskScore");
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(correctInputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, correctSortedOutputFile);
    }

    @Test
    public void correctFiltered() throws Exception {
        form.setFirstYearFilter(2000);
        form.setLastYearFilter(2010);
        String input = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(correctInputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(input, correctFilteredOutputFile);
    }

    @Test
    public void incorrectFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/").file(incorrectInputFile).flashAttr("form", form))
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

}
