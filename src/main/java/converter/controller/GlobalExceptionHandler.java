package converter.controller;

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Контроллер обработки исключений
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабаботка исключения загрузки файла, размер которого превышающего допустимый предел
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("error", "размер файла превысил допустимый предел (128 Кб)");
        logger.warn("Файл превысил допустимый размер ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения загрузки пустого файла
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(FileNotFoundException.class)
    public String handleError(FileNotFoundException e, Model model) {
        model.addAttribute("error", "файл не загружен или пустой");
        logger.warn("Файл не загружен или загружен пустой ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при создании XML файла
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(JAXBException.class)
    public String handleError(JAXBException e, Model model) {
        model.addAttribute("error", "ошибка при создании XML файла");
        logger.warn("Ошибка при создании XML файла ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при загрузке некорректного CSV файла
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(CsvRequiredFieldEmptyException.class)
    public String handleError(CsvRequiredFieldEmptyException e, Model model) {
        model.addAttribute("error", "CSV файл некорректен");
        logger.warn("Некорректный CSV файл ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при некорректном выборе типа сортировки
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleError(NoSuchElementException e, Model model) {
        model.addAttribute("error", "некорректно выбран тип сортировки");
        logger.warn("Тип сортировки выбран некорректно ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения при некорректном вводе значений для фильтрации
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public String handleError(org.springframework.validation.BindException e, Model model) {
        model.addAttribute("error", "некорректно введены значения для фильтрации");
        logger.warn("Данные для фильтрации введены некорректно ({})", e.getMessage());
        return "error";
    }

    /**
     * Обрабаботка исключения, вызванного потоками данных
     *
     * @param e     обрабатываемое исключение
     * @param model модель
     * @return шаблон страницы с ошибкой
     */
    @ExceptionHandler(IOException.class)
    public String handleError(IOException e, Model model) {
        model.addAttribute("error", e.getLocalizedMessage());
        logger.warn("Ошибка ввода-вывода ({})", e.getMessage());
        return "error";
    }
}
