package converter;

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", "размер файла превысил допустимый предел (128 Кб)");
        return "error";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleError(FileNotFoundException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", "файл не загружен или пустой");
        return "error";
    }

    @ExceptionHandler(IOException.class)
    public String handleError(IOException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", e.getLocalizedMessage());
        return "error";
    }

    @ExceptionHandler(JAXBException.class)
    public String handleError(JAXBException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", "ошибка при создании XML файла");
        return "error";
    }

    @ExceptionHandler(CsvRequiredFieldEmptyException.class)
    public String handleError(CsvRequiredFieldEmptyException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", "CSV файл некорректен");
        return "error";
    }


}
